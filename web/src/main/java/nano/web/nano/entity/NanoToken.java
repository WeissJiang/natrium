package nano.web.nano.entity;

import nano.web.security.Privilege;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Nano token
 */
public class NanoToken {

    // status
    public static final String VALID = "VALID";
    public static final String INVALID = "INVALID";
    public static final String VERIFYING = "VERIFYING";

    // verifying
    public static final String VERIFYING_TIMEOUT = "VERIFYING_TIMEOUT";
    public static final String VERIFIED = "VERIFIED";

    /**
     * 拼接verifying status
     */
    public static String verifyingStatus(String username, String verificationCode) {
        return "%s:%s:%s".formatted(VERIFYING, username, verificationCode);
    }

    private Integer id;
    private String token;

    private String name;

    private Number chatId;
    private Number userId;

    /**
     * VALID
     * INVALID
     * VERIFYING:{username}:{code}
     */
    private String status;

    /**
     * 权限
     *
     * @see Privilege
     */
    private String privilege;

    private Timestamp lastActiveTime;
    private Timestamp creationTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Number getChatId() {
        return chatId;
    }

    public void setChatId(Number chatId) {
        this.chatId = chatId;
    }

    public Number getUserId() {
        return userId;
    }

    public void setUserId(Number userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public Timestamp getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(Timestamp lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NanoToken nanoToken = (NanoToken) o;
        return Objects.equals(id, nanoToken.id) &&
               Objects.equals(token, nanoToken.token) &&
               Objects.equals(name, nanoToken.name) &&
               Objects.equals(chatId, nanoToken.chatId) &&
               Objects.equals(userId, nanoToken.userId) &&
               Objects.equals(status, nanoToken.status) &&
               Objects.equals(privilege, nanoToken.privilege) &&
               Objects.equals(lastActiveTime, nanoToken.lastActiveTime) &&
               Objects.equals(creationTime, nanoToken.creationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, name, chatId, userId, status, privilege, lastActiveTime, creationTime);
    }
}
