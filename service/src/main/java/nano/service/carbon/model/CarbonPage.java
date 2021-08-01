package nano.service.carbon.model;

import java.util.List;

public record CarbonPage(
        String name,
        String code,
        String description,
        List<CarbonKey> keyList
) {
}
