package server.database.entities.auth.config;

import lombok.Generated;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Provides authentication context to controllers.
 */
@Generated
public class AuthContext {
    /**
     * Returns the authentication DAO key, i.e. the email.
     *
     * @return the authentication DAO key, i.e. the email
     */
    public static String get() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
