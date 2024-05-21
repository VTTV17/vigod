package api.Seller.customers;

import static java.lang.Thread.sleep;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import api.Buyer.login.LoginSF;
import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.customer.CustomerInfo;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class Customers {
    String CUSTOMER_INFORMATION_PATH = "/beehiveservices/api/customer-profiles/detail/%s/%s";
    String CREATE_SEGMENT_PATH = "/beehiveservices/api/segments/create/";
    String GET_LIST_SEGMENT_OF_CUSTOMER = "/beehiveservices/api/segments/%s/%s";
    String SEARCH_CUSTOMER_PATH = "/beehiveservices/api/customer-profiles/";
    String UPDATE_CUSTOMER_PROFILE_PATH = "/beehiveservices/api/customer-profiles/edit/";
    String GET_200_CUSTOMERS_PATH = "/beehiveservices/api/customer-profiles/%s/v2?page=0&size=200&keyword=&sort=&branchIds=&ignoreBranch=true&searchField=NAME&operationDebtAmount=ALL&debtAmountValue=0&langKey=en";
    String ASSIGN_STAFF_TO_CUSTOMER_PATH = "/beehiveservices/api/customer-profiles/bulk-assign-customer-to-a-staff/%s";
    String EXPORT_CUSTOMER_PATH = "/beehiveservices/api/customer-profiles/export/%s/v2?keyword=&branchIds=&ignoreBranch=true&searchField=NAME&operationDebtAmount=ALL&debtAmountValue=0&langKey=vi";
    String ASSIGN_PARTNER_TO_CUSTOMER = "/beehiveservices/api/customer-profiles/update-partner/%s";
    private String customerTag;
    private static String segmentName;
    private static int segmentID;
    LoginDashboardInfo loginInfo;

    API api = new API();
    LoginInformation loginInformation;

    public Customers(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    public class CustomerManagementInfo {
    	List<String> customerName = new ArrayList<>();
        List<Integer> customerId = new ArrayList<>();
        List<String> userId = new ArrayList<>();
        List<Integer> totalOrder = new ArrayList<>();
        List<Float> debtAmount = new ArrayList<>();
        List<String> saleChannel = new ArrayList<>();
        List<Integer> responsibleStaffUserId = new ArrayList<>();
    }    
    public CustomerManagementInfo getCustomerManagementInfo() {
    	
    	JsonPath jsonResponse = getAllCustomerJsonPath();
    	
    	CustomerManagementInfo info = new CustomerManagementInfo();
    	info.setCustomerName(jsonResponse.getList("fullName"));
    	info.setCustomerId(jsonResponse.getList("id"));
    	info.setUserId(jsonResponse.getList("userId"));
    	info.setTotalOrder(jsonResponse.getList("totalOrder"));
    	info.setDebtAmount(jsonResponse.getList("orderDebtSummary"));
    	info.setSaleChannel(jsonResponse.getList("saleChannel"));
    	info.setResponsibleStaffUserId(jsonResponse.getList("responsibleStaffUserId"));

        return info;
    }    
    
    public Customers addCustomerTagForMailCustomer(String keywords) {
        Response searchCustomerByEmail = new API().get("%s%s/v2?keyword=%s&searchField=%s".formatted(SEARCH_CUSTOMER_PATH, loginInfo.getStoreID(), keywords, "EMAIL"), loginInfo.getAccessToken());
        searchCustomerByEmail.then().statusCode(200);

        String customerName = Pattern.compile("fullName.{4}(\\w+)").matcher(searchCustomerByEmail.asPrettyString()).results().map(matchResult -> matchResult.group(1)).toList().get(0);
        int profileId = Pattern.compile("id.{3}(\\d+)").matcher(searchCustomerByEmail.asPrettyString()).results().map(matchResult -> Integer.valueOf(matchResult.group(1))).toList().get(0);
        customerTag = "AutoTag" + new DataGenerator().generateDateTime("ddMMHHmmss");
        String body = """
                {
                     "id": "%s",
                     "fullName": "%s",
                     "phones": [],
                     "emails": [
                         {
                             "email": "%s",
                             "emailName": "%s"
                         }
                     ],
                     "note": "",
                     "tags": [
                         "%s"
                     ],
                     "countryCode": "VN",
                     "address": "",
                     "locationCode": "",
                     "districtCode": "",
                     "wardCode": "",
                     "gender": null,
                     "birthday": null,
                     "partnerId": null,
                     "companyName": "",
                     "taxCode": "",
                     "backupPhones": [],
                     "backupEmails": []
                 }""".formatted(profileId, customerName, keywords, customerName, customerTag);
        Response updateCustomerProfile = api.put("%s%s".formatted(UPDATE_CUSTOMER_PROFILE_PATH, loginInfo.getStoreID()), loginInfo.getAccessToken(), body);
        updateCustomerProfile.then().statusCode(200);
        return this;
    }

    public void createSegment() {
        segmentName = "Auto - Segment - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        String body = """
                {
                    "name": "%s",
                    "matchCondition": "ALL",
                    "conditions": [
                        {
                            "name": "Customer Data_Customer tag_is equal to",
                            "value": "%s"
                        }
                    ]
                }
                """.formatted(segmentName, customerTag);
        Response createSegment = api.post(CREATE_SEGMENT_PATH + loginInfo.getStoreID(), loginInfo.getAccessToken(), body);
        createSegment.then().statusCode(200);
        segmentID = createSegment.jsonPath().getInt("id");
    }

    public int getSegmentID() {
        return Customers.segmentID;
    }

    public String getSegmentName() {
        return Customers.segmentName;
    }

    public void createSegmentByAPI(String account, String password, String phoneCode) throws InterruptedException {
        // login SF to create new Customer in Dashboard
        new LoginSF(loginInformation).LoginToSF(account, password, phoneCode);

        // wait customer is added
        sleep(3000);

        // add tag and create segment by tag name
        addCustomerTagForMailCustomer(account).createSegment();
    }

    public List<Integer> getListSegmentOfCustomer(int customerId) {
        if (customerId != 0) {
            Response customerInfo = api.get(GET_LIST_SEGMENT_OF_CUSTOMER.formatted(loginInfo.getStoreID(), customerId), loginInfo.getAccessToken());
            customerInfo.then().statusCode(200);
            return customerInfo.jsonPath().getList("id");
        } else return null;
    }

    public int getCustomerID(String keywords) {
        Response searchCustomerByEmail = new API().get("%s%s/v2?keyword=%s&searchField=%s".formatted(SEARCH_CUSTOMER_PATH, loginInfo.getStoreID(), keywords, "EMAIL"), loginInfo.getAccessToken());
        searchCustomerByEmail.then().statusCode(200);

        return Pattern.compile("id.{3}(\\d+)").matcher(searchCustomerByEmail.asPrettyString()).results().map(matchResult -> Integer.valueOf(matchResult.group(1))).toList().get(0);
    }

    public JsonPath getAllCustomerJsonPath() {
        Response response = api.get(GET_200_CUSTOMERS_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken())
                .then().statusCode(200).extract().response();
        return response.jsonPath();
    }
    
    public List<String> getAllCustomerNames() {
        return getAllCustomerJsonPath().getList("fullName");
    }
    
    public List<Integer> getAllCustomerIds() {
    	return getAllCustomerJsonPath().getList("id");
    }
 
    /**
     * Retrieves a JsonPath object containing information about customers assigned to a specific staff member.
     * @param staffUserId The ID (userId) of the staff member whose assigned customers are to be retrieved.
     * @return A JsonPath object representing the retrieved customer data, enabling easy navigation and extraction of information.
     */
    public JsonPath getCustomersAssignedToStaffJsonPath(int staffUserId) {
        Response response = api.get(GET_200_CUSTOMERS_PATH.formatted(loginInfo.getStoreID()) + "&responsibleStaffUserIds=%s".formatted(staffUserId), loginInfo.getAccessToken())
                .then().statusCode(200).extract().response();
        return response.jsonPath();
    } 
    
    public List<String> getNamesOfCustomersAssignedToStaff(int staffUserId) {
    	return getCustomersAssignedToStaffJsonPath(staffUserId).getList("fullName");
    }
    
    public List<Integer> getIdsOfCustomersAssignedToStaff(int staffUserId) {
    	return getCustomersAssignedToStaffJsonPath(staffUserId).getList("id");
    }

    public List<String> getAllAccountCustomer() {
        return getAllCustomerJsonPath().getList("findAll { it.guest == false }.fullName");
    }
    public List<Integer> getAllAccountCustomerId() {
        return getAllCustomerJsonPath().getList("findAll { it.guest == false }.id");
    }

    /**
     * Assigns a specific staff member to a designated customer within the current store.
     * @param staffUserId The ID (userId) of the staff member to be assigned.
     * @param customerId The ID of the customer to whom the staff member will be assigned.
     */
    public void assignStaffToCustomer(int staffUserId, int customerId) {
        String body = """
        		{
					"userId": %s,
					"storeId": "%s",
					"customerIds": [%s]
    		    }
        """.formatted(staffUserId, loginInfo.getStoreID(), customerId);
        Response createRecord = api.post(ASSIGN_STAFF_TO_CUSTOMER_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body);
        createRecord.then().statusCode(200);
    }    
    
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
    
    public void exportCustomerFile() {
    	Response response = api.get(EXPORT_CUSTOMER_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
    	response.then().statusCode(200);
    }    
    public void assignPartnerToCustomer(int customerId, int partnerId){
        String body = """
                {
                    "customerIds": "%s",
                    "partnerId": %s
                }
                """.formatted(customerId,partnerId);
        Response response = api.put(ASSIGN_PARTNER_TO_CUSTOMER.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken(),body);
        response.then().statusCode(200);
    }
}
