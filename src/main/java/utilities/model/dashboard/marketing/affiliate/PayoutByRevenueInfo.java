package utilities.model.dashboard.marketing.affiliate;

import lombok.Data;

import java.util.List;

@Data
public class PayoutByRevenueInfo {
    long potentialRevenue;
    long paidAmount;
    String partnerCode;
    String partnerName;
    String partnerStatus;
    String partnerType;
    long payableAmount;
    long totalIncome;
    long commissionRevenue;
    long individualRevenue;
    long teamRevenue;
    long incomeAfterTax;
    int id;
    int partnerId;
    String typeCommission;
    String path;
    List<Object> teamFxRevenue;
    List<Object> subIncomes;
    int commissionRate;
    int tax;
    int commissionId;
}
