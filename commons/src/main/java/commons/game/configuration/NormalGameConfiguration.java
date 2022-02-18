package commons.game.configuration;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Configuration for the normal game mode, which contains a fixed number of questions.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@DiscriminatorValue("normal")
public class NormalGameConfiguration extends GameConfiguration {
    /**
     * The number of questions in the game.
     */
    @Column(nullable = false)
    private int numQuestions = 10;
}
