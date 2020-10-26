package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.nano.Bot;
import nano.web.scripting.Scripting;
import nano.web.telegram.BotContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Evaluate JavaScript
 */
@Component
public class Nano262Handler implements Onion.Middleware<BotContext> {

    private final Scripting scripting;

    public Nano262Handler(Scripting scripting) {
        this.scripting = scripting;
    }

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
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
        var result = this.scripting.eval(text);
        if (StringUtils.isEmpty(result)) {
            return;
        }
        // Text of the message to be sent, 1-4096 characters after entities parsing
        if (result.length() > 4096) {
            context.sendMessage("⚠️Evaluated result is too long");
            return;
        }
        context.replyMessage(result);
    }
}
