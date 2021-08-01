package nano.service.nano.model;

public record NanoObject(
        String key,
        String name,
        String type,
        Number size,
        byte[] data,
        String extension
) {
}
