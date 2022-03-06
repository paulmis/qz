package commons.entities.game.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import commons.entities.utils.DTO;
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
    protected Float speedModifier;
}
