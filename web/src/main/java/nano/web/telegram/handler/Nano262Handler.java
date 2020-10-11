package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.nano.Bot;
import nano.web.telegram.BotContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static nano.web.scripting.Scripting.eval;

@Component
public class Nano262Handler implements Onion.Middleware<BotContext>{

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var text = context.text();
        if (!StringUtils.isEmpty(text) && Bot.NANO_262.equals(context.bot().getName())) {
            context.replyMessage(eval(text));
        } else {
            next.next();
        }
    }
}
