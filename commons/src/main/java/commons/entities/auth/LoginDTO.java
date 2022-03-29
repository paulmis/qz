package commons.entities.auth;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.game.GameDTO;
import commons.entities.utils.Views;
import lombok.*;

/**
 * DTO for the login response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    /**
     * The user's id.
     */
    @JsonView(Views.Public.class)
    @NonNull
    String token;

    /**
     * The current game.
     */
    @JsonView(Views.Public.class)
    GameDTO game;

    /**
     * User data.
     */
    @JsonView(Views.Public.class)
    UserDTO user;
}
