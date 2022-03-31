package client.communication.user;


import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import client.utils.ClientState;
import client.utils.communication.ServerUtils;
import commons.entities.auth.UserDTO;
import commons.entities.utils.ApiError;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;


/**
 * User communication. Handles getting user info and changing username.
 */
@Slf4j
public class UserCommunication {

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
        void handle(ApiError error);
    }

    /**
     * Function that gets all the info about the currently logged in user.
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

        invocation.submit(new InvocationCallback<Response>() {

            @Override
            public void completed(Response response) {
                if (response.getStatus() == 200) {
                    ClientState.user = response.readEntity(UserDTO.class);
                    getUserInfoHandlerSuccess.handle(ClientState.user);
                } else {
                    ApiError error = response.readEntity(ApiError.class);
                    getUserInfoHandlerFail.handle(error);
                }
            }

            @Override
            public void failed(Throwable throwable) {
                throwable.printStackTrace();
                getUserInfoHandlerFail.handle(null);
            }
        });
    }

    /**
     * Handler for when changing the username succeeds.
     */
    public interface ChangeUsernameHandlerSuccess {
        void handle(UserDTO userDTO);
    }

    /**
     * Handler for when changing the username fails.
     */
    public interface ChangeUsernameHandlerFail {
        void handle(ApiError error);
    }

    /**
     * Function that changes the username of the currently logged in user.
     *
     * @param changeUsernameHandlerSuccess The function that will be called if the request is successful.
     * @param changeUsernameHandlerFail The function that will be called if the request is unsuccessful.
     */
    public void changeUsername(String newUsername, ChangeUsernameHandlerSuccess changeUsernameHandlerSuccess,
                          ChangeUsernameHandlerFail changeUsernameHandlerFail) {
        Invocation invocation = ServerUtils.getRequestTarget()
                .path("/api/user/username")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildPost(Entity.entity(newUsername, APPLICATION_JSON));

        invocation.submit(new InvocationCallback<Response>() {

            @Override
            public void completed(Response response) {
                if (response.getStatus() == 200) {
                    ClientState.user = response.readEntity(UserDTO.class);
                    changeUsernameHandlerSuccess.handle(ClientState.user);
                } else {
                    ApiError error = response.readEntity(ApiError.class);
                    changeUsernameHandlerFail.handle(error);
                }
            }

            @Override
            public void failed(Throwable throwable) {
                throwable.printStackTrace();
                changeUsernameHandlerFail.handle(null);
            }
        });
    }
}
