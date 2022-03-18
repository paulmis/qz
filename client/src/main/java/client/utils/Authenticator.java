package client.utils;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Class that handles authentication additional information.
 */
public class Authenticator implements ClientRequestFilter {

    private final String token;

    public Authenticator(String token) {
        this.token = token;
    }

    /**
     * Function that acts as a middleware in order to
     * keep track of the token on every request we make.
     *
     * @param requestContext context holder for request.
     */
    public void filter(ClientRequestContext requestContext) {
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        headers.add("Authorization", "Bearer " + token);
    }
}