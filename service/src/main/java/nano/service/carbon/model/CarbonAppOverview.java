package nano.service.carbon.model;

public record CarbonAppOverview(
        String id,
        String name,
        String description,
        String url,
        Integer pageCount,
        Integer keyCount
) {
}
