package nano.web.nano;

public class Bot {

    public static final String BOT_NANO = "nano";
    public static final String BOT_026 = "bot-026";
    public static final String BOT_100 = "bot-100";

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
}
