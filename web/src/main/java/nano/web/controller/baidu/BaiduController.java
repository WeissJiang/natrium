package nano.web.controller.baidu;

import nano.support.Result;
import nano.web.baidu.TranslationService;
import nano.web.security.Authorized;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static nano.web.security.Ticket.BAIDU_TRANSLATION;

@CrossOrigin
@RestController
@RequestMapping("/api/baidu")
public class BaiduController {

    private final TranslationService translationService;

    public BaiduController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Authorized(ticket = BAIDU_TRANSLATION)
    @PostMapping("/translate")
    public ResponseEntity<?> translate(@NotNull @RequestBody Map<String, String> payload) {
        var input = payload.get("input");
        var from = payload.get("from");
        var to = payload.get("to");
        var translated = this.translationService.translate(input, from, to);
        return ResponseEntity.ok(Result.of(translated));
    }
}
