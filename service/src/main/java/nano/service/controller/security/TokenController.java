package nano.service.controller.security;

import nano.service.security.Authorized;
import nano.service.security.Privilege;
import nano.service.security.SecurityService;
import nano.service.security.Token;
import nano.support.Result;
import nano.service.task.PruneVerifyingTimeoutTokenTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @Authorized(privilege = Privilege.BASIC)
    @PostMapping("/deleteSelf")
    public ResponseEntity<?> deleteTokenSelf(@Token String token) {
        this.securityService.deleteTheToken(token);
        return ResponseEntity.ok(Result.empty());
    }

    @Authorized(privilege = Privilege.BASIC)
    @PostMapping("/delete")
    public ResponseEntity<?> deleteToken(@Token String token, @RequestParam("id") List<Integer> idList) {
        this.securityService.deleteSpecificToken(token, idList);
        return ResponseEntity.ok(Result.empty());
    }

    @Authorized(privilege = Privilege.BASIC)
    @GetMapping("/list")
    public ResponseEntity<?> getTokenList(@Token String token) {
        var tokenList = this.securityService.getAssociatedTokenList(token);
        return ResponseEntity.ok(Result.of(tokenList));
    }

    @Authorized(privilege = Privilege.NANO_API)
    @PostMapping("/pruneVerifyingTimeoutToken")
    public ResponseEntity<?> verifyingTimeoutToken() {
        var count = this.pruneVerifyingTimeoutTokenTask.pruneVerifyingTimeoutToken();
        return ResponseEntity.ok(Result.of(Map.of("count", count)));
    }
}
