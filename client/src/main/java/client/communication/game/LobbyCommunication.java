package client.communication.game;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import client.utils.communication.ServerUtils;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;

import commons.entities.game.GameDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides functions retrieving lobby data from the server.
 */
@Slf4j
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
                log.error("Starting game failed");
                handlerFail.handle();
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Handler for when the leave lobby succeeds.
     */
    public interface LeaveGameHandlerSuccess {
        void handle(Response response);
    }

    /**
     * Handler for when the leave lobby fails.
     */
    public interface LeaveGameHandlerFail {
        void handle();
    }

    /**
     * Function that causes the user to leave the lobby.
     *
     * @param handleSuccess handler for when request succeeds
     * @param handleFail handler for when request fails
     */
    public void leaveLobby(LeaveGameHandlerSuccess handleSuccess,
                           LeaveGameHandlerFail handleFail) {
        // Build the query invocation
        Invocation request = ServerUtils.getRequestTarget()
            .path("/api/lobby/leave")
            .request(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .buildDelete();
        // Perform the query asynchronously
        request.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                handleSuccess.handle(response);
            }

            @Override
            public void failed(Throwable throwable) {
                log.error("");
                handleFail.handle();
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Handler for when a disband lobby succeeds.
     */
    public interface DisbandLobbyHandlerSuccess {
        void handle(Response response);
    }

    /**
     * Handler for when a disband lobby fails.
     */
    public interface DisbandLobbyHandlerFail {
        void handle();
    }

    /**
     * Function that causes the host to delete the lobby.
     *
     * @param handleSuccess handler for when request succeeds
     * @param handleFail handler for when request fails
     */
    public void disbandLobby(DisbandLobbyHandlerSuccess handleSuccess,
                             DisbandLobbyHandlerFail handleFail) {
        // Build the query invocation
        Invocation request = ServerUtils.getRequestTarget()
                .path("/api/lobby/delete")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildDelete();
        // Perform the query asynchronously
        request.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                handleSuccess.handle(response);
            }

            @Override
            public void failed(Throwable throwable) {
                log.error("Disbanding lobby failed");
                handleFail.handle();
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
     * @param handleSuccess The function that will be called if the request is successful.
     * @param handleFail The function that will be called if the request is unsuccessful.
     * @param lobbyId The lobby id of the lobby that needs to be found.
     */
    public void getLobbyInfo(GetLobbyInfoHandlerSuccess handleSuccess,
                             GetLobbyInfoHandlerFail handleFail, UUID lobbyId) {
        // Build the query invocation
        Invocation request = ServerUtils.getRequestTarget()
                .path("/api/lobby/" + lobbyId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildGet();
        // Perform the query asynchronously
        request.submit(new InvocationCallback<GameDTO>() {
            @Override
            public void completed(GameDTO o) {
                handleSuccess.handle(o);
            }

            @Override
            public void failed(Throwable throwable) {
                log.error("Couldn't retrieve lobby: " + lobbyId);
                handleFail.handle();
                throwable.printStackTrace();
            }
        });
    }
}
