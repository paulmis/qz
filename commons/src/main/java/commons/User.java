package commons;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;


/**
 * User entity - describes an user in the context of the entire application.
 */

@NoArgsConstructor
@AllArgsConstructor (access = AccessLevel.PUBLIC)
@ToString (includeFieldNames = false)
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
    @NonNull private String password;

    /**
     * score - integer representing a player's total score.
     */
    private int score;

    /**
     * id - random unique uuid assigned to a certain player.
     */
    private int gamesPlayed;
}
