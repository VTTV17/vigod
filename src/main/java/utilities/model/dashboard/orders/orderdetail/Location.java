package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class Location {
    private Long id;
    private String locationName;
    private String locationCode;
    private String locationPath;
    private String locationPathName;
    private Long quantity;
    private Long selectedQuantity;
}
