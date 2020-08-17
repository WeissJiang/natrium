package nano.telegram.handler;

import com.jayway.jsonpath.JsonPath;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.service.WikiService;
import nano.support.Onion;
import nano.telegram.BotApi;
import nano.telegram.BotContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WikiHandler implements Onion.Middleware<BotContext> {

    private static final String URL_PREFIX = "https://zh.m.wikipedia.org/wiki/";
    private static final String PREFIX = "wiki ";

    @NonNull
    private final WikiService wikiService;

    @NonNull
    private final BotApi botApi;

    public void via(BotContext context, Onion.Next next) throws Exception {
        String text = context.readParameter("$.message.text");
        Integer chatId = context.readParameter("$.message.chat.id");

        if (StringUtils.isEmpty(text) || !text.startsWith(PREFIX)) {
            next.next();
            return;
        }
        var content = text.substring(PREFIX.length());
        var extracts = this.wikiService.getWikiExtracts(content);
        var extractsDocument = JsonPath.parse(extracts);
        Map<String, Map<String, Object>> pages = extractsDocument.read("$.query.pages");

        if (CollectionUtils.isEmpty(pages) || pages.containsKey("-1")) {
            this.botApi.sendMessage(chatId, "nano没有找到：" + content);
            return;
        }
        var url = URL_PREFIX + content;
        var wiki = new ArrayList<>(pages.values()).get(0);
        var extract = wiki.get("extract");
        if (StringUtils.isEmpty(extract)) {
            this.botApi.sendMessage(chatId, url);
            return;
        }
        this.botApi.sendMessage(chatId, extract + "\n" + url);
    }
}