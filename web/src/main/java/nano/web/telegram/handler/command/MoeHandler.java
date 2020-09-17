package nano.web.telegram.handler.command;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.web.service.mediawiki.MoeService;
import nano.web.telegram.BotContext;
import nano.web.telegram.handler.AbstractCommandHandler;
import org.springframework.stereotype.Component;

/**
 * 萌娘百科
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MoeHandler extends AbstractCommandHandler {

    @NonNull
    private final MoeService moeService;

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
                Usage: /moe title
                """;
    }
}
