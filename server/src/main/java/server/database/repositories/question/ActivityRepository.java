package server.database.repositories.question;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import server.database.entities.question.Activity;

/**
 * Repository for the Activity entity.
 */
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
}