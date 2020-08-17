package nano.telegram.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.service.WikiService;
import nano.support.Onion;
import nano.telegram.BotApi;
import nano.telegram.BotContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class WikiHandler implements Onion.Middleware<BotContext> {

    private static final String PREFIX = "wiki ";

    @NonNull
    private final WikiService wikiService;

    @NonNull
    private final BotApi botApi;

    public void via(BotContext context, Onion.Next next) throws Exception {
        Integer chatId = context.readParameter("message.chat.id");
        String originalText = context.readParameter("message.text");

        if (StringUtils.isEmpty(originalText)
                || !originalText.startsWith(PREFIX)) {
            next.next();
            return;
        }
        var content = originalText.substring(PREFIX.length());
        var extracts = this.wikiService.getWikiExtracts(content);
        this.botApi.sendMessage(chatId, extracts);
    }
}
