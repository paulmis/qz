package server.database.repositories.question;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import server.database.entities.question.Activity;

/**
 * Repository for the Activity entity.
 */
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    Optional<Activity> findByIdAndAbandonedIsFalse(UUID id);

    @Query("select a from Activity a where a.id = ?1 and a.abandoned = true")
    Optional<Activity> findByIdAndAbandonedIsTrue(UUID id);

    @Query("select a from Activity a where a.abandoned = false")
    List<Activity> findByAbandonedIsFalse();

    @Query("select a from Activity a "
            + "where a.abandoned = false "
            + "and a.description like concat('%', 'ing', '%') "
            + "and a.description not like concat('%', '?')")
    List<Activity> findQuestionAcceptable();
}
