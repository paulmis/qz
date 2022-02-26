package server.database.entities.auth.config;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configures the bcrypt hashing function.
 */
@Data
@Configuration
public class BCryptConfig {
    /**
     * Provides a password encryption bean for hashing.
     *
     * @return password encryption bean
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
