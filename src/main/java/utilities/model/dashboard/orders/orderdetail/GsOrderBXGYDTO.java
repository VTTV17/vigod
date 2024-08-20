package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class GsOrderBXGYDTO {
    private Long itemId;
    private Long modelId;
    private Long bxgyId;
    private Long bcOrderId;
    private double promoAmount;
    private Long orderItemId;
    private String createdDate;
    private String lastModifiedDate;
    private String sku;
    private String bxgyName;
    private String promotionType;
    private String giftType;
}
