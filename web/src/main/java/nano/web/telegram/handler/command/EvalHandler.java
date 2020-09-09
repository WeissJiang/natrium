package nano.web.telegram.handler.command;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.web.service.scripting.ScriptService;
import nano.web.telegram.BotContext;
import nano.web.telegram.handler.AbstractCommandHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EvalHandler extends AbstractCommandHandler {

    @NonNull
    private final ScriptService scriptService;

    @Override
    public void handle(BotContext context, String script) {
        var result = this.scriptService.eval(script);
        context.sendMessage(result);
    }

    @Override
    protected String command() {
        return "eval";
    }

    @Override
    protected String help() {
        return """
                Usage: /eval script
                """;
    }

}
