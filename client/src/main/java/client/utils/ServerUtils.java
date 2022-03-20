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
import commons.entities.UserDTO;
import commons.entities.game.NormalGameDTO;
import commons.entities.game.configuration.NormalGameConfigurationDTO;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.SseEventSource;

/**
 * Utilities for communicating with the server.
 */
public class ServerUtils {

    private static final String SERVER = "http://localhost:8080/";
    private static Client client = ClientBuilder.newClient().register(JavaTimeModule.class)
            .register(JacksonJsonProvider.class).register(JavaTimeModule.class);
    public static boolean loggedIn = false;

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

    /**
     * Gets a list of all the emoji urls from the backend.
     *
     * @return List of emoji urls
     */
    public List<URL> getEmojis() {
        try {
            return Arrays.asList(
                    new URL("https://emoji.gg/assets/emoji/8434-epic-awesome.png"),
                    new URL("https://emoji.gg/assets/emoji/8434-epic-awesome.png"),
                    new URL("https://emoji.gg/assets/emoji/8434-epic-awesome.png"),
                    new URL("https://emoji.gg/assets/emoji/8434-epic-awesome.png"),
                    new URL("https://emoji.gg/assets/emoji/8434-epic-awesome.png"),
                    new URL("https://emoji.gg/assets/emoji/8434-epic-awesome.png"),
                    new URL("https://emoji.gg/assets/emoji/8434-epic-awesome.png"),
                    new URL("https://emoji.gg/assets/emoji/8434-epic-awesome.png"));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Gets a list of all the powerUp urls from the backend.
     *
     * @return List of emoji urls
     */
    public List<URL> getPowerUps() {
        try {
            return Arrays.asList(
                    new URL("https://emoji.gg/assets/emoji/8434-epic-awesome.png"),
                    new URL("https://emoji.gg/assets/emoji/8434-epic-awesome.png"),
                    new URL("https://emoji.gg/assets/emoji/8434-epic-awesome.png"),
                    new URL("https://emoji.gg/assets/emoji/8434-epic-awesome.png"),
                    new URL("https://emoji.gg/assets/emoji/8434-epic-awesome.png"),
                    new URL("https://emoji.gg/assets/emoji/8434-epic-awesome.png"),
                    new URL("https://emoji.gg/assets/emoji/8434-epic-awesome.png"),
                    new URL("https://emoji.gg/assets/emoji/8434-epic-awesome.png"));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Function that causes the user to leave the lobby.
     */
    public void leaveLobby() {
        var request = client
                .target(SERVER).path("/api/lobby/leave")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
        if (request.getStatus() == Response.Status.OK.getStatusCode()) {
            System.out.println("Left the lobby successfully");
        } else if (request.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
            throw new IllegalStateException("User/Lobby not found");
        }
    }

    /**
     * Function that causes the user to leave the game.
     */
    public void quitGame() {
        System.out.println("Quitting game");
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

    public String register(String email, String password) {
        System.out.println("Registering new User...\n");
        return "200";
    }

    /**
     * Handler for when the log in succeeds.
     */
    public interface LogInHandlerSuccess {
        void handle(String token);
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
        invocation.submit(new InvocationCallback<String>() {

            @Override
            public void completed(String o) {
                System.out.println(o);
                logInHandlerSuccess.handle(o);
                client = client.register(new Authenticator(o));
                loggedIn = true;
            }

            @Override
            public void failed(Throwable throwable) {
                logInHandlerFail.handle();
                System.out.println(throwable);
            }
        });
    }

    public String connect() {
        System.out.println("New connection!\n");
        return "200";
    }

    /**
     * This function subscribes to the SSE event source.
     * It calls the SSE open endpoint and handles the events.
     *
     * @param sseHandler The handler of sse events, exceptions and completion.
     */
    public void subscribeToSSE(SSEHandler sseHandler) {

        // The following lines create a new game and start it.
        // These should be removed when we have proper lobby joining and creating implemented.
        var config = new NormalGameConfigurationDTO(null, 60, 1, 20);
        var game = new NormalGameDTO();
        game.setId(UUID.randomUUID());
        game.setConfiguration(config);

        var r  = client.target(SERVER).path("/api/lobby")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(game, APPLICATION_JSON));

        var lobby = r.readEntity(NormalGameDTO.class);

        client.target(SERVER).path("/api/lobby/" + lobby.getId() + "/start")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(game, APPLICATION_JSON));

        // This creates the WebTarget that the sse event source will use.
        var target = client.target(SERVER).path("api/sse/open");

        // Builds the event source with the target.
        SseEventSource eventSource = SseEventSource.target(target).build();

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
}
