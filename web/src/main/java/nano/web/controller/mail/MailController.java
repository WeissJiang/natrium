package nano.web.controller.mail;

import nano.support.mail.TextMail;
import nano.web.messageing.Exchanges;
import nano.web.security.AuthenticationInterceptor;
import nano.web.security.Authorized;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static nano.web.security.NanoPrivilege.NANO_API;

/**
 * Send mail
 *
 * @see AuthenticationInterceptor
 */
@CrossOrigin
@RestController
@RequestMapping("/api/mail")
public class MailController {

    private final RabbitMessagingTemplate rabbitMessagingTemplate;

    public MailController(RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    @Authorized(NANO_API)
    @PostMapping("/sendTextMail")
    public ResponseEntity<?> sendTextMail(@RequestBody TextMail mail) {
        this.rabbitMessagingTemplate.convertAndSend(Exchanges.MAIL, "text", mail);
        return ResponseEntity.ok("OK");
    }
}
