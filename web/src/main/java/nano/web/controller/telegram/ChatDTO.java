package nano.web.controller.telegram;

import java.util.Objects;
import java.util.StringJoiner;

public class ChatDTO {

    private String id;

    private String username;
    private String title;
    private String firstname;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
        ChatDTO chatDTO = (ChatDTO) o;
        return Objects.equals(id, chatDTO.id) &&
               Objects.equals(username, chatDTO.username) &&
               Objects.equals(title, chatDTO.title) &&
               Objects.equals(firstname, chatDTO.firstname) &&
               Objects.equals(type, chatDTO.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, title, firstname, type);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ChatDTO.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("username='" + username + "'")
                .add("title='" + title + "'")
                .add("firstname='" + firstname + "'")
                .add("type='" + type + "'")
                .toString();
    }
}
