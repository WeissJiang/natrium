package nano.web.util;

import ua_parser.Parser;

import java.util.Objects;

public class UserAgentParserModule {

    private static final Parser parser = new Parser();

    public static String parseToString(String s) {
        return Objects.requireNonNull(parser.parse(s)).toString();
    }
}
