package nano.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class WebApplicationTests {

    @Autowired
    public ApplicationContext context;

    @Test
    void contextLoads() {
    }

}
