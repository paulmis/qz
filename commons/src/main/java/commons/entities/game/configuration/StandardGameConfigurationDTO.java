package commons.entities.game.configuration;

import commons.entities.utils.DTO;
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
    protected Integer numQuestions;
}
