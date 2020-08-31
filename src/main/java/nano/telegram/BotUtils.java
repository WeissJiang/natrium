package nano.telegram;

import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Telegram bot utils
 */
public class BotUtils {

    private BotUtils() {
    }

    public static String parseCommand(String command, String text) {
        Objects.requireNonNull(command);
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        var regex = "(?i)^/?" + command.trim() + "\\s";
        var split = text.trim().split(regex);
        if (split.length < 2) {
            return null;
        }
        return split[1].trim();
    }
}
