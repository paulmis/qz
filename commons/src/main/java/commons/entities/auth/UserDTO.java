package commons.entities.auth;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.utils.DTO;
import commons.entities.utils.Views;
import java.util.UUID;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;

/**
 * Data transfer object for the user entity.
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDTO implements DTO {

    /**
     * The user's ID.
     */
    @JsonView(Views.Public.class)
    protected UUID id;

    /**
     * User's nickname.
     */
    @JsonView(Views.Public.class)
    protected String nickname;

    /**
     * User's name.
     */
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long.")
    @NotNull(message = "Username must be set.")
    @JsonView(Views.Public.class)
    @NonNull
    protected String username;

    /**
     * User's email address.
     */
    @Email(message = "Invalid email address.")
    @Size(max = 50, message = "Email address must be less than 50 characters long.")
    @NotNull(message = "Email must be set.")
    @JsonView(Views.Private.class)
    @NonNull
    protected String email;

    /**
     * User's password.
     */
    @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters long.")
    @NotNull(message = "Password must be set.")
    @JsonView(Views.Internal.class)
    @NonNull
    protected String password;

    /**
     * User's global score.
     */
    @JsonView(Views.Public.class)
    protected int score;

    /**
     * Number of games played by the user.
     */
    @JsonView(Views.Public.class)
    protected int gamesPlayed;
}
