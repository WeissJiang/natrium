package nano.telegram.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nano.support.Onion;
import nano.support.json.JsonObject;
import nano.telegram.BotApi;
import nano.telegram.BotContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EchoHandler implements Onion.Middleware<BotContext> {

    @NonNull
    private final BotApi botApi;

    @Override
    public void via(BotContext context, Onion.Next next) throws Exception {
        var parameters = context.getParameters();
        var message = parameters.getJsonObject("message");
        var chatId = message.getJsonObject("chat").getInteger("id");
        var text = message.getString("text");

        // just echo
        var jo = new JsonObject()
                .put("chat_id", chatId)
                .put("text", text);
        var result = this.botApi.call("sendMessage", jo.getMap());

        var attributes = context.getAttributes();
        attributes.put("result", new JsonObject(result));
        next.next();
    }
}
