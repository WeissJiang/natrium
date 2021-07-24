package nano.service.controller.security;

import java.util.Objects;
import java.util.StringJoiner;

public class UserDTO {

    private String id;

    private String username;
    private String firstname;

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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id) &&
               Objects.equals(username, userDTO.username) &&
               Objects.equals(firstname, userDTO.firstname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, firstname);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserDTO.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("username='" + username + "'")
                .add("firstname='" + firstname + "'")
                .toString();
    }
}
