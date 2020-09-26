package nano.web.security.entity;

import nano.web.security.NanoPrivilege;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Nano token
 */
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

    @Override
    public String toString() {
        return new StringJoiner(", ", NanoToken.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("token='" + token + "'")
                .add("name='" + name + "'")
                .add("chatId=" + chatId)
                .add("userId=" + userId)
                .add("status='" + status + "'")
                .add("privilege='" + privilege + "'")
                .add("lastActiveTime=" + lastActiveTime)
                .add("creationTime=" + creationTime)
                .toString();
    }
}
