package server.database.entities.auth.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Manages JWT tokens.
 */
@Component
public class JWTHandler {
    /**
     * TTL for all JWT tokens in seconds.
     */
    public static final long JWT_TTL = 3600;

    /**
     * The JWT encryption secret.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Generates a JWT token for the given user.
     *
     * @param email user's email
     * @return a new JWT token
     * @throws IllegalArgumentException if the provided id is null
     * @throws JWTCreationException     if the token could not be created
     */
    public String generateToken(String email) throws IllegalArgumentException, JWTCreationException {
        return JWT.create()
                .withSubject("User")
                .withClaim("email", email)
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * Verifies the given JWT token and returns the ID of the user.
     *
     * @param token JWT token
     * @return user's email
     * @throws TokenExpiredException if the token expired
     * @throws SignatureVerificationException if the token could not be verified
     */
    public String validateToken(String token) throws TokenExpiredException, SignatureVerificationException {
        DecodedJWT jwt = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User")
                .build()
                .verify(token);
        return jwt.getClaim("email").asString();
    }
}
