package api.dashboard.customers;

import api.dashboard.login.Login;
import api.storefront.login.LoginSF;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static utilities.character_limit.CharacterLimit.*;

public class Customers {
    String CREATE_POS_CUSTOMER_PATH = "/beehiveservices/api/customer-profiles/POS/";
    String CREATE_SEGMENT_PATH = "/beehiveservices/api/segments/create/";

    String SEARCH_CUSTOMER_PATH = "/beehiveservices/api/customer-profiles/";
    String UPDATE_CUSTOMER_PROFILE_PATH = "/beehiveservices/api/customer-profiles/edit/";
    String GET_CUSTOMER_LIST_IN_SEGMENT_PATH = "/beehiveservices/api/customer-profiles/%s/v2?segmentId=%s";
    String GET_200_CUSTOMERS_PATH = "/beehiveservices/api/customer-profiles/%s/v2?page=0&size=200&keyword=&sort=&branchIds=&ignoreBranch=true&searchField=NAME&operationDebtAmount=ALL&debtAmountValue=0&langKey=en";
    private String customerTag;
    private static int profileId;

    private String customerPhoneNum;

    private static String segmentName;

    private static int segmentID;
    LoginDashboardInfo loginInfo;

    API api = new API();
    LoginInformation loginInformation;

    public Customers(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public Customers createNewCustomer() {
        String apiCustomerName = randomAlphabetic(nextInt(MAX_CUSTOMER_NAME) + 1);
        customerPhoneNum = random(nextInt(MAX_PHONE_NUMBER - MIN_PHONE_NUMBER + MIN_PHONE_NUMBER), false, true);
        customerTag = randomAlphabetic(nextInt(MAX_CUSTOMER_TAG_LENGTH) + 1);
        String body = """
                {
                    "name": "%s",
                    "phone": "%s",
                    "email": "",
                    "note": "",
                    "tags": [
                        "%s"
                    ],
                    "address": "",
                    "locationCode": "",
                    "districtCode": "",
                    "wardCode": "",
                    "isCreateUser": true,
                    "gender": null,
                    "birthday": null,
                    "countryCode": "VN",
                    "storeName": "%s",
                    "langKey": "en"
                }""".formatted(apiCustomerName, customerPhoneNum, customerTag, loginInfo.getStoreName());

        Response createCustomerResponse = api.post(CREATE_POS_CUSTOMER_PATH + loginInfo.getStoreID(), loginInfo.getAccessToken(), body);
        createCustomerResponse.then().statusCode(200);
        createCustomerResponse.jsonPath().getInt("userId");
        profileId = createCustomerResponse.jsonPath().getInt("id");
        return this;
    }

    public Customers addCustomerTagForPhoneCustomer(String customerName) {
        Response searchCustomerByName = new API().get("%s%s/v2?keyword=%s".formatted(SEARCH_CUSTOMER_PATH, loginInfo.getStoreID(), customerName), loginInfo.getAccessToken());
        searchCustomerByName.then().statusCode(200);

        profileId = Integer.parseInt(searchCustomerByName.jsonPath().getList("id").get(0).toString());
        String apiCustomerPhoneCode = String.valueOf(searchCustomerByName.jsonPath().getList("phone").get(0)).replace("(", "").replace(")", " ").split(" ")[0];
        customerPhoneNum = String.valueOf(searchCustomerByName.jsonPath().getList("phone").get(0)).replace(")", " ").split(" ")[1];
        customerTag = "AutoTag" + new DataGenerator().generateDateTime("ddMMHHmmss");

        String body = """
                {
                    "id": "%s",
                    "fullName": "%s",
                    "phones": [
                        {
                            "phoneCode": "%s",
                            "phoneName": "%s",
                            "phoneNumber": "%s"
                        }
                    ],
                    "emails": [],
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
                }""".formatted(profileId, customerName, apiCustomerPhoneCode, customerName, customerPhoneNum, customerTag);
        Response updateCustomerProfile = api.put("%s%s".formatted(UPDATE_CUSTOMER_PROFILE_PATH, loginInfo.getStoreID()), loginInfo.getAccessToken(), body);
        updateCustomerProfile.then().statusCode(200);
        return this;
    }

    public Customers addCustomerTagForMailCustomer(String keywords) {
        Response searchCustomerByEmail = new API().get("%s%s/v2?keyword=%s&searchField=%s".formatted(SEARCH_CUSTOMER_PATH, loginInfo.getStoreID(), keywords, "EMAIL"), loginInfo.getAccessToken());
        searchCustomerByEmail.then().statusCode(200);

        String customerName = Pattern.compile("fullName.{4}(\\w+)").matcher(searchCustomerByEmail.asPrettyString()).results().map(matchResult -> matchResult.group(1)).toList().get(0);
        profileId = Pattern.compile("id.{3}(\\d+)").matcher(searchCustomerByEmail.asPrettyString()).results().map(matchResult -> Integer.valueOf(matchResult.group(1))).toList().get(0);
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

    public List<Integer> getListCustomerInSegment(Integer segmentID) {
        Response segmentDetail = api.get(GET_CUSTOMER_LIST_IN_SEGMENT_PATH.formatted(loginInfo.getStoreID(), segmentID), loginInfo.getAccessToken());
        segmentDetail.then().statusCode(200);
        return segmentDetail.jsonPath().getList("id");
    }

    public int getCustomerID(String keywords) {
        Response searchCustomerByEmail = new API().get("%s%s/v2?keyword=%s&searchField=%s".formatted(SEARCH_CUSTOMER_PATH, loginInfo.getStoreID(), keywords, "EMAIL"), loginInfo.getAccessToken());
        searchCustomerByEmail.then().statusCode(200);

        return Pattern.compile("id.{3}(\\d+)").matcher(searchCustomerByEmail.asPrettyString()).results().map(matchResult -> Integer.valueOf(matchResult.group(1))).toList().get(0);
    }

    public int getProfileId() {
        return Customers.profileId;
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
    	List<String> name = getAllCustomerJsonPath().getList("findAll { it.guest == false }.fullName");
    	return name;
    }    
}
