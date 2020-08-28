package nano.telegram.handler.text;

import com.jayway.jsonpath.JsonPath;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.service.WikiService;
import nano.support.Onion;
import nano.telegram.BotContext;
import nano.telegram.BotUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WikiHandler implements Onion.Middleware<BotContext> {

    private static final String URL_PREFIX = "https://%s.m.wikipedia.org/wiki/";
    private static final String COMMAND = "wiki";

    // wikipedia language
    private static final String EN = "en";
    private static final String ZH = "zh";

    @NonNull
    private final WikiService wikiService;

    public void via(BotContext context, Onion.Next next) throws Exception {
        var text = context.text();

        var content = BotUtils.parseCommand(COMMAND, text);
        if (StringUtils.isEmpty(content)) {
            next.next();
            return;
        }

        var extract = this.fetchExtract(content);
        context.sendMessage(extract);
    }

    private String fetchExtract(String title) {
        for (var language : List.of(ZH, EN)) {
            var extract = this.fetchExtractByLanguage(title, language);
            if (extract != null) {
                return extract;
            }
        }
        return "nano没有找到：" + title;
    }

    /**
     * 根据语言获取摘要
     */
    private String fetchExtractByLanguage(String title, String language) {
        var extracts = this.wikiService.getWikiExtracts(title, language);
        var extractsDocument = JsonPath.parse(extracts);
        List<String> extractList = extractsDocument.read("$.query.pages.*.extract");
        if (CollectionUtils.isEmpty(extractList)) {
            return null;
        }
        var url = URL_PREFIX.formatted(language) + title;
        var extract = extractList.get(0);
        if (StringUtils.isEmpty(extract)) {
            return url;
        }
        return extract + "\n" + url;
    }
}
