package nano.web.telegram.handler.ext;

import nano.support.Onion;
import nano.support.Predicates;
import nano.web.nano.Bot;
import nano.web.security.NanoPrivilege;
import nano.web.security.UserService;
import nano.web.telegram.BotContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MailSetupHandler implements Onion.Middleware<BotContext> {

    private final UserService userService;

    public MailSetupHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var text = context.text();
        var bot = context.bot();
        if (Bot.NANO.equals(bot.getName()) && isSetMailCommand(text)) {
            this.trySetMailAddress(context);
        } else {
            next.next();
        }
    }

    private void trySetMailAddress(BotContext context){
        var user = context.getSession().getUser();
        if (!context.userPrivilegeList().contains(NanoPrivilege.MAIL)) {
            context.replyMessage("设置失败，无邮件📧服务权限");
            return;
        }
        var mailAddress = getMailAddress(context.text());
        if (!Predicates.EMAIL.test(mailAddress)) {
            context.replyMessage("非法的邮箱格式");
            return;
        }
        user.setEmail(mailAddress);
        this.userService.createOrUpdateUser(user);
        context.replyMessage("设置成功");
    }

    private static boolean isSetMailCommand(String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }
        return "/setmail ".equalsIgnoreCase(text.substring(0, "/setmail ".length()));
    }

    private static String getMailAddress(String text) {
        return text.substring("/setmail ".length());
    }
}
