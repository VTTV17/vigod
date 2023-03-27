package api.dashboard.customers;

import api.storefront.login.LoginSF;
import api.storefront.signup.SignUp;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.data.DataGenerator;

import java.util.List;

import static api.dashboard.login.Login.*;
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
    public static String apiCustomerName;
    public static String apiCustomerTag;
    public static String apiCustomerMail;
    public static int apiBuyerId;
    public static int apiProfileId;

    public static String apiCustomerPhoneCode;
    public static String apiCustomerPhoneNum;

    public static String apiSegmentName;

    public static int apiSegmentID;

    API api = new API();

    public Customers createNewCustomer() {
        apiCustomerName = randomAlphabetic(nextInt(MAX_CUSTOMER_NAME) + 1);
        apiCustomerPhoneNum = random(nextInt(MAX_PHONE_NUMBER - MIN_PHONE_NUMBER + MIN_PHONE_NUMBER), false, true);
        apiCustomerTag = randomAlphabetic(nextInt(MAX_CUSTOMER_TAG_LENGTH) + 1);
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
                }""".formatted(apiCustomerName, apiCustomerPhoneNum, apiCustomerTag, apiStoreName);

        Response createCustomerResponse = api.post(CREATE_POS_CUSTOMER_PATH + apiStoreID, accessToken, body);
        createCustomerResponse.then().statusCode(200);
        apiBuyerId = createCustomerResponse.jsonPath().getInt("userId");
        apiProfileId = createCustomerResponse.jsonPath().getInt("id");
        return this;
    }

    public Customers addCustomerTagForPhoneCustomer(String customerName) {
        Response searchCustomerByName = new API().get("%s%s/v2?keyword=%s".formatted(SEARCH_CUSTOMER_PATH, apiStoreID, customerName), accessToken);
        searchCustomerByName.then().statusCode(200);

        apiBuyerId = Integer.parseInt(searchCustomerByName.jsonPath().getList("userId").get(0).toString());
        apiProfileId = Integer.parseInt(searchCustomerByName.jsonPath().getList("id").get(0).toString());
        apiCustomerPhoneCode = String.valueOf(searchCustomerByName.jsonPath().getList("phone").get(0)).replace("(", "").replace(")", " ").split(" ")[0];
        apiCustomerPhoneNum = String.valueOf(searchCustomerByName.jsonPath().getList("phone").get(0)).replace(")", " ").split(" ")[1];
        apiCustomerTag = "AutoTag" + new DataGenerator().generateDateTime("ddMMHHmmss");

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
                }""".formatted(apiProfileId, customerName, apiCustomerPhoneCode, customerName, apiCustomerPhoneNum, apiCustomerTag);
        Response updateCustomerProfile = api.put("%s%s".formatted(UPDATE_CUSTOMER_PROFILE_PATH, apiStoreID), accessToken, body);
        updateCustomerProfile.then().statusCode(200);
        return this;
    }

    public Customers addCustomerTagForMailCustomer(String customerName) {
        Response searchCustomerByName = new API().get("%s%s/v2?keyword=%s".formatted(SEARCH_CUSTOMER_PATH, apiStoreID, customerName), accessToken);
        searchCustomerByName.then().statusCode(200);

        apiBuyerId = Integer.parseInt(searchCustomerByName.jsonPath().getList("userId").get(0).toString());
        apiProfileId = Integer.parseInt(searchCustomerByName.jsonPath().getList("id").get(0).toString());
        apiCustomerMail = String.valueOf(searchCustomerByName.jsonPath().getList("email").get(0));
        apiCustomerTag = "AutoTag" + new DataGenerator().generateDateTime("ddMMHHmmss");
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
                 }""".formatted(apiProfileId, customerName, apiCustomerMail, customerName, apiCustomerTag);
        Response updateCustomerProfile = api.put("%s%s".formatted(UPDATE_CUSTOMER_PROFILE_PATH, apiStoreID), accessToken, body);
        updateCustomerProfile.then().statusCode(200);
        return this;
    }

    public void createSegment() {
        apiSegmentName = "Auto - Segment - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
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
                """.formatted(apiSegmentName, apiCustomerTag);
        Response createSegment = api.post(CREATE_SEGMENT_PATH + apiStoreID, accessToken, body);
        createSegment.prettyPrint();
        createSegment.then().statusCode(200);
        apiSegmentID = createSegment.jsonPath().getInt("id");
    }

    public void createSegmentByAPI() throws InterruptedException {
        // sign up SF account
        new SignUp().signUpByMail();

        // login SF to create new Customer in Dashboard
        new LoginSF().LoginToSF();

        // wait customer is added
        sleep(3000);

        // add tag and create segment by tag name
        new Customers().addCustomerTagForMailCustomer(SignUp.apiCustomerName).createSegment();
    }

    public List<Integer> getListCustomerInSegment(Integer segmentID) {
        Response segmentDetail = api.get(GET_CUSTOMER_LIST_IN_SEGMENT_PATH.formatted(apiStoreID, segmentID), accessToken);
        segmentDetail.then().statusCode(200);
        return segmentDetail.jsonPath().getList("id");
    }
    
    public JsonPath getAllCustomerJsonPath() {
    	Response response = api.get(GET_200_CUSTOMERS_PATH.formatted(apiStoreID), accessToken);
    	response.then().statusCode(200);
    	return response.jsonPath();
    }
    
    public List<String> getAllCustomerNames() {
    	return getAllCustomerJsonPath().getList("fullName");
    }    
}
