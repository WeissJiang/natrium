package nano;

import nano.component.ConfigVars;
import nano.security.AuthenticationInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Objects;

@SpringBootApplication
public class NanoApplication implements WebMvcConfigurer, ApplicationContextAware {

    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(NanoApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    @ConfigurationProperties("nano")
    public ConfigVars configVars() {
        return new ConfigVars();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        var ctx = Objects.requireNonNull(this.applicationContext);
        var interceptor = ctx.getBean(AuthenticationInterceptor.class);
        var telegramApi = "/api/telegram/**";
        var telegramWebhookApi = "/api/telegram/webhook*";
        // Telegram API interceptor, exclude Telegram webhook API
        registry.addInterceptor(interceptor).addPathPatterns(telegramApi).excludePathPatterns(telegramWebhookApi);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
