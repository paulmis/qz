package server.database.entities.question;

import commons.entities.ActivityDTO;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.modelmapper.ModelMapper;
import server.database.entities.utils.BaseEntity;

/**
 * Activity data structure - describes a single activity and its energetic cost.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
public class Activity extends BaseEntity<ActivityDTO> {

    /**
     * Construct a new entity from a DTO.
     *
     * @param dto DTO to map to entity.
     */
    public Activity(ActivityDTO dto) {
        new ModelMapper().map(dto, this);
    }

    /**
     * Description of the activity.
     */
    private String description;

    /**
     * The energy cost in Wh of the activity.
     */
    private int cost;

    /**
     * The filepath to the icon of the activity.
     */
    private String icon;

    /**
     * Source of the information in the activity.
     */
    @URL
    private String source;

    @Override
    public ActivityDTO getDTO() {
        return new ModelMapper().map(this, ActivityDTO.class);
    }
}
