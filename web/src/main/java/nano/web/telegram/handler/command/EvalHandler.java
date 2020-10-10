package nano.web.telegram.handler.command;

import nano.web.telegram.BotContext;
import nano.web.telegram.handler.AbstractCommandHandler;
import org.springframework.stereotype.Component;

import static nano.web.scripting.Scripting.eval;

/**
 * 执行JavaScript脚本
 */
@Component
public class EvalHandler extends AbstractCommandHandler {

    @Override
    public void handle(BotContext context, String script) {
        context.replyMessage(eval(script));
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
