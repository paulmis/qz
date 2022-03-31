package server.database.entities.game.configuration;

import commons.entities.game.configuration.GameConfigurationDTO;
import commons.entities.game.configuration.SurvivalGameConfigurationDTO;
import java.time.Duration;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

/**
 * Configuration for the survival game mode.
 * In this mode, the player has to survive as long as possible, while the
 * time available to answer questions decreases with each round.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public class SurvivalGameConfiguration extends GameConfiguration {

    /**
     * Creates a new game configuration from a DTO.
     *
     * @param dto source DTO
     */
    public SurvivalGameConfiguration(GameConfigurationDTO dto) {
        new ModelMapper().map(dto, this);
    }

    /**
     * The speed increase/decrease of the game.
     */
    @Column(nullable = false)
    float speedModifier = 1.0f;

    @Override
    public SurvivalGameConfigurationDTO getDTO() {
        ModelMapper mapper = new ModelMapper();
        mapper.addConverter(
                context -> (int) context.getSource().toMillis(),
                Duration.class, Integer.class);

        return mapper.map(this, SurvivalGameConfigurationDTO.class);
    }
}
