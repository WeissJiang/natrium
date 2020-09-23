package nano.web.telegram.handler.command;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.web.messageing.MailService;
import nano.web.telegram.BotContext;
import nano.web.telegram.handler.AbstractCommandHandler;
import org.springframework.stereotype.Component;

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
        this.mailService.sendTextMail(email, tail);
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
