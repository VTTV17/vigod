package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class OrderItemIMEIs {
    private Long id;
    private Long orderItemId;
    private String imeiSerial;
}
