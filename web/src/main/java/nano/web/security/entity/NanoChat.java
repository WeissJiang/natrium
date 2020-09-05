package nano.web.security.entity;

import lombok.Data;

/**
 * Telegram chat
 */
@Data
public class NanoChat {

    private Number id;

    private String username;
    private String title;
    private String firstname;
    private String type;
}
