package nano.web.controller.security;

import lombok.Data;

import java.time.Instant;

@Data
public class TokenDTO {

    private Integer id;
    private String name;

    private String privilege;

    private Instant creationTime;
    private Instant lastActiveTime;

    private Boolean current;
}
