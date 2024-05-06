package api.Seller.cashbook;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class CashbookAPI {
    String GET_RECORD_LIST = "/cashbookservice/api/cash-books/store/%s?page=0&size=1000000&sort=createdDate,desc";
    String CREATE_RECORD_PATH = "/cashbookservice/api/cash-books/store/%s";
    String CashbookSummaryPath = "/cashbookservice/api/cash-books/summary/%s?createdDateFrom.greaterThanOrEqual=%sT17:00:00.000Z&createdDateTo.lessThanOrEqual=%sT16:59:59.000Z";
    
    API api = new API();
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;

    public CashbookAPI(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public Response getCashbookSummaryResponse() {
    	Instant now = Instant.now();
    	Instant yesterday = now.minus(1, ChronoUnit.DAYS);
    	
    	Date currentDate = Date.from(now);
    	Date yesterdayDate = Date.from(yesterday);
    	
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	
    	String currentDateTime = dateFormat.format(currentDate);   
    	String yesterdayDateTime = dateFormat.format(yesterdayDate);  
    	
    	Response response = api.get(CashbookSummaryPath.formatted(loginInfo.getStoreID(), yesterdayDateTime, currentDateTime), loginInfo.getAccessToken());
    	response.then().statusCode(200);
    	return response;
    }    
    
    public List<BigDecimal> getCasbookSummary() {
    	
    	String amount = getCashbookSummaryResponse().asPrettyString();
    	
        List<BigDecimal> summary = new ArrayList<>();
        Matcher matcher = Pattern.compile("\"\\w+\": (\\d+\\.*\\d*)")
            .matcher(amount);
        while (matcher.find()) {
          summary.add(new BigDecimal(matcher.group(1)));
        }
    	
    	return summary;
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
