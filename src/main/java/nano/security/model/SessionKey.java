package nano.security.model;

import lombok.Data;

import java.time.Instant;

@Data
public class SessionKey {

    private Number chatId;
    private Number userId;
}
