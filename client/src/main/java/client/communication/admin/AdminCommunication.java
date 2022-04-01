package client.communication.admin;

import client.utils.ClientState;
import client.utils.communication.ServerUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import commons.entities.ActivityDTO;

import java.io.*;
import java.util.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import commons.entities.auth.UserDTO;
import commons.entities.game.GameDTO;
import commons.entities.utils.ApiError;
import javafx.scene.image.Image;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.AttachmentBuilder;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import static javax.ws.rs.core.MediaType.*;


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
        void handle(ApiError error);
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
        request.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                if (response.getStatus() == 200) {
                    handleSuccess.handle(response.readEntity(new GenericType<List<ActivityDTO>>() {}));
                } else {
                    ApiError error = response.readEntity(ApiError.class);
                    handleFail.handle(error);
                }
            }

            @Override
            public void failed(Throwable throwable) {
                throwable.printStackTrace();
                handleFail.handle(null);
            }
        });
    }

    /**
     * The UPDATE activity handler success.
     */
    public interface UpdateActivityHandlerSuccess {
        void handle();
    }

    /**
     * The UPDATE activity handler fail.
     */
    public interface UpdateActivityHandlerFail {
        void handle(ApiError error);
    }

    private byte[] convertToBytes(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    /**
     * The update activity function. Updates an activity.
     *
     * @param activityDTO the new version of the activity.
     * @param handleSuccess the handler for successful update.
     * @param handleFail the handler for a failed update.
     */
    public void updateActivity(ActivityDTO activityDTO, File image, AdminCommunication.UpdateActivityHandlerSuccess handleSuccess,
                               AdminCommunication.UpdateActivityHandlerFail handleFail) {

        // The list of attachments
        List<Attachment> attachments = new ArrayList<>();

        // Add the activity dto as an attachment.
        attachments.add((new AttachmentBuilder())
                .mediaType(APPLICATION_JSON)
                .object(activityDTO)
                .contentDisposition(new ContentDisposition("form-data;name=\"activityDTO\""))
        .build());

        // If the image is not null add it to the attachments.
        if (image != null) {
            try {
                attachments.add((new AttachmentBuilder())
                        .mediaType(APPLICATION_OCTET_STREAM)
                        .object(new FileInputStream(image))
                        .contentDisposition(new ContentDisposition("form-data;name=\"image\";filename=\"image\""))
                .build());
            } catch (FileNotFoundException e) {
                log.error("Couldn't create input stream.");
                e.printStackTrace();
            }
        }

        // Create the multipart body that holds all attachments.
        var multiPartBody = new MultipartBody(attachments);

        // Build the query invocation
        Invocation request = ServerUtils.getRequestTarget()
                .path("/api/activity/save/image")
                .request(APPLICATION_JSON)
                .header("Content-Type", "multipart/form-data")
                .buildPost(Entity.entity(multiPartBody, "multipart/mixed"));

        // Perform the query asynchronously
        request.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                if (response.getStatus() == 200 || response.getStatus() == 201) {
                    handleSuccess.handle();
                } else {
                    ApiError error = response.readEntity(ApiError.class);
                    handleFail.handle(error);
                }
            }

            @Override
            public void failed(Throwable throwable) {
                throwable.printStackTrace();
                handleFail.handle(null);
            }
        });
    }

    /**
     * The DELETE activity handler success.
     */
    public interface DeleteActivityHandlerSuccess {
        void handle();
    }

    /**
     * The DELETE activity handler fail.
     */
    public interface DeleteActivityHandlerFail {
        void handle(ApiError error);
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
                .path("/api/activity/" + id.toString() + "/delete")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .buildPost(Entity.json(""));

        // Perform the query asynchronously
        request.submit(new InvocationCallback<Response>() {
            @Override
            public void completed(Response response) {
                if (response.getStatus() == 200) {
                    handleSuccess.handle();
                } else {
                    ApiError error = response.readEntity(ApiError.class);
                    handleFail.handle(error);
                }
            }

            @Override
            public void failed(Throwable throwable) {
                throwable.printStackTrace();
                handleFail.handle(null);
            }
        });
    }
}
