package nano.web.controller.telegram;

import nano.web.controller.Result;
import nano.web.nano.Bot;
import nano.web.security.Authorized;
import nano.web.telegram.ChatService;
import nano.web.telegram.TelegramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static nano.web.security.NanoPrivilege.NANO_API;

/**
 * Handle chat requests
 *
 * @see TelegramService
 * @see ChatService
 * @see Authorized
 */
@Authorized(NANO_API)
@CrossOrigin
@RestController
@RequestMapping("/api/telegram")
public class ChatController {

    private final TelegramService telegramService;
    private final ChatService chatService;

    public ChatController(TelegramService telegramService,
                          ChatService chatService) {
        this.telegramService = telegramService;
        this.chatService = chatService;
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<?> sendMessage(@RequestParam("chatId") Integer chatId,
                                         @RequestParam("text") String text) {
        var result = this.telegramService.sendMessage(Bot.BOT_ROOT, chatId, text);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/chat/list")
    public ResponseEntity<?> getChatList() {
        var chatDTOList = this.chatService.getChatList();
        return ResponseEntity.ok(Result.of(chatDTOList));
    }
}
