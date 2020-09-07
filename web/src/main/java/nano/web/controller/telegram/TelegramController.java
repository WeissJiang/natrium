package nano.web.controller.telegram;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.web.security.AuthenticationInterceptor;
import nano.web.security.SecurityService;
import nano.web.telegram.BotHandler;
import nano.web.telegram.TelegramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Handle telegram request
 *
 * @see TelegramService
 * @see AuthenticationInterceptor
 */
@Slf4j
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
