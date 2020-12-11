package nano.web.nano.model;

import java.util.Objects;

public class Bot {

    public static final String NANO = "nano";
    public static final String NANO_026 = "nano-026";
    public static final String NANO_100 = "nano-100";
    public static final String NANO_233 = "nano-233";
    public static final String NANO_262 = "nano-262";

    private String name;
    private String username;
    private String token;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bot bot = (Bot) o;
        return Objects.equals(name, bot.name) &&
               Objects.equals(username, bot.username) &&
               Objects.equals(token, bot.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, username, token);
    }
}
