package nano.web.telegram;

import lombok.SneakyThrows;
import nano.support.Onion;
import nano.web.telegram.handler.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class BotHandler implements ApplicationContextAware {

    private final Onion<BotContext> onion = new Onion<>();

    private ApplicationContext context;

    @PostConstruct
    public void init() {
        var ctx = this.context;
        this.onion.use(ctx.getBean(ExceptionHandler.class));
        this.onion.use(ctx.getBean(LogHandler.class));
        this.onion.use(ctx.getBean(SessionInitializeHandler.class));
        this.onion.use(ctx.getBean(VerificationHandler.class));
        // command handler
        var commandHandlers = ctx.getBeansOfType(AbstractCommandHandler.class);
        commandHandlers.values().forEach(this.onion::use);
        // start handler
        this.onion.use(ctx.getBean(HelpHandler.class));
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
        var ctx = this.context;
        var telegramService = ctx.getBean(TelegramService.class);
        context.setTelegramService(telegramService);
        return context;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        this.context = applicationContext;
    }
}
