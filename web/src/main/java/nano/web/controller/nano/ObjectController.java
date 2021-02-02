package nano.web.controller.nano;

import nano.support.Result;
import nano.web.nano.ObjectService;
import nano.web.nano.model.NanoObject;
import nano.web.security.Authorized;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

import static nano.web.security.NanoPrivilege.NANO_API;

@RestController
@RequestMapping("/api/object/")
public class ObjectController {

    private final ObjectService objectService;

    public ObjectController(ObjectService objectService) {
        this.objectService = objectService;
    }

    @Authorized(NANO_API)
    @PostMapping
    public ResponseEntity<?> putObject(@RequestParam("file") MultipartFile file) {
        var object = convertToObject(file);
        var key = this.objectService.putObject(object);
        return ResponseEntity.ok(Result.of(key));
    }

    @GetMapping("/{key}")
    public ResponseEntity<byte[]> getObject(@PathVariable("key") String key) {
        var object = this.objectService.getObject(key);
        var mediaType = MediaType.parseMediaType(object.getType());
        var data = object.getData();
        return ResponseEntity.ok().contentType(mediaType).body(data);
    }

    private static @NotNull NanoObject convertToObject(@NotNull MultipartFile file) {
        try {
            var object = new NanoObject();
            object.setName(file.getName());
            object.setType(file.getContentType());
            object.setSize(file.getSize());
            object.setData(file.getBytes());
            file.getBytes();
            return object;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
