package utilities.model.dashboard.orders.ordermanagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class OrderListSummaryVM {
    private int toConfirmCount;
    private int shippedCount;
    private int deliveredCount;
    private int cancelledCount;
    private double customerDebt;
    private double sellerDebt;
    private double receivedAmount;
}
