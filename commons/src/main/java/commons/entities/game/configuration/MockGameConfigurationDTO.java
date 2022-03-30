package commons.entities.game.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import commons.entities.utils.Views;
import java.time.Duration;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Data transfer object for the survival-mode game configuration.`
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonView(Views.Public.class)
public class MockGameConfigurationDTO extends GameConfigurationDTO {
    /**
     * Normal game config constructor.
     *
     * @param id the id of the config.
     * @param answerTime the answer time per question.
     * @param capacity the capacity of the lobby.
     * @param speedModifier the speed modifier of the game.
     * @param streakSize the minimum number of correct answers for a streak to start.
     * @param streakMultiplier the streak multiplier that will be applied on a streak.
     * @param pointsCorrect the number of points per correct answer.
     * @param pointsWrong the number of points per incorrect answer.
     * @param correctAnswerThreshold the correct answer threshold.
     */
    public MockGameConfigurationDTO(UUID id, Integer answerTime, Integer capacity, Float speedModifier,                                    Integer streakSize,
                                    Float streakMultiplier,
                                    Integer pointsCorrect,
                                    Integer pointsWrong,
                                    Integer correctAnswerThreshold) {
        this.id = id;
        this.answerTime = answerTime;
        this.capacity = capacity;
        this.streakSize = streakSize;
        this.streakMultiplier = streakMultiplier;
        this.pointsCorrect = pointsCorrect;
        this.pointsWrong = pointsWrong;
        this.correctAnswerThreshold = correctAnswerThreshold;
    }
}
