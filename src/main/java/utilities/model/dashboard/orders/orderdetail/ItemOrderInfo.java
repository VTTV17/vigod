package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class ItemOrderInfo {
    private Long id;
    private Long itemId;
    private String name;
    private double price;
    private double totalAmount;
    private String currency;
    private String imageUrl;
    private int quantity;
    private double weight;
    private double height;
    private double length;
    private double width;
    private String createdDate;
    private Boolean isDeposit;
    private Boolean flashSale;
    private String inventoryManageType;
    private List<OrderItemIMEIs> orderItemIMEIs;
    private Boolean isHasLot;
    private Boolean isHasLocation;
    private Boolean isOrderCreatedBeforeItemEnabledLotDate;
    private List<Object> lstReturnedImei;
    private double totalDiscount;
    private double priceDiscount;
    private int totalQuantity;
    private List<ItemTotalDiscount> itemTotalDiscounts;
    private Boolean deposit;
    private Boolean hasLot;
    private Boolean hasLocation;
    private String conversionUnitName;
    private Long conversionUnitItemId;
    private String parentId;
    private Long variationId;
    private String variationName;
    private LotLocation lotLocation;
    private String sku;
    private GsOrderBXGYDTO gsOrderBXGYDTO;
}
