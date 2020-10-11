package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.baidu.BaikeService;
import nano.web.mediawiki.MoeService;
import nano.web.mediawiki.WikiService;
import nano.web.nano.Bot;
import nano.web.telegram.BotContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class Bot100Handler implements Onion.Middleware<BotContext> {

    private final MoeService moeService;
    private final WikiService wikiService;
    private final BaikeService baikeService;

    private final List<Function<String, String>> fetcherList = new ArrayList<>();

    public Bot100Handler(MoeService moeService, WikiService wikiService, BaikeService baikeService) {
        this.moeService = moeService;
        this.wikiService = wikiService;
        this.baikeService = baikeService;
        // add fetchers
        this.fetcherList.add(this::fetchWikiExtract);
        this.fetcherList.add(this::fetchMoeExtract);
        this.fetcherList.add(this::fetchBaikeExtract);
    }

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var text = context.text();
        if (!StringUtils.isEmpty(text) && Bot.BOT_100.equals(context.botKey())) {
            for (var fetcher : this.fetcherList) {
                String extract = fetcher.apply(text);
                if (extract != null) {
                    context.replyMessage(extract);
                    return;
                }
            }
            context.replyMessage("nano没有找到：" + text);
        } else {
            next.next();
        }
    }

    private String fetchWikiExtract(String title) {
        for (var language : List.of("zh", "en", "ja")) {
            var extract = this.wikiService.getPageExtract(title, language);
            if (extract != null) {
                return extract;
            }
        }
        return null;
    }

    private String fetchMoeExtract(String title) {
        return this.moeService.getPageExtract(title, "zh");
    }

    private String fetchBaikeExtract(String title) {
        return this.baikeService.getBaikeExtract(title);
    }
}
