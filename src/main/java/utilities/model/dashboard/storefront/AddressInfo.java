package utilities.model.dashboard.storefront;

import lombok.Data;

@Data
public class AddressInfo {
    String country;
    String address; //address of country = VN
    String cityProvince;
    String ward;
    String district;
    String streetAddress;   //address 1 of country  = NonVN
    String address2;
    String stateRegionProvince;
    String city;
    String zipCode;
}
