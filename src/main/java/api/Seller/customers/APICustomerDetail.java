package api.Seller.customers;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.customer.CustomerInfo;
import utilities.model.dashboard.customer.CustomerProfileFB;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APICustomerDetail {
    Logger logger = LogManager.getLogger(APICustomerDetail.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APICustomerDetail (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
    	loginInfo = new Login().getInfo(loginInformation);
    }
    String CUSTOMER_INFORMATION_PATH = "/beehiveservices/api/customer-profiles/detail/%s/%s";
    String cusDetailGoSocialPath = "/beehiveservices/api/customer-profiles/social-chat/store/%s/profile/%s";

    public CustomerInfo getInfo(int customerId) {
        if (customerId != 0) {
            Response getCustomerInfo = api.get(CUSTOMER_INFORMATION_PATH.formatted(loginInfo.getStoreID(), customerId), loginInfo.getAccessToken()).then()
                    .statusCode(200)
                    .extract()
                    .response();

            CustomerInfo customerInfo = new CustomerInfo();
            customerInfo.setCustomerId(customerId);
            customerInfo.setMainEmail(getCustomerInfo.jsonPath().getString("emails[0].email"));
            customerInfo.setMainEmailName(getCustomerInfo.jsonPath().getString("emails[0].emailName"));
            customerInfo.setMainPhoneNumber(getCustomerInfo.jsonPath().getString("phones[0].phoneNumber"));
            customerInfo.setMainPhoneName(getCustomerInfo.jsonPath().getString("phones[0].phoneName"));
            customerInfo.setUserId(getCustomerInfo.jsonPath().getString("userId"));
            return customerInfo;
        } else return new CustomerInfo();
    }
    
    public CustomerProfileFB getCustomerInfoAtGoSocial(int customerId) {
		Response response = api.get(cusDetailGoSocialPath.formatted(loginInfo.getStoreID(), customerId), loginInfo.getAccessToken()).then()
				.statusCode(200)
				.extract()
				.response();
		return response.as(CustomerProfileFB.class);
    }
}
