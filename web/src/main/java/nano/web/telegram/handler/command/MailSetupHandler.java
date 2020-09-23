package nano.web.telegram.handler.command;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.web.security.NanoPrivilege;
import nano.web.security.UserService;
import nano.web.telegram.BotContext;
import nano.web.telegram.handler.AbstractCommandHandler;
import org.springframework.stereotype.Component;

/**
 * 设置邮箱
 */
@Component
@RequiredArgsConstructor
public class MailSetupHandler extends AbstractCommandHandler {

    @NonNull
    private final UserService userService;

    @Override
    protected void handle(BotContext context, String tail) {
        var user = context.getSession().getUser();
        if (this.userService.hasPrivilege(user.getId(), NanoPrivilege.MAIL)) {
            user.setEmail(tail);
            this.userService.createOrUpdateUser(user);
            context.sendMessage("设置成功");
        } else {
            context.sendMessage("设置失败，无邮件📧服务权限");
        }

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
