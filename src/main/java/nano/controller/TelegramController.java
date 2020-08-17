package nano.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping("/{token}")
    public ResponseEntity<?> webhook(@PathVariable("token") String token,
                                     @RequestBody Map<String, Object> parameterMap) {
        this.telegramService.checkTelegramWebhookToken(token);
        this.telegramService.handleWebhook(parameterMap);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/set-webhook")
    public ResponseEntity<?> setWebhook() {
        var result = this.telegramService.setWebhook();
        return ResponseEntity.ok(result);
    }
}
