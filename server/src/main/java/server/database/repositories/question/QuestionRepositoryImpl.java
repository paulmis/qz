package server.database.repositories.question;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;
import server.database.entities.question.Question;

/**
 * Implementation of the custom QuestionRepository interface logic.
 */
@Repository
@Transactional
public class QuestionRepositoryImpl implements QuestionRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    /**
     * Get the question at the given index from the database.
     *
     * @param n The index of the question to get.
     * @return The question at the given index.
     */
    @Override
    public Question getNthQuestion(int n) {
        return getNthQuestionWithCriteria(n, new Predicate[0]);
    }

    /**
     * Get nth question that matches the criteria.
     *
     * @param n        The index of the question to get.
     * @param criteria The criteria to use to find the question.
     * @return The question at the given index.
     */
    @Override
    public Question getNthQuestionWithCriteria(int n, Predicate[] criteria) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Question> criteriaQuery = criteriaBuilder.createQuery(Question.class);
        Root<Question> from = criteriaQuery.from(Question.class);
        CriteriaQuery<Question> select = criteriaQuery.select(from);
        select.where(criteria);
        TypedQuery<Question> typedQuery = entityManager.createQuery(select);
        typedQuery.setFirstResult(n);
        typedQuery.setMaxResults(1);
        return typedQuery.getSingleResult();
    }
}
