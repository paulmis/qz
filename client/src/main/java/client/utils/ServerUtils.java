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

package client.utils;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import commons.entities.auth.LoginDTO;
import commons.entities.auth.UserDTO;
import commons.entities.game.GameDTO;
import commons.entities.game.NormalGameDTO;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 * Utilities for communicating with the server.
 */
public class ServerUtils {

    private static final String SERVER = "http://localhost:8080/";
    public static Client client = ClientBuilder.newClient().register(JavaTimeModule.class)
            .register(JacksonJsonProvider.class).register(JavaTimeModule.class);
    public static boolean loggedIn = false;

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
        loggedIn = false;
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        JacksonJsonProvider provider = new JacksonJsonProvider(mapper);
        return ClientBuilder.newClient().register(provider);
    }

    public String register(String email, String password) {
        System.out.println("Registering new User...\n");
        return "200";
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
        var invocation = client
                .target(SERVER).path("/api/auth/login")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildPost(Entity.entity(user, APPLICATION_JSON));
        invocation.submit(new InvocationCallback<LoginDTO>() {

            @Override
            public void completed(LoginDTO loginDTO) {
                System.out.println(loginDTO);
                logInHandlerSuccess.handle(loginDTO);
                client = client.register(new Authenticator(loginDTO.getToken()));
                loggedIn = true;
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
        var config = new NormalGameConfigurationDTO(null, 60, 1, 20);
        var game = new NormalGameDTO();
        game.setId(UUID.randomUUID());
        game.setConfiguration(config);

        var invocation = client
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
        var invocation = client
                .target(SERVER).path("/api/lobby/available")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildGet();

        invocation.submit(new InvocationCallback<List<GameDTO>>() {

            @Override
            public void completed(List<GameDTO> o) {
                System.out.println(o);
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
        var invocation = client
                .target(SERVER).path("/api/lobby/" + lobbyId.toString() + "/join")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildPut(Entity.entity("", APPLICATION_JSON));

        invocation.submit(new InvocationCallback<GameDTO>() {

            @Override
            public void completed(GameDTO game) {
                System.out.println(game);
                ClientState.game = game;
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
     * Function that gets all the info about the currently logged in player.
     *
     * @param getUserInfoHandlerSuccess The function that will be called if the request is successful.
     * @param getUserInfoHandlerFail The function that will be called if the request is unsuccessful.
     */
    public void getMyInfo(GetUserInfoHandlerSuccess getUserInfoHandlerSuccess,
                          GetUserInfoHandlerFail getUserInfoHandlerFail) {
        var invocation = client
                .target(SERVER).path("/api/user")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildGet();

        invocation.submit(new InvocationCallback<UserDTO>() {

            @Override
            public void completed(UserDTO o) {
                System.out.println(o);
                getUserInfoHandlerSuccess.handle(o);
            }

            @Override
            public void failed(Throwable throwable) {
                getUserInfoHandlerFail.handle();
                throwable.printStackTrace();
            }
        });
    }

    public String connect() {
        System.out.println("New connection!\n");
        return "200";
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
