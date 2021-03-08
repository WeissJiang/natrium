package nano.web.controller.security;

import nano.support.Result;
import nano.web.security.Authorized;
import nano.web.security.Token;
import nano.web.security.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static nano.web.security.Privilege.BASIC;
import static nano.web.security.Privilege.NANO_API;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Authorized(privilege = BASIC)
    @GetMapping("/user")
    public ResponseEntity<?> getUser(@Token String token) {
        var userDTO = this.userService.getUserByToken(token);
        return ResponseEntity.ok(Result.of(userDTO));
    }

    @Authorized(privilege = NANO_API)
    @GetMapping("/list")
    public ResponseEntity<?> getUserList() {
        var userDTOList = this.userService.getUserList();
        return ResponseEntity.ok(Result.of(userDTOList));
    }
}
