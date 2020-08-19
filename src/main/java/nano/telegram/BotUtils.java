package nano.telegram;

import org.springframework.util.StringUtils;

import java.util.Objects;

public class BotUtils {

    private BotUtils() {
    }

    public static String parseCommand(String command, String text) {
        Objects.requireNonNull(command);
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        var regex = "(?i)^" + command + " ";
        var split = text.split(regex);
        if (split.length < 2) {
            return null;
        }
        return split[1].trim();
    }
}
