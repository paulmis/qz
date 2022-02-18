package commons;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

/**
 * Activity data structure - describes a single activity and its energetic cost.
 */
@Entity
public abstract class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @ManyToMany(mappedBy = "ActivitiesAsked")
    List<Question> UsedIn;

    public String description;
    public int cost;
    public String icon;

    @SuppressWarnings("unused")
    private Activity() {
        // for object mapper
    }

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
