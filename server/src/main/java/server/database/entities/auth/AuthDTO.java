package server.database.entities.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authentication credentials DTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthDTO {
    protected String username;
    protected String email;
    protected String password;
}
