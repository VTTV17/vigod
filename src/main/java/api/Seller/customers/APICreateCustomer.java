package api.Seller.customers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import api.Seller.login.Login;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import utilities.api.API;
import utilities.model.dashboard.customer.create.CreateCustomerModel;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APICreateCustomer {
    Logger logger = LogManager.getLogger(APICreateCustomer.class);

    private String checkPhoneDuplicatePath = "/beehiveservices/api/customer-profiles/check-phone/<storeId>";
    private String createCustomerPath = "/beehiveservices/api/customer-profiles/POS/<storeId>";
    
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    
    public APICreateCustomer (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
    	loginInfo = new Login().getInfo(loginInformation);
    }
   
	public boolean checkPhoneDuplicate(String phoneCode, String phoneNumber) {
		JSONObject payload = new JSONObject();
		payload.put("phoneCode", phoneCode);
		payload.put("phoneNumber", phoneNumber);
		
		String basePath = checkPhoneDuplicatePath.replaceAll("<storeId>", String.valueOf(loginInfo.getStoreID()));
		String token = loginInfo.getAccessToken();
		
		Response response = api.post(basePath, token, payload.toString());
		return response.getBody().as(boolean.class);
	} 
	
	@SneakyThrows
	public Response createCustomer(CreateCustomerModel customerData) {
		
		String basePath = createCustomerPath.replaceAll("<storeId>", String.valueOf(loginInfo.getStoreID()));
		String token = loginInfo.getAccessToken();
		String payload = new ObjectMapper().writeValueAsString(customerData);
		
		Response response = api.post(basePath, token, payload);
		response.then().statusCode(200);
		return response;
	} 

}
