package nano.web.accounting;

import nano.support.Json;
import nano.web.accounting.model.AccountingMonthData;
import nano.web.accounting.model.AccountingMonthDataView;
import nano.web.nano.repository.KeyValueRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
public class AccountingService {

    private static final String ACCOUNTING = "accounting";

    private final KeyValueRepository keyValueRepository;

    public AccountingService(KeyValueRepository keyValueRepository) {
        this.keyValueRepository = keyValueRepository;
    }

    public @Nullable AccountingMonthDataView getAccountingMonthData(@NotNull String month) {
        var keyValue = this.keyValueRepository.queryKeyValue("%s:%s".formatted(ACCOUNTING, month));
        if (keyValue == null) {
            return null;
        }
        var value = keyValue.getValue();
        if (value == null) {
            return null;
        }
        var monthData = Json.decodeValue(value, AccountingMonthData.class);
        if (monthData == null) {
            return null;
        }
        return new AccountingMonthDataView(monthData);
    }

    public @NotNull AccountingMonthDataView createAccountingMonthData(@NotNull AccountingMonthData data) {
        this.keyValueRepository.createKeyValue("%s:%s".formatted(ACCOUNTING, data.getMonth()), Json.encode(data));
        return new AccountingMonthDataView(data);
    }

    public @NotNull AccountingMonthDataView updateAccountingMonthData(@NotNull AccountingMonthData data) {
        this.keyValueRepository.updateKeyValue("%s:%s".formatted(ACCOUNTING, data.getMonth()), Json.encode(data));
        return new AccountingMonthDataView(data);
    }
}
