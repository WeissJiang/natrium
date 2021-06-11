package nano.web.telegram.handler;

import nano.support.Onion;
import nano.web.accounting.AccountingService;
import nano.web.accounting.model.AccountingDateData;
import nano.web.accounting.model.AccountingMonthDataView;
import nano.web.nano.model.Bot;
import nano.web.security.Privilege;
import nano.web.telegram.BotContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

import static nano.support.Sugar.render;

@Component
public class Nano000Handler implements Onion.Middleware<BotContext> {

    private final AccountingService accountingService;

    public Nano000Handler(AccountingService accountingService) {
        this.accountingService = accountingService;
    }

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        if (Bot.NANO_000.equals(context.bot().getName())) {
            var userPrivilegeList = context.userPrivilegeList();
            if (!userPrivilegeList.contains(Privilege.ACCOUNTING)
                    && !userPrivilegeList.contains(Privilege.NANO_API)) {
                context.replyMessage("权限不足");
                return;
            }
            var text = context.text();
            if (ObjectUtils.isEmpty(text)) {
                return;
            }
            if (context.commands().stream().anyMatch(it -> it.contains("/accounting"))) {
                handleAccounting(context);
            } else if (context.commands().stream().anyMatch(it -> it.contains("/record"))) {
                handleRecord(context);
            }

        } else {
            next.next();
        }
    }

    private void handleRecord(BotContext context) {
        var text = context.text();
        var now = Instant.now();
        var date = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneOffset.of("+8"))
                .format(now);
        var month = date.substring(0, 7);
        var monthData = this.accountingService.getAccountingMonthData(month);
        if (monthData == null) {
            monthData = this.accountingService.createThisEmptyMonthData();
        }
        var split = text.split(" ");
        if (split.length < 2) {
            context.replyMessage("非法数据，数据格式：/record <总金额>,<笔数>，如：/record 10000,10");
            return;
        }
        var data = split[1];
        var split2 = data.split(",");
        if (split2.length < 2) {
            context.replyMessage("非法数据，数据格式：/record <总金额>,<笔数>，如：/record 10000,10");
            return;
        }
        int totalAmount;
        int quantity;
        try {
            totalAmount = Integer.parseInt(split2[0]);
            quantity = Integer.parseInt(split2[1]);
        } catch (NumberFormatException ex) {
            context.replyMessage("非法数据，数据格式：/record <总金额>,<笔数>，如：/record 10000,10");
            return;
        }
        var dateOptional = monthData.getDetail().stream().filter(it -> it.getDate().equals(date)).findFirst();
        if (dateOptional.isEmpty()) {
            return;
        }
        var theDate = dateOptional.get();
        theDate.setQuantity(quantity);
        theDate.setTotalAmount(totalAmount);
        this.accountingService.updateAccountingMonthData(monthData);
        context.replyMessage("录入成功");
    }

    private void handleAccounting(@NotNull BotContext context) {
        var text = context.text();
        var now = Instant.now();
        var date = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneOffset.of("+8"))
                .format(now);
        var split = text.split(" ");
        if (split.length >= 2) {
            date = split[1];
        }
        if (!date.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            context.replyMessage("非法日期，日期格式：/accounting <yyyy-MM-dd>，如：/accounting 2021-06-01");
            return;
        }
        var month = date.substring(0, 7);
        var monthData = this.accountingService.getAccountingMonthDataView(month);
        if (monthData == null) {
            context.replyMessage("该月份无数据");
            return;
        }
        var message = buildAccountingMessage(monthData, date);
        if (message == null) {
            context.replyMessage("该月份无数据");
            return;
        }
        context.replyMessage(message);
    }

    private static String buildAccountingMessage(@NotNull AccountingMonthDataView monthData, @NotNull String date) {
        var detail = monthData.getDetail();
        var dataViewOptional = detail.stream().filter(it -> Objects.equals(it.getDate(), date)).findFirst();
        if (dataViewOptional.isEmpty()) {
            return null;
        }
        var dateView = dataViewOptional.get();
        var template = """
                ${date}总金额：${totalAmount}
                已经下发：${singleAmount} * ${quantity} = ${handOutAmount}
                结余：${lastBalance} + ${balanceAmount} = ${rawBalance}
                扣除下发结余：${balanceAmountTheDay}
                """;
        var scope = Map.of(
                "date", dateView.getDate(),
                "totalAmount", format(dateView.getTotalAmount()),
                "singleAmount", format(dateView.getSingleAmount()),
                "quantity", format(dateView.getQuantity()),
                "lastBalance", format(dateView.getLastBalance()),
                "handOutAmount", format(dateView.getHandOutAmount()),
                "balanceAmount", format(dateView.getBalanceAmount()),
                "rawBalance", format(dateView.getLastBalance() + dateView.getBalanceAmount()),
                "balanceAmountTheDay", format(dateView.getBalanceAmountTheDay())
        );
        return render(template, scope);
    }

    private static String format(int number) {
        return NumberFormat.getInstance().format(number);
    }
}
