package utilities.model.dashboard.customer.create;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import utilities.model.dashboard.customer.CustomerEmail;
import utilities.model.dashboard.customer.CustomerGeoLocation;
import utilities.model.dashboard.customer.CustomerPhone;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
public class CreateCustomer {

	String name;
	String phone;
	String email;
	String note;
	List<String> tags;
	
    String address;
    String address2;
    String city;
    String zipCode;
    String locationCode;
    String districtCode;
    String wardCode;
    Boolean isCreateUser;
    String gender;
    String birthday;
    String countryCode;
    CustomerGeoLocation geoLocation;
    List<CustomerPhone> phones;
    CustomerEmail emails;
    String storeName;
    String langKey;
    String branchId;	
	
}