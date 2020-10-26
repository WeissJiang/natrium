package nano.web.telegram;

import nano.support.Onion;
import nano.support.Onion.Middleware;
import nano.support.Sugar;
import nano.web.nano.ConfigVars;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Map;

import static nano.support.Sugar.*;

@Component
public class BotHandler implements ApplicationContextAware {

    private final Onion<BotContext> onion = new Onion<>();

    private ApplicationContext context;

    @PostConstruct
    public void init() {
        var middlewareMap = this.context.getBeansOfType(Middleware.class);
        Assert.notEmpty(middlewareMap, "middlewareMap is empty");
        var middlewares = new ArrayList<>(middlewareMap.values());
        AnnotationAwareOrderComparator.sort(middlewares);
        forEach(map(middlewares, Sugar::cast), this.onion::use);
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
