package nano.support;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTests {

    @Test
    public void testEncode() {
        var subJson = Json.encode(Map.of("foo", "bar"));
        var json = Json.encode(Map.of("baz", subJson));
        var expected = "{\"baz\":\"{\\\"foo\\\":\\\"bar\\\"}\"}";
        assertEquals(expected, json);
    }
}
