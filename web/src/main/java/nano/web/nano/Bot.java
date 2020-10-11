package nano.web.nano;

public class Bot {

    public static final String NANO = "nano";
    public static final String NANO_026 = "nano-026";
    public static final String NANO_100 = "nano-100";
    public static final String NANO_233 = "nano-233";

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
