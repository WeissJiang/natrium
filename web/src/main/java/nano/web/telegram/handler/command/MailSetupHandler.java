package nano.web.telegram.handler.command;

import nano.support.Predicates;
import nano.web.security.NanoPrivilege;
import nano.web.security.UserService;
import nano.web.telegram.BotContext;
import nano.web.telegram.handler.AbstractCommandHandler;
import org.springframework.stereotype.Component;

/**
 * 设置邮箱
 */
@Component
public class MailSetupHandler extends AbstractCommandHandler {

    private final UserService userService;

    public MailSetupHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void handle(BotContext context, String tail) {
        var user = context.getSession().getUser();
        if (!context.userPrivilegeList().contains(NanoPrivilege.MAIL)) {
            context.replyMessage("设置失败，无邮件📧服务权限");
            return;
        }
        if (!Predicates.EMAIL.test(tail)) {
            context.replyMessage("非法的邮箱格式");
            return;
        }
        user.setEmail(tail);
        this.userService.createOrUpdateUser(user);
        context.replyMessage("设置成功");
    }

    @Override
    protected String command() {
        return "setmail";
    }

    @Override
    protected String help() {
        return "Usage: /setmail {email}";
    }
}
