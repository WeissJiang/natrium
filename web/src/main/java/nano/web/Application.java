package nano.web;

import nano.support.SimpleResourceLoader;
import nano.support.configuration.ConditionalOnRabbit;
import nano.web.security.AuthenticationInterceptor;
import nano.web.service.messageing.ExchangeDeclarer;
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
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.*;

import java.time.Duration;
import java.util.Objects;

@EnableAsync
@SpringBootApplication(proxyBeanMethods = false)
public class Application implements ApplicationContextAware, WebMvcConfigurer {

    private ApplicationContext applicationContext;

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
     * Rest template for sending HTTP request
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    /**
     * A simple resource loader
     */
    @Bean
    public SimpleResourceLoader simpleResourceLoader(ResourceLoader resourceLoader) {
        return new SimpleResourceLoader(resourceLoader);
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
     * 为API增加鉴权
     *
     * @see nano.web.security.Authorized
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        var interceptor = this.applicationContext.getBean(AuthenticationInterceptor.class);
        registry.addInterceptor(interceptor).addPathPatterns("/api/**");
    }

    /**
     * 增加Scripting相关静态资源的content type
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaTypes(Scripting.MEDIA_TYPE);
    }

    /**
     * 增加Scripting相关静态资源的resource handler
     *
     * @see WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter#addResourceHandlers
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        var resourceProperties = this.applicationContext.getBean(ResourceProperties.class);
        var cachePeriod = resourceProperties.getCache().getPeriod();
        var resourceChainProperties = resourceProperties.getChain();
        var cacheControl = resourceProperties.getCache().getCachecontrol().toHttpCacheControl();
        var registration = registry.addResourceHandler("/**/*.mjs", "/**/*.jsx", "/**/*.ts", "/**/*.tsx", "/**/*.less")
                .addResourceLocations(resourceProperties.getStaticLocations())
                .setCachePeriod(getSeconds(cachePeriod)).setCacheControl(cacheControl);
        this.configureResourceChain(registration, resourceChainProperties);
    }

    /**
     * 无论resource chain有没有enable，都增加一个用于处理Scripting相关静态资源编译的transformer
     * resource chain cache默认为true
     */
    private void configureResourceChain(ResourceHandlerRegistration registration,
                                        ResourceProperties.Chain resourceChainProperties) {
        var chainEnabled = Objects.requireNonNullElse(resourceChainProperties.getEnabled(), false);
        var cacheResource = chainEnabled && resourceChainProperties.isCache();
        var transformer = this.applicationContext.getBean(ScriptResourceTransformer.class);
        var sharedCache = transformer.getCache();
        var resourceChain = registration.resourceChain(cacheResource, sharedCache);
        resourceChain.addTransformer(transformer);
    }

    /**
     * Duration -> seconds
     */
    private static Integer getSeconds(Duration cachePeriod) {
        return (cachePeriod != null) ? (int) cachePeriod.getSeconds() : null;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
