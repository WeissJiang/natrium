package nano.web.security.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class NanoToken {

    public static final String VALID = "VALID";
    public static final String INVALID = "INVALID";
    public static final String VERIFICATING = "VERIFICATING";

    /**
     * 拼接verificating status
     */
    public static String verificatingStatus(String username, String verificationCode) {
        return "%s:%s:%s".formatted(VERIFICATING, username, verificationCode);
    }

    private String token;

    private String name;

    private Number chatId;
    private Number userId;

    /**
     * VALID
     * INVALID
     * VERIFICATING:{username}:{code}
     */
    private String status;

    private Timestamp lastActiveTime;
    private Timestamp creationTime;

}
