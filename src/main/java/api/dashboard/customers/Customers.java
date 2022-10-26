package api.dashboard.customers;

import io.restassured.response.Response;
import utilities.api.API;

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
    public static String customerName;
    public static String customerTag;
    public static int buyerId;
    public static int profileId;
    public static String customerPhone;

    public static String segmentName;

    public static int segmentID;

    API api = new API();

    public Customers createNewCustomer() {
        customerName = randomAlphabetic(nextInt(MAX_CUSTOMER_NAME) + 1);
        customerPhone = random(nextInt(MAX_PHONE_NUMBER - MIN_PHONE_NUMBER + 1) + MIN_PHONE_NUMBER, false, true);
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
                }""".formatted(customerName, customerPhone, customerTag, storeName);

        Response createCustomerResponse = api.post(CREATE_POS_CUSTOMER_PATH + storeID, accessToken, body);
        buyerId = createCustomerResponse.jsonPath().getInt("userId");
        profileId = createCustomerResponse.jsonPath().getInt("id");
        return this;
    }

    public Customers addCustomerTag(String customerName) {
        Response searchCustomerByName = new API().get("%s%s/v2?keyword=%s".formatted(SEARCH_CUSTOMER_PATH, storeID, customerName), accessToken);
        buyerId = Integer.parseInt(searchCustomerByName.jsonPath().getString("userId").replace("[","").replace("]", ""));
        profileId = Integer.parseInt(searchCustomerByName.jsonPath().getString("id").replace("[","").replace("]", ""));
        String phoneBackup = searchCustomerByName.jsonPath().getString("phoneBackup").replace("[","").replace("]", "");
        customerTag = randomAlphabetic(nextInt(MAX_CUSTOMER_TAG_LENGTH) + 1);

        String body = """
                {
                    "id": "%s",
                    "fullName": "%s",
                    "backupPhone": [
                        "%s"
                    ],
                    "email": "",
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
                    "taxCode": ""
                }""".formatted(profileId, customerName, phoneBackup, customerTag);

        new API().put(UPDATE_CUSTOMER_PROFILE_PATH + storeID, accessToken, body).prettyPrint();
        return this;
    }

    public void createSegment() {
        createNewCustomer();
        segmentName = randomAlphabetic(nextInt(MAX_SEGMENT_NAME_LENGTH) + 1);
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
        segmentID = api.post(CREATE_SEGMENT_PATH + storeID, accessToken, body).jsonPath().getInt("id");
    }
}
