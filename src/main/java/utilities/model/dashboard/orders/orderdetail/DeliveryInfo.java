package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class DeliveryInfo {
    private String createdDate;
    private String lastModifiedDate;
    private int id;
    private String contactName;
    private String phoneNumber;
    private String email;
    private String countryCode;
    private String locationCode;
    private String districtCode;
    private String address;
    private String address2;
    private String wardCode;
    private String city;
    private String zipCode;
    private String phoneCode;
    private Object geoLocation;
}
