package nano.web.telegram;

import nano.support.Pair;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * Telegram bot utils
 */
public abstract class BotUtils {

    private final static Pattern commandPattern = Pattern.compile("^/(?<cmd>\\w+)(\\s+(?<tail>(.|\n)*))?$");

    public static Pair<String, String> parseCommand(String text) {
        if (StringUtils.isEmpty(text)) {
            return Pair.empty();
        }
        var matcher = commandPattern.matcher(text.trim());
        if (!matcher.find()) {
            return Pair.empty();
        }
        var cmd = matcher.group("cmd");
        var tail = matcher.group("tail");
        return Pair.of(cmd, tail);
    }

    public static String parseCommand(String command, String text) {
        Assert.notNull(command, "command is null");
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
