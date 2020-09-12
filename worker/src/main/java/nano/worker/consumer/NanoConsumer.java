package nano.worker.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

@Slf4j
public class NanoConsumer {

    @RabbitListener(queuesToDeclare = @Queue("nano"))
    public void consume(String message) {
        log.info(message);
    }
}
