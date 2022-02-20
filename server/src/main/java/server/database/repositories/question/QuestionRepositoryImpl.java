package server.database.repositories.question;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
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
        CriteriaBuilder criteriaBuilder = entityManager
                .getCriteriaBuilder();
        CriteriaQuery<Question> criteriaQuery = criteriaBuilder
                .createQuery(Question.class);
        Root<Question> from = criteriaQuery.from(Question.class);
        CriteriaQuery<Question> select = criteriaQuery.select(from);
        TypedQuery<Question> typedQuery = entityManager.createQuery(select);
        typedQuery.setFirstResult(n);
        typedQuery.setMaxResults(1);
        return typedQuery.getSingleResult();
    }
}
