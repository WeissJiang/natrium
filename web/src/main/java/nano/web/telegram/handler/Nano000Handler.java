package nano.web.telegram.handler;

import nano.support.Json;
import nano.support.Onion;
import nano.web.accounting.AccountingService;
import nano.web.accounting.model.AccountingMonthDataView;
import nano.web.nano.model.Bot;
import nano.web.telegram.BotContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Component
public class Nano000Handler implements Onion.Middleware<BotContext> {

    private final AccountingService accountingService;

    public Nano000Handler(AccountingService accountingService) {
        this.accountingService = accountingService;
    }

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        if (Bot.NANO_000.equals(context.bot().getName())) {
            var text = context.text();
            if (ObjectUtils.isEmpty(text)) {
                return;
            }
            if (context.commands().stream().noneMatch(it -> it.contains("/accounting"))) {
                return;
            }
            var now = Instant.now();
            var date = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    .withZone(ZoneOffset.of("+8"))
                    .format(now);
            var split = text.split(" ");
            if (split.length >= 2) {
                date = split[1];
            }
            var month = date.substring(0, 7);
            var monthData = this.accountingService.getAccountingMonthData(month);
            if (monthData == null) {
                return;
            }
            var message = buildMessage(monthData, date);
            if (message == null) {
                return;
            }
            context.replyMessage(message);
        } else {
            next.next();
        }
    }

    private static String buildMessage(@NotNull AccountingMonthDataView monthData, @NotNull String date) {
        var detail = monthData.getDetail();
        var dataViewOptional = detail.stream().filter(it -> Objects.equals(it.getDate(), date)).findFirst();
        if (dataViewOptional.isEmpty()) {
            return null;
        }
        var dateView = dataViewOptional.get()
        return Json.encode(dateView);
    }
}
