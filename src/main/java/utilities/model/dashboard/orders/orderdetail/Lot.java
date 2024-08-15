package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class Lot {
    private int id;
    private int storeId;
    private String lotName;
    private String lotCode;
    private String manufactureDate;
    private String expiryDate;
    private String expiredInValues;
    private Long remainingStock;
    private Long remainingExpiryDays;
    private Long selectedQuantity;
    private boolean isLotDeleted;
    private Long remainingReturnStock;
    private List<Location> locations;
}
