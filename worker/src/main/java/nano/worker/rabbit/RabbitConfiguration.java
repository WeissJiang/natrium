package nano.worker.rabbit;

import nano.support.configuration.ConditionalOnRabbit;
import nano.worker.consumer.MailConsumer;
import nano.worker.consumer.NanoConsumer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Config Rabbit beans
 */
@ConditionalOnRabbit
@Configuration(proxyBeanMethods = false)
public class RabbitConfiguration {

    /**
     * nano message consumer
     */
    @Bean
    public NanoConsumer nanoConsumer() {
        return new NanoConsumer();
    }

    /**
     * For consuming sending mail message
     */
    @Bean
    @ConditionalOnProperty("spring.mail.host")
    public MailConsumer mailConsumer() {
        return new MailConsumer();
    }

    /**
     * Convert message to JSON
     *
     * @see org.springframework.amqp.support.converter.SimpleMessageConverter
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
