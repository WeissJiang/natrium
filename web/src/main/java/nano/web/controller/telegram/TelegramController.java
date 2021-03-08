package nano.web.controller.telegram;

import nano.support.Result;
import nano.web.security.Authorized;
import nano.web.security.SecurityService;
import nano.web.telegram.BotHandler;
import nano.web.telegram.TelegramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static nano.web.security.Privilege.NANO_API;

/**
 * Handle Telegram requests
 *
 * @see TelegramService
 */
@CrossOrigin
@RestController
@RequestMapping("/api/telegram")
public class TelegramController {

    private final BotHandler botHandler;
    private final TelegramService telegramService;
    private final SecurityService securityService;

    public TelegramController(BotHandler botHandler,
                              TelegramService telegramService,
                              SecurityService securityService) {
        this.botHandler = botHandler;
        this.telegramService = telegramService;
        this.securityService = securityService;
    }

    @PostMapping("/webhook/{bot}/{key}")
    public ResponseEntity<?> webhook(@PathVariable("bot") String botName, @PathVariable("key") String key,
                                     @RequestBody Map<String, ?> parameterMap) throws Exception {
        // check key
        this.securityService.checkNanoApiKey(key);
        // handle request
        this.botHandler.handleAsync(botName, parameterMap);
        // always return ok
        return ResponseEntity.ok().build();
    }

    @Authorized(privilege = NANO_API)
    @PostMapping("/setWebhook")
    public ResponseEntity<?> setWebhook() {
        var result = this.telegramService.setWebhook();
        return ResponseEntity.ok(Result.of(result));
    }
}
