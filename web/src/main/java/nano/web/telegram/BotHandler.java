package nano.web.telegram;

import nano.support.Onion;
import nano.support.Onion.Middleware;
import nano.support.Sugar;
import nano.web.nano.ConfigVars;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Map;

import static nano.support.Onion.compose;
import static nano.support.Sugar.cast;

@Component
public class BotHandler implements ApplicationContextAware {

    private final Onion<BotContext> onion = new Onion<>();

    private ApplicationContext context;

    @PostConstruct
    public void init() {
        var middlewares = this.getSortedMiddlewares();
        Assert.notEmpty(middlewares, "middlewares is empty");
        this.onion.use(compose(middlewares));
    }

    /**
     * Get sorted BotContext Middleware array from context
     */
    private @NotNull Middleware<BotContext>[] getSortedMiddlewares() {
        return this.context.getBeansOfType(Middleware.class).values().stream()
                .filter(m -> ResolvableType.forClass(m.getClass()).as(Middleware.class).getGeneric(0).getRawClass() == BotContext.class)
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .map(Sugar::<Middleware<BotContext>>cast)
                .toArray(len -> cast(new Middleware[len]));
    }

    @Async
    public void handleAsync(@NotNull String botName, Map<String, ?> parameters) throws Exception {
        this.internalHandle(this.buildContext(botName, parameters));
    }

    public void handle(@NotNull String botName, Map<String, ?> parameters) throws Exception {
        this.internalHandle(this.buildContext(botName, parameters));
    }

    /**
     * handle context
     */
    private void internalHandle(@NotNull BotContext context) throws Exception {
        this.onion.handle(context);
    }

    /**
     * build context
     */
    private @NotNull BotContext buildContext(@NotNull String botName, Map<String, ?> parameters) {
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
