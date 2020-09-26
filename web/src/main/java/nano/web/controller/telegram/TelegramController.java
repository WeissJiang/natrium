package nano.web.controller.telegram;

import nano.web.security.Authorized;
import nano.web.telegram.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static nano.web.security.NanoPrivilege.NANO_API;

/**
 * Handle telegram requests
 *
 * @see TelegramService
 * @see Authorized
 */
@Authorized(NANO_API)
@CrossOrigin
@RestController
@RequestMapping("/api/telegram")
public class TelegramController {

    private final TelegramService telegramService;

    public TelegramController(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @PostMapping("/setWebhook")
    public ResponseEntity<?> setWebhook() {
        var result = this.telegramService.setWebhook();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(@RequestParam("chatId") Integer chatId,
                                         @RequestParam("text") String text) {
        var result = this.telegramService.sendMessage(chatId, text);
        return ResponseEntity.ok(result);
    }
}
