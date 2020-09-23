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
    private String languageCode;

    private Boolean isBot;

    private String email;
}
