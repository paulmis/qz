package commons.entities.game.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.utils.Views;
import java.time.Duration;
import java.util.UUID;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import jdk.jfr.Description;
import lombok.*;

/**
 * Data transfer object for the standard-mode game configuration.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonView(Views.Public.class)
public class NormalGameConfigurationDTO extends GameConfigurationDTO {
    /**
     * Number of questions in the game.
     */
    @DecimalMin(value = "10")
    @DecimalMax(value = "100")
    @Description("Number of questions")
    protected Integer numQuestions = 20;

    /**
     * Normal game config constructor.
     *
     * @param id the id of the config.
     * @param answerTime the answer time per question.
     * @param capacity the capacity of the lobby.
     * @param numQuestions the number of questions in the game.
     * @param streakSize the minimum number of correct answers for a streak to start.
     * @param streakMultiplier the streak multiplier that will be applied on a streak.
     * @param pointsCorrect the number of points per correct answer.
     * @param pointsWrong the number of points per incorrect answer.
     * @param correctAnswerThreshold the correct answer threshold.
     */
    public NormalGameConfigurationDTO(UUID id, Duration answerTime, Integer capacity, Integer numQuestions,
                                      Integer streakSize,
                                      Float streakMultiplier,
                                      Integer pointsCorrect,
                                      Integer pointsWrong,
                                      Integer correctAnswerThreshold) {
        this.id = id;
        this.answerTime = answerTime;
        this.capacity = capacity;
        this.numQuestions = numQuestions;
        this.streakSize = streakSize;
        this.streakMultiplier = streakMultiplier;
        this.pointsCorrect = pointsCorrect;
        this.pointsWrong = pointsWrong;
        this.correctAnswerThreshold = correctAnswerThreshold;
    }
}
