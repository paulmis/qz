package client.communication.game;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import client.utils.ClientState;
import client.utils.SSEHandler;
import client.utils.ServerUtils;
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
import javax.ws.rs.sse.SseEventSource;

/**
 * Provides functions responsible for sending and receiving messages to and from the server.
 */
public class GameCommunication {
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

    /**
     * This function subscribes to the SSE event source.
     * It calls the SSE open endpoint and handles the events.
     *
     * @param sseHandler The handler of sse events, exceptions and completion.
     */
    public void subscribeToSSE(SSEHandler sseHandler) {
        ServerUtils.getRequestTarget()
                .path("/api/lobby/" + ClientState.game.getId() + "/start")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity("", APPLICATION_JSON));

        // This creates the WebTarget that the sse event source will use.
        var target = ServerUtils.getRequestTarget().path("api/sse/open");

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
     * Handler for when getting the current question succeeds.
     */
    public interface GetQuestionHandlerSuccess {
        void handle(QuestionDTO userDTO);
    }

    /**
     * Handler for when getting the current question fails.
     */
    public interface GetQuestionHandlerFail {
        void handle();
    }

    /**
     * Requests the current question from the server.
     *
     * @param gameId the id of the game
     * @param handlerSuccess the handler for when the request succeeds
     * @param handlerFail the handler for when the request fails
     */
    public static void getCurrentQuestion(UUID gameId,
                                   GetQuestionHandlerSuccess handlerSuccess, GetQuestionHandlerFail handlerFail) {
        // Built the query invocation
        Invocation invocation =
            ServerUtils.getRequestTarget()
                .path("/api/lobby/" + gameId + "/join")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildGet();

        // Perform the query asynchronously
        invocation.submit(new InvocationCallback<QuestionDTO>() {

            @Override
            public void completed(QuestionDTO o) {
                System.out.println(o);
                ServerUtils.lobbyId = o.getId();
                handlerSuccess.handle(o);
            }

            @Override
            public void failed(Throwable throwable) {
                handlerFail.handle();
                throwable.printStackTrace();
            }
        });
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
     * Sends the answer to the current question to the server.
     *
     * @param gameId the id of the game
     * @param answer the answer to the current question
     * @param handlerSuccess the handler for when the request succeeds
     * @param handlerFailure the handler for when the request fails
     */
    public static void putAnswer(UUID gameId, AnswerDTO answer,
                                 PutAnswerHandlerSuccess handlerSuccess, PutAnswerHandlerFail handlerFailure) {
        // Built the query invocation
        Invocation invocation =
                ServerUtils.getRequestTarget()
                        .path("/api/lobby/" + gameId + "/answer")
                        .request(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .buildPut(Entity.entity(answer, APPLICATION_JSON));

        // Perform the query asynchronously
        invocation.submit(new InvocationCallback<Response>() {

            @Override
            public void completed(Response response) {
                System.out.println("Answer sent successfully");
                handlerSuccess.handle();
            }

            @Override
            public void failed(Throwable throwable) {
                System.out.println("Answer sent failed");
                handlerFailure.handle();
                throwable.printStackTrace();
            }
        });
    }

}
