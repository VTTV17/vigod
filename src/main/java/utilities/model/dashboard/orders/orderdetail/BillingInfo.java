package utilities.model.dashboard.orders.orderdetail;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class BillingInfo {
    private String contactName;
    private String address1;
    private String address2;
    private String address3;
    private String address4;
    private String address5;
    private String phone;
    private String phone2;
    private String country;
    private String countryCode;
    private String district;
    private String ward;
    private String outSideCity;
    private String zipCode;
    private String phoneCode;
    private String email;
    private String insideCityCode;
    private String stateCode;
    private String fullAddress;
    private String fullAddressEn;
}
