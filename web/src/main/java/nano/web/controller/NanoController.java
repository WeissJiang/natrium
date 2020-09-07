package nano.web.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.web.service.AmqpService;
import nano.web.service.NanoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/nano")
public class NanoController {

    @NonNull
    private final NanoService nanoService;

    @NonNull
    private final AmqpService amqpService;

    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok("pong\n");
    }

    @GetMapping("/nano")
    public ResponseEntity<?> nano() {
        var nano = this.nanoService.nano();
        return ResponseEntity.ok(nano);
    }

    @PostMapping("/message")
    public ResponseEntity<?> message(@RequestParam("m") String m) {
        this.amqpService.send("nano", "nano", m);
        return ResponseEntity.ok("OK");
    }
}
