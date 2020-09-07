package nano.worker;

import nano.worker.consumer.NanoConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class ApplicationTests {

    @Autowired
    public ApplicationContext context;

    @MockBean
    public NanoConsumer nanoConsumer;

    @Test
    void contextLoads() {
    }

}
