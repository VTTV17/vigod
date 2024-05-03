package api.Seller.signup;

import java.util.HashMap;
import java.util.Map;

import api.JsonObjectBuilder;

public class RegisterPayloadBuilder extends JsonObjectBuilder {
	
    public RegisterPayloadBuilder givenDisplayName(String displayName) {
        jsonData.put("displayName", displayName);
        return this;
    }

    public RegisterPayloadBuilder givenPassword(String password) {
        jsonData.put("password", password);
        return this;
    }

    public RegisterPayloadBuilder givenLocationCode(String locationCode) {
        jsonData.put("locationCode", locationCode);
        return this;
    }

    public RegisterPayloadBuilder givenLangKey(String langKey) {
        jsonData.put("langKey", langKey);
        return this;
    }

    public RegisterPayloadBuilder givenMobile(String countryCode, String phoneNumber) {
        Map<String, Object> mobile = new HashMap<>();
        mobile.put("countryCode", countryCode);
        mobile.put("phoneNumber", phoneNumber);
        jsonData.put("mobile", mobile);
        return this;
    }

}
