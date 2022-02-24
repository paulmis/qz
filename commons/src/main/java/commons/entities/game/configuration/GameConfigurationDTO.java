package commons.entities.game.configuration;

import commons.entities.utils.DTO;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

/**
 * DTO for game configuration.
 */
@Data
@NoArgsConstructor
@Generated
public abstract class GameConfigurationDTO implements DTO {
    /**
     * The id of the game configuration.
     */
    protected UUID id;

    /**
     * Time to answer in seconds.
     */
    protected Integer answerTime;
}
