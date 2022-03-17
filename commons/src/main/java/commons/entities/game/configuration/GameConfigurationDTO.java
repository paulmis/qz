package commons.entities.game.configuration;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import commons.entities.game.GameDTO;
import commons.entities.utils.DTO;
import java.util.UUID;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import jdk.jfr.Description;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for game configuration.
 */
@Data
@NoArgsConstructor
@MappedSuperclass
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
    @JsonSubTypes.Type(value = NormalGameConfigurationDTO.class, name = "NormalGameConfigurationDTO"),
    @JsonSubTypes.Type(value = SurvivalGameConfigurationDTO.class, name = "SurvivalGameConfigurationDTO")
})
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


    /**
     * Capacity of the lobby.
     */
    @DecimalMin(value = "1")
    @DecimalMax(value = "8")
    @Description("Capacity of lobby")
    protected Integer capacity;
}
