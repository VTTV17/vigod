package utilities.model.dashboard.customer.create;

import java.util.List;

import lombok.Data;
@Data
public class UICreateCustomerData {

	String name;
	String phone;
	String email;
	String note;
	List<String> tags;
	
	String country;
    String address;
    String address2;
    String city;
    String zipCode;
    String province;
    String district;
    String ward;
    
    Boolean isCreateUser;
    String gender;
    String birthday;
}