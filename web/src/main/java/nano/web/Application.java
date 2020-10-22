package nano.web;

import nano.support.configuration.ConditionalOnRabbit;
import nano.support.templating.SugarViewResolver;
import nano.support.validation.Validating;
import nano.support.validation.Validator;
import nano.web.messageing.ExchangeDeclarer;
import nano.web.nano.ConfigVars;
import nano.web.nano.ValidateInterceptor;
import nano.web.scripting.Scripting;
import nano.web.security.AuthenticationInterceptor;
import nano.web.security.Token;
import nano.web.security.TokenArgumentResolver;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Map;

@EnableAsync
@EnableScheduling
@SpringBootApplication(proxyBeanMethods = false)
public class Application implements ApplicationContextAware, WebMvcConfigurer {

    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
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
     * 校验切面
     *
     * @see Validating
     * @see Validator
     * @see ValidateInterceptor
     */
    @Bean
    public DefaultPointcutAdvisor validatePointcutAdvisor(ValidateInterceptor interceptor) {
        // advisor
        var advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(AnnotationMatchingPointcut.forMethodAnnotation(Validating.class));
        advisor.setAdvice(interceptor);
        return advisor;
    }

    /**
     * Declare exchanges on rabbit property set
     */
    @Bean
    @ConditionalOnRabbit
    public ExchangeDeclarer exchangeDeclarer() {
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
     * Add token argument resolver
     *
     * @see Token
     * @see TokenArgumentResolver
     */
    @Override
    public void addArgumentResolvers(@NotNull List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(this.context.getBean(TokenArgumentResolver.class));
    }

    /**
     * 为API增加鉴权
     *
     * @see nano.web.security.Authorized
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.context.getBean(AuthenticationInterceptor.class)).addPathPatterns("/api/**");
    }

    /**
     * 增加Scripting相关静态资源的media type
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer cnc) {
        var js = MediaType.parseMediaType(Scripting.TEXT_JAVASCRIPT);
        cnc.mediaTypes(Map.of("mjs", js, "jsx", js, "ts", js, "tsx", js, "less", js));
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        this.context = applicationContext;
    }
}
