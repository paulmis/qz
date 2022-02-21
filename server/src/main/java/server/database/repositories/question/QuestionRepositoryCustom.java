package server.database.repositories.question;

import server.database.entities.question.Question;

/**
 * QuestionRepositoryCustom.java - Custom methods for QuestionRepository.
 */
public interface QuestionRepositoryCustom {
    /** Get the question at the given index from the database.
     *
     * @param n The index of the question to get.
     * @return The question at the given index.
     */
    Question getNthQuestion(int n);
}
