package nano.service.carbon.model;

import java.util.List;

public record CarbonKey(
         String key,
         String pageCode,
         String original,
         List<CarbonText> translation,
         String description
) {
}
