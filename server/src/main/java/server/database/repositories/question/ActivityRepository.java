package server.database.repositories.question;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import server.database.entities.question.Activity;

/**
 * Repository for the Activity entity.
 */
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    Optional<Activity> findByDescriptionAndCost(@NonNull String description, @NonNull long cost);

    //@Override
    //@lombok.NonNull
    //Optional<Activity> findById(UUID uuid);
}