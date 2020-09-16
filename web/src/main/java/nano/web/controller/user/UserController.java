package nano.web.controller.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.web.security.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    @NonNull
    private final SecurityService securityService;

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestHeader("X-Token") String token) {
        var userDTO = this.securityService.getUserByToken(token);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/token/createVerificatingToken")
    public ResponseEntity<?> createVerificatingToken(@RequestHeader("User-Agent") String ua,
                                                     @RequestParam("username") String username) {
        var result = this.securityService.createVerificatingToken(username, ua);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/token/verification")
    public ResponseEntity<?> tokenVerification(@RequestHeader("X-Token") String token) {
        var userDTO = this.securityService.checkTokenVerification(token);
        return ResponseEntity.ok(Objects.requireNonNullElse(userDTO, "Verificating"));
    }
}
