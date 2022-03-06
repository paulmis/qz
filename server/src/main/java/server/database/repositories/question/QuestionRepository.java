package server.database.repositories.question;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import server.database.entities.question.Question;

/**
 * JPA repository for accessing question data.
 */
public interface QuestionRepository extends JpaRepository<Question, UUID> {
}
