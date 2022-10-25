package api.dashboard.customers;

import io.restassured.response.Response;
import utilities.api.API;

import static api.dashboard.login.Login.*;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomStringUtils.random;
import static utilities.character_limit.CharacterLimit.*;

public class Customers {
    String CREATE_POS_CUSTOMER_PATH = "/beehiveservices/api/customer-profiles/POS/";
    String CREATE_SEGMENT_PATH = "/beehiveservices/api/segments/create/";
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
        customerTag = randomAlphabetic(nextInt(MAX_CUSTOMER_TAG_LENGTH));
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
