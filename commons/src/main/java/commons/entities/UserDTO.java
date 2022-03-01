package commons.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import commons.entities.utils.DTO;
import java.util.UUID;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Data transfer object for the user entity.
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class UserDTO implements DTO {

    /**
     * The user's ID.
     */
    protected UUID id;

    /**
     * User's name.
     */
    @NonNull
    protected String username;

    /**
     * User's email address.
     */
    @Email
    @NonNull
    protected String email;

    /**
     * User's password.
     */
    @Size(min = 8)
    @NonNull
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
