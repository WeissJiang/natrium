package nano.web.controller.telegram;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.web.security.SecurityService;
import nano.web.telegram.BotHandler;
import nano.web.telegram.TelegramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Handle webhook request
 *
 * @see TelegramService
 */
@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/telegram")
public class WebhookController {

    @NonNull
    private final SecurityService securityService;

    @NonNull
    private final BotHandler botHandler;

    @PostMapping("/webhook/{token}")
    public ResponseEntity<?> webhook(@PathVariable("token") String token,
                                     @RequestBody Map<String, Object> parameterMap) {
        // check token
        this.securityService.checkNanoApiToken(token);
        // handle request
        this.botHandler.handleAsync(parameterMap);
        // always return ok
        return ResponseEntity.ok().build();
    }
}
