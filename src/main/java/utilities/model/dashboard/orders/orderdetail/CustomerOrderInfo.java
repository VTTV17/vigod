package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class CustomerOrderInfo {
    private Long customerId;
    private String name;
    private String email;
    private String phone;
    private Long userId;
    private double debtAmount;
    private Boolean guest;
    private Avatar avatar;
}
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
class Avatar {
    private int imageId;
    private String imageUUID;
    private String urlPrefix;
    private String extension;
    private String fullUrl;
}