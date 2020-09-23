package nano.web.telegram.handler.command;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.web.messageing.MailService;
import nano.web.telegram.BotContext;
import nano.web.telegram.handler.AbstractCommandHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 发送邮件给自己
 */
@Component
@RequiredArgsConstructor
public class MailHandler extends AbstractCommandHandler {

    @NonNull
    private final MailService mailService;

    @Override
    protected void handle(BotContext context, String tail) {
        var email = context.getSession().getUser().getEmail();
        if (StringUtils.isEmpty(email)) {
            context.sendMessage("邮箱📮未设置，发送/setmail设置邮箱，如：\n/setmail alice@google.com");
        } else {
            this.mailService.sendTextMail(email, tail);
        }
    }

    @Override
    protected String command() {
        return "mail";
    }

    @Override
    protected String help() {
        return "Usage: mail {content}";
    }
}
