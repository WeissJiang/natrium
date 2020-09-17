package nano.web.telegram.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.support.Onion;
import nano.web.security.SecurityService;
import nano.web.security.entity.NanoToken;
import nano.web.telegram.BotContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

import static nano.web.security.TokenCode.isVerificationCode;

/**
 * 用户Token验证
 */
@Component
@RequiredArgsConstructor
public class VerificationHandler implements Onion.Middleware<BotContext> {

    @NonNull
    private final SecurityService securityService;

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var text = context.text();
        var session = context.getSession();
        if (session != null && isVerificationCode(text)) {
            var username = session.getUser().getUsername();
            var tokenResultMap = this.securityService.verificateToken(username, text);
            if (!tokenResultMap.isEmpty()) {
                context.sendMessage(buildMessage(tokenResultMap));
            }
        } else {
            next.next();
        }
    }

    /**
     * 根据结果组织验证信息
     */
    private static String buildMessage(Map<NanoToken, String> tokenResultMap) {
        var verificated = new ArrayList<String>();
        var timeout = new ArrayList<String>();
        tokenResultMap.forEach((token, result) -> {
            switch (result) {
                case NanoToken.VERIFICATED -> verificated.add(token.getName());
                case NanoToken.VERIFICATION_TIMEOUT -> timeout.add(token.getName());
                default -> throw new IllegalStateException("Illegal verification result");
            }
        });
        var message = new StringBuilder();
        if (!verificated.isEmpty()) {
            message.append("验证成功：\n").append(String.join("\n", verificated));
        }
        if (!timeout.isEmpty()) {
            if (message.length() > 0) {
                message.append("\n");
            }
            message.append("验证超时：\n").append(String.join("\n", timeout));
        }
        return message.toString();
    }
}
