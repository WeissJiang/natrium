package nano.support.jdbc;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SqlUtilsTests {

    @Test
    public void testUnderscoreName() {
        assertEquals("apple", SqlUtils.underscoreName("apple"));
        assertEquals("red_apple", SqlUtils.underscoreName("redApple"));
        assertEquals("big_red_apple", SqlUtils.underscoreName("bigRedApple"));
    }

    @Test
    public void testPropertyName() {
        assertEquals("apple", SqlUtils.propertyName("apple"));
        assertEquals("redApple", SqlUtils.propertyName("red_apple"));
        assertEquals("bigRedApple", SqlUtils.propertyName("big_red_apple"));
    }

    @Test
    public void testEntityColumnNames() {
        var names = SqlUtils.entityColumnNames(Apple.class);
        assertIterableEquals(List.of("color", "tastes"), names);
    }
}
