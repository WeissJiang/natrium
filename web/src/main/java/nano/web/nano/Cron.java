package nano.web.nano;

import nano.support.Task;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Collections.emptyMap;
import static nano.support.Sugar.forEach;
import static nano.support.Sugar.map;

@Profile("prod")
@Component
public class Cron implements ApplicationContextAware {

    private ApplicationContext context;

    /**
     * Polling
     * <p>
     * Fixed delay 24h
     */
    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
    public void polling() {
        var taskList = this.getTaskList();
        forEach(taskList, Runnable::run);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    private List<Runnable> getTaskList() {
        var tasks = List.of("pruneVerifyingTimeoutTokenTask");
        return map(tasks, name -> () -> {
            var task = this.context.getBean(name, Task.class);
            task.execute(emptyMap());
        });
    }
}
