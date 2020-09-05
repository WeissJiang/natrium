package nano.web.telegram;

import lombok.SneakyThrows;
import nano.support.Onion;
import nano.web.telegram.handler.ExceptionHandler;
import nano.web.telegram.handler.LogHandler;
import nano.web.telegram.handler.SessionInitializeHandler;
import nano.web.telegram.handler.text.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class BotHandler implements ApplicationContextAware {

    private final Onion<BotContext> onion = new Onion<>();

    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        var ctx = this.applicationContext;
        this.onion.use(ctx.getBean(ExceptionHandler.class));
        this.onion.use(ctx.getBean(LogHandler.class));
        this.onion.use(ctx.getBean(SessionInitializeHandler.class));
        // text message
        var textMessageHandler = Onion.compose(
                ctx.getBean(BabelHandler.class),
                ctx.getBean(WikiHandler.class),
                ctx.getBean(MoeHandler.class),
                ctx.getBean(BaikeHandler.class),
                ctx.getBean(EvalHandler.class),
                ctx.getBean(StartHandler.class)
        );
        this.onion.use(textMessageHandler);
    }

    @Async
    @SneakyThrows
    public void handle(Map<String, Object> parameters) {
        var context = new BotContext(parameters);
        // build context
        var ctx = this.applicationContext;
        var telegramService = ctx.getBean(TelegramService.class);
        context.setTelegramService(telegramService);
        // handle context
        this.onion.handle(context);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
