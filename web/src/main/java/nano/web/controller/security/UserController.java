package nano.web.controller.security;

import nano.web.controller.Result;
import nano.web.security.Authorized;
import nano.web.security.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static nano.web.security.NanoPrivilege.*;
import static nano.web.security.TokenCode.X_TOKEN_DIGEST;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Authorized(BASIC)
    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestAttribute(X_TOKEN_DIGEST) String token) {
        var userDTO = this.userService.getUserByToken(token);
        return ResponseEntity.ok(Result.of(userDTO));
    }

    @Authorized(NANO_API)
    @GetMapping("/list")
    public ResponseEntity<?> getUserList() {
        var userDTOList = this.userService.getUserList();
        return ResponseEntity.ok(Result.of(userDTOList));
    }
}
