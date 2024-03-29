package commons.entities.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.game.configuration.GameConfigurationDTO;
import commons.entities.questions.QuestionDTO;
import commons.entities.utils.DTO;
import commons.entities.utils.Views;
import java.time.LocalDateTime;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
    @JsonSubTypes.Type(value = DefiniteGameDTO.class, name = "DefiniteGameDTO")
})
@JsonView(Views.Public.class)
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
     * The name of the game given by the user.
     */
    protected String gameName;

    /**
     * If the lobby is private or not.
     */
    protected Boolean isPrivate;

    /**
     * The creation date of the game.
     */
    protected LocalDateTime createDate;

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
     * The number of the current question.
     */
    protected Integer currentQuestionNumber;

    /**
     * The current question.
     */
    protected QuestionDTO currentQuestion;

    /**
     * The players in the game.
     */
    protected Set<GamePlayerDTO> players;

    /**
     * The head of the lobby - person in charge with special privileges.
     */
    protected UUID host;

    /**
     * Copy constructor.
     *
     * @param gameDTO the game DTO to copy
     */
    public GameDTO(GameDTO gameDTO) {
        this.id = gameDTO.getId();
        this.gameId = gameDTO.getGameId();
        this.gameName = gameDTO.getGameName();
        this.isPrivate = gameDTO.getIsPrivate();
        this.createDate = gameDTO.getCreateDate();
        this.gameType = gameDTO.getGameType();
        this.configuration = gameDTO.getConfiguration();
        this.status = gameDTO.getStatus();
        this.currentQuestionNumber = gameDTO.getCurrentQuestionNumber();
        this.currentQuestion = gameDTO.getCurrentQuestion();
        this.players = gameDTO.getPlayers();
        this.host = gameDTO.getHost();
    }



    /**
     * Checks if the game is singleplayer or multiplayer.
     *
     * @return whether the game is singleplayer or multiplayer
     */
    public boolean isSingleplayer() {
        return this.configuration.getCapacity() == 1;
    }
}
