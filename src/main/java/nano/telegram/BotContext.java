package nano.telegram;

import lombok.Data;
import nano.support.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

@Data
public class BotContext {

    private Map<String, Object> attributes = new HashMap<>();

    private JsonObject parameters;

    public static BotContext create(JsonObject parameters) {
        var context = new BotContext();
        context.setParameters(parameters);
        return context;
    }

}
