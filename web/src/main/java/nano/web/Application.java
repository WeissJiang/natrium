package nano.web;

import nano.web.security.AuthenticationInterceptor;
import nano.web.security.SecurityService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableAsync
@SpringBootApplication
public class Application implements ApplicationContextAware, WebMvcConfigurer {

    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder,ConfigVars configVars) {
        System.out.println(configVars);
        return builder.build();
    }

    @Bean
    @ConfigurationProperties("nano")
    public ConfigVars configVars() {
        return new ConfigVars();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        var ctx = this.applicationContext;
        var securityService = ctx.getBean(SecurityService.class);
        var interceptor = new AuthenticationInterceptor(securityService);
        var telegramApi = "/api/telegram/**";
        var telegramWebhookApi = "/api/telegram/webhook/*";
        // Telegram API interceptor, exclude Telegram webhook API
        registry.addInterceptor(interceptor).addPathPatterns(telegramApi).excludePathPatterns(telegramWebhookApi);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
