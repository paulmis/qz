/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package client.utils.communication;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;

import client.communication.admin.AdminCommunication;
import client.utils.Authenticator;
import client.utils.ClientState;
import client.utils.EncryptionUtils;
import client.utils.PreferencesManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import commons.entities.ActivityDTO;
import commons.entities.auth.LoginDTO;
import commons.entities.auth.UserDTO;
import commons.entities.utils.ApiError;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.AttachmentBuilder;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

/**
 * Utilities for communicating with the server.
 */
@Slf4j
public class ServerUtils {

    private static String SERVER = "http://localhost:8080/";
    public static SSEHandler sseHandler = new SSEHandler();
    public static Client client = newClient();

    public static String getImagePathFromId(UUID id) {
        return SERVER + "api/resource/" + id.toString();
    }

    /**
     * Provides a request target for the server that can be used to build and invoke a query.
     *
     * @return the request target.
     */
    public static WebTarget getRequestTarget() {
        return client.target(SERVER);
    }

    /**
     * This function creates a new client with the mandatory
     * JacksonJsonProvider and the JavaTimeModule.
     *
     * @return the new client.
     */
    private static Client newClient() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        JacksonJsonProvider provider = new JacksonJsonProvider(mapper);
        return ClientBuilder.newClient().register(provider);
    }

    /**
     * Resets the client.
     */
    private void resetClient() {
        if (client != null) {
            client.close();
        }
        client = newClient();
    }

    /**
     * Function to check if entered email is indeed an email.
     *
     * @param email email string entered by user
     * @return true if it is a valid email, false otherwise
     */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."
                + "[a-zA-Z0-9_+&*-]+)*@"
                + "(?:[a-zA-Z0-9-]+\\.)+[a-z"
                + "A-Z]{2,7}$";

        Pattern emailPattern = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return emailPattern.matcher(email).matches();
    }

    /**
     * Handler for when the register succeeds.
     */
    public interface RegisterHandler {
        void handle(Response response, LoginDTO dto, ApiError error);
    }

    /**
     * Function that registers a new user.
     *
     * @param username        string representing the name of the user.
     * @param email           string representing the email of the user.
     * @param password        string representing the password of the user.
     * @param image           the file of the user profile pic.
     * @param registerHandler handler called when a response is received.
     */
    public void register(String username, String email, String password,
                           File image, RegisterHandler registerHandler) {
        resetClient();

        // The list of attachments
        List<Attachment> attachments = new ArrayList<>();

        // Add the user dto as an attachment.
        UserDTO user = new UserDTO(username, email, password);
        attachments.add((new AttachmentBuilder())
                .mediaType(APPLICATION_JSON)
                .object(user)
                .contentDisposition(new ContentDisposition("form-data;name=\"userData\""))
                .build());

        // If the image is not null add it to the attachments.
        if (image != null) {
            try {
                attachments.add((new AttachmentBuilder())
                        .mediaType(APPLICATION_OCTET_STREAM)
                        .object(new FileInputStream(image))
                        .contentDisposition(new ContentDisposition("form-data;name=\"image\";filename=\"image\""))
                        .build());
            } catch (FileNotFoundException e) {
                log.error("Couldn't create input stream.");
                e.printStackTrace();
            }
        }

        // Create the multipart body that holds all attachments.
        var multiPartBody = new MultipartBody(attachments);

        var invocation = client
                .target(SERVER)
                .register(new org.apache.cxf.jaxrs.provider.MultipartProvider())
                .path("/api/auth/register")
                .request(APPLICATION_JSON)
                .header("Content-Type", "multipart/form-data")
                .buildPost(Entity.entity(multiPartBody, "multipart/mixed"));

        invocation.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response o) {
                if (o.getStatus() == 201) {
                    LoginDTO loginDTO = o.readEntity(LoginDTO.class);
                    client = client.register(new Authenticator(loginDTO.getToken()));
                    ClientState.user = loginDTO.getUser();
                    registerHandler.handle(o, loginDTO, new ApiError());
                } else if (o.getStatus() == 400) {
                    registerHandler.handle(o, null, o.readEntity(ApiError.class));
                } else if (o.getStatus() == 409) {
                    registerHandler.handle(o, null, new ApiError());
                }
            }

            @Override
            public void failed(Throwable throwable) {
                System.out.println("HERE " + throwable.toString());
            }
        });
    }

    /**
     * Handler for when the log in succeeds.
     */
    public interface LogInHandlerSuccess {
        void handle(LoginDTO token);
    }

    /**
     * Handler for when the log in fails.
     */
    public interface LogInHandlerFail {
        void handle();
    }

    /**
     * Handler for when token is valid.
     */
    public interface LoginValidHandler {
        void handle(LoginDTO data);
    }

    /**
     * Check whether the provided token is valid.
     *
     * @param token token to check.
     * @param handler handler to call if the token is valid.
     */
    public void checkTokenValid(String token, LoginValidHandler handler) {
        log.debug("Checking token validity");
        client = newClient();
        Invocation invocation = client.target(SERVER).path("/api/user").request(APPLICATION_JSON)
                .header("Authorization", "Bearer " + token).buildGet();
        invocation.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response o) {
                if (o.getStatus() == 200) {
                    LoginDTO data = o.readEntity(LoginDTO.class);
                    log.info("Token is valid: {}", data.getUser());

                    client = client.register(new Authenticator(token));
                    ClientState.user = data.getUser();
                    ClientState.game = data.getGame();

                    handler.handle(data);
                } else {
                    log.info("Token is not valid");
                }
            }

            @Override
            public void failed(Throwable throwable) {
                log.info("Token not valid");
            }
        });
    }

    /**
     * Logs the user in, granting access to the API.
     *
     * @param email    string representing the email of the user.
     * @param password string representing the password of the user.
     */
    public void logIn(String email, String password,
                      LogInHandlerSuccess logInHandlerSuccess, LogInHandlerFail logInHandlerFail) {

        resetClient();
        UserDTO user = new UserDTO("", email, password);
        Invocation invocation = client
                .target(SERVER).path("/api/auth/login")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildPost(Entity.entity(user, APPLICATION_JSON));
        invocation.submit(new InvocationCallback<LoginDTO>() {

            @Override
            public void completed(LoginDTO loginDTO) {
                log.info("Logged in: " + loginDTO.getUser());
                client = client.register(new Authenticator(loginDTO.getToken()));
                ClientState.user = loginDTO.getUser();
                logInHandlerSuccess.handle(loginDTO);
            }

            @Override
            public void failed(Throwable throwable) {
                logInHandlerFail.handle();
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Function to connect to the server and sets the server path.
     *
     * @param serverPath the server path to connect to
     * @return whether the connection was successful or not
     */
    public boolean connect(String serverPath) {
        try {
            Response r = client.target(serverPath).path("/api/misc/ping").request().get();
            if (r.getStatus() == Response.Status.OK.getStatusCode()) {
                SERVER = serverPath;
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the global leaderboard from the database(mock function for now).
     *
     * @return the list of users that make up the global leaderboard.
     */
    public List<UserDTO> getGlobalLeaderboard() {
        var r = client.target(SERVER).path("/api/leaderboard/score")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get();
        if (r.getStatus() == Response.Status.OK.getStatusCode()) {
            return r.readEntity(new GenericType<>() {
            });
        }
        return new ArrayList<>();
    }

    /**
     * The get user info handler success.
     */
    public interface GetUserInfoHandlerSuccess {
        void handle(UserDTO userDTO);
    }

    /**
     * The get user info handler fail.
     */
    public interface GetUserInfoHandlerFail {
        void handle(ApiError error);
    }

    /**
     * Gets information about a user by their id.
     *
     * @param userId the id of the user
     * @param handleSuccess the function to call on a successful request.
     * @param handleFail the function to call on a failed request.
     */
    public static void getUserInfoById(UUID userId,
                                GetUserInfoHandlerSuccess handleSuccess,
                                GetUserInfoHandlerFail handleFail) {
        // Build the query invocation
        Invocation request = ServerUtils.getRequestTarget()
                .path("/api/user/" + userId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildGet();

        // Perform the query asynchronously
        request.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                if (response.getStatus() == 200) {
                    handleSuccess.handle(response.readEntity(UserDTO.class));
                } else {
                    ApiError error = response.readEntity(ApiError.class);
                    handleFail.handle(error);
                }
            }

            @Override
            public void failed(Throwable throwable) {
                throwable.printStackTrace();
                handleFail.handle(null);
            }
        });
    }

    public void signOut() {
        resetClient();
    }
}
