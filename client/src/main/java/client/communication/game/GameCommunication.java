package client.communication.game;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import client.utils.ClientState;
import client.utils.communication.ServerUtils;
import commons.entities.AnswerDTO;
import commons.entities.questions.QuestionDTO;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides functions retrieving game data from the server.
 */
@Slf4j
public class GameCommunication {
    /**
     * Requests the answer from the server.
     *
     * @param gameId         the id of the game.
     * @param handlerSuccess the handler for when the request succeeds.
     * @param handlerFailure the handler for when the request fails.
     */
    public static void updateCurrentAnswer(UUID gameId,
                                           UpdateAnswerHandlerSuccess handlerSuccess,
                                           UpdateAnswerHandlerFailure handlerFailure) {

        log.debug("Updating current answer");

        // Build the query invocation
        Invocation invocation = ServerUtils.getRequestTarget()
                .path("/api/answer/" + gameId + "/answer")
                .request(APPLICATION_JSON)
                .buildGet();

        // Perform the query asynchronously
        invocation.submit(new InvocationCallback<Response>() {

            @Override
            public void completed(Response response) {
                if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                    handlerSuccess.handle(response.readEntity(AnswerDTO.class));
                } else {
                    log.error("Failed to update current answer: {}", response.getStatus());
                    handlerFailure.handle();
                }
            }

            @Override
            public void failed(Throwable throwable) {
                log.error("Failed to update current answer", throwable);
                handlerFailure.handle();
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Requests the current question from the server.
     *
     * @param gameId         the id of the game
     * @param handlerSuccess the handler for when the request succeeds
     * @param handlerFail    the handler for when the request fails
     */
    public static void updateCurrentQuestion(UUID gameId,
                                             UpdateQuestionHandlerSuccess handlerSuccess,
                                             UpdateQuestionHandlerFail handlerFail) {
        // Build the query invocation
        Invocation invocation =
                ServerUtils.getRequestTarget()
                        .path("/api/game/" + gameId + "/question")
                        .request(APPLICATION_JSON)
                        .buildGet();

        // Perform the query asynchronously
        invocation.submit(new InvocationCallback<Response>() {

            @Override
            public void completed(Response response) {
                if (response.getStatus() == 200) {
                    QuestionDTO question = response.readEntity(QuestionDTO.class);
                    ClientState.game.setCurrentQuestion(question);
                    handlerSuccess.handle(question);
                } else {
                    handlerFail.handle();
                }

            }

            @Override
            public void failed(Throwable throwable) {
                handlerFail.handle();
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Sends the answer to the current question to the server.
     *
     * @param gameId         the id of the game
     * @param answer         the answer to the current question
     * @param handlerSuccess the handler for when the request succeeds
     * @param handlerFailure the handler for when the request fails
     */
    public static void putAnswer(UUID gameId, AnswerDTO answer,
                                 PutAnswerHandlerSuccess handlerSuccess, PutAnswerHandlerFail handlerFailure) {
        // Build the query invocation
        Invocation invocation =
                ServerUtils.getRequestTarget()
                        .path("/api/game/" + gameId + "/answer")
                        .request(APPLICATION_JSON)
                        .buildPut(Entity.entity(answer, APPLICATION_JSON));

        // Perform the query asynchronously
        invocation.submit(new InvocationCallback<Response>() {

            @Override
            public void completed(Response response) {
                log.info("Answer sent successfully");
                handlerSuccess.handle();
            }

            @Override
            public void failed(Throwable throwable) {
                log.error("Failed to send the answer");
                handlerFailure.handle();
                throwable.printStackTrace();
            }
        });
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
     * Function that causes the user to leave the game.
     */
    public void quitGame(QuitGameHandler quitGameHandler) {
        Invocation request = ServerUtils.getRequestTarget()
                .path("/api/game/leave")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildPost(Entity.json("{}"));

        request.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                quitGameHandler.handle(response);
            }

            @Override
            public void failed(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Gets a list of the leaderboard images from the server.
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
     * Handler for when the quitting game succeeds.
     */
    public interface QuitGameHandler {
        void handle(Response response);
    }

    /**
     * Handler for when getting the current question succeeds.
     */
    public interface UpdateQuestionHandlerSuccess {
        void handle(QuestionDTO userDTO);
    }

    /**
     * Handler for when getting the current question fails.
     */
    public interface UpdateQuestionHandlerFail {
        void handle();
    }

    /**
     * Handler for when getting the answer succeeds.
     */
    public interface UpdateAnswerHandlerSuccess {
        void handle(AnswerDTO answerDTO);
    }

    /**
     * Handler for when getting the current answer fails.
     */
    public interface UpdateAnswerHandlerFailure {
        void handle();
    }

    /**
     * Handler for when sending answer to the current question succeeds.
     */
    public interface PutAnswerHandlerSuccess {
        void handle();
    }

    /**
     * Handler for when sending answer to the current question fails.
     */
    public interface PutAnswerHandlerFail {
        void handle();
    }

}
