package server.database.entities.game;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * A game that has a defined amount of questions and is expected to end after the questions run out.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DefiniteGame extends Game {
    /**
     * The amount of questions in the game.
     */
    protected int questionsCount;

    /**
     * The amount of questions left in the game.
     *
     * @return The amount of questions left in the game.
     */
    public int getQuestionsCount() {
        return questionsCount;
    }
}
