package nano.web.telegram.handler.command;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.web.security.UserService;
import nano.web.security.entity.NanoUser;
import nano.web.security.repository.UserRepository;
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
        user.setEmail(tail);
        this.userService.createOrUpdateUser(user);
        context.sendMessage("设置成功");
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
