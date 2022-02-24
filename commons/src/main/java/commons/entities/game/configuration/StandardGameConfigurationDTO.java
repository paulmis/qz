package commons.entities.game.configuration;

import commons.entities.utils.DTO;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for the standard-mode game configuration.
 */
@Data
@NoArgsConstructor
@Generated
public class StandardGameConfigurationDTO extends GameConfigurationDTO implements DTO {
    /**
     * Number of questions in the game.
     */
    protected Integer numQuestions;
}
