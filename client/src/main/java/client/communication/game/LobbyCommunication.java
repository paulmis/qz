package client.communication.game;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import client.utils.communication.ServerUtils;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;

/**
 * Provides functions retrieving lobby data from the server.
 */
public class LobbyCommunication {

    /**
     * Handler for when starting the game succeeds.
     */
    public interface StartGameHandlerSuccess {
        void handle(Response response);
    }

    /**
     * Handler for when starting the game fails.
     */
    public interface StartGameHandlerFail {
        void handle();
    }

    /**
     * Requests the current question from the server.
     *
     * @param gameId the id of the game
     * @param handlerSuccess the handler for when the request succeeds
     * @param handlerFail the handler for when the request fails
     */
    public static void startGame(UUID gameId,
                                 StartGameHandlerSuccess handlerSuccess, StartGameHandlerFail handlerFail) {
        // Build the query invocation
        Invocation invocation =
            ServerUtils.getRequestTarget()
                .path("/api/lobby/" + gameId + "/start")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildPut(Entity.entity("", APPLICATION_JSON));

        // Perform the query asynchronously
        invocation.submit(new InvocationCallback<Response>() {

            @Override
            public void completed(Response response) {
                handlerSuccess.handle(response);
            }

            @Override
            public void failed(Throwable throwable) {
                System.out.println("Starting game failed");
                handlerFail.handle();
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Handler for when the leave lobby succeeds.
     */
    public interface LeaveGameHandler {
        void handle(Response response);
    }

    /**
     * Function that causes the user to leave the lobby.
     */
    public void leaveLobby(LeaveGameHandler leaveGameHandler) {
        Invocation request = ServerUtils.getRequestTarget()
            .path("/api/lobby/leave")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .buildDelete();

        request.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                leaveGameHandler.handle(response);
            }

            @Override
            public void failed(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
