package server.database.entities.game.configuration;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Configuration for the normal game mode, which contains a fixed number of questions.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@DiscriminatorValue("standard")
public class StandardGameConfiguration extends GameConfiguration {
    /**
     * The number of questions in the game.
     */
    @Column(nullable = false)
    @NonNull Integer numQuestions = 10;
}
