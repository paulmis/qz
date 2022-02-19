package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Activity data structure - describes a single activity and its energetic cost.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
public abstract class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

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
