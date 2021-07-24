package nano.service.controller.accounting;

import nano.service.accounting.AccountingService;
import nano.service.accounting.model.AccountingMonthData;
import nano.service.accounting.model.AccountingMonthDataView;
import nano.service.security.Authorized;
import nano.service.security.Privilege;
import nano.support.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Authorized(privilege = {Privilege.NANO_API, Privilege.ACCOUNTING})
@RestController
@RequestMapping("/api/accounting")
public class AccountingController {

    private final AccountingService accountingService;

    public AccountingController(AccountingService accountingService) {
        this.accountingService = accountingService;
    }

    @GetMapping("/monthData")
    public ResponseEntity<?> getApp(@RequestParam("month") String month) {
        var result = this.accountingService.getAccountingMonthDataView(month);
        return ResponseEntity.ok(Result.of(result));
    }

    @PostMapping("/monthData")
    public ResponseEntity<?> createApp(@RequestBody AccountingMonthData data) {
        this.accountingService.createAccountingMonthData(data);
        return ResponseEntity.ok(Result.of(new AccountingMonthDataView(data)));
    }

    @PutMapping("/monthData")
    public ResponseEntity<?> updateApp(@RequestBody AccountingMonthData data) {
        this.accountingService.updateAccountingMonthData(data);
        return ResponseEntity.ok(Result.of(new AccountingMonthDataView(data)));
    }

}
