package nano.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.security.repository.NanoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class NanoController {

    @NonNull
    private final NanoRepository nanoRepository;

    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok("pong\n");
    }

    @GetMapping("/nano")
    public ResponseEntity<?> version() {
        var version = this.nanoRepository.getPostgresVersion();
        return ResponseEntity.ok(version);
    }
}
