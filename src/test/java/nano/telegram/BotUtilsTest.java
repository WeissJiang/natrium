package nano.telegram;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BotUtilsTest {

    @Test
    public void testParseCommand() {
        assertEquals("hello world", BotUtils.parseCommand("babel", "babel hello world"));
        assertEquals("hello world", BotUtils.parseCommand("babel", "BaBeL hello world"));
        assertEquals("hello babel", BotUtils.parseCommand("babel", "babel hello babel"));
        assertEquals("哈啰👋", BotUtils.parseCommand("babel", "babel 哈啰👋"));
        assertNull(BotUtils.parseCommand("babel", ""));
        assertNull(BotUtils.parseCommand("babel", "hello world"));
    }
}
