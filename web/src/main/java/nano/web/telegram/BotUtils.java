package nano.web.telegram;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Telegram bot utils
 */
public abstract class BotUtils {

    public static String parseCommand(String command, String text) {
        Assert.notNull(command, "command");
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
