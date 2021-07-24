package nano.service.nano;

import nano.service.nano.entity.KeyValue;
import nano.service.nano.model.NanoObject;
import nano.service.nano.repository.KeyValueRepository;
import nano.service.nano.repository.NanoBlobRepository;
import nano.service.security.TokenCode;
import nano.support.Json;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final NanoBlobRepository nanoBlobRepository;

    public ObjectService(KeyValueRepository keyValueRepository, NanoBlobRepository nanoBlobRepository) {
        this.keyValueRepository = keyValueRepository;
        this.nanoBlobRepository = nanoBlobRepository;
    }

    @Transactional
    public String putObject(@NotNull NanoObject object) {
        var key = TokenCode.generateUUID();
        var objectKey = getObjectKey(key);
        var objectValue = Map.of(
                "key", key,
                "name", object.getName(),
                "extension", object.getExtension(),
                "type", object.getType(),
                "size", object.getSize()
        );
        this.keyValueRepository.createKeyValue(objectKey, Json.encode(objectValue));
        this.nanoBlobRepository.upsertBlob(objectKey, Base64.getEncoder().encodeToString(object.getData()));
        return key;
    }

    @Transactional
    public List<String> batchPutObject(@NotNull List<@NotNull NanoObject> objectList) {
        if (CollectionUtils.isEmpty(objectList)) {
            return Collections.emptyList();
        }
        return objectList.stream().map(this::putObject).toList();
    }

    @Transactional
    public void batchDropObject(@NotNull List<@NotNull String> keyList) {
        if (CollectionUtils.isEmpty(keyList)) {
            return;
        }
        var internalKeyList = map(keyList, ObjectService::getObjectKey);
        this.keyValueRepository.deleteKeyValue(internalKeyList);
        this.nanoBlobRepository.deleteBlob(internalKeyList);
    }

    public @NotNull NanoObject getObject(@NotNull String key) {
        return this.objectCache.computeIfAbsent(key, (_key) -> {
            var objectKey = getObjectKey(_key);
            var keyValue = this.keyValueRepository.queryKeyValue(objectKey);
            Assert.notNull(keyValue, "object is not exist, key: " + objectKey);
            var object = mapToNanoObject(keyValue);
            // Get data
            var nanoBlob = this.nanoBlobRepository.queryBlob(objectKey);
            var encodedData = nanoBlob.getBlob();
            var data = Base64.getDecoder().decode(encodedData);
            object.setData(data);
            return object;
        });
    }

    public @NotNull List<@NotNull NanoObject> getObjectList() {
        var pattern = "^%s:".formatted(OBJECT);
        var keyList = this.keyValueRepository.queryListByPattern(pattern);
        return map(keyList, ObjectService::mapToNanoObject);
    }

    private static @NotNull String getObjectKey(@NotNull String key) {
        return "object:%s".formatted(key);
    }

    private static NanoObject mapToNanoObject(@NotNull KeyValue keyValue) {
        var objectJson = keyValue.getValue();
        var objectMap = Json.decodeValueAsMap(objectJson);
        var object = new NanoObject();
        object.setKey(String.valueOf(objectMap.get("key")));
        object.setName(String.valueOf(objectMap.get("name")));
        object.setSize((Number) objectMap.get("size"));
        object.setType(String.valueOf(objectMap.get("type")));
        object.setExtension(String.valueOf(objectMap.get("extension")));
        return object;
    }
}
