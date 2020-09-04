package nano.security.entity;

import lombok.Data;

/**
 * Telegram user
 */
@Data
public class NanoUser {

    private Number id;

    private String username;
    private String firstname;
    private Boolean isBot;
    private String languageCode;
}
