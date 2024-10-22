package utilities.model.dashboard.products.inventory;

import api.Seller.orders.order_management.APIAllOrders;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class InventoryMapping {
    int branchId;
    long itemId;
    Long modelId;
    int stock;
    APIAllOrders.Channel channel;
    String inventoryId;
    public InventoryMapping(int branchId, long itemId, int stock, APIAllOrders.Channel channel,String inventoryId ){
        this.branchId = branchId;
        this.itemId = itemId;
        this.stock = stock;
        this.channel = channel;
        this.inventoryId = inventoryId;
    };
}
