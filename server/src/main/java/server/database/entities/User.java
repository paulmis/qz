package server.database.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.HashSet;
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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import server.database.entities.game.GamePlayer;


/**
 * User entity - describes an user in the context of the entire application.
 */
@NoArgsConstructor
@AllArgsConstructor (access = AccessLevel.PUBLIC)
@Data
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
    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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
    private Set<GamePlayer> gamePlayers = Collections.synchronizedSet(new HashSet<>());
}
