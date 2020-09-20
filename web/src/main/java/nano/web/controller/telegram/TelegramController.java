package nano.web.controller.telegram;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.web.security.Authorized;
import nano.web.telegram.TelegramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static nano.web.security.TokenPrivilege.NANO_API;

/**
 * Handle telegram requests
 *
 * @see TelegramService
 * @see Authorized
 */
@Slf4j
@Authorized(NANO_API)
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/telegram")
public class TelegramController {

    @NonNull
    private final TelegramService telegramService;

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
