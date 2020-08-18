package nano.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.constant.ConfigVars;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SecurityService {

    @NonNull
    private final Environment env;

    public void checkNanoToken(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new AuthenticationException("Missing token");
        }

        var nanoToken = this.env.getProperty(ConfigVars.NANO_TOKEN, "");
        if (!token.equals(nanoToken)) {
            throw new AuthenticationException("Illegal token");
        }
    }
}
