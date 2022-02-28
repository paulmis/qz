package server.database.entities.game.configuration;

import commons.entities.game.configuration.GameConfigurationDTO;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
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
@Entity
@MappedSuperclass
public class SurvivalGameConfiguration extends GameConfiguration {

    /**
     * Convert a DTO to an entity.
     *
     * @param dto DTO to convert to entity.
     */
    public SurvivalGameConfiguration(GameConfigurationDTO dto) {
        new ModelMapper().map(dto, this);
    }

    /**
     * The speed increase/decrease of the game.
     */
    @Column(nullable = false)
    Float speedModifier = 1.0f;
}
