package nano.worker.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NanoConsumer {

    @RabbitListener(queuesToDeclare = @Queue("nano"))
    public void consume(Message message) {
        log.info(message.toString());
    }
}
