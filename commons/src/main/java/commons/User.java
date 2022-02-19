package commons;

import java.util.UUID;
import javax.persistence.Column;
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
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@ToString(includeFieldNames = false)
@Data public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String email;
    @NonNull private String password;
    private int score;
    private int gamesPlayed;
}
