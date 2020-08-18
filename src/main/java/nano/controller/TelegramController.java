package nano.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.security.SecurityService;
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
    private final SecurityService securityService;

    @PostMapping("/{token}")
    public ResponseEntity<?> webhook(@PathVariable("token") String token,
                                     @RequestBody Map<String, Object> parameterMap) {
        this.securityService.checkNanoToken(token);
        this.telegramService.handleWebhook(parameterMap);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/setWebhook")
    public ResponseEntity<?> setWebhook(@RequestParam("token") String token) {
        this.securityService.checkNanoToken(token);
        var result = this.telegramService.setWebhook();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(@RequestParam("token") String token,
                                         @RequestParam("chatId") Integer chatId,
                                         @RequestParam("text") String text) {
        this.securityService.checkNanoToken(token);
        var result = this.telegramService.sendMessage(chatId, text);
        return ResponseEntity.ok(result);
    }
}
