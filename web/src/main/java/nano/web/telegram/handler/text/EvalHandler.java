package nano.web.telegram.handler.text;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nano.support.Onion;
import nano.web.telegram.BotContext;
import nano.web.telegram.BotUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.script.ScriptEngineManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class EvalHandler implements Onion.Middleware<BotContext> {

    private static final String COMMAND = "eval";

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var text = context.text();

        var content = BotUtils.parseCommand(COMMAND, text);
        if (StringUtils.isEmpty(content)) {
            next.next();
            return;
        }

        context.sendMessage(eval(content));
    }

    @SneakyThrows
    private static String eval(String script) {
        var manager = new ScriptEngineManager();
        var engine = manager.getEngineByName("graal.js");
        Assert.notNull(engine, "engine");
        return String.valueOf(engine.eval(script));
    }
}
