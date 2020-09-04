package nano.security.entity;

import lombok.Data;

import java.sql.Timestamp;

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

    private Timestamp creationTime;
    private Timestamp lastAccessedTime;

}
