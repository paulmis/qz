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

import commons.Quote;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.glassfish.jersey.client.ClientConfig;


/**
 * Utilities for communicating with the server.
 */
public class ServerUtils {

    private static final String SERVER = "http://localhost:8080/";

    /** Obtain all existing quotes from the server manually.
     *
     * @throws IOException Exception thrown during the GET request
     */
    public void getQuotesTheHardWay() throws IOException {
        var url = new URL("http://localhost:8080/api/quotes");
        var is = url.openConnection().getInputStream();
        var br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    /** Obtain all existing quotes from the server.
     *
     * @return List of quotes obtained from the server
     */
    public List<Quote> getQuotes() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Quote>>() {});
    }

    /** Add a new quote to the remote server.
     *
     * @param quote Quote to add
     * @return Quote returned by the server
     */
    public Quote addQuote(Quote quote) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/quotes") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
    }

    /**
     * Gets a list of all the emoji urls from the backend
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
     * Function that causes the user to leave the game.
     */
    public void quitGame() {
        System.out.println("Quitting game");
    }

    public List<URL> getLeaderBoardImages() {
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
}