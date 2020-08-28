package nano.telegram;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.support.Onion;
import nano.telegram.handler.ExceptionHandler;
import nano.telegram.handler.LogHandler;
import nano.telegram.handler.text.BabelHandler;
import nano.telegram.handler.text.EvalHandler;
import nano.telegram.handler.text.FoolHandler;
import nano.telegram.handler.text.WikiHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class BotHandler {

    private final Onion<BotContext> onion = new Onion<>();

    @NonNull
    private final ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        var ctx = Objects.requireNonNull(this.applicationContext);
        this.onion.use(ctx.getBean(ExceptionHandler.class));
        this.onion.use(ctx.getBean(LogHandler.class));
        // text message
        var textMessageHandler = Onion.compose(
                ctx.getBean(BabelHandler.class),
                ctx.getBean(WikiHandler.class),
                ctx.getBean(EvalHandler.class),
                ctx.getBean(FoolHandler.class)
        );
        this.onion.use(textMessageHandler);
    }

    public void handle(BotContext context) throws Exception {
        this.onion.handle(context);
    }

}
