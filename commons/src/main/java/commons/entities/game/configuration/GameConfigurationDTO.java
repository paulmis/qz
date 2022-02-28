package commons.entities.game.configuration;

import commons.entities.utils.DTO;
import java.util.UUID;
import javax.persistence.MappedSuperclass;
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
    protected Integer answerTime;
}
