package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Activity data structure - describes a single activity and its energetic cost.
 */
@Entity
public abstract class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @ManyToMany(mappedBy = "activities")
    List<Question> usedIn;

    public String description;
    public int cost;
    public String icon;

    @SuppressWarnings("unused")
    private Activity() {
        // for object mapper
    }

    /**
     * Constructor for the Activity class.
     *
     * @param description a string describing the activity.
     * @param cost        the energy cost in Wh of the activity.
     * @param icon        the filepath to the icon of the activity.
     */
    public Activity(String description, int cost, String icon) {
        this.description = description;
        this.cost = cost;
        this.icon = icon;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
