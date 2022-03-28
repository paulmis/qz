package client.communication.game;

import client.utils.ClientState;
import client.utils.communication.ServerUtils;
import commons.entities.auth.UserDTO;
import commons.entities.game.GameDTO;
import commons.entities.game.NormalGameDTO;
import commons.entities.game.configuration.GameConfigurationDTO;
import commons.entities.game.configuration.NormalGameConfigurationDTO;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import java.util.List;
import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class LobbyListCommunication {
    /**
     * Handler for when the create lobby succeeds.
     */
    public interface CreateLobbyHandlerSuccess {
        void handle(GameDTO game);
    }

    /**
     * Handler for when the create lobby fails.
     */
    public interface CreateLobbyHandlerFail {
        void handle();
    }

    /**
     * This function makes a call to create a new lobby.
     *
     * @param createLobbyHandlerSuccess The function that will be called if the request is successful.
     * @param createLobbyHandlerFail The function that will be called if the request is unsuccessful.
     */
    public void createLobby(GameConfigurationDTO config, CreateLobbyHandlerSuccess createLobbyHandlerSuccess,
                            CreateLobbyHandlerFail createLobbyHandlerFail) {
        var game = new NormalGameDTO();
        game.setId(UUID.randomUUID());
        game.setConfiguration(config);

        Invocation invocation = ServerUtils.getRequestTarget()
                .path("/api/lobby")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildPost(Entity.entity(game, APPLICATION_JSON));

        invocation.submit(new InvocationCallback<GameDTO>() {

            @Override
            public void completed(GameDTO game) {
                System.out.println(game);
                ClientState.game = game;
                ServerUtils.subscribeToSSE(ServerUtils.sseHandler);
                createLobbyHandlerSuccess.handle(game);
            }

            @Override
            public void failed(Throwable throwable) {
                createLobbyHandlerFail.handle();
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Handler for when getting all lobbies succeeds.
     */
    public interface GetLobbiesHandlerSuccess {
        void handle(List<GameDTO> games);
    }

    /**
     * Handler for when getting all lobbies fails.
     */
    public interface GetLobbiesHandlerFail {
        void handle();
    }

    /**
     * Function that gets all the lobbies in the database.
     *
     * @param getLobbiesHandlerSuccess The function that will be called if the request is successful.
     * @param getLobbiesHandlerFail The function that will be called if the request is unsuccessful.
     */
    public void getLobbies(GetLobbiesHandlerSuccess getLobbiesHandlerSuccess,
                           GetLobbiesHandlerFail getLobbiesHandlerFail) {
        Invocation invocation = ServerUtils.getRequestTarget()
                .path("/api/lobby/available")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildGet();

        invocation.submit(new InvocationCallback<List<GameDTO>>() {

            @Override
            public void completed(List<GameDTO> o) {
                getLobbiesHandlerSuccess.handle(o);
            }

            @Override
            public void failed(Throwable throwable) {
                getLobbiesHandlerFail.handle();
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Handler for when joining a lobby succeeds.
     */
    public interface JoinLobbyHandlerSuccess {
        void handle(GameDTO gameDTO);
    }

    /**
     * Handler if the joining of the lobby fails.
     */
    public interface JoinLobbyHandlerFail {
        void handle();
    }

    /**
     * This function handles a user joining a lobby.
     *
     * @param lobbyId The id of the lobby that the user wants to join.
     * @param joinLobbyHandlerSuccess The function that will be called if the request is successful.
     * @param joinLobbyHandlerFail The function that will be called if the request is unsuccessful.
     */
    public void joinLobby(UUID lobbyId, JoinLobbyHandlerSuccess joinLobbyHandlerSuccess,
                          JoinLobbyHandlerFail joinLobbyHandlerFail) {
        Invocation invocation = ServerUtils.getRequestTarget()
                .path("/api/lobby/" + lobbyId.toString() + "/join")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildPut(Entity.entity("", APPLICATION_JSON));

        invocation.submit(new InvocationCallback<GameDTO>() {

            @Override
            public void completed(GameDTO game) {
                System.out.println(game);
                ClientState.game = game;
                ServerUtils.subscribeToSSE(ServerUtils.sseHandler);
                joinLobbyHandlerSuccess.handle(game);
            }

            @Override
            public void failed(Throwable throwable) {
                joinLobbyHandlerFail.handle();
                throwable.printStackTrace();
            }
        });
    }

    /**
     * Handler for when getting the logged in user succeeds.
     */
    public interface GetUserInfoHandlerSuccess {
        void handle(UserDTO userDTO);
    }

    /**
     * Handler for when getting the logged in user fails.
     */
    public interface GetUserInfoHandlerFail {
        void handle();
    }

    /**
     * Function that gets all the info about the currently logged in player.
     *
     * @param getUserInfoHandlerSuccess The function that will be called if the request is successful.
     * @param getUserInfoHandlerFail The function that will be called if the request is unsuccessful.
     */
    public void getMyInfo(GetUserInfoHandlerSuccess getUserInfoHandlerSuccess,
                          GetUserInfoHandlerFail getUserInfoHandlerFail) {
        Invocation invocation = ServerUtils.getRequestTarget()
                .path("/api/user")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildGet();

        invocation.submit(new InvocationCallback<UserDTO>() {

            @Override
            public void completed(UserDTO o) {
                getUserInfoHandlerSuccess.handle(o);
            }

            @Override
            public void failed(Throwable throwable) {
                getUserInfoHandlerFail.handle();
                throwable.printStackTrace();
            }
        });
    }
}
