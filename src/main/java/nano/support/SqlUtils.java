package nano.support;

import lombok.NonNull;

import java.util.regex.Pattern;

public abstract class SqlUtils {

    private static final Pattern slimPattern = Pattern.compile("\\s+");

    public static String slim(@NonNull String sql) {
        return slimPattern.matcher(sql).replaceAll(" ");
    }
}
