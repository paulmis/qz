package commons.entities.game.configuration;

import commons.entities.utils.DTO;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data transfer object for the survival-mode game configuration.
 */
@Data
@NoArgsConstructor
public class SurvivalGameConfigurationDTO extends GameConfigurationDTO implements DTO {
    /**
     * Speed modifier for the game.
     */
    protected Float speedModifier;
}
