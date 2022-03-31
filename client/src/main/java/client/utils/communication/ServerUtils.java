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

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import client.utils.Authenticator;
import client.utils.ClientState;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import commons.entities.auth.LoginDTO;
import commons.entities.auth.UserDTO;
import commons.entities.game.GameDTO;
import commons.entities.game.GamePlayerDTO;
import commons.entities.game.NormalGameDTO;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import commons.entities.utils.ApiError;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Utilities for communicating with the server.
 */
@Slf4j
public class ServerUtils {

    private static String SERVER = "http://localhost:8080/";
    public static SSEHandler sseHandler = new SSEHandler();
    public static Client client = ClientBuilder.newClient().register(JavaTimeModule.class)
            .register(JacksonJsonProvider.class).register(JavaTimeModule.class);

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
    private Client newClient() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        JacksonJsonProvider provider = new JacksonJsonProvider(mapper);
        return ClientBuilder.newClient().register(provider);
    }

    /** Gets a list of the leaderboard images from the server.
     *
     * @return a list of leaderboard images.
     */
    public List<URL> getLeaderBoardImages() {
        try {
            return Arrays.asList(
                    new URL("https://en.gravatar.com/userimage/215919617/deb21f77ed0ec5c42d75b0dae551b912.png?size=50"),
                    new URL("https://en.gravatar.com/userimage/215919617/deb21f77ed0ec5c42d75b0dae551b912.png?size=50"),
                    new URL("https://en.gravatar.com/userimage/215919617/deb21f77ed0ec5c42d75b0dae551b912.png?size=50"),
                    new URL("https://en.gravatar.com/userimage/215919617/deb21f77ed0ec5c42d75b0dae551b912.png?size=50"),
                    new URL("https://en.gravatar.com/userimage/215919617/deb21f77ed0ec5c42d75b0dae551b912.png?size=50"));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Handler for when the register succeds.
     */
    public interface RegisterHandler {
        void handle(Response response, ApiError error);
    }

    /**
     * Function that registers a new user.
     *
     * @param username string representing
     *              the email of the user.
     * @param email string representing
     *              the email of the user.
     * @param password string representing
     *                 the password of the user.
     */
    public void register(String username, String email, String password,
                           RegisterHandler registerHandler) {
        client = this.newClient();
        UserDTO user = new UserDTO(username, email, password);
        var invocation = client
                .target(SERVER).path("/api/auth/register")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildPost(Entity.entity(user, APPLICATION_JSON));

        invocation.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response o) {
                if (o.getStatus() == 201) {
                    LoginDTO loginDTO = o.readEntity(LoginDTO.class);
                    client = client.register(new Authenticator(loginDTO.getToken()));
                    ClientState.user = loginDTO.getUser();
                    registerHandler.handle(o, new ApiError());
                } else if (o.getStatus() == 400) {
                    registerHandler.handle(o, o.readEntity(ApiError.class));
                } else if (o.getStatus() == 409) {
                    registerHandler.handle(o, new ApiError());
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
     * Logs the user in, granting access to the API.
     *
     * @param email string representing the email of the user.
     * @param password string representing the password of the user.
     */
    public void logIn(String email, String password,
                      LogInHandlerSuccess logInHandlerSuccess, LogInHandlerFail logInHandlerFail) {

        client = this.newClient();
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
     */
    public void connect(String serverPath) {
        this.SERVER = serverPath;
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
            return r.readEntity(new GenericType<List<UserDTO>>() {});
        }
        return new ArrayList<>();
    }

    public void signOut() {
        client = newClient();
    }
}
