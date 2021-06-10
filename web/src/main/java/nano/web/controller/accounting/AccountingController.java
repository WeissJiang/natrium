package nano.web.controller.accounting;

import nano.support.Result;
import nano.web.accounting.AccountingService;
import nano.web.accounting.model.AccountingMonthData;
import nano.web.security.Authorized;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static nano.web.security.Privilege.*;

@Authorized(privilege = {NANO_API, ACCOUNTING})
@RestController
@RequestMapping("/api/accounting")
public class AccountingController {

    private final AccountingService accountingService;

    public AccountingController(AccountingService accountingService) {
        this.accountingService = accountingService;
    }

    @GetMapping("/monthData")
    public ResponseEntity<?> getApp(@RequestParam("month") String month) {
        var data = this.accountingService.getAccountingMonthData(month);
        return ResponseEntity.ok(Result.of(data));
    }

    @PostMapping("/monthData")
    public ResponseEntity<?> createApp(@RequestBody AccountingMonthData data) {
        this.accountingService.createAccountingMonthData(data);
        return ResponseEntity.ok(Result.empty());
    }

    @PutMapping("/monthData")
    public ResponseEntity<?> updateApp(@RequestBody AccountingMonthData data) {
        this.accountingService.updateAccountingMonthData(data);
        return ResponseEntity.ok(Result.empty());
    }

}
