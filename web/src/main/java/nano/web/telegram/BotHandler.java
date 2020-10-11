package nano.web.telegram;

import nano.support.Onion;
import nano.web.nano.ConfigVars;
import nano.web.telegram.handler.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

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
        // start handler
        onion.use(ctx.getBean(StartHandler.class));
        // function handlers
        onion.use(ctx.getBean(Nano026Handler.class));
        onion.use(ctx.getBean(Nano100Handler.class));
        onion.use(ctx.getBean(Nano262Handler.class));
        onion.use(ctx.getBean(VerificationHandler.class));
        // command handler
        var commandHandlers = ctx.getBeansOfType(AbstractCommandHandler.class);
        commandHandlers.values().forEach(onion::use);

    }

    @Async
    public void handleAsync(String botName, Map<String, ?> parameters) throws Exception {
        this.internalHandle(this.buildContext(botName, parameters));
    }

    public void handle(String botName, Map<String, ?> parameters) throws Exception {
        this.internalHandle(this.buildContext(botName, parameters));
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
    private BotContext buildContext(String botName, Map<String, ?> parameters) {
        var ctx = this.context;
        var bot = ctx.getBean(ConfigVars.class).getBots().get(botName);
        Assert.notNull(bot, "No matching Bot found");
        var context = new BotContext(bot, parameters);
        // build context
        var telegramService = ctx.getBean(TelegramService.class);
        context.setTelegramService(telegramService);
        return context;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        this.context = applicationContext;
    }
}
