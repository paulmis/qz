package commons.entities.game.configuration;

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
public class SurvivalGameConfigurationDTO implements Serializable {
    /**
     * UUID of the current game configuration.
     */
    private UUID id;

    /**
     * Time to answer in seconds.
     */
    private Integer answerTime;

    /**
     * Speed modifier for the game.
     */
    private Float speedModifier;
}
