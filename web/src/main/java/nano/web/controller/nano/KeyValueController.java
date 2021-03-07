package nano.web.controller.nano;

import nano.support.Result;
import nano.web.nano.repository.KeyValueRepository;
import nano.web.security.Authorized;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static nano.web.security.NanoPrivilege.NANO_API;

@CrossOrigin
@RestController
@RequestMapping("/api/kv/")
public class KeyValueController {

    private final KeyValueRepository keyValueRepository;

    public KeyValueController(KeyValueRepository keyValueRepository) {
        this.keyValueRepository = keyValueRepository;
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> read(@PathVariable("key") String key) {
        var keyValue = this.keyValueRepository.queryKeyValue(key);
        if (keyValue == null) {
            var error = "key %s not found".formatted(key);
            return ResponseEntity.ok(Result.error(error));
        }
        return ResponseEntity.ok(Result.of(keyValue));
    }

    @Authorized(NANO_API)
    @PostMapping("/{key}")
    public ResponseEntity<?> create(@PathVariable("key") String key,
                                    @RequestBody String value) {
        this.keyValueRepository.createKeyValue(key, value);
        return ResponseEntity.ok(Result.empty());
    }

    @Authorized(NANO_API)
    @PutMapping("/{key}")
    public ResponseEntity<?> update(@PathVariable("key") String key,
                                    @RequestBody String value) {
        this.keyValueRepository.updateKeyValue(key, value);
        return ResponseEntity.ok(Result.empty());
    }
}
