package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class DeliveryOrder {
    private String createdDate;
    private String lastModifiedDate;
    private int id;
    private int weight;
    private double originalFee;
    private double fee;
    private double feeDeduction;
    private String currency;
    private boolean freeShipping;
    private String status;
    private String statusUpdatedDate;
    private DeliveryInfo deliveryInfo;
    private PickUpAddress pickUpAddress;
    private int deliveryServiceId;
    private String providerName;
    private String serviceName;
    private int bcOrderId;
    private String shippingServiceNameVi;
    private String shippingServiceNameEn;
    private int length;
    private int width;
    private int height;
    private double codAmount;
}
