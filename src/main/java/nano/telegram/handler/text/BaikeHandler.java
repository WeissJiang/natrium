package nano.telegram.handler.text;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.service.baidu.BaikeService;
import nano.support.Onion;
import nano.telegram.BotContext;
import nano.telegram.BotUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BaikeHandler implements Onion.Middleware<BotContext> {

    private static final String COMMAND = "baike";

    @NonNull
    private final BaikeService baikeService;

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var text = context.text();

        var content = BotUtils.parseCommand(COMMAND, text);
        if (StringUtils.isEmpty(content)) {
            next.next();
            return;
        }

        var extract = this.baikeService.getBaikeExtract(content);
        if (extract == null) {
            extract = "nano没有找到：" + content;
        }
        context.sendMessage(extract);
    }
}
