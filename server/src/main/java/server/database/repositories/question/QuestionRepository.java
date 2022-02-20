package server.database.repositories.question;

import org.springframework.data.jpa.repository.JpaRepository;
import server.database.entities.question.Question;

/**
 * JPA repository for accessing question data.
 */
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Question getRandomQuestion();

    Question getQuestionById(Long id);
}