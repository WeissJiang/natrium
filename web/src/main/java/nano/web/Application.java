package nano.web;

import nano.support.configuration.ConditionalOnRabbit;
import nano.support.templating.SugarViewResolver;
import nano.web.messageing.ExchangeDeclarer;
import nano.web.nano.ConfigVars;
import nano.web.scripting.Scripting;
import nano.web.security.AuthenticationInterceptor;
import nano.web.security.TokenDesensitizationInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.List;

@EnableAsync
@EnableScheduling
@SpringBootApplication(proxyBeanMethods = false)
public class Application implements ApplicationContextAware, WebMvcConfigurer {

    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Templating
     *
     * @see nano.support.Sugar#render
     */
    @Bean
    public SugarViewResolver sugarViewResolver() {
        var resolver = new SugarViewResolver();
        resolver.setPrefix("classpath:/templates/");
        return resolver;
    }

    /**
     * App config vars
     */
    @Bean
    @ConfigurationProperties("nano")
    public ConfigVars configVars() {
        return new ConfigVars();
    }

    /**
     * Cache resources with ETag
     */
    @Bean
    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    /**
     * Rest template for sending HTTP request
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    /**
     * Declare exchanges on rabbit property set
     */
    @Bean
    @ConditionalOnRabbit
    public ExchangeDeclarer exchangeDeclarer(Environment env) {
        return new ExchangeDeclarer();
    }

    /**
     * Convert message to JSON
     */
    @Bean
    @ConditionalOnRabbit
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 为API增加鉴权
     *
     * @see nano.web.security.Authorized
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.context.getBean(TokenDesensitizationInterceptor.class)).order(1).addPathPatterns("/api/**");
        registry.addInterceptor(this.context.getBean(AuthenticationInterceptor.class)).order(2).addPathPatterns("/api/**");
    }

    /**
     * 增加Scripting相关静态资源的media type
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer cnc) {
        var mediaTypes = new HashMap<String, MediaType>();
        var javaScript = MediaType.parseMediaType(Scripting.TEXT_JAVASCRIPT);
        List.of("mjs", "jsx", "ts", "tsx", "less").forEach(ext -> mediaTypes.put(ext, javaScript));
        cnc.mediaTypes(mediaTypes);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

}
