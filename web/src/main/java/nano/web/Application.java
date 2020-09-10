package nano.web;

import nano.support.SimpleResourceLoader;
import nano.web.security.AuthenticationInterceptor;
import nano.web.security.SecurityService;
import nano.web.service.scripting.ScriptResourceTransformer;
import nano.web.service.scripting.Scripting;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@EnableAsync
@SpringBootApplication
public class Application implements ApplicationContextAware, WebMvcConfigurer {

    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
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

    @Bean
    public SimpleResourceLoader simpleResourceLoader(ResourceLoader resourceLoader) {
        return new SimpleResourceLoader(resourceLoader);
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
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaTypes(Scripting.MEDIA_TYPE);
    }

    /**
     * @see WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter#addResourceHandlers
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        var ctx = this.applicationContext;
        var resourceProperties = ctx.getBean(ResourceProperties.class);
        var cachePeriod = resourceProperties.getCache().getPeriod();
        var cacheControl = resourceProperties.getCache().getCachecontrol().toHttpCacheControl();
        var registration = registry.addResourceHandler("/**/*.mjs", "/**/*.jsx", "/**/*.ts", "/**/*.tsx", "/**/*.less")
                .addResourceLocations(resourceProperties.getStaticLocations())
                .setCachePeriod(getSeconds(cachePeriod)).setCacheControl(cacheControl);
        var resourceChain = registration.resourceChain(true);
        resourceChain.addTransformer(ctx.getBean(ScriptResourceTransformer.class));
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private static Integer getSeconds(Duration cachePeriod) {
        return (cachePeriod != null) ? (int) cachePeriod.getSeconds() : null;
    }


}
