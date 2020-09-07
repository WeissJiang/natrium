package nano.web.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmqpService {

    @NonNull
    private final AmqpAdmin amqpAdmin;

    @NonNull
    private final RabbitMessagingTemplate rabbitMessagingTemplate;


    @PostConstruct
    public void init() {
        if (this.brokerNotDefined()) {
            return;
        }
        this.amqpAdmin.declareExchange(new DirectExchange("nano"));
    }

    public void send(String exchange, String routingKey, Object payload) {
        this.rabbitMessagingTemplate.convertAndSend(exchange, routingKey, payload);
    }

    private boolean brokerNotDefined() {
        var host = this.rabbitMessagingTemplate.getRabbitTemplate().getConnectionFactory().getHost();
        return "?".equals(host);
    }
}
