package nano.support;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EntityUtilsTests {

    @Test
    public void testUnderscoreName() {
        assertEquals("apple", EntityUtils.underscoreName("apple"));
        assertEquals("red_apple", EntityUtils.underscoreName("redApple"));
        assertEquals("big_red_apple", EntityUtils.underscoreName("bigRedApple"));
    }

    @Test
    public void testPropertyName() {
        assertEquals("apple", EntityUtils.propertyName("apple"));
        assertEquals("redApple", EntityUtils.propertyName("red_apple"));
        assertEquals("bigRedApple", EntityUtils.propertyName("big_red_apple"));
    }

    @Test
    public void testEntityColumnNames() {
        var names = EntityUtils.entityColumnNames(Apple.class);
        assertIterableEquals(List.of("color", "tastes"), names);
    }
}
