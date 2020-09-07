package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.telegram.BotContext;
import org.springframework.util.StringUtils;

public abstract class AbstractCommandHandler implements Onion.Middleware<BotContext> {

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var text = context.text();

        if (StringUtils.isEmpty(text)) {
            next.next();
            return;
        }

        var tail = this.parseCommand(text);

        if (StringUtils.hasText(tail)) {
            this.handle(context, tail);
        } else if (text.startsWith("/" + this.command())) {
            context.sendMessage(this.help());
        }else {
            next.next();
        }
    }

    public String parseCommand(String text) {
        var command = this.command();
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        var regex = "(?i)^/?" + command.trim() + "\\s";
        var split = text.trim().split(regex);
        if (split.length < 2) {
            return null;
        }
        return split[1].trim();
    }

    protected abstract void handle(BotContext context, String tail);

    protected abstract String command();

    protected abstract String help();
}
