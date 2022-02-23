package commons.entities;

import java.io.Serializable;
import java.util.UUID;
import lombok.Data;

/**
 * Data transfer object for game players.
 */
@Data
public class GamePlayerDto implements Serializable {
    /**
     * The player's unique identifier (in-game).
     */
    private final UUID id;

    /**
     * Current score of the player.
     */
    private final Integer score;

    /**
     * Player's answer streak.
     */
    private final Integer streak;

    /**
     * Player's in-game nickname.
     */
    private final String nickname;
}
