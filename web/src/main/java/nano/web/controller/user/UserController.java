package nano.web.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestHeader("X-Token") String token) {
        var user = new UserDTO();
        user.setFirstname("test");
        return ResponseEntity.ok(user);
    }

    @PostMapping("/token/createLoginToken")
    public ResponseEntity<?> createLoginToken(@RequestParam("username") String username) {
        var result = Map.of(
                "token", "abc123",
                "verificationCode", "102938",
                "username", username
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/token/verification")
    public ResponseEntity<?> tokenVerification(@RequestHeader("X-Token") String token) {
        return ResponseEntity.ok("verificating");
    }
}
