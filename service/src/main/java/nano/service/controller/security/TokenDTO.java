package nano.service.controller.security;

import java.time.Instant;

public record TokenDTO(
         Integer id,
         String name,
         String privilege,
         Instant lastActiveTime,
         Instant creationTime,
         Boolean current
) {
}
