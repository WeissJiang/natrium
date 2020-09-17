package nano.web;

import lombok.extern.slf4j.Slf4j;
import nano.web.security.SecurityService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("prod")
@Component
public class Cron implements ApplicationContextAware {

    private ApplicationContext context;

    /**
     * Fixed delay 24h
     */
    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
    public void pruneVerificatingTimeoutToken() {
        var count = this.context.getBean(SecurityService.class).pruneVerificatingTimeoutToken();
        log.info("Prune verificating timeout token: {}", count);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        this.context = applicationContext;
    }
}
