package nano.support;

import nano.security.entity.NanoUser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class TestSqlUtils {

    @Test
    public void testUnderscoreName() {
        assertEquals("apple", SqlUtils.underscoreName("apple"));
        assertEquals("red_apple", SqlUtils.underscoreName("redApple"));
        assertEquals("big_red_apple", SqlUtils.underscoreName("bigRedApple"));
    }

    @Test
    public void testEntityColumnNames() {
        var names = SqlUtils.entityColumnNames(NanoUser.class);
        assertIterableEquals(List.of("firstname", "id", "is_bot", "language_code", "username"), names);
    }
}
