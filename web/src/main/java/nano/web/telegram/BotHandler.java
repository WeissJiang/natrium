package nano.web.telegram;

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
        var onion = this.onion;
        // pre handle handlers
        onion.use(ctx.getBean(ExceptionHandler.class));
        onion.use(ctx.getBean(LogHandler.class));
        onion.use(ctx.getBean(SessionInitializeHandler.class));
        onion.use(ctx.getBean(AuthenticationHandler.class));
        // function handlers
        onion.use(ctx.getBean(Bot026Handler.class));
        onion.use(ctx.getBean(Bot100Handler.class));
        onion.use(ctx.getBean(VerificationHandler.class));
        // command handler
        var commandHandlers = ctx.getBeansOfType(AbstractCommandHandler.class);
        commandHandlers.values().forEach(onion::use);
        // start handler
        onion.use(ctx.getBean(HelpHandler.class));
    }

    @Async
    public void handleAsync(String botKey, Map<String, ?> parameters) throws Exception {
        this.internalHandle(this.buildContext(botKey, parameters));
    }

    public void handle(String botKey, Map<String, ?> parameters) throws Exception {
        this.internalHandle(this.buildContext(botKey, parameters));
    }

    /**
     * handle context
     */
    private void internalHandle(BotContext context) throws Exception {
        this.onion.handle(context);
    }

    /**
     * build context
     */
    private BotContext buildContext(String botKey, Map<String, ?> parameters) {
        var context = new BotContext(botKey, parameters);
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
