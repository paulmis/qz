package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import java.util.UUID;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class User {
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Id
    private String email;
    private String password;
    private int score;
    private int gamesPlayed;


    @SuppressWarnings("unused")
    private User() {
        // for object mapper
    }

    public User(UUID id, String email, String password, int score, int gamesPlayed) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.score = score;
        this.gamesPlayed = gamesPlayed;
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
