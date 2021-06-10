package nano.web.accounting.model;

import java.util.List;

public class AccountingMonthData {

    private String month;
    private List<AccountingDateData> detail;
    private Integer beginningBalance;

    public List<AccountingDateData> getDetail() {
        return detail;
    }

    public void setDetail(List<AccountingDateData> detail) {
        this.detail = detail;
    }

    public Integer getBeginningBalance() {
        return beginningBalance;
    }

    public void setBeginningBalance(Integer beginningBalance) {
        this.beginningBalance = beginningBalance;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
