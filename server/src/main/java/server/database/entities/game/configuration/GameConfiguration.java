package server.database.entities.game.configuration;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base class for game configuration.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DiscriminatorColumn(name = "game_mode")
public abstract class GameConfiguration {
    @Id
    private UUID id;

    /**
     * Time (in seconds) available for each player to answer each question.
     * In the future, we could switch to a Duration datatype, but JPA/Hibernate doesn't support it out of the box.
     */
    @Column(nullable = false)
    private Integer answerTime = 10;
}
