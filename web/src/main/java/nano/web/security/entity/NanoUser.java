package nano.web.security.entity;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Telegram user
 */
public class NanoUser {

    private Long id;

    private String username;
    private String firstname;
    private String languageCode;

    private Boolean isBot;

    private String email;

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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public Boolean getIsBot() {
        return isBot;
    }

    public void setIsBot(Boolean isBot) {
        this.isBot = isBot;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NanoUser nanoUser = (NanoUser) o;
        return Objects.equals(id, nanoUser.id) &&
               Objects.equals(username, nanoUser.username) &&
               Objects.equals(firstname, nanoUser.firstname) &&
               Objects.equals(languageCode, nanoUser.languageCode) &&
               Objects.equals(isBot, nanoUser.isBot) &&
               Objects.equals(email, nanoUser.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, firstname, languageCode, isBot, email);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", NanoUser.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("username='" + username + "'")
                .add("firstname='" + firstname + "'")
                .add("languageCode='" + languageCode + "'")
                .add("isBot=" + isBot)
                .add("email='" + email + "'")
                .toString();
    }
}
