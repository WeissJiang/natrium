package nano.web.messageing;

import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.List;

import static nano.web.messageing.Exchanges.MAIL;
import static nano.web.messageing.Exchanges.NANO;

/**
 * Declare exchanges on rabbit property set
 *
 * @see Exchanges
 * @see nano.web.Application#exchangeDeclarer
 */
public class ExchangeDeclarer {

    private AmqpAdmin amqpAdmin;

    @PostConstruct
    public void declareExchange() {
        Assert.notNull(this.amqpAdmin, "this.amqpAdmin is null");
        for (var exchange : List.of(NANO, MAIL)) {
            this.amqpAdmin.declareExchange(new DirectExchange(exchange));
        }
    }

    @Autowired
    public void setAmqpAdmin(@NotNull AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }
}
