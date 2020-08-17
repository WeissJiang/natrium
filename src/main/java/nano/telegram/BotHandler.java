package nano.telegram;

public interface BotHandler {

    void handle(BotContext context) throws Exception;
}
