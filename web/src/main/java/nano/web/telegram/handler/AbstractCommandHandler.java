package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.nano.ConfigVars;
import nano.web.telegram.BotContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 * CommandHandler通用处理
 */
public abstract class AbstractCommandHandler implements Onion.Middleware<BotContext> {

    private ConfigVars configVars;

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var text = context.text();

        if (StringUtils.isEmpty(text)) {
            next.next();
            return;
        }

        var tail = this.parseCommand(context.chatType(), text);

        if (StringUtils.hasText(tail)) {
            this.handle(context, tail);
        } else if (text.startsWith("/" + this.command())) {
            context.replyMessage(this.help());
        } else {
            next.next();
        }
    }

    private String parseCommand(String chatType, String text) {
        var cmd = this.command().trim();
        String regex = switch (chatType) {
            case "supergroup" -> "(?i)/%s@%s\\s".formatted(cmd, this.configVars.getBotName());
            case "private" -> "(?i)^/?%s\\s".formatted(cmd);
            default -> null;
        };
        if (regex == null) {
            return null;
        }
        var split = text.trim().split(regex);
        if (split.length < 2) {
            return null;
        }
        return split[1].trim();
    }

    protected abstract void handle(BotContext context, String tail) throws Exception;

    protected abstract String command();

    protected abstract String help();

    @Autowired
    public void setConfigVars(ConfigVars configVars) {
        this.configVars = configVars;
    }
}
