package nano.telegram;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.support.Onion;
import nano.telegram.handler.ExceptionHandler;
import nano.telegram.handler.LogHandler;
import nano.telegram.handler.text.BabelHandler;
import nano.telegram.handler.text.FoolHandler;
import nano.telegram.handler.text.WikiHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

import static nano.support.Sugar.reduce;

@Component
@RequiredArgsConstructor
public class BotHandler {

    @NonNull
    private final ExceptionHandler exceptionHandler;
    @NonNull
    private final LogHandler logHandler;

    // text message

    @NonNull
    private final BabelHandler babelHandler;
    @NonNull
    private final WikiHandler wikiHandler;
    @NonNull
    private final FoolHandler foolHandler;

    private final Onion<BotContext> onion = new Onion<>();

    @PostConstruct
    public void init() {
        this.onion.use(this.exceptionHandler);
        this.onion.use(this.logHandler);
        // text message
        this.onion.use(this.textMessageHandler());
    }

    private Onion.Middleware<BotContext> textMessageHandler() {
        var handlerList = List.of(
                this.babelHandler,
                this.wikiHandler,
                this.foolHandler
        );
        return reduce(handlerList, (ctx, nxt) -> nxt.next(), Onion::compose);
    }

    public void handle(BotContext context) throws Exception {
        this.onion.handle(context);
    }
}
