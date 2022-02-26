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
 * Configuration for the normal game mode, which contains a fixed number of questions.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@MappedSuperclass
public class StandardGameConfiguration extends GameConfiguration {
    /**
     * Convert a DTO to an entity.
     *
     * @param dto the dto to convert
     */
    public StandardGameConfiguration(GameConfigurationDTO dto) {
        new ModelMapper().map(dto, this);
    }

    /**
     * The number of questions in the game.
     */
    @Column(nullable = false)
    Integer numQuestions = 10;
}
