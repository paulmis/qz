package server.database.entities.game.configuration;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Base class for game configuration.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
// As we want to make switching between different game modes as painless as possible, keep all configs in the same table
@Inheritance(strategy = javax.persistence.InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "game_mode")
public abstract class GameConfiguration {
    @Id
    private Long id;

    @Column(nullable = false)
    private Integer answerTime = 10;
}
