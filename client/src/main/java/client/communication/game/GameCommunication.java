package client.communication.game;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import client.utils.ClientState;
import client.utils.communication.ServerUtils;
import commons.entities.ActivityDTO;
import commons.entities.AnswerDTO;
import commons.entities.game.GamePlayerDTO;
import commons.entities.game.PowerUp;
import commons.entities.game.ReactionDTO;
import commons.entities.questions.QuestionDTO;
import commons.entities.utils.ApiError;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
     */
    public void getReactions(GetReactionsHandlerSuccess handlerSuccess, GetReactionsHandlerFail handlerFail) {
        Invocation request = ServerUtils.getRequestTarget()
                .path("/api/reaction")
                .request(APPLICATION_JSON)
                .buildGet();

        request.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                if (response.getStatus() == 200) {
                    Map<String, URI> urls = response.readEntity(new GenericType<>() {
                    });
                    handlerSuccess.handle(urls);
                } else {
                    log.error("Failed to get the reactions: status code {}", response.getStatus());
                    handlerFail.handle();
                }
            }

            @Override
            public void failed(Throwable throwable) {
                log.error("Failed to get the reactions", throwable);
                handlerFail.handle();
            }
        });
    }

    /**
     * Function that causes the user to leave the game.
     */
    public void quitGame(QuitGameHandler quitGameHandler) {
        Invocation request = ServerUtils.getRequestTarget()
                .path("/api/game/leave")
                .request(APPLICATION_JSON)
                .buildPost(Entity.json(""));

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
                .buildPost(Entity.entity(powerUp, APPLICATION_JSON));

        // Perform the query asynchronously
        request.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                if (response.getStatus() == 200) {
                    try {
                        handleSuccess.handle(response.readEntity(ActivityDTO.class));
                    } catch (Exception e) {
                        handleSuccess.handle(null);
                    }
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
     * Function that tells the server the player sent a reaction.
     *
     * @param reaction The reaction sent.
     * @param handleSuccess handler for successful operation.
     * @param handleFail handler for failed operation.
     */
    public void sendReaction(ReactionDTO reaction,
                             SendReactionHandlerSuccess handleSuccess, SendReactionHandlerFail handleFail) {
        log.debug("Sending reaction: {}", reaction.getReactionType());

        // Build the query invocation
        Invocation request = ServerUtils.getRequestTarget()
                .path("/api/reaction/send")
                .request(APPLICATION_JSON)
                .buildPost(Entity.entity(reaction, APPLICATION_JSON));

        // Perform the query asynchronously
        request.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                log.debug("Reaction sent response: {}", response.getStatus());
                handleSuccess.handle();
            }

            @Override
            public void failed(Throwable throwable) {
                handleFail.handle(null);
                log.error("Could not send reaction: {}", throwable.getMessage());
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
     * Handler for when getting emojis succeeds.
     */
    public interface GetReactionsHandlerSuccess {
        void handle(Map<String, URI> emojis);
    }

    /**
     * Handler for when getting emojis fails.
     */
    public interface GetReactionsHandlerFail {
        void handle();
    }

    /**
     * Handler for when sending a power-up succeeds.
     */
    public interface SendPowerUpHandlerSuccess {
        void handle(ActivityDTO activity);
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

    /**
     * Handler for when sending a reaction succeeds.
     */
    public interface SendReactionHandlerSuccess {
        void handle();
    }

    /**
     * Handler for when sending a reaction fails.
     */
    public interface SendReactionHandlerFail {
        void handle(ApiError error);
    }

}
