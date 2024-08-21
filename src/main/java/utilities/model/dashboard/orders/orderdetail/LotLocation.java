package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class LotLocation {
    private String itemName;
    private int lotDateId;
    private int quantity;
    private int itemId;
    private String itemImage;
    private boolean hasLot;
    private List<Lot> lots;
    private List<Location> locations;
    private String modelName;
    private int modelId;
    private String barCode;
}
