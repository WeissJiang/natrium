package nano.web.security.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class NanoToken {
    String token;

    String name;

    Number chatId;
    Number userId;

    Timestamp lastActiveTime;
    Timestamp creationTime;

}
