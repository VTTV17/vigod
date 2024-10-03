package api.Seller.customers;

import api.Seller.login.Login;
import io.restassured.response.Response;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utilities.api.API;
import utilities.model.dashboard.customer.CustomerDebtRecord;
import utilities.model.dashboard.customer.CustomerInfo;
import utilities.model.dashboard.customer.CustomerInfoFull;
import utilities.model.dashboard.customer.CustomerOrder;
import utilities.model.dashboard.customer.CustomerOrderSummary;
import utilities.model.dashboard.customer.CustomerProfileFB;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.marketing.loyaltyProgram.LoyaltyProgramInfo;
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
    String orderSummaryPath = "/orderservices2/api/customer-orders/store/<storeId>/customerId/<customerId>/summary";
    String membershipPath = "/beehiveservices/api/memberships/<storeId>/<customerId>";
    String pointPath = "/orderservices2/api/loyalty-earning-points/all-point-types/summary?storeId=%s&buyerId=%s";
    String orderPath = "/beehiveservices/api/bc-orders/orders/storeId/%s?page=0&size=50&userId=%s&customerId=%s&userIdChannel=";
    String debtRecordPath = "/orderservices2/api/customer-debt/get-all/storeId/<storeId>/customerId/<customerId>?page=0&size=50";
    
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

    public CustomerInfoFull getFullInfo(int customerId){
        Response getCustomerInfo = api.get(CUSTOMER_INFORMATION_PATH.formatted(loginInfo.getStoreID(), customerId), loginInfo.getAccessToken()).then()
                .statusCode(200)
                .extract()
                .response();
        return getCustomerInfo.as(CustomerInfoFull.class);
    }
    
    public CustomerOrderSummary getOrderSummary(int customerId) {
		String basePath = orderSummaryPath.replaceAll("<storeId>", String.valueOf(loginInfo.getStoreID())).replaceAll("<customerId>", String.valueOf(customerId));
		String token = loginInfo.getAccessToken();
    	
		Response response = api.get(basePath, token).then().statusCode(200).extract().response();
		return response.as(CustomerOrderSummary.class);
    }    
    
    /**
     * Retrieve the membership labeled to a customer
     * @param customerId Do not mistake customerId (customer-profile table) for userId (jhi_user table)
     * @return LoyaltyProgramInfo DTO
     */
    public LoyaltyProgramInfo getMembership(int customerId) {
    	String basePath = membershipPath.replaceAll("<storeId>", String.valueOf(loginInfo.getStoreID())).replaceAll("<customerId>", String.valueOf(customerId));
    	String token = loginInfo.getAccessToken();
    	
    	Response response = api.get(basePath, token).then().statusCode(200).extract().response();
    	return response.as(LoyaltyProgramInfo.class);
    }    
    
    /**
     * Retrieve the points given to a customer
     * @param userId Do not mistake userId (jhi_user table) for customerId (customer-profile table)
     * @return Response object
     */
    public Response getPoint(int userId) {
    	String basePath = pointPath.formatted(loginInfo.getStoreID(), userId);
    	String token = loginInfo.getAccessToken();
    	
    	Response response = api.get(basePath, token).then().statusCode(200).extract().response();
    	return response;
    }
    public int getEarningPoint(int userId) {
    	Object earningPoint = getPoint(userId).jsonPath().get("findAll { it.'event' == 'EARN' }.value[0]");
    	
    	if (earningPoint ==null) return 0;
    	
    	return (int)earningPoint;
    }
    
    /**
     * Retrieve the orders belonging to a customer
     * @param customerId (customer-profile table)
     * @param userId (jhi_user table) for customerId (customer-profile table)
     * @return CustomerOrder DTO
     */
    public List<CustomerOrder> getOrders(int customerId, int userId) {
    	String basePath = orderPath.formatted(loginInfo.getStoreID(), userId, customerId);
    	String token = loginInfo.getAccessToken();
    	
    	Response response = api.get(basePath, token).then().statusCode(200).extract().response();
    	return response.jsonPath().getList(".", CustomerOrder.class);
    }    
    
    /**
     * Retrieve the debt records belonging to a customer
     * @param customerId (customer-profile table)
     * @return CustomerDebtRecord DTO
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     */
    public List<CustomerDebtRecord> getDebtRecords(int customerId) throws JsonMappingException, JsonProcessingException {
    	String basePath = debtRecordPath.replaceAll("<storeId>", String.valueOf(loginInfo.getStoreID())).replaceAll("<customerId>", String.valueOf(customerId));
    	String token = loginInfo.getAccessToken();
    	
    	Response response = api.get(basePath, token).then().statusCode(200).extract().response();
    	
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
    	
    	return mapper.readValue(response.asPrettyString(), new TypeReference<List<CustomerDebtRecord>>() {});
    }    
}
