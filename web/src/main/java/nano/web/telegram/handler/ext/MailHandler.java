package nano.web.telegram.handler.ext;

import nano.support.Onion;
import nano.support.mail.MailService;
import nano.support.mail.TextMail;
import nano.web.nano.Bot;
import nano.web.telegram.BotContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;

/**
 * Mail handler
 */
@Component
public class MailHandler implements Onion.Middleware<BotContext> {

    private final MailService mailService;

    public MailHandler(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        var text = context.text();
        var bot = context.bot();
        if (Bot.NANO.equals(bot.getName()) && isMailCommand(text)) {
            this.trySendMail(context);
        } else {
            next.next();
        }
    }

    private void trySendMail(BotContext context) throws MessagingException {
        var email = context.getSession().getUser().getEmail();
        if (StringUtils.isEmpty(email)) {
            context.replyMessage("邮箱📮未设置，发送/setmail设置邮箱，如：\n/setmail somename@example.com");
            return;
        }
        var message = getMailBody(context.text());
        if (StringUtils.isEmpty(message)) {
            context.replyMessage("Mail内容为空，发送邮件：/mail {message}");
            return;
        }
        this.mailService.sendTextMail(createTextMail(email, message));
        context.replyMessage("邮件📧投递成功");
    }

    private static TextMail createTextMail(String email, String message) {
        var mail = new TextMail();
        mail.setTo(email);
        mail.setSubject("Shinonome Lab Mail Service");
        mail.setText(message);
        return mail;
    }

    private static boolean isMailCommand(String text) {
        int len = "/mail ".length();
        if (StringUtils.isEmpty(text) || text.length() < len) {
            return false;
        }
        return "/mail ".equalsIgnoreCase(text.substring(0, len));
    }

    private static String getMailBody(String text) {
        return text.substring("/mail ".length());
    }
}
