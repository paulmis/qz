package commons.entities.game.configuration;

import commons.entities.utils.DTO;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for the survival-mode game configuration.
 */
@Data
@NoArgsConstructor
@Generated
public class SurvivalGameConfigurationDTO extends GameConfigurationDTO implements DTO {
    /**
     * Speed modifier for the game.
     */
    protected Float speedModifier;
}
