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
import javax.ws.rs.sse.SseEventSource;

/**
 * Utilities for communicating with the server.
 */
public class ServerUtils {

    private static final String SERVER = "http://localhost:8080/";
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
                    client = client.register(new Authenticator(o.readEntity(String.class)));
                    ClientState.user = user;
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
     * Function that checks user credentials.
     *
     * @param email string representing
     *              the email of the user.
     * @param password string representing
     *                 the password of the user.
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
                System.out.println(loginDTO);
                client = client.register(new Authenticator(loginDTO.getToken()));
                ClientState.user = user;
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
     * Handler for when the create lobby succeeds.
     */
    public interface CreateLobbyHandlerSuccess {
        void handle(GameDTO game);
    }

    /**
     * Handler for when the create lobby fails.
     */
    public interface CreateLobbyHandlerFail {
        void handle();
    }

    /**
     * This function makes a call to create a new lobby.
     *
     * @param createLobbyHandlerSuccess The function that will be called if the request is successful.
     * @param createLobbyHandlerFail The function that will be called if the request is unsuccessful.
     */
    public void createLobby(CreateLobbyHandlerSuccess createLobbyHandlerSuccess,
                            CreateLobbyHandlerFail createLobbyHandlerFail) {
        var config = new NormalGameConfigurationDTO(null, Duration.ofSeconds(10), 1, 10, 3, 2f, 100, 0, 75);
        var game = new NormalGameDTO();
        game.setId(UUID.randomUUID());
        game.setConfiguration(config);

        Invocation invocation = client
                .target(SERVER).path("/api/lobby")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildPost(Entity.entity(game, APPLICATION_JSON));

        invocation.submit(new InvocationCallback<GameDTO>() {

            @Override
            public void completed(GameDTO game) {
                System.out.println(game);
                ClientState.game = game;
                createLobbyHandlerSuccess.handle(game);
            }

            @Override
            public void failed(Throwable throwable) {
                createLobbyHandlerFail.handle();
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Handler for when getting all lobbies succeeds.
     */
    public interface GetLobbiesHandlerSuccess {
        void handle(List<GameDTO> games);
    }

    /**
     * Handler for when getting all lobbies fails.
     */
    public interface GetLobbiesHandlerFail {
        void handle();
    }

    /**
     * Function that gets all the lobbies in the database.
     *
     * @param getLobbiesHandlerSuccess The function that will be called if the request is successful.
     * @param getLobbiesHandlerFail The function that will be called if the request is unsuccessful.
     */
    public void getLobbies(GetLobbiesHandlerSuccess getLobbiesHandlerSuccess,
                            GetLobbiesHandlerFail getLobbiesHandlerFail) {
        Invocation invocation = client
                .target(SERVER).path("/api/lobby/available")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildGet();

        invocation.submit(new InvocationCallback<List<GameDTO>>() {

            @Override
            public void completed(List<GameDTO> o) {
                getLobbiesHandlerSuccess.handle(o);
            }

            @Override
            public void failed(Throwable throwable) {
                getLobbiesHandlerFail.handle();
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Handler for when joining a lobby succeeds.
     */
    public interface JoinLobbyHandlerSuccess {
        void handle(GameDTO gameDTO);
    }

    /**
     * Handler if the joining of the lobby fails.
     */
    public interface JoinLobbyHandlerFail {
        void handle();
    }

    /**
     * This function handles a user joining a lobby.
     *
     * @param lobbyId The id of the lobby that the user wants to join.
     * @param joinLobbyHandlerSuccess The function that will be called if the request is successful.
     * @param joinLobbyHandlerFail The function that will be called if the request is unsuccessful.
     */
    public void joinLobby(UUID lobbyId, JoinLobbyHandlerSuccess joinLobbyHandlerSuccess,
                           JoinLobbyHandlerFail joinLobbyHandlerFail) {
        Invocation invocation = client
                .target(SERVER).path("/api/lobby/" + lobbyId.toString() + "/join")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildPut(Entity.entity("", APPLICATION_JSON));

        invocation.submit(new InvocationCallback<GameDTO>() {

            @Override
            public void completed(GameDTO game) {
                System.out.println(game);
                ClientState.game = game;
                subscribeToSSE(sseHandler);
                joinLobbyHandlerSuccess.handle(game);
            }

            @Override
            public void failed(Throwable throwable) {
                joinLobbyHandlerFail.handle();
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Handler for when getting the logged in user succeeds.
     */
    public interface GetUserInfoHandlerSuccess {
        void handle(UserDTO userDTO);
    }

    /**
     * Handler for when getting the logged in user fails.
     */
    public interface GetUserInfoHandlerFail {
        void handle();
    }

    /**
     * Function that gets all the info about the currently logged in user.
     *
     * @param getUserInfoHandlerSuccess The function that will be called if the request is successful.
     * @param getUserInfoHandlerFail The function that will be called if the request is unsuccessful.
     */
    public void getMyInfo(GetUserInfoHandlerSuccess getUserInfoHandlerSuccess,
                          GetUserInfoHandlerFail getUserInfoHandlerFail) {
        Invocation invocation = client
                .target(SERVER).path("/api/user")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildGet();

        invocation.submit(new InvocationCallback<UserDTO>() {

            @Override
            public void completed(UserDTO o) {
                getUserInfoHandlerSuccess.handle(o);
            }

            @Override
            public void failed(Throwable throwable) {
                getUserInfoHandlerFail.handle();
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Handler for getting the lobby info succeeds.
     */
    public interface GetLobbyInfoHandlerSuccess {
        void handle(GameDTO gameDTO);
    }

    /**
     * Handler for getting the lobby info fails.
     */
    public interface GetLobbyInfoHandlerFail {
        void handle();
    }

    /**
     * Function that gets all the lobby info from the provided lobby id.
     *
     * @param getLobbyInfoHandlerSuccess The function that will be called if the request is successful.
     * @param getLobbyInfoHandlerFail The function that will be called if the request is unsuccessful.
     */
    public void getLobbyInfo(GetLobbyInfoHandlerSuccess getLobbyInfoHandlerSuccess,
                          GetLobbyInfoHandlerFail getLobbyInfoHandlerFail) {
        Invocation invocation = client
                .target(SERVER).path("/api/lobby/" + lobbyId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildGet();
        invocation.submit(new InvocationCallback<GameDTO>() {
            @Override
            public void completed(GameDTO o) {
                getLobbyInfoHandlerSuccess.handle(o);
            }

            @Override
            public void failed(Throwable throwable) {
                getLobbyInfoHandlerFail.handle();
                throwable.printStackTrace();
            }
        });
    }

    public String connect() {
        System.out.println("New connection!\n");
        return "200";
    }

    /**
     * Handler for when a disband lobby succeeds.
     */
    public interface DisbandLobbyHandler {
        void handle(Response response);
    }

    /**
     * Function that causes the host to delete the lobby.
     */
    public void disbandLobby(DisbandLobbyHandler disbandLobbyHandler) {
        var request = client
                .target(SERVER).path("/api/lobby/delete")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildDelete();
        request.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                disbandLobbyHandler.handle(response);
            }

            @Override
            public void failed(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    /**
     * This function subscribes to the SSE event source.
     * It calls the SSE open endpoint and handles the events.
     *
     * @param sseHandler The handler of sse events, exceptions and completion.
     */
    public static void subscribeToSSE(SSEHandler sseHandler) {
        // This creates the WebTarget that the sse event source will use.
        var target = getRequestTarget().path("/api/sse/open");

        // Builds the event source with the target.
        SseEventSource eventSource = SseEventSource.target(target).reconnectingEvery(0, MICROSECONDS).build();

        // Registers the handling of events, exceptions and completion.
        eventSource.register(
                sseHandler::handleEvent,
                sseHandler::handleException,
                sseHandler::handleCompletion);

        // Opens the sse listener.
        eventSource.open();

        // Sets the source of the events in the handler.
        sseHandler.setSseEventSource(eventSource);
    }

    /**
     * Handler for starting a game.
     */
    public interface StartLobbyHandler {
        void handle(Response response);
    }

    /**
     * This function starts a lobby from the server.
     *
     * @param startLobbyHandler the handler of the response.
     */
    public void startLobby(StartLobbyHandler startLobbyHandler) {
        Invocation invocation = client.target(SERVER)
            .path("/api/lobby/" + ClientState.game.getId() + "/start")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildPut(Entity.entity("", APPLICATION_JSON));

        invocation.submit(new InvocationCallback<Response>() {

            @Override
            public void completed(Response o) {
                startLobbyHandler.handle(o);
            }

            @Override
            public void failed(Throwable throwable) {

            }
        });
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
