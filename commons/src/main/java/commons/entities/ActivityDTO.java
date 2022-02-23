package commons.entities;

import java.io.Serializable;
import java.util.UUID;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for activity entities.
 */
@Data
@NoArgsConstructor
@Generated
public class ActivityDTO implements Serializable {
    /**
     * UUID of the activity.
     */
    private UUID id;

    /**
     * Name of the activity.
     */
    private String description;

    /**
     * Energy consumption of the activity.
     */
    private int cost;

    /**
     * URL of the picture of the activity.
     */
    private String icon;
}
