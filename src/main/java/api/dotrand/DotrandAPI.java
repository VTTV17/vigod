package api.dotrand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.restassured.response.Response;
import utilities.api.API;

public class DotrandAPI {
	final static Logger logger = LogManager.getLogger(DotrandAPI.class);
	
	static public String getPhoneRegexJsonPath(String countryCode) {
		Response results = new API("https://api.dotrand.com").get("/api/v1/phone?iso2=%s".formatted(countryCode), "noTokenNeeded");
		String format = results.then().statusCode(200).extract().jsonPath().getString("data.format");
		logger.info("Retrieved phone regex for {}: {}",countryCode, format);
		return format;
	}
}
