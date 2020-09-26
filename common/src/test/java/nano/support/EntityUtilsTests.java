package nano.support;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

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

    public static class Apple {

        private String color;
        private String tastes;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getTastes() {
            return tastes;
        }

        public void setTastes(String tastes) {
            this.tastes = tastes;
        }
    }
}
