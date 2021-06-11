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
            var date = it.getDate();
            var totalAmount = it.getTotalAmount();
            var serviceFee = (int) Math.floor(0.061 * totalAmount);
            var balanceAmount = totalAmount - serviceFee;
            var singleAmount = 49900;
            var quantity = it.getQuantity();
            var handOutAmount = singleAmount * quantity;
            var balanceAmountTheDay = lastBalance + balanceAmount - handOutAmount;

            lastBalance = balanceAmountTheDay;
            monthlySummary += totalAmount;

            var item = new AccountingDateDataView();
            item.setDate(date);
            item.setTotalAmount(totalAmount);
            item.setServiceFee(serviceFee);
            item.setBalanceAmount(balanceAmount);
            item.setSingleAmount(singleAmount);
            item.setQuantity(quantity);
            item.setHandOutAmount(handOutAmount);
            item.setBalanceAmountTheDay(balanceAmountTheDay);
            detail.add(item);
        }
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

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Integer getTotalAmount() {
            return totalAmount;
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
