package nano.web.telegram.handler.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nano.web.telegram.BotContext;
import nano.web.telegram.handler.AbstractCommandHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.script.ScriptEngineManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class EvalHandler extends AbstractCommandHandler {

    @Override
    public void handle(BotContext context, String script) {
        context.sendMessage(eval(script));
    }


    @SneakyThrows
    private static String eval(String script) {
        var manager = new ScriptEngineManager();
        var engine = manager.getEngineByName("graal.js");
        Assert.notNull(engine, "engine is null");
        return String.valueOf(engine.eval(script));
    }

    @Override
    protected String command() {
        return "eval";
    }

}
