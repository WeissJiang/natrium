package nano.web.telegram.handler;

import nano.support.Json;
import nano.support.Onion;
import nano.web.accounting.AccountingService;
import nano.web.nano.model.Bot;
import nano.web.telegram.BotContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

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
            context.replyMessage(Json.encode(monthData));
        } else {
            next.next();
        }
    }
}
