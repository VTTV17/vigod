package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class PaymentHistoryInfo {
    private int id;
    private String createDate;
    private String paymentMethod;
    private double paymentAmount;
    private String note;
    private String paymentReceivedBy;
    private long bcOrderId;
}
