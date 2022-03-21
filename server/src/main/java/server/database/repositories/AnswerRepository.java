package server.database.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.database.entities.answer.Answer;

/**
 * Answer Repository - Interface that initializes repository.
 */
@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {}
