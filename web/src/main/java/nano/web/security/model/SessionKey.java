package nano.web.security.model;

import lombok.Data;

@Data
public class SessionKey {

    private Number chatId;
    private Number userId;
}
