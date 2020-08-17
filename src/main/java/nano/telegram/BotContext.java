package nano.telegram;

import lombok.Data;
import nano.support.json.JsonObject;

@Data
public class BotContext {

    private JsonObject attributes = new JsonObject();

    private JsonObject parameters;

    public static BotContext create(JsonObject parameters) {
        var context = new BotContext();
        context.setParameters(parameters);
        return context;
    }
}
