package nano.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.component.TelegramBotApi;
import nano.service.TelegramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/telegram")
public class TelegramController {

    @NonNull
    private final TelegramService telegramService;

    @NonNull
    private final TelegramBotApi telegramBotApi;

    @PostMapping("/{token}")
    public ResponseEntity<?> webhook(@PathVariable("token") String token,
                                     @RequestBody Map<String, Object> request) {
        this.telegramBotApi.checkTgWebhookToken(token);
        this.telegramService.handleRequest(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/set-webhook")
    public ResponseEntity<?> setWebhook() {
        var result = this.telegramBotApi.setWebhook();
        return ResponseEntity.ok(result);
    }
}
