package nano.web.controller.security;

import lombok.Data;

import java.time.Instant;

@Data
public class TokenDTO {

    private String name;
    private Instant creationTime;
    private Instant lastActiveTime;

    private Boolean current;
}
