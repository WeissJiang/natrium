package nano.web.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.support.mail.TextMail;
import nano.web.security.AuthenticationInterceptor;
import nano.web.security.Authorized;
import nano.web.messageing.Exchanges;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Send mail
 *
 * @see AuthenticationInterceptor
 */
@Slf4j
@Authorized
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {

    @NonNull
    private final RabbitMessagingTemplate rabbitMessagingTemplate;

    @PostMapping("/sendTextMail")
    public ResponseEntity<?> sendTextMail(@RequestBody TextMail mail) {
        this.rabbitMessagingTemplate.convertAndSend(Exchanges.MAIL, "text", mail);
        return ResponseEntity.ok("OK");
    }
}
