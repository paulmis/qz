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

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import commons.entities.UserDTO;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;


/**
 * Utilities for communicating with the server.
 */
public class ServerUtils {

    private static final String SERVER = "http://localhost:8080/";
    private static Client client = ClientBuilder.newClient(new ClientConfig());
    public static boolean loggedIn = false;

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
     * Function that checks user credentials.
     *
     * @param email string representing
     *              the email of the user.
     * @param password string representing
     *                 the password of the user.
     * @return String representing the jwt
     *          of the user.
     */
    public String logIn(String email, String password) {
        client = ClientBuilder.newClient(new ClientConfig());
        UserDTO user = new UserDTO("alex", email, password);
        var answer = client
                .target(SERVER).path("/api/auth/login")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(user, APPLICATION_JSON), String.class);
        client = client.register(new Authenticator(answer));
        loggedIn = true;
        return answer;
    }

    public String connect() {
        System.out.println("New connection!\n");
        return "200";
    }
}
