package nano.service.controller.carbon;

import nano.service.carbon.model.CarbonApp;
import nano.service.security.Authorized;
import nano.service.security.Privilege;
import nano.support.Result;
import nano.service.carbon.CarbonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/carbon")
public class CarbonController {

    private final CarbonService carbonService;

    public CarbonController(CarbonService carbonService) {
        this.carbonService = carbonService;
    }

    @GetMapping("/text")
    public ResponseEntity<?> getText(@RequestParam("appId") String appId,
                                     @RequestParam("key") String key,
                                     @RequestParam("locale") String locale) {
        var text = this.carbonService.getText(appId, key, locale);
        return ResponseEntity.ok(Result.of(text));
    }

    @GetMapping("/app/list")
    public ResponseEntity<?> getAppList() {
        var appList = this.carbonService.getAppList();
        return ResponseEntity.ok(Result.of(appList));
    }

    @GetMapping("/app")
    public ResponseEntity<?> getApp(@RequestParam("appId") String appId) {
        var app = this.carbonService.getApp(appId);
        return ResponseEntity.ok(Result.of(app));
    }

    @Authorized(privilege = Privilege.NANO_API)
    @PostMapping("/app")
    public ResponseEntity<?> createApp(@RequestBody CarbonApp app) {
        this.carbonService.createApp(app);
        return ResponseEntity.ok(Result.empty());
    }

    @Authorized(privilege = Privilege.NANO_API)
    @PutMapping("/app")
    public ResponseEntity<?> updateApp(@RequestBody CarbonApp app) {
        this.carbonService.updateApp(app);
        return ResponseEntity.ok(Result.empty());
    }

}
