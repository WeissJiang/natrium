package nano.web.controller.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.web.controller.Result;
import nano.web.security.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static nano.web.security.TokenCode.*;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    @NonNull
    private final UserService userService;

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestAttribute(D_TOKEN) String token) {
        var userDTO = this.userService.getUserByToken(token);
        return ResponseEntity.ok(Result.of(userDTO));
    }
}
