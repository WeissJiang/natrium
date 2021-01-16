package nano.web.controller.nano;

import nano.support.validation.Validated;
import nano.support.Result;
import nano.web.messageing.Exchanges;
import nano.web.nano.NanoService;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/nano")
public class NanoController {

    private final RabbitMessagingTemplate messagingTemplate;

    private final NanoService nanoService;

    public NanoController(RabbitMessagingTemplate messagingTemplate, NanoService nanoService) {
        this.messagingTemplate = messagingTemplate;
        this.nanoService = nanoService;
    }

    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok("pong\n");
    }

    @GetMapping("/system")
    public ResponseEntity<?> system() {
        var nano = this.nanoService.system();
        return ResponseEntity.ok(nano);
    }

    @GetMapping("/beans")
    public ResponseEntity<?> beans() {
        var beans = this.nanoService.getBeanDefinitionNames();
        return ResponseEntity.ok(beans);
    }

    @Validated(NanoMessageValidator.class)
    @PostMapping("/message")
    public ResponseEntity<?> message(@RequestParam("m") String m) {
        this.messagingTemplate.convertAndSend(Exchanges.NANO, "nano", m);
        return ResponseEntity.ok(Result.of("OK"));
    }
}
