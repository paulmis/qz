package server.database.repositories.question;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import server.database.entities.question.Question;

/**
 * Implementation of the custom methods for the QuestionRepository interface.
 */
public class QuestionRepositoryImpl implements QuestionRepositoryCustom {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * Get a random question from the database.
     *
     * @return Random question from the DB.
     */
    @Override
    public Question getRandomQuestion() {
        return null;
    }
}
