package nano.web.controller.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.web.controller.Result;
import nano.web.security.Authorized;
import nano.web.security.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static nano.web.security.TokenCode.*;

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
    public ResponseEntity<?> getTokenVerification(@RequestAttribute(D_TOKEN) String token) {
        var result = this.securityService.getTokenVerification(token);
        return ResponseEntity.ok(Result.of(result));
    }

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

    @GetMapping("/list")
    public ResponseEntity<?> getTokenList(@RequestAttribute(D_TOKEN) String token) {
        var tokenList = this.securityService.getAssociatedTokenList(token);
        return ResponseEntity.ok(Result.of(tokenList));
    }

    @Authorized
    @PostMapping("/pruneVerificatingTimeoutToken")
    public ResponseEntity<?> verificatingTimeoutToken() {
        var count = this.securityService.pruneVerificatingTimeoutToken();
        return ResponseEntity.ok(Result.of(Map.of("count", count)));
    }
}
