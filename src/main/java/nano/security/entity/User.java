package nano.security.entity;

import lombok.Data;

@Data
public class User {

    private Number id;

    private String username;
    private String firstname;
    private Boolean isBot;
    private String languageCode;
}
