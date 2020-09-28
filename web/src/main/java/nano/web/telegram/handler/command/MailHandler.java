package nano.web.telegram.handler.command;

import nano.web.messageing.MailService;
import nano.web.security.NanoPrivilege;
import nano.web.telegram.BotContext;
import nano.web.telegram.handler.AbstractCommandHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;

/**
 * 发送邮件给自己
 */
@Component
public class MailHandler extends AbstractCommandHandler {

    private final MailService mailService;

    public MailHandler(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    protected void handle(BotContext context, String tail) throws MessagingException {
        var email = context.getSession().getUser().getEmail();
        if (StringUtils.isEmpty(email)) {
            context.sendMessage("邮箱📮未设置，发送/setmail设置邮箱，如：\n/setmail alice@google.com");
            return;
        }
        if (!context.userPrivilegeList().contains(NanoPrivilege.MAIL)) {
            context.sendMessage("发送失败，无邮件📧服务权限");
            return;
        }
        this.mailService.sendTextMail(email, tail);
        context.sendMessage("邮件📧投递成功");
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
