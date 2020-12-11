package nano.web.telegram;

import nano.support.Json;
import nano.support.Onion;
import nano.web.nano.model.Bot;
import nano.web.security.SessionService;
import nano.web.telegram.handler.StartHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BotHandlerTests {

    @Autowired
    public BotHandler botHandler;

    @MockBean
    public StartHandler startHandler;

    @MockBean
    public SessionService sessionService;

    @Test
    public void testHandleStartCommand() throws Exception {
        var payload = """
                {
                    "update_id": 1,
                    "message": {
                        "message_id": 1,
                        "from": {
                            "id": 1,
                            "is_bot": false,
                            "first_name": "first_name",
                            "username": "username",
                            "language_code": "zh-hans"
                        },
                        "chat": {
                            "id": 1,
                            "first_name": "first_name",
                            "username": "username",
                            "type": "private"
                        },
                        "date": 0,
                        "text": "/start",
                        "entities": [
                            {
                                "offset": 0,
                                "length": 6,
                                "type": "bot_command"
                            }
                        ]
                    }
                }
                """;
        doAnswer(invocation -> {
            var context = invocation.getArgument(0, BotContext.class);
            assertTrue(context.commands().contains("/start"));
            var next = invocation.getArgument(1, Onion.Next.class);
            next.next();
            return null;
        }).when(this.startHandler).via(Mockito.any(), Mockito.any());
        // test handle
        this.botHandler.handle(Bot.NANO, Json.decodeValueAsMap(payload));
        verify(this.startHandler, times(1)).via(Mockito.any(), Mockito.any());
    }
}
