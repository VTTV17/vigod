package utilities.model.dashboard.storefront;

import lombok.Builder;
import lombok.Getter;
import utilities.enums.AccountType;

@Builder
@Getter
public class BuyerSignupData {
	
	AccountType type;
	String country;
	String countryCode;
	String phoneCode;
	String username;
	String password;
	String email;
	String phone;
	String displayName;
	String birthday;
	
}