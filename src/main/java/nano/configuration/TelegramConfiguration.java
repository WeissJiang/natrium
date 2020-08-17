package nano.configuration;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.support.Onion;
import nano.telegram.BotContext;
import nano.telegram.BotHandler;
import nano.telegram.handler.EchoHandler;
import nano.telegram.handler.LogHandler;
import nano.telegram.handler.WikiHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TelegramConfiguration {

    @NonNull
    private final ApplicationContext applicationContext;

    @Bean
    public BotHandler telegramBotHandler() {
        var onion = new Onion<BotContext>();
        var clazzList = List.of(
                LogHandler.class,
                WikiHandler.class,
                EchoHandler.class
        );
        for (var clazz : clazzList) {
            var handler = this.applicationContext.getBean(clazz);
            onion.use(handler);
        }
        return onion::handle;
    }
}
