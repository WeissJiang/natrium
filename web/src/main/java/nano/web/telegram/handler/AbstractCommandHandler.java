package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.telegram.BotContext;
import nano.web.telegram.BotUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractCommandHandler implements Onion.Middleware<BotContext> {

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var tail = BotUtils.parseCommand(this.command(), context.text());

        if (StringUtils.hasText(tail)) {
            this.handle(context, tail);
        } else {
            var commands = context.commands();
            if (!commands.isEmpty() && ("/" + this.command()).equals(commands.get(0))) {
                context.sendMessage(this.help());
            } else {
                next.next();
            }
        }
    }

    protected abstract void handle(BotContext context, String tail);

    protected abstract String command();

    protected abstract String help();
}
