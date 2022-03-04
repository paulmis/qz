package server.database.repositories.question;

import javax.persistence.criteria.Predicate;
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

    /**
     * Get nth question that matches the criteria.
     *
     * @param n The index of the question to get.
     * @param criteria The criteria to use to find the question.
     * @return The question at the given index.
     */
    Question getNthQuestionWithCriteria(int n, Predicate[] criteria);
}
