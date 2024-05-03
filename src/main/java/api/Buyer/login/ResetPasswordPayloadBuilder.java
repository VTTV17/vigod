package api.Buyer.login;

import api.JsonObjectBuilder;

public class ResetPasswordPayloadBuilder extends JsonObjectBuilder {
	
    public ResetPasswordPayloadBuilder givenEmail(String email) {
        jsonData.put("email", email);
        return this;
    }
    
    public ResetPasswordPayloadBuilder givenShopURL(String shopURL) {
    	jsonData.put("goSellShopUrl", shopURL);
    	return this;
    }
    
    public ResetPasswordPayloadBuilder givenShopName(String shopName) {
    	jsonData.put("goSellShopName", shopName);
    	return this;
    }
    
    public ResetPasswordPayloadBuilder givenCountryCode(String phoneCode) {
    	jsonData.put("countryCode", phoneCode);
    	return this;
    }
    
    public ResetPasswordPayloadBuilder givenPhoneNumber(String phoneNumber) {
    	jsonData.put("phoneNumber", phoneNumber);
    	return this;
    }
    
}
