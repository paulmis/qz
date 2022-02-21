package server.database.entities.game.configuration;

import javax.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Configuration for the normal game mode, which contains a fixed number of questions.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class StandardGameConfiguration extends GameConfiguration {
    /**
     * The number of questions in the game.
     */
    @Column(nullable = false)
    @NonNull Integer numQuestions = 10;
}
