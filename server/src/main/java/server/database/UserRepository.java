package server.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import commons.User;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {}
