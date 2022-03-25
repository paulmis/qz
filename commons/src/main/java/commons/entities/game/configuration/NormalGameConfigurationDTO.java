package commons.entities.game.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
public class NormalGameConfigurationDTO extends GameConfigurationDTO {
    /**
     * Number of questions in the game.
     */
    @DecimalMin(value = "10")
    @DecimalMax(value = "100")
    @Description("Number of questions")
    protected Integer numQuestions;

    /**
     * Normal game config constructor.
     *
     * @param id the id of the config.
     * @param answerTime the answer time per question.
     * @param capacity the capacity of the lobby.
     * @param numQuestions the number of questions in the game.
     */
    public NormalGameConfigurationDTO(UUID id, Integer answerTime, Integer capacity, Integer numQuestions) {
        this.id = id;
        this.answerTime = answerTime;
        this.capacity = capacity;
        this.numQuestions = numQuestions;
    }
}
