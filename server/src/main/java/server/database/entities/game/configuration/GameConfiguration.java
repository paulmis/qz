package server.database.entities.game.configuration;

import commons.entities.game.configuration.GameConfigurationDTO;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import server.database.entities.utils.BaseEntity;

/**
 * Base class for game configuration.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
public abstract class GameConfiguration extends BaseEntity<GameConfigurationDTO> {
    /**
     * Time (in seconds) available for each player to answer each question.
     * In the future, we could switch to a Duration datatype, but JPA/Hibernate doesn't support it out of the box.
     */
    @Column(nullable = false)
    private Integer answerTime = 10;
}
