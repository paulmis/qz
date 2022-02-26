package server.database.entities.auth.config;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import server.database.entities.User;
import server.database.repositories.UserRepository;

/**
 * Authentication filter that parses authentication requests and validates them.
 */
@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired private JWTHandler handler;
    @Autowired private CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // Retrieve the token from the Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {
            // Parse the header and check if the token is well-formed
            String jwt = authHeader.substring(7);
            if (jwt.isBlank()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JWT Token in Bearer Header");
            } else {
                try {
                    // Validate the token and retrieve the user
                    String email = handler.validateToken(jwt);
                    UserDetails authDAO = customUserDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    authDAO.getPassword(),
                                    authDAO.getAuthorities());

                    // Set the user in the security context
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                    // Catch exceptions
                } catch (TokenExpiredException exc) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token has expired");
                } catch (SignatureVerificationException ex) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                } catch (NoSuchElementException | UsernameNotFoundException ex) {
                    response.sendError(HttpServletResponse.SC_CONFLICT, "JWT Token's user no longer exists");
                }
            }
        }

        // Invoke the next filter in the chain
        filterChain.doFilter(request, response);
    }
}