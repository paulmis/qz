package commons.entities.game.configuration;

import commons.entities.utils.DTO;
import java.util.UUID;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import jdk.jfr.Description;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for game configuration.
 */
@Data
@NoArgsConstructor
@MappedSuperclass
public abstract class GameConfigurationDTO implements DTO {
    /**
     * The id of the game configuration.
     */
    protected UUID id;

    /**
     * Time to answer in seconds.
     */
    @DecimalMin(value = "3")
    @DecimalMax(value = "60")
    @Description("Seconds per question")
    protected Integer answerTime;
}
