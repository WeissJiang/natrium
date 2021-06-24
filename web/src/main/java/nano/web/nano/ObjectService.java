package nano.web.nano;

import nano.support.Json;
import nano.web.nano.model.NanoObject;
import nano.web.nano.repository.KeyValueRepository;
import nano.web.security.TokenCode;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static nano.support.Sugar.map;

@Service
public class ObjectService {

    private static final int DEFAULT_CACHE_LIMIT = 256;
    private static final String OBJECT = "object";

    private final Map<String, NanoObject> objectCache = new LinkedHashMap<>(DEFAULT_CACHE_LIMIT, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, NanoObject> eldest) {
            return this.size() > DEFAULT_CACHE_LIMIT;
        }
    };

    private final KeyValueRepository keyValueRepository;

    public ObjectService(KeyValueRepository keyValueRepository) {
        this.keyValueRepository = keyValueRepository;
    }

    public String putObject(@NotNull NanoObject object) {
        var key = TokenCode.generateUUID() + object.getExtension();
        var objectKey = getObjectKey(key);
        var objectValue = Map.of(
                "name", object.getName(),
                "extension", object.getExtension(),
                "type", object.getType(),
                "size", object.getSize(),
                "data", Base64.getEncoder().encodeToString(object.getData())
        );
        this.keyValueRepository.createKeyValue(objectKey, Json.encode(objectValue));
        return key;
    }

    public List<String> batchPutObject(@NotNull List<@NotNull NanoObject> objectList) {
        if (CollectionUtils.isEmpty(objectList)) {
            return Collections.emptyList();
        }
        return objectList.stream().map(this::putObject).toList();
    }

    public void batchDropObject(@NotNull List<@NotNull String> keyList) {
        if (CollectionUtils.isEmpty(keyList)) {
            return;
        }
        var internalKeyList = map(keyList, ObjectService::getObjectKey);
        this.keyValueRepository.deleteKeyValue(internalKeyList);
    }

    public @NotNull NanoObject getObject(@NotNull String key) {
        return this.objectCache.computeIfAbsent(key, (_key) -> {
            var objectKey = getObjectKey(_key);
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
        });
    }

    public @NotNull List<@NotNull String> getObjectList() {
        var pattern = "^%s:".formatted(OBJECT);
        var keyList = this.keyValueRepository.queryKeyListByPattern(pattern);
        return map(keyList, it -> it.replaceFirst(pattern, ""));
    }

    private static @NotNull String getObjectKey(@NotNull String key) {
        return "object:%s".formatted(key);
    }
}
