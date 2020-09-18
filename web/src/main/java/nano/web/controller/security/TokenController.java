package nano.web.controller.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.web.controller.Result;
import nano.web.security.Authorized;
import nano.web.security.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class TokenController {

    @NonNull
    private final SecurityService securityService;

    @PostMapping("/createVerificatingToken")
    public ResponseEntity<?> createVerificatingToken(@RequestHeader("User-Agent") String ua,
                                                     @RequestParam("username") String username) {
        var result = this.securityService.createVerificatingToken(username, ua);
        return ResponseEntity.ok(Result.of(result));
    }

    @GetMapping("/verification")
    public ResponseEntity<?> getTokenVerification(@RequestHeader("X-Token") String token) {
        var result = this.securityService.getTokenVerification(token);
        return ResponseEntity.ok(Result.of(result));
    }

    @Authorized
    @PostMapping("/pruneVerificatingTimeoutToken")
    public ResponseEntity<?> verificatingTimeoutToken() {
        var count = this.securityService.pruneVerificatingTimeoutToken();
        return ResponseEntity.ok(Result.of(Map.of("count", count)));
    }

    @PostMapping("/self/delete")
    public ResponseEntity<?> deleteSelfToken(@RequestHeader("X-Token") String token) {
        this.securityService.deleteToken(token);
        return ResponseEntity.ok(Result.empty());
    }

    @GetMapping("/list")
    public ResponseEntity<?> getTokenList(@RequestHeader("X-Token") String token) {
        var tokenList = this.securityService.getTokenList(token);
        return ResponseEntity.ok(Result.of(tokenList));
    }
}
