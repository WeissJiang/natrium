package nano.web.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.web.ConfigVars;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SecurityService {

    @NonNull
    private final ConfigVars configVars;

    public void checkNanoToken(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new AuthenticationException("Missing token");
        }

        var nanoToken = this.configVars.getNanoApiToken();
        if (!token.equals(nanoToken)) {
            throw new AuthenticationException("Illegal token");
        }
    }
}
