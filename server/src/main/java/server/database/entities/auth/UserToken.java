package server.database.entities.auth;

import java.util.UUID;
import javax.persistence.Column;
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
     * Entity ID.
     */
    @Id
    protected UUID id;

    /**
     * The token string.
     */
    @Column
    protected String token;

    /**
     * The user this token was generated for.
     */
    @ManyToOne
    protected User user;

    // TODO: DTO
}
