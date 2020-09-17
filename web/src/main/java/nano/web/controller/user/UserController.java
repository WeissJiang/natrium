package nano.web.controller.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.web.controller.Result;
import nano.web.security.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(Result.of(userDTO));
    }

    @PostMapping("/token/createVerificatingToken")
    public ResponseEntity<?> createVerificatingToken(@RequestHeader("User-Agent") String ua,
                                                     @RequestParam("username") String username) {
        var result = this.securityService.createVerificatingToken(username, ua);
        return ResponseEntity.ok(Result.of(result));
    }

    @PostMapping("/token/verification")
    public ResponseEntity<?> checkTokenVerification(@RequestHeader("X-Token") String token) {
        var result = this.securityService.checkTokenVerification(token);
        return ResponseEntity.ok(Result.of(result));
    }

    @PostMapping("/token/delete")
    public ResponseEntity<?> deleteToken(@RequestHeader("X-Token") String token) {
        this.securityService.deleteToken(token);
        return ResponseEntity.ok(Result.empty());
    }
}
