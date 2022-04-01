package client.scenes.admin;

import client.utils.communication.ServerUtils;
import commons.entities.ActivityDTO;
import java.util.UUID;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Data;

/**
 * A helper class for wrapping an image
 * and displaying everything inside a compact way
 * that avoids redownloading images.
 */
@Data
public class ActivityView {

    /**
     * The ID of the activity.
     */
    protected UUID id;

    /**
     * Name of the activity.
     */
    protected String description;

    /**
     * Energy consumption of the activity.
     */
    protected Long cost;

    /**
     * URL of the picture of the activity.
     */
    protected String icon;

    /**
     * Source of the information in the activity.
     */
    protected String source;

    /**
     * The actual image of the user.
     */
    private Image image;

    /**
     * Creates an activity view from an activity DTO.
     * This also initialize the image to a recovered image from the server.
     *
     * @param activityDTO the activity dto.
     */
    public ActivityView(ActivityDTO activityDTO) {
        this.id = activityDTO.getId();
        this.cost = activityDTO.getCost();
        this.description = activityDTO.getDescription();
        this.source = activityDTO.getSource();
        this.icon = activityDTO.getIcon();
        if (this.icon != null) {
            this.image = new Image(ServerUtils.getImagePathFromId(this.icon), true);
        } else {
            this.image = null;
        }
    }

    /**
     * Converts the activityView back to dto.
     *
     * @return the new dto.
     */
    public ActivityDTO toDTO() {
        var activityDTO = new ActivityDTO();
        activityDTO.setSource(this.source);
        activityDTO.setIcon(this.icon);
        activityDTO.setDescription(this.description);
        activityDTO.setCost(this.cost);
        activityDTO.setId(this.id);
        return activityDTO;
    }
}
