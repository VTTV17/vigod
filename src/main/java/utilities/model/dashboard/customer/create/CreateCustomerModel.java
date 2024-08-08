package utilities.model.dashboard.customer.create;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import utilities.model.dashboard.customer.CustomerEmail;
import utilities.model.dashboard.customer.CustomerGeoLocation;
import utilities.model.dashboard.customer.CustomerPhone;

@Data
public class CreateCustomerModel {

	String name;
	String phone;
	String email = "";
	String note = "";
	List<String> tags = List.of();
	
    String address = "";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String address2; //empty if not provided, foreign address only
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String city; //empty if not provided, foreign address only
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String zipCode; //empty if not provided, foreign address only
    
    String locationCode = ""; //empty if not provided
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String districtCode; //empty if not provided, vn address only
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String wardCode; //empty if not provided, vn address only
    
    Boolean isCreateUser = false; //false if not provided
    String gender; //null if not provided
    String birthday; //null if not provided
    String countryCode;
    CustomerGeoLocation geoLocation = new CustomerGeoLocation(); //empty if not provided
    List<CustomerPhone> phones;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    CustomerEmail emails; //only present if provided
    
    String storeName;
    String langKey;
    String branchId;
	
}