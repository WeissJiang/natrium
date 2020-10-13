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
import java.util.Map;
import java.util.function.Function;

@Component
public class Nano100Handler implements Onion.Middleware<BotContext> {

    private final MoeService moeService;
    private final WikiService wikiService;
    private final BaikeService baikeService;

    private final List<Function<String, String>> fetcherList = new ArrayList<>();

    public Nano100Handler(MoeService moeService, WikiService wikiService, BaikeService baikeService) {
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
        if (Bot.NANO_100.equals(context.bot().getName())) {
            this.fetchAndSendExtract(context);
        } else {
            next.next();
        }
    }

    private void fetchAndSendExtract(BotContext context) {
        var text = context.text();
        if (StringUtils.isEmpty(text)) {
            context.sendMessage("⚠️The title is empty, please input title");
            return;
        }
        for (var fetcher : this.fetcherList) {
            String extract = fetcher.apply(text);
            if (extract != null) {
                replyMessageWithoutPreview(context, extract);
                return;
            }
        }
        context.replyMessage("nano没有找到：" + text);
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

    private static void replyMessageWithoutPreview(BotContext context, String text) {
        var payload = Map.of(
                "chat_id", context.chatId(),
                "reply_to_message_id", context.messageId(),
                "disable_web_page_preview", true,
                "text", text
        );
        context.getTelegramService().sendMessage(context.bot(), payload);
    }
}
