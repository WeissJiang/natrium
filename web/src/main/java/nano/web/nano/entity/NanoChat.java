package nano.web.nano.entity;

import java.util.Objects;

/**
 * Telegram chat
 */
public class NanoChat {

    private Long id;

    private String username;
    private String title;
    private String firstname;
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NanoChat nanoChat = (NanoChat) o;
        return Objects.equals(id, nanoChat.id) &&
               Objects.equals(username, nanoChat.username) &&
               Objects.equals(title, nanoChat.title) &&
               Objects.equals(firstname, nanoChat.firstname) &&
               Objects.equals(type, nanoChat.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, title, firstname, type);
    }
}
