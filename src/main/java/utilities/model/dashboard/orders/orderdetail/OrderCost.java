package utilities.model.dashboard.orders.orderdetail;

import lombok.Data;

@Data
public class OrderCost {
    private Integer id;
    private Double amount;
    private String name;
    private Long orderId;
}
