package nano.support.jdbc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SimpleJdbcSelectTests {

    @Test
    public void testGetSql() {
        var select = new SimpleJdbcSelect<>(Apple.class).withTableName("apple").whereEqual("id");
        assertEquals("SELECT id, name, producing_area FROM apple WHERE id = :id;", select.getSql(false));
        assertEquals("SELECT COUNT(*) FROM apple WHERE id = :id;", select.getSql(true));

        var select2 = new SimpleJdbcSelect<>(Apple.class).withTableName("apple")
                .whereClause("""
                        WHERE name LIKE CONCAT('%', :name , '%')
                        AND producing_area REGEX :producingArea
                        """);
        assertEquals("SELECT id, name, producing_area FROM apple WHERE name LIKE CONCAT('%', :name , '%') AND producing_area REGEX :producingArea;", select2.getSql(false));
    }
}
