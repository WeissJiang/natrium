package nano.web.telegram;

import nano.web.controller.telegram.ChatDTO;
import nano.web.security.entity.NanoChat;
import nano.web.security.repository.ChatRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

import static nano.support.Sugar.map;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public List<ChatDTO> getChatList() {
        var chatList = this.chatRepository.queryChatList();
        return map(chatList, ChatService::convert);
    }

    private static ChatDTO convert(@NotNull NanoChat chat) {
        var chatDTO = new ChatDTO();
        chatDTO.setId(String.valueOf(chat.getId()));
        chatDTO.setFirstname(chat.getFirstname());
        chatDTO.setTitle(chat.getTitle());
        chatDTO.setType(chat.getType());
        chatDTO.setUsername(chat.getUsername());
        return chatDTO;
    }
}
