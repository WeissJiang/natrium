package nano.web.security.entity;

import lombok.Data;

/**
 * Telegram user
 */
@Data
public class NanoUser {

    private Long id;

    private String username;
    private String firstname;
    private Boolean isBot;
    private String languageCode;

    private String email;
}
