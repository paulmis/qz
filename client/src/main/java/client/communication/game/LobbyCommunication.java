package client.communication.game;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import client.utils.ClientState;
import client.utils.communication.ServerUtils;
import commons.entities.game.configuration.GameConfigurationDTO;
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
}
