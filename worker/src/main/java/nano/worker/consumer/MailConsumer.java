package nano.worker.consumer;

import lombok.extern.slf4j.Slf4j;
import nano.support.Json;
import nano.support.mail.TextMail;
import nano.worker.service.MailService;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class MailConsumer {

    private MailService mailService;

    @RabbitListener(queuesToDeclare = @Queue("mail.text"))
    public void consume(TextMail mail) {
        log.info("send mail: {}", Json.encode(mail));
        this.mailService.sendTextMail(mail);
    }

    @Autowired
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }
}
