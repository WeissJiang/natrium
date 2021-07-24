package nano.service.messageing;

import nano.service.ServiceApplication;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Declare exchanges on rabbit property set
 *
 * @see Exchanges
 * @see ServiceApplication#exchangeDeclarer
 */
public class ExchangeDeclarer {

    private AmqpAdmin amqpAdmin;

    @PostConstruct
    public void declareExchange() {
        Assert.notNull(this.amqpAdmin, "this.amqpAdmin is null");
        for (var exchange : List.of(Exchanges.NANO, Exchanges.MAIL)) {
            this.amqpAdmin.declareExchange(new DirectExchange(exchange));
        }
    }

    @Autowired
    public void setAmqpAdmin(@NotNull AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }
}
