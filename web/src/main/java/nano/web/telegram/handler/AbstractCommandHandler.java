package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.telegram.BotContext;
import nano.web.telegram.BotUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractCommandHandler implements Onion.Middleware<BotContext> {

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var text = context.text();

        var tail = BotUtils.parseCommand(this.command(), text);
        if (StringUtils.isEmpty(tail)) {
            next.next();
            return;
        }
        this.handle(context, tail);
    }

    protected abstract void handle(BotContext context, String tail);

    protected abstract String command();
}
