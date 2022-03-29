package client.scenes.admin;

import client.utils.communication.ServerUtils;
import commons.entities.ActivityDTO;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Data;

import java.util.UUID;

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
    private ImageView image;

    public ActivityView(ActivityDTO activityDTO) {
        this.id = activityDTO.getId();
        this.cost = activityDTO.getCost();
        this.description = activityDTO.getDescription();
        this.source = activityDTO.getSource();
        this.icon = activityDTO.getIcon();
        this.image = new ImageView(new Image(ServerUtils.getImagePathFromId(this.icon)));
        this.image.setFitHeight(50);
        this.image.setFitWidth(50);
    }

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
