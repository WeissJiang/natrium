package nano.telegram;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
public class BotHandlerTest {

    @Autowired
    public BotHandler botHandler;

    @Test
    public void testHandle() throws Exception {
        var context = new BotContext(new HashMap<>());
        this.botHandler.handle(context);
    }
}
