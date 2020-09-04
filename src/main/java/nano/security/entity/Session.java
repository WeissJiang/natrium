package nano.security.entity;

import lombok.Data;

import java.time.Instant;

/**
 * Chat ID和User ID确定唯一会话
 *
 * @see Chat
 * @see User
 */
@Data
public class Session {

    private Integer id;

    private Number chatId;
    private Number userId;

    private String attributes;

    private Instant creationTime;
    private Instant lastAccessedTime;

}
