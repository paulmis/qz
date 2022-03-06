package commons.entities;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.utils.DTO;
import commons.entities.utils.Views;
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
    @JsonView(Views.Public.class)
    protected UUID id;

    /**
     * User's name.
     */
    @NonNull
    @JsonView(Views.Public.class)
    protected String username;

    /**
     * User's email address.
     */
    @Email
    @NonNull
    @JsonView(Views.Private.class)
    protected String email;

    /**
     * User's password.
     */
    @Size(min = 8)
    @NonNull
    @JsonView(Views.Internal.class)
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
