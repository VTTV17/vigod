package utilities.model.dashboard.orders.ordermanagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class OrderListInfo {
    private OrderListSummaryVM orderListSummaryVM;
    private List<OrderInManagement> response;
}

