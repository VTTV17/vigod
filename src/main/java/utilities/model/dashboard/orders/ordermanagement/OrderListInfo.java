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

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class Item {
    private int id;
    private int itemId;
    private String itemModelId;
    private String modelId;
    private String name;
    private double price;
    private double totalDiscount;
    private int quantity;
    private double weight;
    private String imageUrl;
    private double costPrice;
    private String barcode;
    private double orgPrice;
    private String modelName;
    private List<Object>orderItemIMEIDTO;
    private String sku;
    private long bcOrderId;
}
