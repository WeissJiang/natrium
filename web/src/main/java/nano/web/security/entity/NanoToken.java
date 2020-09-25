package nano.web.security.entity;

import lombok.Data;
import nano.web.security.NanoPrivilege;

import java.sql.Timestamp;

/**
 * Nano token
 */
@Data
public class NanoToken {

    // status
    public static final String VALID = "VALID";
    public static final String INVALID = "INVALID";
    public static final String VERIFICATING = "VERIFICATING";

    // verificating
    public static final String VERIFICATION_TIMEOUT = "VERIFICATION_TIMEOUT";
    public static final String VERIFICATED = "VERIFICATED";

    /**
     * 拼接verificating status
     */
    public static String verificatingStatus(String username, String verificationCode) {
        return "%s:%s:%s".formatted(VERIFICATING, username, verificationCode);
    }

    private Integer id;
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

    /**
     * 权限
     *
     * @see NanoPrivilege
     */
    private String privilege;

    private Timestamp lastActiveTime;
    private Timestamp creationTime;

}
