package nano.worker;

import lombok.extern.slf4j.Slf4j;
import nano.support.configuration.ConditionalOnRabbit;
import nano.worker.consumer.MailConsumer;
import nano.worker.consumer.NanoConsumer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication(proxyBeanMethods = false)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Convert message to JSON
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * nano message consumer
     */
    @Bean
    @ConditionalOnRabbit
    public NanoConsumer nanoConsumer() {
        return new NanoConsumer();
    }

    /**
     * For consuming sending mail message
     */
    @Bean
    @ConditionalOnRabbit
    @ConditionalOnProperty("spring.mail.host")
    public MailConsumer mailConsumer() {
        return new MailConsumer();
    }

}
