package server.database.repositories.question;

import server.database.entities.question.Question;

/**
 * JPA question repository - Custom methods for QuestionRepository.
 */
public interface QuestionRepositoryCustom {
    /** Get a random question from the database.
     *
     * @return Random question from the DB.
     */
    Question getRandomQuestion();
}
