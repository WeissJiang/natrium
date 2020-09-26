package nano.web.telegram.handler.command;

import nano.web.mediawiki.MoeService;
import nano.web.telegram.BotContext;
import nano.web.telegram.handler.AbstractCommandHandler;
import org.springframework.stereotype.Component;

/**
 * 萌娘百科
 */
@Component
public class MoeHandler extends AbstractCommandHandler {

    private final MoeService moeService;

    public MoeHandler(MoeService moeService) {
        this.moeService = moeService;
    }

    public void handle(BotContext context, String title) {
        var extract = this.moeService.getPageExtract(title, "zh");
        if (extract == null) {
            extract = "nano没有找到：" + title;
        }
        context.sendMessage(extract);
    }

    @Override
    protected String command() {
        return "moe";
    }

    @Override
    protected String help() {
        return """
                Usage: /moe {title}
                """;
    }
}
