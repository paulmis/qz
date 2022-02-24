package commons.entities.game;

import commons.entities.game.configuration.GameConfigurationDTO;
import commons.entities.utils.DTO;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

/**
 * DTO for the game entity.
 */
@Data
@NoArgsConstructor
@Generated
public class GameDTO implements DTO {
    /**
     * The unique identifier of the game.
     */
    protected UUID id;

    /**
     * The user-friendly identifier of the game.
     */
    protected String gameId;

    /**
     * The creation date of the game.
     */
    protected Date createDate;

    /**
     * The type of the game.
     */
    protected GameType gameType;

    /**
     * The configuration of the game.
     */
    protected GameConfigurationDTO configuration;

    /**
     * The status of the game.
     */
    protected GameStatus status;

    /**
     * The current question of the game.
     */
    protected Integer currentQuestion;

    /**
     * The players in the game.
     */
    protected Set<GamePlayerDTO> players;
}
