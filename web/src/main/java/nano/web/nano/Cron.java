package nano.web.nano;

import nano.web.security.SecurityService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Profile("prod")
@Component
public class Cron implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(Cron.class);

    private ApplicationContext context;

    /**
     * Prune verifying timeout token
     * Fixed delay 24h
     */
    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
    public void pruneVerifyingTimeoutToken() {
        var count = this.context.getBean(SecurityService.class).pruneVerifyingTimeoutToken();
        log.info("Prune verifying timeout token: {}", count);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        this.context = applicationContext;
    }
}
