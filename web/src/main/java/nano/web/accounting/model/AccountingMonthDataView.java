package nano.web.accounting.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AccountingMonthDataView {

    private String month;
    private List<AccountingDateDataView> detail;
    private Integer beginningBalance;
    private Integer monthlySummary;

    public AccountingMonthDataView(@NotNull AccountingMonthData accountingMonthData) {
        var detail = new ArrayList<AccountingDateDataView>();
        var lastBalance = accountingMonthData.getBeginningBalance();
        var monthlySummary = 0;
        for (var it : accountingMonthData.getDetail()) {
            var item = new AccountingDateDataView();
            item.setDate(it.getDate());
            item.setLastBalance(lastBalance);

            var totalAmount = it.getTotalAmount();
            item.setTotalAmount(it.getTotalAmount());

            var serviceFee = (int) Math.floor(0.061 * totalAmount);
            item.setServiceFee(serviceFee);

            var balanceAmount = totalAmount - serviceFee;
            item.setBalanceAmount(balanceAmount);

            var singleAmount = 49900;
            item.setSingleAmount(singleAmount);

            var quantity = it.getQuantity();
            item.setQuantity(quantity);

            var handOutAmount = singleAmount * quantity;
            item.setHandOutAmount(handOutAmount);

            var balanceAmountTheDay = lastBalance + balanceAmount - handOutAmount;
            item.setBalanceAmountTheDay(balanceAmountTheDay);

            detail.add(item);
            //
            lastBalance = balanceAmountTheDay;
            monthlySummary += totalAmount;
        }
        this.month = accountingMonthData.getMonth();
        this.detail = detail;
        this.beginningBalance = accountingMonthData.getBeginningBalance();
        this.monthlySummary = monthlySummary;
    }

    public static class AccountingDateDataView {

        private String date;
        private Integer totalAmount;
        private Integer quantity;
        private Integer serviceFee;
        private Integer balanceAmount;
        private Integer singleAmount;
        private Integer handOutAmount;
        private Integer balanceAmountTheDay;
        private Integer lastBalance;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Integer getTotalAmount() {
            return totalAmount;
        }

        public Integer getLastBalance() {
            return lastBalance;
        }

        public void setLastBalance(Integer lastBalance) {
            this.lastBalance = lastBalance;
        }

        public void setTotalAmount(Integer totalAmount) {
            this.totalAmount = totalAmount;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Integer getServiceFee() {
            return serviceFee;
        }

        public void setServiceFee(Integer serviceFee) {
            this.serviceFee = serviceFee;
        }

        public Integer getBalanceAmount() {
            return balanceAmount;
        }

        public void setBalanceAmount(Integer balanceAmount) {
            this.balanceAmount = balanceAmount;
        }

        public Integer getSingleAmount() {
            return singleAmount;
        }

        public void setSingleAmount(Integer singleAmount) {
            this.singleAmount = singleAmount;
        }

        public Integer getHandOutAmount() {
            return handOutAmount;
        }

        public void setHandOutAmount(Integer handOutAmount) {
            this.handOutAmount = handOutAmount;
        }

        public Integer getBalanceAmountTheDay() {
            return balanceAmountTheDay;
        }

        public void setBalanceAmountTheDay(Integer balanceAmountTheDay) {
            this.balanceAmountTheDay = balanceAmountTheDay;
        }
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public List<AccountingDateDataView> getDetail() {
        return detail;
    }

    public void setDetail(List<AccountingDateDataView> detail) {
        this.detail = detail;
    }

    public Integer getBeginningBalance() {
        return beginningBalance;
    }

    public void setBeginningBalance(Integer beginningBalance) {
        this.beginningBalance = beginningBalance;
    }

    public Integer getMonthlySummary() {
        return monthlySummary;
    }

    public void setMonthlySummary(Integer monthlySummary) {
        this.monthlySummary = monthlySummary;
    }
}
