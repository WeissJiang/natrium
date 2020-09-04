package nano.security.model;

import lombok.Data;

import java.time.Instant;

@Data
public class SessionSeed {

    private Number chatId;
    private Number userId;

    private Instant lastAccessedTime;
}
