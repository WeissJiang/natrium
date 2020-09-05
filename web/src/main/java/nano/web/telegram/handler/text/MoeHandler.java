package nano.web.telegram.handler.text;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.web.service.mediawiki.MoeService;
import nano.support.Onion;
import nano.web.telegram.BotContext;
import nano.web.telegram.BotUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class MoeHandler implements Onion.Middleware<BotContext> {

    private static final String COMMAND = "moe";

    @NonNull
    private final MoeService moeService;

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var text = context.text();

        var content = BotUtils.parseCommand(COMMAND, text);
        if (StringUtils.isEmpty(content)) {
            next.next();
            return;
        }
        var extract = this.moeService.getPageExtract(content, "zh");
        if (extract == null) {
            extract = "nano没有找到：" + content;
        }
        context.sendMessage(extract);
    }
}
