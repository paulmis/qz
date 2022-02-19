package server.database;

import commons.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * User Repository - Interface that initializes repository.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {}
