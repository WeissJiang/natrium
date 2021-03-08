package nano.web.controller.security;

import nano.support.Result;
import nano.web.security.Authorized;
import nano.web.security.SecurityService;
import nano.web.security.Token;
import nano.web.task.PruneVerifyingTimeoutTokenTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static nano.web.security.Privilege.BASIC;
import static nano.web.security.Privilege.NANO_API;

@CrossOrigin
@RestController
@RequestMapping("/api/token")
public class TokenController {

    private final SecurityService securityService;

    private final PruneVerifyingTimeoutTokenTask pruneVerifyingTimeoutTokenTask;

    public TokenController(SecurityService securityService,
                           PruneVerifyingTimeoutTokenTask pruneVerifyingTimeoutTokenTask) {
        this.securityService = securityService;
        this.pruneVerifyingTimeoutTokenTask = pruneVerifyingTimeoutTokenTask;
    }

    @PostMapping("/createVerifyingToken")
    public ResponseEntity<?> createVerifyingToken(@RequestHeader("User-Agent") String ua,
                                                  @RequestParam("username") String username) {
        var result = this.securityService.createVerifyingToken(username, ua);
        return ResponseEntity.ok(Result.of(result));
    }

    @GetMapping("/verification")
    public ResponseEntity<?> getTokenVerification(@Token String token) {
        var result = this.securityService.getTokenVerification(token);
        return ResponseEntity.ok(Result.of(result));
    }

    @Authorized(privilege = BASIC)
    @PostMapping("/deleteSelf")
    public ResponseEntity<?> deleteTokenSelf(@Token String token) {
        this.securityService.deleteTheToken(token);
        return ResponseEntity.ok(Result.empty());
    }

    @Authorized(privilege = BASIC)
    @PostMapping("/delete")
    public ResponseEntity<?> deleteToken(@Token String token, @RequestParam("id") List<Integer> idList) {
        this.securityService.deleteSpecificToken(token, idList);
        return ResponseEntity.ok(Result.empty());
    }

    @Authorized(privilege = BASIC)
    @GetMapping("/list")
    public ResponseEntity<?> getTokenList(@Token String token) {
        var tokenList = this.securityService.getAssociatedTokenList(token);
        return ResponseEntity.ok(Result.of(tokenList));
    }

    @Authorized(privilege = NANO_API)
    @PostMapping("/pruneVerifyingTimeoutToken")
    public ResponseEntity<?> verifyingTimeoutToken() {
        var count = this.pruneVerifyingTimeoutTokenTask.pruneVerifyingTimeoutToken();
        return ResponseEntity.ok(Result.of(Map.of("count", count)));
    }
}
