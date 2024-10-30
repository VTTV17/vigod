package utilities.model.dashboard.products.inventory;

import api.Seller.orders.order_management.APIAllOrders;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InventoryMapping {
    int branchId;
    long itemId;
    String modelId;
    int stock;
    APIAllOrders.Channel channel;
    String inventoryId;
    public InventoryMapping(int branchId, long itemId, APIAllOrders.Channel channel,String inventoryId ){
        this.branchId = branchId;
        this.itemId = itemId;
        this.channel = channel;
        this.inventoryId = inventoryId;
    }
    public InventoryMapping(int branchId, long itemId,String modelId, APIAllOrders.Channel channel,String inventoryId ){
        this.branchId = branchId;
        this.itemId = itemId;
        this.modelId = modelId;
        this.channel = channel;
        this.inventoryId = inventoryId;
    };
}
