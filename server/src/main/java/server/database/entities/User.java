package server.database.entities;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import server.database.entities.game.GamePlayer;


/**
 * User entity - describes an user in the context of the entire application.
 */

@NoArgsConstructor
@AllArgsConstructor (access = AccessLevel.PUBLIC)
@ToString (includeFieldNames = false)
@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class User {
    /**
     * id - random unique uuid assigned to a certain player.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * email - string used for authentication purposes representing the email of the user.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * password - string representing user's salted password.
     */
    @NonNull private String password;

    /**
     * score - integer representing a player's total score.
     */
    private int score;

    /**
     * id - random unique uuid assigned to a certain player.
     */
    private int gamesPlayed;

    /**
     * Relation to player entities for each individual game.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<GamePlayer> gamePlayers = new LinkedHashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
