package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.nano.model.Bot;
import nano.web.security.SecurityService;
import nano.web.nano.entity.NanoToken;
import nano.web.telegram.BotContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

import static nano.web.security.TokenCode.isVerificationCode;

/**
 * Token verification
 */
@Component
public class VerificationHandler implements Onion.Middleware<BotContext> {

    private final SecurityService securityService;

    public VerificationHandler(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        var text = context.text();
        var session = context.getSession();
        if (Bot.NANO.equals(context.bot().getName()) && session != null && isVerificationCode(text)) {
            var user = session.getUser();
            var token = session.getToken();
            var tokenResultMap = this.securityService.verifyToken(user, token, text);
            if (!tokenResultMap.isEmpty()) {
                context.replyMessage(buildMessage(tokenResultMap));
            }
        } else {
            next.next();
        }
    }

    /**
     * Build message
     */
    private static String buildMessage(Map<NanoToken, String> tokenResultMap) {
        var verified = new ArrayList<String>();
        var timeout = new ArrayList<String>();
        tokenResultMap.forEach((token, result) -> {
            switch (result) {
                case NanoToken.VERIFIED -> verified.add(token.getName());
                case NanoToken.VERIFYING_TIMEOUT -> timeout.add(token.getName());
                default -> throw new IllegalStateException("Illegal verification result");
            }
        });
        var message = new StringBuilder();
        if (!verified.isEmpty()) {
            message.append("Verification successful:\n").append(String.join("\n", verified));
        }
        if (!timeout.isEmpty()) {
            if (message.length() > 0) {
                message.append("\n");
            }
            message.append("Verification successful:\n").append(String.join("\n", timeout));
        }
        return message.toString();
    }
}
