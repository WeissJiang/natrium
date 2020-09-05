package nano.support;

import nano.security.entity.NanoUser;
import nano.support.jdbc.SqlUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class SqlUtilsTest {

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
        var names = SqlUtils.entityColumnNames(NanoUser.class);
        assertIterableEquals(List.of("firstname", "id", "is_bot", "language_code", "username"), names);
    }
}
