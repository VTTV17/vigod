package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class ItemTotalDiscount {
    private Double value;
    private String discountType;
    private String label;
    private int referenceId;
    private String name;
    private Double maximumMembershipAmount;
}
