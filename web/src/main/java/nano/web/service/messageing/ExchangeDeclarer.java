package nano.web.service.messageing;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

import static nano.web.service.messageing.Exchanges.*;

/**
 * Declare exchanges on rabbit property set
 *
 * @see Exchanges
 * @see nano.web.Application#exchangeDeclarer
 */
@Slf4j
public class ExchangeDeclarer {

    @NonNull
    private AmqpAdmin amqpAdmin;

    @PostConstruct
    public void declareExchange() {
        for (var exchange : List.of(NANO, MAIL)) {
            this.amqpAdmin.declareExchange(new DirectExchange(exchange));
        }
    }

    @Autowired
    public void setAmqpAdmin(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }
}
