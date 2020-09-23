package nano.web.telegram.handler.command;

import lombok.extern.slf4j.Slf4j;
import nano.web.telegram.BotContext;
import nano.web.telegram.handler.AbstractCommandHandler;
import org.springframework.stereotype.Component;

import static nano.web.scripting.Scripting.eval;

/**
 * 执行JavaScript脚本
 */
@Slf4j
@Component
public class EvalHandler extends AbstractCommandHandler {

    @Override
    public void handle(BotContext context, String script) {
        context.sendMessage(eval(script));
    }

    @Override
    protected String command() {
        return "eval";
    }

    @Override
    protected String help() {
        return """
                Usage: /eval {script}
                """;
    }

}
