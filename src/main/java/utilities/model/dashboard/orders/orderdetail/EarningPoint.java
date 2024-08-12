package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class EarningPoint {
    private int id;
    private double value;
    private String event;
    private int storeId;
    private int buyerId;
    private int sourceId;
    private String sourceType;
    private String earnDay;
    private double remainingValue;
}
