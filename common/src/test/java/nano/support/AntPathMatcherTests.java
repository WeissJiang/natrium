package nano.support;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.util.AntPathMatcher;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class AntPathMatcherTests {

    @Test
    public void testMatch() {
        var matcher = new AntPathMatcher();
        var variables = matcher.extractUriTemplateVariables("/hello/{name}", "/hello/world");
        assertEquals(Map.of("name", "world"), variables);
    }
}
