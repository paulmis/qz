package server.database.entities.question;

import static server.utils.TestHelpers.getUUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import commons.entities.ActivityDTO;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.modelmapper.ModelMapper;
import server.database.entities.utils.BaseEntity;

/**
 * Activity data structure - describes a single activity and its energetic cost.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
public class Activity extends BaseEntity<ActivityDTO> {

    /**
     * Construct a new entity from a DTO.
     *
     * @param dto DTO to map to entity.
     */
    public Activity(ActivityDTO dto) {
        new ModelMapper().map(dto, this);
        if (dto.getId() == null) {
            // Avoid instances without an id set
            this.id = getUUID(0);
        }
    }

    /**
     * Description of the activity.
     */
    @Column(nullable = false)
    @NotBlank
    @NonNull
    private String description;

    /**
     * The energy cost in Wh of the activity.
     */
    @Column(nullable = false)
    @PositiveOrZero
    private long cost;

    /**
     * The filepath to the icon of the activity.
     */
    private String icon;

    /**
     * Source of the information in the activity.
     * Any sane URL will be at most 2048 characters long, hence this length limit.
     */
    @Column(length = 2048)
    @URL
    private String source;

    /**
     * Marks the activity as abandoned (i.e. it will not be used to generate new questions).
     */
    @Column(nullable = false)
    @JsonIgnore
    private boolean abandoned = false;

    @Override
    public ActivityDTO getDTO() {
        return new ModelMapper().map(this, ActivityDTO.class);
    }
}
