package nano.web.controller.telegram;

import nano.web.security.SecurityService;
import nano.web.telegram.BotHandler;
import nano.web.telegram.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Handle webhook requests
 *
 * @see TelegramService
 */
@CrossOrigin
@RestController
@RequestMapping("/api/telegram")
public class WebhookController {

    private final SecurityService securityService;

    private final BotHandler botHandler;

    @Autowired
    public WebhookController(SecurityService securityService, BotHandler botHandler) {
        this.securityService = securityService;
        this.botHandler = botHandler;
    }

    @PostMapping("/webhook/{key}")
    public ResponseEntity<?> webhook(@PathVariable("key") String key,
                                     @RequestBody Map<String, Object> parameterMap) throws Exception {
        // check key
        this.securityService.checkNanoApiKey(key);
        // handle request
        this.botHandler.handleAsync(parameterMap);
        // always return ok
        return ResponseEntity.ok().build();
    }
}
