package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.nano.Bot;
import nano.web.scripting.Scripting;
import nano.web.telegram.BotContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class Nano262Handler implements Onion.Middleware<BotContext> {

    private final Scripting scripting;

    public Nano262Handler(Scripting scripting) {
        this.scripting = scripting;
    }

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        if (Bot.NANO_262.equals(context.bot().getName())) {
            this.evalScript(context);
        } else {
            next.next();
        }
    }

    private void evalScript(BotContext context) {
        var text = context.text();
        if (StringUtils.isEmpty(text)) {
            context.sendMessage("⚠️The script is empty");
            return;
        }
        context.replyMessage(this.scripting.eval(text));
    }
}
