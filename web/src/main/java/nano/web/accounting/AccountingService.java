package nano.web.accounting;

import nano.support.Json;
import nano.web.accounting.model.AccountingDateData;
import nano.web.accounting.model.AccountingMonthData;
import nano.web.accounting.model.AccountingMonthDataView;
import nano.web.nano.repository.KeyValueRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
public class AccountingService {

    private static final String ACCOUNTING = "accounting";

    private final KeyValueRepository keyValueRepository;

    public AccountingService(KeyValueRepository keyValueRepository) {
        this.keyValueRepository = keyValueRepository;
    }

    public @Nullable AccountingMonthData getAccountingMonthData(@NotNull String month) {
        var keyValue = this.keyValueRepository.queryKeyValue("%s:%s".formatted(ACCOUNTING, month));
        if (keyValue == null) {
            return null;
        }
        var value = keyValue.getValue();
        if (value == null) {
            return null;
        }
        return Json.decodeValue(value, AccountingMonthData.class);
    }

    public @Nullable AccountingMonthDataView getAccountingMonthDataView(@NotNull String month) {
        var monthData = this.getAccountingMonthData(month);
        if (monthData == null) {
            return null;
        }
        return new AccountingMonthDataView(monthData);
    }

    public void createAccountingMonthData(@NotNull AccountingMonthData data) {
        this.keyValueRepository.createKeyValue("%s:%s".formatted(ACCOUNTING, data.getMonth()), Json.encode(data));
    }

    public void updateAccountingMonthData(@NotNull AccountingMonthData data) {
        this.keyValueRepository.updateKeyValue("%s:%s".formatted(ACCOUNTING, data.getMonth()), Json.encode(data));
    }

    public @NotNull AccountingMonthData createThisEmptyMonthData() {
        var data = new AccountingMonthData();
        var yearMonth = YearMonth.now(ZoneId.of("+8"));
        var month = yearMonth.toString();
        data.setMonth(month);
        data.setBeginningBalance(0);
        var detail = new ArrayList<AccountingDateData>();

        for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
            var dateData = new AccountingDateData();
            dateData.setTotalAmount(0);
            dateData.setQuantity(0);
            var _date = i < 10 ? "0" + i : String.valueOf(i);
            dateData.setDate(month + "-" + _date);
            detail.add(dateData);
        }
        data.setDetail(detail);
        this.createAccountingMonthData(data);
        return data;
    }
}
