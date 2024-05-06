package api;

public class CapchaPayloadBuilder extends JsonObjectBuilder {

    public CapchaPayloadBuilder givenCaptchaResponse(String value) {
        jsonData.put("captchaResponse", value);
        return this;
    }
    
    public CapchaPayloadBuilder givenGReCaptchaResponse(String value) {
    	jsonData.put("g-recaptcha-response", value);
    	return this;
    }

    public CapchaPayloadBuilder givenImageBase64(String value) {
        jsonData.put("imageBase64", value);
        return this;
    }
}
