package server.database.entities.question;

import commons.entities.ActivityDTO;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
        ModelMapper mapper = new ModelMapper();
        mapper.map(dto, this);
    }

    /**
     * List of questions in which the activity is used.
     * Needed for the many-to-many relation.
     */
    @ManyToMany(mappedBy = "activities")
    public List<Question> usedIn;

    /**
     * Description of the activity.
     */
    public String description;

    /**
     * The energy cost in Wh of the activity.
     */
    public int cost;

    /**
     * The filepath to the icon of the activity.
     */
    public String icon;


}
