package api.dashboard.customers;

import api.dashboard.login.Login;
import api.storefront.login.LoginSF;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.customer.CustomerInfo;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

public class Customers {
    String CUSTOMER_INFORMATION_PATH = "/beehiveservices/api/customer-profiles/detail/storeId/customerId";
    String CREATE_SEGMENT_PATH = "/beehiveservices/api/segments/create/";
    String GET_LIST_SEGMENT_OF_CUSTOMER = "/beehiveservices/api/segments/%s/%s";
    String SEARCH_CUSTOMER_PATH = "/beehiveservices/api/customer-profiles/";
    String UPDATE_CUSTOMER_PROFILE_PATH = "/beehiveservices/api/customer-profiles/edit/";
    String GET_200_CUSTOMERS_PATH = "/beehiveservices/api/customer-profiles/%s/v2?page=0&size=200&keyword=&sort=&branchIds=&ignoreBranch=true&searchField=NAME&operationDebtAmount=ALL&debtAmountValue=0&langKey=en";
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
        createSegment.prettyPrint();
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
        Response response = api.get(GET_200_CUSTOMERS_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response.jsonPath();
    }

    public List<String> getAllCustomerNames() {
        return getAllCustomerJsonPath().getList("fullName");
    }

    public List<String> getAllAccountCustomer() {
        return getAllCustomerJsonPath().getList("findAll { it.guest == false }.fullName");
    }

    public CustomerInfo getInfo(int customerId) {
        if (customerId != 0) {
            Response getCustomerInfo = api.get(CUSTOMER_INFORMATION_PATH.replace("customerId", String.valueOf(customerId)).replace("storeId", String.valueOf(loginInfo.getStoreID())), loginInfo.getAccessToken());
            getCustomerInfo.then().statusCode(200);

            CustomerInfo customerInfo = new CustomerInfo();
            customerInfo.setCustomerId(customerId);
            customerInfo.setMainEmail(getCustomerInfo.jsonPath().getString("emails[0].email"));
            customerInfo.setMainEmailName(getCustomerInfo.jsonPath().getString("emails[0].emailName"));
            customerInfo.setMainPhoneNumber(getCustomerInfo.jsonPath().getString("phones[0].phoneNumber"));
            customerInfo.setMainPhoneName(getCustomerInfo.jsonPath().getString("phones[0].phoneName"));

            return customerInfo;
        } else return new CustomerInfo();
    }
}
