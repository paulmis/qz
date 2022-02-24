package commons.entities;

import java.io.Serializable;
import java.util.UUID;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for the user entity.
 */
@Data
@NoArgsConstructor
@Generated
public class UserDTO implements Serializable {

    /**
     * The user's ID.
     */
    protected UUID id;

    /**
     * User's email address.
     */
    @Email
    protected String email;

    /**
     * User's password.
     */
    @Size(min = 8)
    protected String password;

    /**
     * User's global score.
     */
    protected int score;

    /**
     * Number of games played by the user.
     */
    protected int gamesPlayed;
}
