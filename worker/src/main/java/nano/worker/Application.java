package nano.worker;

import lombok.extern.slf4j.Slf4j;
import nano.support.configuration.ConditionalOnRabbitProperty;
import nano.worker.consumer.MailConsumer;
import nano.worker.consumer.NanoConsumer;
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
     * nano message consumer
     */
    @Bean
    @ConditionalOnRabbitProperty
    public NanoConsumer nanoConsumer() {
        return new NanoConsumer();
    }

    /**
     * For consuming sending mail message
     */
    @Bean
    @ConditionalOnRabbitProperty
    @ConditionalOnProperty("spring.mail.host")
    public MailConsumer mailConsumer() {
        return new MailConsumer();
    }

}
