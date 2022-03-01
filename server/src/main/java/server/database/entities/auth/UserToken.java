package server.database.entities.auth;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import server.database.entities.User;

/**
 * Represents a JWT token used to authenticate a user.
 */
@Entity
@Data
@NoArgsConstructor
public class UserToken {
    /**
     * The token string.
     */
    @Id
    protected String token;

    /**
     * The user this token was generated for.
     */
    @ManyToOne
    protected User user;
}
