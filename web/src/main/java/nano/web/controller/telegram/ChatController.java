package nano.web.controller.telegram;

import nano.web.controller.Result;
import nano.web.security.Authorized;
import nano.web.telegram.ChatService;
import nano.web.telegram.TelegramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat/list")
    public ResponseEntity<?> getChatList() {
        var chatDTOList = this.chatService.getChatList();
        return ResponseEntity.ok(Result.of(chatDTOList));
    }
}
