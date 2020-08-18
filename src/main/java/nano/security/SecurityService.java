package nano.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SecurityService {

    @NonNull
    private final Environment env;

    /**
     * nano token
     */
    public String getNanoToken() {
        return this.env.getProperty("NANO_TOKEN", "");
    }

    public void checkNanoToken(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new AuthenticationException("token missing");
        }

        if (!token.equals(this.getNanoToken())) {
            throw new AuthenticationException("Illegal token");
        }
    }
}
