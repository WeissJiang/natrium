package nano.web.controller.security;

import nano.web.controller.Result;
import nano.web.security.Authorized;
import nano.web.security.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static nano.web.security.NanoPrivilege.BASIC;
import static nano.web.security.NanoPrivilege.NANO_API;
import static nano.web.security.TokenCode.D_TOKEN;

@CrossOrigin
@RestController
@RequestMapping("/api/token")
public class TokenController {

    private final SecurityService securityService;

    public TokenController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @PostMapping("/createVerifyingToken")
    public ResponseEntity<?> createVerifyingToken(@RequestHeader("User-Agent") String ua,
                                                     @RequestParam("username") String username) throws Exception {
        var result = this.securityService.createVerifyingToken(username, ua);
        return ResponseEntity.ok(Result.of(result));
    }

    @GetMapping("/verification")
    public ResponseEntity<?> getTokenVerification(@RequestAttribute(D_TOKEN) String token) {
        var result = this.securityService.getTokenVerification(token);
        return ResponseEntity.ok(Result.of(result));
    }

    @Authorized(BASIC)
    @PostMapping("/delete")
    public ResponseEntity<?> deleteToken(@RequestAttribute(D_TOKEN) String token,
                                         @RequestParam(name = "id", required = false) List<Integer> idList) {
        // 登出删除Token
        if (CollectionUtils.isEmpty(idList)) {
            this.securityService.deleteTheToken(token);
        }
        // 管理删除Token
        else {
            this.securityService.deleteSpecificToken(token, idList);
        }
        return ResponseEntity.ok(Result.empty());
    }

    @Authorized(BASIC)
    @GetMapping("/list")
    public ResponseEntity<?> getTokenList(@RequestAttribute(D_TOKEN) String token) {
        var tokenList = this.securityService.getAssociatedTokenList(token);
        return ResponseEntity.ok(Result.of(tokenList));
    }

    @Authorized(NANO_API)
    @PostMapping("/pruneVerifyingTimeoutToken")
    public ResponseEntity<?> verifyingTimeoutToken() {
        var count = this.securityService.pruneVerifyingTimeoutToken();
        return ResponseEntity.ok(Result.of(Map.of("count", count)));
    }
}
