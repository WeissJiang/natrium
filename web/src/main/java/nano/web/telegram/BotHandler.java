package nano.web.telegram;

import lombok.SneakyThrows;
import nano.support.Onion;
import nano.web.telegram.handler.*;
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
        // command handler
        var commandHandlers = ctx.getBeansOfType(AbstractCommandHandler.class);
        commandHandlers.values().forEach(this.onion::use);
        // start handler
        this.onion.use(ctx.getBean(StartHandler.class));
    }

    @Async
    public void handleAsync(Map<String, Object> parameters) {
        this.internalHandle(this.buildContext(parameters));
    }

    public void handle(Map<String, Object> parameters) {
        this.internalHandle(this.buildContext(parameters));
    }

    /**
     * handle context
     */
    @SneakyThrows
    private void internalHandle(BotContext context) {
        this.onion.handle(context);
    }

    /**
     * build context
     */
    private BotContext buildContext(Map<String, Object> parameters) {
        var context = new BotContext(parameters);
        // build context
        var ctx = this.applicationContext;
        var telegramService = ctx.getBean(TelegramService.class);
        context.setTelegramService(telegramService);
        return context;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
