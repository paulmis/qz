package commons.entities.game.configuration;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import commons.entities.game.GameDTO;
import commons.entities.utils.DTO;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import jdk.jfr.Description;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for game configuration.
 */
@Data
@NoArgsConstructor
@MappedSuperclass
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
    @JsonSubTypes.Type(value = NormalGameConfigurationDTO.class, name = "NormalGameConfigurationDTO"),
    @JsonSubTypes.Type(value = SurvivalGameConfigurationDTO.class, name = "SurvivalGameConfigurationDTO")
})
public abstract class GameConfigurationDTO implements DTO {
    /**
     * The id of the game configuration.
     */
    protected UUID id;

    /**
     * Time to answer in milliseconds.
     */
    @DecimalMin(value = "3")
    @DecimalMax(value = "60")
    @Description("Milliseconds per question")
    protected Integer answerTime;


    /**
     * Capacity of the lobby.
     */
    @DecimalMin(value = "1")
    @DecimalMax(value = "8")
    @Description("Capacity of lobby")
    protected Integer capacity;

    /**
     * The streak size required for a streak to be applied.
     */
    @DecimalMin(value = "2")
    @DecimalMax(value = "100")
    @Description("Minimum streak size")
    protected Integer streakSize;

    /**
     * The streak multiplier that is to be applied.
     */
    @DecimalMin(value = "1")
    @DecimalMax(value = "10")
    @Description("Streak multiplier")
    protected Float streakMultiplier;

    /**
     * The number of points per correct answer.
     */
    @DecimalMin(value = "0")
    @DecimalMax(value = "1000")
    @Description("Points for correct answer")
    protected Integer pointsCorrect;

    /**
     * The number of points per incorrect answer.
     */
    @DecimalMin(value = "-1000")
    @DecimalMax(value = "1000")
    @Description("Points for wrong answer")
    protected Integer pointsWrong;

    /**
     * The threshold for an answer to be considered correct.
     */
    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Description("Correct answer threshold")
    protected Integer correctAnswerThreshold;
}
