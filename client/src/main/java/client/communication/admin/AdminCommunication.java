package client.communication.admin;

import client.utils.communication.ServerUtils;
import commons.entities.ActivityDTO;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Slf4j
public class AdminCommunication {


    public interface GetAllActivitiesHandlerSuccess {
        void handle(List<ActivityDTO> activities);
    }

    public interface GetAllActivitiesHandlerFail {
        void handle();
    }

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

    public interface UpdateActivityHandlerSuccess {
        void handle(Response response);
    }

    public interface UpdateActivityHandlerFail {
        void handle();
    }

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

    public interface DeleteActivityHandlerSuccess {
        void handle(Response response);
    }

    public interface DeleteActivityHandlerFail {
        void handle();
    }

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
