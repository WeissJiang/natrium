package nano.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Reply HTTP status 403 and reason
 */
public class AuthenticationException extends ResponseStatusException {

    public AuthenticationException(String reason) {
        super(HttpStatus.FORBIDDEN, reason);
    }
}
