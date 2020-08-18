package nano.configuration;

import lombok.extern.slf4j.Slf4j;
import nano.support.Onion;
import nano.telegram.BotContext;
import nano.telegram.BotHandler;
import nano.telegram.handler.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TelegramConfiguration {

    @Bean
    public BotHandler telegramBotHandler(ApplicationContext ctx) {
        var exceptionHandler = ctx.getBean(ExceptionHandler.class);
        var logHandler = ctx.getBean(LogHandler.class);
        var babelHandler = ctx.getBean(BabelHandler.class);
        var wikiHandler = ctx.getBean(WikiHandler.class);
        var foolHandler = ctx.getBean(FoolHandler.class);

        var onion = new Onion<BotContext>();
        onion.use(exceptionHandler);
        onion.use(logHandler);
        onion.use(babelHandler);
        onion.use(wikiHandler);
        onion.use(foolHandler);

        return onion::handle;
    }
}
