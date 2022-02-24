package commons.entities.game;

import commons.entities.utils.DTO;
import java.util.UUID;
import lombok.Data;

/**
 * DTO for game player.
 */
@Data
public class GamePlayerDTO implements DTO {
    /**
     * UUID of game player.
     */
    private final UUID id;

    /**
     * Current score of the player.
     */
    private final Integer score;

    /**
     * Streak of the player.
     */
    private final Integer streak;

    /**
     * Nickname of the player.
     */
    private final String nickname;
}
