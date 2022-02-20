package server.database.entities.game.gamemodes;

import java.util.Optional;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import server.database.entities.game.Game;
import server.database.entities.game.configuration.NormalGameConfiguration;
import server.database.entities.question.Question;

/**
 * Normal game mode.
 */
@Entity
@Getter
@Setter
@ToString
@DiscriminatorValue("normal")
public class NormalGame extends Game {

    public NormalGame() {
        super();
        this.setConfiguration(new NormalGameConfiguration());
    }

    /**
     * Get the next question in the game.
     *
     * @return The current question.
     */
    @Override
    public Optional<Question> getNextQuestion() {
        // TODO: proper implementation of this
        return Optional.empty();
    }
}
