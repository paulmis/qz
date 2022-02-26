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
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import server.database.entities.game.GamePlayer;


/**
 * User entity - describes an user in the context of the entire application.
 */
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PUBLIC)
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
     * User's nickname.
     */
    @Column(nullable = false, unique = true)
    @NonNull private String username;

    /**
     * email - string used for authentication purposes representing the email of the user.
     */
    @Column(nullable = false, unique = true)
    @NonNull private String email;

    /**
     * password - string representing user's salted password.
     */
    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    @NonNull private String password;

    /**
     * score - integer representing a player's total score.
     */
    @Column
    private int score = 0;

    /**
     * id - random unique uuid assigned to a certain player.
     */
    @Column
    private int gamesPlayed = 0;

    /**
     * Relation to player entities for each individual game.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GamePlayer> gamePlayers = Collections.synchronizedSet(new HashSet<>());
}
