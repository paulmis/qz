package commons.entities.game.configuration;

import commons.entities.utils.DTO;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import jdk.jfr.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * Data transfer object for the standard-mode game configuration.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class StandardGameConfigurationDTO extends GameConfigurationDTO implements DTO {
    /**
     * Number of questions in the game.
     */
    @DecimalMin(value = "10")
    @DecimalMax(value = "100")
    @Description("Number of questions")
    protected Integer numQuestions;
}
