package commons.entities;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * DTO for authentication.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthDTO {
    @Email
    @NonNull
    protected String username;

    /**
     * User's email address.
     */
    @Email
    @NonNull
    protected String email;

    /**
     * User's password.
     */
    @Size(min = 8)
    @NonNull
    protected String password;
}
