package api.Seller.signup;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import api.CapchaPayloadBuilder;
import api.JsonObjectBuilder;
import io.restassured.response.Response;
import utilities.api.API;

public class SignUpDashboardAPI {
    String signupPath = "/api/register/gosell";
    API api = new API();

    public Response signupPhoneResponse(String phoneCode, String phone, String locationCode, String langKey, String password) {
    	
    	JSONObject capchaPayload = new CapchaPayloadBuilder().givenCaptchaResponse("").givenGReCaptchaResponse("").givenImageBase64("").build();
    	JSONObject registerPayload = new RegisterPayloadBuilder().givenDisplayName("").givenPassword(password).givenLocationCode(locationCode)
    			.givenLangKey(langKey).givenMobile(phoneCode, phone).build();
    	String body = JsonObjectBuilder.mergeJSONObjects(capchaPayload, registerPayload).toString();
        
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Host", "api.beecow.info");
        headerMap.put("x-request-origin", "DASHBOARD");
        headerMap.put("isinternational", "false");
        headerMap.put("platform", "WEB");

        Response signupResponse = api.post(signupPath, "noTokenNeeded", body, headerMap);

        signupResponse.then().log().ifValidationFails().statusCode(201);

        return signupResponse;
    }
    
}
