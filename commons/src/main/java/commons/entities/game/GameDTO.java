package commons.entities.game;

import commons.entities.game.configuration.GameConfigurationDTO;
import commons.entities.utils.DTO;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO for the game entity.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
@ToString(callSuper = true)
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
    protected int currentQuestion;

    /**
     * The players in the game.
     */
    protected Set<GamePlayerDTO> players;

    /**
     * The head of the lobby - person in charge with special privileges.
     */
    protected UUID head;

    /**
     * Copy constructor.
     *
     * @param gameDTO the game DTO to copy
     */
    public GameDTO(GameDTO gameDTO) {
        this.id = gameDTO.getId();
        this.gameId = gameDTO.getGameId();
        this.createDate = gameDTO.getCreateDate();
        this.gameType = gameDTO.getGameType();
        this.configuration = gameDTO.getConfiguration();
        this.status = gameDTO.getStatus();
        this.currentQuestion = gameDTO.getCurrentQuestion();
        this.players = gameDTO.getPlayers();
        this.head = gameDTO.getHead();
    }
}
