package nano.telegram.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.support.Onion;
import nano.support.json.Json;
import nano.support.json.JsonObject;
import nano.telegram.BotApi;
import nano.telegram.BotContext;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EchoHandler {

    @NonNull
    private final BotApi botApi;

    public void handle(BotContext context, Onion.Next next) throws Exception {
        var parameters = context.getParameters();
        var message = parameters.getJsonObject("message");
        var chatId = message.getJsonObject("chat").getInteger("id");
        var originalText = message.getString("text");

        var text = "nano: " + originalText;
        // just echo
        var sendParameters = new JsonObject().put("chat_id", chatId).put("text", text);
        var result = this.botApi.call("sendMessage", sendParameters.getMap());
        // log
        log.info("echo result: {}", Json.encode(result));
        // next
        next.next();
    }
}
