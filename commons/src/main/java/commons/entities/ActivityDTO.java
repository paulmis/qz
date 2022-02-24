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
    protected UUID id;

    /**
     * Name of the activity.
     */
    protected String description;

    /**
     * Energy consumption of the activity.
     */
    protected int cost;

    /**
     * URL of the picture of the activity.
     */
    protected String icon;
}
