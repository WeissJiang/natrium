package nano.web.telegram;

import nano.support.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BotUtilsTests {

    @Test
    public void testParseCommand() {
        assertEquals(Pair.of("babel", "hello world"), BotUtils.parseCommand("/babel hello world"));
        assertEquals(Pair.of("babel", "hello world"), BotUtils.parseCommand(" /babel  hello world"));
        assertEquals(Pair.of("babel", "hello world"), BotUtils.parseCommand("/babel \r\nhello world\r\n"));
        assertEquals(Pair.of("babel", "hello\nworld"), BotUtils.parseCommand(" /babel hello\nworld "));
        assertEquals(Pair.ofLeft("babel"), BotUtils.parseCommand(" /babel  "));
        assertEquals(Pair.empty(), BotUtils.parseCommand("/babel?hello"));
    }

    @Test
    public void testParseCommand2() {
        assertEquals("hello world", BotUtils.parseCommand("babel", "/babel hello world"));
        assertEquals("hello world", BotUtils.parseCommand("babel", "babel hello world"));
        assertEquals("hello world", BotUtils.parseCommand("babel ", " babel  hello world"));
        assertEquals("hello world", BotUtils.parseCommand("babel", "BaBeL hello world"));
        assertEquals("hello babel", BotUtils.parseCommand("babel", "babel hello babel"));
        assertEquals("哈啰👋", BotUtils.parseCommand("babel", "babel 哈啰👋"));
        assertNull(BotUtils.parseCommand("babel", ""));
        assertNull(BotUtils.parseCommand("babel", "hello world"));
    }
}
