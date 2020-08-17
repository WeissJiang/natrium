package nano.configuration;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.support.Onion;
import nano.telegram.BotContext;
import nano.telegram.BotHandler;
import nano.telegram.handler.EchoHandler;
import nano.telegram.handler.LogHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TelegramConfiguration {

    @NonNull
    private final LogHandler logHandler;

    @NonNull
    private final EchoHandler echoHandler;

    @Bean
    public BotHandler telegramBotHandler() {
        var onion = new Onion<BotContext>();
        onion.use(this.logHandler::handle);
        onion.use(this.echoHandler::handle);
        return onion::handle;
    }

}
