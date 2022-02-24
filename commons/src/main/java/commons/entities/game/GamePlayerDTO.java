package commons.entities.game;

import commons.entities.utils.DTO;
import java.util.UUID;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

/**
 * DTO for game player.
 */
@Data
@NoArgsConstructor
@Generated
public class GamePlayerDTO implements DTO {
    /**
     * UUID of game player.
     */
    protected UUID id;

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
}
