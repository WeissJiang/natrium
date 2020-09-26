package nano.web.controller.security;

import java.time.Instant;
import java.util.Objects;
import java.util.StringJoiner;

public class TokenDTO {

    private Integer id;
    private String name;

    private String privilege;

    private Instant creationTime;
    private Instant lastActiveTime;

    private Boolean current;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }

    public Instant getLastActiveTime() {
        return lastActiveTime;
    }

    public void setLastActiveTime(Instant lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenDTO tokenDTO = (TokenDTO) o;
        return Objects.equals(id, tokenDTO.id) &&
               Objects.equals(name, tokenDTO.name) &&
               Objects.equals(privilege, tokenDTO.privilege) &&
               Objects.equals(creationTime, tokenDTO.creationTime) &&
               Objects.equals(lastActiveTime, tokenDTO.lastActiveTime) &&
               Objects.equals(current, tokenDTO.current);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, privilege, creationTime, lastActiveTime, current);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TokenDTO.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("privilege='" + privilege + "'")
                .add("creationTime=" + creationTime)
                .add("lastActiveTime=" + lastActiveTime)
                .add("current=" + current)
                .toString();
    }
}
