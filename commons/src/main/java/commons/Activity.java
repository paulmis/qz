package commons;

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
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * List of questions in which the activity is used.
     * Needed for the many-to-many relation.
     */
    @ManyToMany(mappedBy = "activities")
    List<Question> usedIn;

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
