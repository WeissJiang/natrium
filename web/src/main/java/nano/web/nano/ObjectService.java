package nano.web.nano;

import nano.support.Json;
import nano.web.nano.model.NanoObject;
import nano.web.nano.repository.KeyValueRepository;
import nano.web.security.TokenCode;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Base64;
import java.util.Map;

@Service
public class ObjectService {

    private final KeyValueRepository keyValueRepository;

    public ObjectService(KeyValueRepository keyValueRepository) {
        this.keyValueRepository = keyValueRepository;
    }

    public String putObject(NanoObject object) {
        var key = TokenCode.generateUUID();
        var objectKey = getObjectKey(key);
        var objectValue = Map.of(
                "name", object.getName(),
                "type", object.getType(),
                "size", object.getSize(),
                "data", Base64.getEncoder().encodeToString(object.getData())
        );
        this.keyValueRepository.createKeyValue(objectKey, Json.encode(objectValue));
        return key;
    }

    public @NotNull NanoObject getObject(@NotNull String key) {
        var objectKey = getObjectKey(key);
        var keyValue = this.keyValueRepository.queryKeyValue(objectKey);
        Assert.notNull(keyValue, "object is not exist, key: " + objectKey);
        var objectJson = keyValue.getValue();
        var objectMap = Json.decodeValueAsMap(objectJson);
        var object = new NanoObject();
        object.setName(String.valueOf(objectMap.get("name")));
        object.setSize((Number) objectMap.get("size"));
        object.setType(String.valueOf(objectMap.get("type")));
        var encodedData = String.valueOf(objectMap.get("data"));
        var data = Base64.getDecoder().decode(encodedData);
        object.setData(data);
        return object;
    }

    private static @NotNull String getObjectKey(@NotNull String key) {
        return "object:%s".formatted(key);
    }
}
