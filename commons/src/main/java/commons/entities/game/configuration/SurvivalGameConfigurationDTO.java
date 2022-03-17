package commons.entities.game.configuration;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.UUID;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import jdk.jfr.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for the survival-mode game configuration.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class SurvivalGameConfigurationDTO extends GameConfigurationDTO {
    /**
     * Speed modifier for the game.
     */
    @DecimalMin(value = "0.1")
    @DecimalMax(value = "10.0")
    @Description("Speed modifier")
    protected Float speedModifier;

    /**
     * Normal game config constructor.
     *
     * @param id the id of the config.
     * @param answerTime the answer time per question.
     * @param capacity the capacity of the lobby.
     * @param speedModifier the speed modifier of the game.
     */
    public SurvivalGameConfigurationDTO(UUID id, Integer answerTime, Integer capacity, Float speedModifier) {
        this.id = id;
        this.answerTime = answerTime;
        this.capacity = capacity;
        this.speedModifier = speedModifier;
    }
}
