package nano.web.telegram.handler.command;

import nano.web.baidu.BaikeService;
import nano.web.telegram.BotContext;
import nano.web.telegram.handler.AbstractCommandHandler;
import org.springframework.stereotype.Service;

/**
 * 百度百科
 */
@Service
public class BaikeHandler extends AbstractCommandHandler {

    private final BaikeService baikeService;

    public BaikeHandler(BaikeService baikeService) {
        this.baikeService = baikeService;
    }

    @Override
    public void handle(BotContext context, String title) {
        var extract = this.baikeService.getBaikeExtract(title);
        if (extract == null) {
            extract = "nano没有找到：" + title;
        }
        context.sendMessage(extract);
    }

    @Override
    protected String command() {
        return "baike";
    }

    @Override
    protected String help() {
        return """
                Usage: /baike {title}
                """;
    }
}
