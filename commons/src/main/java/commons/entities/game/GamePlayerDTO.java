package commons.entities.game;

import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.utils.DTO;
import commons.entities.utils.Views;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for game player.
 */
@Data
@NoArgsConstructor
@JsonView(Views.Public.class)
public class GamePlayerDTO implements DTO {
    /**
     * UUID of game player.
     */
    protected UUID id;

    /**
     * Associated user.
     */
    protected UUID userId;

    /**
     * Current score of the player.
     */
    protected Integer score;

    /**
     * Streak of the player.
     */
    protected Integer streak;

    /**
     * Nickname of the player.
     */
    protected String nickname;

    /**
     * ID of the user's profile image.
     */
    protected UUID profilePic;

    /**
     * The date the player joined the lobby.
     */
    protected LocalDateTime joinDate;
}
