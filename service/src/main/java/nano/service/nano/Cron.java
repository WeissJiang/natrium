package nano.service.nano;

import nano.service.nano.repository.TaskRepository;
import nano.support.Json;
import nano.support.Task;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static nano.support.Sugar.forEach;
import static nano.support.Sugar.map;

@Profile("production")
@Component
public class Cron implements ApplicationContextAware {

    private ApplicationContext context;

    /**
     * Polling every 30 seconds
     */
    @Scheduled(fixedDelay = 30 * 1000)
    public void polling() {
        var taskList = this.getTaskList();
        forEach(taskList, Runnable::run);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    private List<Runnable> getTaskList() {
        var taskRepository = this.context.getBean(TaskRepository.class);
        var taskList = taskRepository.queryAllAvailableTaskList();
        return map(taskList, task -> () -> {
            var taskBean = this.context.getBean(task.name(), Task.class);
            taskBean.execute(Json.decodeValueAsMap(task.options()));
            taskRepository.updateLastExecutionTime(task.id(), Timestamp.from(Instant.now()));
        });
    }
}
