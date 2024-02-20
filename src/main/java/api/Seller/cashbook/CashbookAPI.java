package api.Seller.cashbook;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class CashbookAPI {
    String GET_RECORD_LIST = "/cashbookservice/api/cash-books/store/%s?page=0&size=1000000&sort=createdDate,desc";
    String CREATE_RECORD_PATH = "/cashbookservice/api/cash-books/store/%s";
    
    API api = new API();
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;

    public CashbookAPI(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    
    public JsonPath getAllRecordJsonPath() {
    	Response response = api.get(GET_RECORD_LIST.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
    	response.then().statusCode(200);
    	return response.jsonPath();
    }
    
    public List<String> getAllTransactionCodes() {
    	return getAllRecordJsonPath().getList("transactionCode");
    }

    /**
     * Creates a new record of the specified type (receipt or payment) and returns a JsonPath object representing the created record.
     * @param branchId The ID of the branch.
     * @param branchName The name of the branch.
     * @param sourceType The source type of the record.
     * @param type The type of record to create (RECEIPT or PAYMENT).
     * @param customerId The ID of the customer.
     * @param customerName The name of the customer.
     * @return A JsonPath object representing the created record, enabling easy navigation and extraction of information.
     */
    public JsonPath createRecordJsonPath(int branchId, String branchName, String sourceType, String type, int customerId, String customerName) {
        String body = """
        		{
    			    "amount": "2000",
    			    "branchId": %s,
    			    "branchName": "%s",
    			    "forAccounting": true,
    			    "groupType": "CUSTOMER",
    			    "paymentMethod": "CASH",
    			    "sourceType": "%s",
    			    "storeId": "%s",
    			    "type": "%s",
    			    "customerId": %s,
    			    "customerName": "%s"
    		    }
        """.formatted(branchId, branchName, sourceType, loginInfo.getStoreID(), type, customerId, customerName);
        Response createRecord = api.post(CREATE_RECORD_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body);
        createRecord.then().statusCode(201);
        return createRecord.jsonPath();
    }  
    
    /**
     * Creates a new receipt record for the specified customer and returns the transaction code of the created receipt.
     * @param branchId The ID of the branch.
     * @param branchName The name of the branch.
     * @param sourceType The source type of the record.
     * @param customerId The ID of the customer.
     * @param customerName The name of the customer.
     * @return The transaction code of the newly created receipt.
     */
    public String createReceipt(int branchId, String branchName, String sourceType, int customerId, String customerName) {
    	return createRecordJsonPath(branchId, branchName, sourceType, "RECEIPT", customerId, customerName).getString("transactionCode");
    }    
    
    /**
     * Creates a new payment record for the specified customer and returns the transaction code of the created payment.
     * @param branchId The ID of the branch.
     * @param branchName The name of the branch.
     * @param sourceType The source type of the record.
     * @param customerId The ID of the customer.
     * @param customerName The name of the customer.
     * @return The transaction code of the newly created payment.
     */
    public String createPayment(int branchId, String branchName, String sourceType, int customerId, String customerName) {
    	return createRecordJsonPath(branchId, branchName, sourceType, "PAYMENT", customerId, customerName).getString("transactionCode");
    }    
    
}
