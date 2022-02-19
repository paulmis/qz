package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Activity data structure - describes a single activity and its energetic cost.
 */
@Data
@NoArgsConstructor
@Entity
public abstract class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToMany(mappedBy = "activities")
    List<Question> usedIn;

    /**
     * A string describing the activity.
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
