package server.database.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.database.entities.User;

/**
 * User Repository - Interface that initializes repository.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the provided email exists.
     *
     * @param email the user's email
     * @return whether the user exists
     */
    boolean existsByEmail(String email);

    /**
     * Checks if the provided email and username are unique.
     *
     * @param email the email to check
     * @param username the username to check
     * @return whether the email or username already exists in the database
     */
    boolean existsByEmailOrUsername(String email, String username);

    /**
     * Get the list of all users ordered by their score.
     *
     * @return a list of all users ordered by their score.
     */
    List<User> findAllByOrderByScoreDesc();
}
