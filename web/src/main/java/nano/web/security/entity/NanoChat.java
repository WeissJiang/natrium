package nano.web.security.entity;

import lombok.Data;

/**
 * Telegram chat
 */
@Data
public class NanoChat {

    private Long id;

    private String username;
    private String title;
    private String firstname;
    private String type;
}
