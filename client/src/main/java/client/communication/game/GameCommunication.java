package client.communication.game;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import client.utils.ClientState;
import client.utils.communication.ServerUtils;
import commons.entities.AnswerDTO;
import commons.entities.game.GamePlayerDTO;
import commons.entities.game.PowerUp;
import commons.entities.questions.QuestionDTO;
import commons.entities.utils.ApiError;
import java.net.URL;
import java.util.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.GenericType;
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
                .path("/api/game/" + gameId + "/answer")
                .request(APPLICATION_JSON)
                .buildGet();

        // Perform the query asynchronously
        invocation.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                    log.info("Answer request completed successfully");
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
     * Update the player scores and the leaderboard.
     *
     * @param gameId game ID we're currently in.
     * @param handlerSuccess Success handler/callback.
     * @param handlerFail Failure handler/callback.
     */
    public static void updateScoreLeaderboard(UUID gameId,
                                              UpdateScoreLeaderboardHandlerSuccess handlerSuccess,
                                              UpdateScoreLeaderboardHandlerFail handlerFail) {
        Invocation request = ServerUtils.getRequestTarget()
                .path("/api/game/" + gameId + "/leaderboard")
                .request(APPLICATION_JSON)
                .buildGet();

        request.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                    log.debug("Leaderboard request completed successfully");
                    handlerSuccess.handle(response.readEntity(new GenericType<List<GamePlayerDTO>>() {}));
                } else {
                    log.error("Failed to update leaderboard: {}", response.getStatus());
                    handlerFail.handle();
                }
            }

            @Override
            public void failed(Throwable throwable) {
                log.error("Failed to update leaderboard", throwable);
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
     * Function that causes the user to leave the game.
     */
    public void getQuestionNumber(UUID gameId,
                                  GetQuestionNumberHandlerSuccess handleSuccess,
                                  GetQuestionNumberHandlerFail handleFail) {
        Invocation invocation = ServerUtils.getRequestTarget()
                .path("/api/game/" + gameId + "/questionNumber")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildGet();

        // Perform the query asynchronously
        invocation.submit(new InvocationCallback<Response>() {

            @Override
            public void completed(Response response) {
                if (response.getStatus() == 200) {
                    handleSuccess.handle(response.readEntity(Integer.class));
                } else {
                    handleFail.handle(response.readEntity(ApiError.class));
                }
            }

            @Override
            public void failed(Throwable throwable) {
                handleFail.handle(null);
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Gets a list of the leaderboard images from the server.
     *
     * @return a list of leaderboard images.
     */
    public Map<UUID, URL> getLeaderBoardImages(List<UUID> userIds) {
        // TODO: implement this properly
        try {
            Map<UUID, URL> leaderBoardImages = new HashMap<>();
            for (UUID userId : userIds) {
                leaderBoardImages.put(userId, new URL(
                        "https://en.gravatar.com/userimage/215919617/deb21f77ed0ec5c42d75b0dae551b912.png?size=50"));
            }
            return leaderBoardImages;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }


    /**
     * Sends a power-up to the game.
     *
     * @param powerUp the power-up to send.
     * @param handleSuccess the success handler.
     * @param handleFail the fail handler.
     */
    public void sendPowerUp(PowerUp powerUp, SendPowerUpHandlerSuccess handleSuccess,
                            SendPowerUpHandlerFail handleFail) {
        // Build the query invocation
        Invocation request = ServerUtils.getRequestTarget()
                .path("/api/game/powerUp")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildPost(Entity.entity(powerUp, APPLICATION_JSON));

        // Perform the query asynchronously
        request.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                if (response.getStatus() == 200) {
                    handleSuccess.handle();
                } else {
                    handleFail.handle(response.readEntity(ApiError.class));
                }
            }

            @Override
            public void failed(Throwable throwable) {
                handleFail.handle(null);
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Handler for when the quitting game succeeds.
     */
    public interface QuitGameHandler {
        void handle(Response response);
    }

    /**
     * Handler for when getting the game leaderboard succeeds.
     */
    public interface UpdateScoreLeaderboardHandlerSuccess {
        void handle(List<GamePlayerDTO> players);
    }

    /**
     * Handler for when getting the game leaderboard fails.
     */
    public interface UpdateScoreLeaderboardHandlerFail {
        void handle();
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

    /**
     * Handler for when sending a power-up succeeds.
     */
    public interface SendPowerUpHandlerSuccess {
        void handle();
    }

    /**
     * Handler for when sending a power-up fails.
     */
    public interface SendPowerUpHandlerFail {
        void handle(ApiError error);
    }

    /**
     * Handler for when getting the question number succeeds.
     */
    public interface GetQuestionNumberHandlerSuccess {
        void handle(Integer questionNumber);
    }

    /**
     * Handler for when getting the question number fails.
     */
    public interface GetQuestionNumberHandlerFail {
        void handle(ApiError error);
    }

}
