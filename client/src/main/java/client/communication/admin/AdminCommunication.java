package client.communication.admin;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import client.utils.communication.ServerUtils;
import commons.entities.ActivityDTO;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;


/**
 * Handles all the communications required inside
 * the admin panel.
 */
@Slf4j
public class AdminCommunication {

    /**
     * The get all activities handler success.
     */
    public interface GetAllActivitiesHandlerSuccess {
        void handle(List<ActivityDTO> activities);
    }

    /**
     * The get all activities handler fail.
     */
    public interface GetAllActivitiesHandlerFail {
        void handle();
    }

    /**
     * Gets all activities stored on the server.
     *
     * @param handleSuccess the function to call on a successful request.
     * @param handleFail the function to call on a failed request.
     */
    public void getAllActivities(AdminCommunication.GetAllActivitiesHandlerSuccess handleSuccess,
                                 AdminCommunication.GetAllActivitiesHandlerFail handleFail) {
        // Build the query invocation
        Invocation request = ServerUtils.getRequestTarget()
                .path("/api/activity")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildGet();

        // Perform the query asynchronously
        request.submit(new InvocationCallback<List<ActivityDTO>>() {
            @Override
            public void completed(List<ActivityDTO> activityDTOS) {
                handleSuccess.handle(activityDTOS);
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
     * The UPDATE activity handler success.
     */
    public interface UpdateActivityHandlerSuccess {
        void handle(Response response);
    }

    /**
     * The UPDATE activity handler fail.
     */
    public interface UpdateActivityHandlerFail {
        void handle();
    }

    /**
     * The update activity function. Updates an activity.
     *
     * @param activityDTO the new version of the activity.
     * @param handleSuccess the handler for successful update.
     * @param handleFail the handler for a failed update.
     */
    public void updateActivity(ActivityDTO activityDTO, AdminCommunication.UpdateActivityHandlerSuccess handleSuccess,
                               AdminCommunication.UpdateActivityHandlerFail handleFail) {
        // Build the query invocation
        Invocation request = ServerUtils.getRequestTarget()
                .path("/api/activity/save")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildPost(Entity.entity(activityDTO, APPLICATION_JSON));

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
     * The DELETE activity handler success.
     */
    public interface DeleteActivityHandlerSuccess {
        void handle(Response response);
    }

    /**
     * The DELETE activity handler fail.
     */
    public interface DeleteActivityHandlerFail {
        void handle();
    }

    /**
     * The delete activity function. Deletes an activity.
     *
     * @param id the new version of the activity.
     * @param handleSuccess the handler for successful delete.
     * @param handleFail the handler for a failed delete.
     */
    public void deleteActivity(UUID id, AdminCommunication.DeleteActivityHandlerSuccess handleSuccess,
                             AdminCommunication.DeleteActivityHandlerFail handleFail) {
        // Build the query invocation
        Invocation request = ServerUtils.getRequestTarget()
                .path("/api/" + id.toString() + "/save")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildPost(Entity.json(""));

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
}
