package client.communication.game;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import client.utils.ClientState;
import client.utils.communication.ServerUtils;
import commons.entities.game.GameDTO;
import commons.entities.game.configuration.GameConfigurationDTO;
import commons.entities.utils.ApiError;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;
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
        void handle(ApiError error);
    }

    /**
     * Function that gets all the lobby info from the provided lobby id.
     *
     * @param lobbyId The lobby id of the lobby that needs to be found.
     * @param handleSuccess The function that will be called if the request is successful.
     * @param handleFail The function that will be called if the request is unsuccessful.
     */
    public void getLobbyInfo(UUID lobbyId,
                             GetLobbyInfoHandlerSuccess handleSuccess, GetLobbyInfoHandlerFail handleFail) {
        // Build the query invocation
        Invocation request = ServerUtils.getRequestTarget()
                .path("/api/lobby/" + lobbyId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildGet();

        // Perform the query asynchronously
        request.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                if (response.getStatus() == 200) {
                    handleSuccess.handle(response.readEntity(GameDTO.class));
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
     * Handler for when the configuration is saved successfully.
     */
    public interface SaveConfigSuccessHandler {
        void handle();
    }

    /**
     * Handler for when the configuration couldn't be saved.
     */
    public interface SaveConfigurationFailHandler {
        void handle();
    }

    /**
     * Requests to update the game configuration.
     *
     * @param gameId the id of the game
     * @param config the new configuration
     * @param handleSuccess  the handler for when the request succeeds
     * @param handleFail the handler for when the request fails
     */
    public void saveConfig(UUID gameId, GameConfigurationDTO config,
                           SaveConfigSuccessHandler handleSuccess, SaveConfigurationFailHandler handleFail) {
        Invocation request = ServerUtils.getRequestTarget()
                .path("/api/lobby/" + gameId + "/config")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildPost(Entity.entity(config, APPLICATION_JSON));

        request.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                if (response.getStatus() == 200) {
                    ClientState.game.setConfiguration(config);
                    handleSuccess.handle();
                } else {
                    handleFail.handle();
                }
            }

            @Override
            public void failed(Throwable throwable) {
                throwable.printStackTrace();
                handleFail.handle();
            }
        });
    }

    /**
     * Handler for when kicking the player succeeds.
     */
    public interface KickPlayerHandlerSuccess {
        void handle();
    }

    /**
     * Handler for when kicking the player fails.
     */
    public interface KickPlayerHandlerFail {
        void handle(ApiError error);
    }


    /**
     * Function that handles kicking a player from a lobby.
     *
     * @param playerId the player id of the user to be kicked.
     * @param handleSuccess the success handler
     * @param handleFail the fail handler
     */
    public void kickPlayer(UUID playerId,
                           KickPlayerHandlerSuccess handleSuccess,
                           KickPlayerHandlerFail handleFail) {
        // Build the query invocation
        Invocation request = ServerUtils.getRequestTarget()
                .path("/api/lobby/kick/" + playerId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildDelete();

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
}
