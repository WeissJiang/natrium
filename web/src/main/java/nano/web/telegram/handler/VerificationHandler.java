package nano.web.telegram.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.support.Onion;
import nano.web.security.entity.NanoToken;
import nano.web.security.model.Session;
import nano.web.security.repository.TokenRepository;
import nano.web.telegram.BotContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 用户Token验证
 */
@Component
@RequiredArgsConstructor
public class VerificationHandler implements Onion.Middleware<BotContext> {

    @NonNull
    private final TokenRepository tokenRepository;

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var text = context.text();
        var session = context.getSession();
        if (session != null && isVerificationCode(text)) {
            var user = session.getUser();
            var username = user.getUsername();
            var nanoTokenList = this.tokenRepository.queryVerificatingToken(username, text);
            if (!CollectionUtils.isEmpty(nanoTokenList)) {
                for (var nanoToken : nanoTokenList) {
                    // set token valid
                    this.tokenRepository.updateTokenStatus(nanoToken.getToken(), NanoToken.VALID);
                }
            }
        } else {
            next.next();
        }
    }

    private static boolean isVerificationCode(String text) {
        return text != null && text.matches("^[0-9]{6}$");
    }

}
