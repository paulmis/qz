package commons.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import commons.entities.utils.DTO;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for activity entities.
 */
@Data
@NoArgsConstructor
public class ActivityDTO implements DTO {
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
    protected long cost;

    /**
     * URL of the picture of the activity.
     */
    protected String icon;

    /**
     * Source of the information in the activity.
     */
    protected String source;

    /**
     * Returns a string with the cost and the highest possible unit.
     * e.g. 12350 Wh -> 12 kWh
     *      19878000 Wh -> 20 MWh
     *
     * @return cost with the unit
     */
    @JsonIgnore
    public String getCostWithHighestUnit() {
        // Calculate n
        int log = 0;
        long mult = 1;
        while (mult * 1000L < this.cost) {
            mult *= 1000;
            log++;
        }
        return this.cost / mult + " " + new String[]{"", "k", "M", "G", "T", "P", "E", "Z", "Y"}[log] + "Wh";
    }
}
