package utilities.model.dashboard.orders.orderdetail;

import lombok.Data;

@Data
public class ItemTotalDiscount {
    private Double value;
    private String discountType;
    private String label;
    private int referenceId;
}
