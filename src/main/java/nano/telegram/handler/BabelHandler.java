package nano.telegram.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.service.BaiduService;
import nano.support.Onion;
import nano.telegram.BotApi;
import nano.telegram.BotContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class BabelHandler implements Onion.Middleware<BotContext> {

    private static final String PREFIX = "babel ";

    @NonNull
    private final BotApi botApi;

    @NonNull
    private final BaiduService baiduService;

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        Integer chatId = context.readParameter("$.message.chat.id");
        String text = context.readParameter("$.message.text");

        if (StringUtils.isEmpty(text) || !text.startsWith(PREFIX)) {
            next.next();
            return;
        }
        var content = text.substring(PREFIX.length());
        var translated = this.baiduService.autoTranslate(content);
        this.botApi.sendMessage(chatId, translated);
    }
}
