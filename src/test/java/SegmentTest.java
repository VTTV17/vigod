import api.dashboard.login.Login;
import api.dashboard.products.CreateProduct;
import api.dashboard.setting.BranchManagement;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

import static java.lang.Thread.sleep;
import static org.apache.commons.lang3.RandomStringUtils.random;

public class SegmentTest {
    String CREATE_POS_CUSTOMER_PATH = "/beehiveservices/api/customer-profiles/POS/%s";
    String CREATE_SEGMENT_PATH = "/beehiveservices/api/segments/create/";
    String CREATE_POS_ORDER_PATH = "/orderservices2/api/gs/checkout/instore/v2";
    String SEGMENT_CUSTOMER_LIST_PATH = "/beehiveservices/api/customer-profiles/%s/v2?size=100&segmentId=%s";
    String customerName;
    String customerPhoneNum;
    int profileID;
    int buyerID;
    LoginDashboardInfo loginInfo = new Login().getInfo();
    
    BranchInfo brInfo;
    LoginInformation loginInformation;
    {
        brInfo = new BranchManagement(loginInformation).getInfo();
    }

    void createNewPOSCustomer() {
        customerName = "Auto - Customer - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        customerPhoneNum = random(12, false, true);
        String body = """
                {
                     "name": "%s",
                     "phone": "%s",
                     "email": "",
                     "note": "",
                     "tags": [],
                     "address": "342",
                     "locationCode": "VN-72",
                     "districtCode": "6306",
                     "wardCode": "630611",
                     "isCreateUser": true,
                     "gender": "MALE",
                     "birthday": null,
                     "countryCode": "VN",
                     "phones": [
                         {
                             "phoneCode": "+84",
                             "phoneName": "413241",
                             "phoneNumber": "%s",
                             "phoneType": "MAIN"
                         }
                     ],
                     "storeName": "%s",
                     "langKey": "en"
                 }""".formatted(customerName, customerPhoneNum, customerPhoneNum, new Login().getInfo().getStoreName());

        Response createCustomerResponse = new API().post(CREATE_POS_CUSTOMER_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body);
        createCustomerResponse.prettyPrint();
        createCustomerResponse.then().statusCode(200);
        profileID = createCustomerResponse.jsonPath().getInt("id");
        buyerID = createCustomerResponse.jsonPath().getInt("userId");
    }

    int createGreaterThanSegment(int numberOfOrders) {
        String segmentName = "Auto - Segment - " + "Order Data_Total Order Number_is greater than %s - ".formatted(numberOfOrders) + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        String body = """
                {
                    "name": "%s",
                    "matchCondition": "ALL",
                    "conditions": [
                        {
                            "name": "Order Data_Total Order Number_is greater than",
                            "value": "%s",
                            "expiredTime": "ALL"
                        }
                    ]
                }""".formatted(segmentName, numberOfOrders);

        Response createSegment = new API().post(CREATE_SEGMENT_PATH + loginInfo.getStoreID(), loginInfo.getAccessToken(), body);
        createSegment.then().statusCode(200);
        return createSegment.jsonPath().getInt("id");
    }

    int createLessThanSegment(int numberOfOrders) {
        String segmentName = "Auto - Segment - " + "Order Data_Total Order Number_is less than %s - ".formatted(numberOfOrders) + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        String body = """
                {
                    "name": "%s",
                    "matchCondition": "ALL",
                    "conditions": [
                        {
                            "name": "Order Data_Total Order Number_is less than",
                            "value": "%s",
                            "expiredTime": "ALL"
                        }
                    ]
                }""".formatted(segmentName, numberOfOrders);

        Response createSegment = new API().post(CREATE_SEGMENT_PATH + loginInfo.getStoreID(), loginInfo.getAccessToken(), body);
        createSegment.then().statusCode(200);
        return createSegment.jsonPath().getInt("id");
    }

    int createEqualSegment(int numberOfOrders) {
        String segmentName = "Auto - Segment - " + "Order Data_Total Order Number_is equal to %s - ".formatted(numberOfOrders) + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        String body = """
                {
                    "name": "%s",
                    "matchCondition": "ALL",
                    "conditions": [
                        {
                            "name": "Order Data_Total Order Number_is equal to",
                            "value": "%s",
                            "expiredTime": "ALL"
                        }
                    ]
                }""".formatted(segmentName, numberOfOrders);

        Response createSegment = new API().post(CREATE_SEGMENT_PATH + loginInfo.getStoreID(), loginInfo.getAccessToken(), body);
        createSegment.then().statusCode(200);
        return createSegment.jsonPath().getInt("id");
    }

    void createPOSOrder() {
        String body = """
                {
                    "buyerId": "%s",
                    "profileId": %s,
                    "guest": false,
                    "cartItemVMs": [
                        {
                            "itemId": "%s",
                            "modelId": "",
                            "quantity": 1,
                            "branchId": %s
                        }
                    ],
                    "checkNoDimension": false,
                    "deliveryInfo": {
                        "contactName": "%s",
                        "phoneNumber": "%s",
                        "phoneCode": "+84",
                        "address": "",
                        "address2": "",
                        "wardCode": "",
                        "districtCode": "",
                        "locationCode": "",
                        "countryCode": "VN",
                        "city": "",
                        "zipCode": ""
                    },
                    "deliveryServiceId": 75,
                    "langKey": "en",
                    "sellerNote": "",
                    "note": "",
                    "paymentCode": "",
                    "paymentMethod": "CASH",
                    "storeId": "%s",
                    "platform": "WEB",
                    "weight": 0,
                    "width": 0,
                    "length": 0,
                    "height": 0,
                    "selfDeliveryFee": 0,
                    "coupons": [],
                    "selfDeliveryFeeDiscount": null,
                    "branchId": %s,
                    "usePoint": null,
                    "receivedAmount": 0,
                    "directDiscount": null,
                    "customerId": %s,
                    "inStore": true
                }""".formatted(buyerID, profileID, new CreateProduct(loginInformation).getProductID(), brInfo.getBranchID().get(0), customerName, customerPhoneNum, loginInfo.getStoreID(), brInfo.getBranchID().get(0), profileID);
        Response createOrderResponse = new API().post(CREATE_POS_ORDER_PATH, loginInfo.getAccessToken(), body);
        createOrderResponse.prettyPrint();
        createOrderResponse.then().statusCode(201);
    }

    List<Integer> getListSegmentCustomer(int segmentID) {
        Response segmentInfo = new API().get(SEGMENT_CUSTOMER_LIST_PATH.formatted(loginInfo.getStoreID(), segmentID), loginInfo.getAccessToken());
        segmentInfo.then().statusCode(200);
        try {
            return segmentInfo.jsonPath().getList("id");
        } catch (NullPointerException ex) {
            return List.of();
        }

    }


    @Test
    void Te() throws InterruptedException {
        // pre-condition
        new Login().setLoginInformation("stgaboned@nbobd.com", "Abc@12345");
        //** test **

        // create segment 1
        int greaterSegmentID1 = createGreaterThanSegment(0);

        // create segment 2
        int greaterSegmentID2 = createGreaterThanSegment(1);

        // create segment 1
        int lessSegmentID1 = createLessThanSegment(2);

        // create segment 2
        int lessSegmentID2 = createLessThanSegment(3);

        // create segment 1
        int equalSegmentID1 = createEqualSegment(1);

        // create segment 2
        int equalSegmentID2 = createEqualSegment(2);

        // create POS customer
        createNewPOSCustomer();

        // create product
        new CreateProduct(loginInformation).createWithoutVariationProduct(false, 10, 10, 10);

        // make POS order
        createPOSOrder();

        sleep(5 * 60 * 1000);

        // check segment after 5 minutes
        //greater
        Assert.assertTrue(getListSegmentCustomer(greaterSegmentID1).contains(profileID), "%s %s %s".formatted(greaterSegmentID1, profileID, getListSegmentCustomer(greaterSegmentID1)));
        Assert.assertFalse(getListSegmentCustomer(greaterSegmentID2).contains(profileID), "%s".formatted(greaterSegmentID2));

        //less
        Assert.assertTrue(getListSegmentCustomer(lessSegmentID1).contains(profileID), "%s".formatted(lessSegmentID1));
        Assert.assertTrue(getListSegmentCustomer(lessSegmentID2).contains(profileID), "%s".formatted(lessSegmentID2));

        //equal
        Assert.assertTrue(getListSegmentCustomer(equalSegmentID1).contains(profileID), "%s".formatted(equalSegmentID1));
        Assert.assertFalse(getListSegmentCustomer(equalSegmentID2).contains(profileID), "%s".formatted(equalSegmentID2));

        // make POS order again
        createPOSOrder();

        sleep(5 * 60 * 1000);

        // check segment after 5 minutes
        //greater
        Assert.assertTrue(getListSegmentCustomer(greaterSegmentID1).contains(profileID), "%s".formatted(greaterSegmentID1));
        Assert.assertTrue(getListSegmentCustomer(greaterSegmentID2).contains(profileID), "%s".formatted(greaterSegmentID2));

        //less
        Assert.assertFalse(getListSegmentCustomer(lessSegmentID1).contains(profileID), "%s".formatted(lessSegmentID1));
        Assert.assertTrue(getListSegmentCustomer(lessSegmentID2).contains(profileID), "%s".formatted(lessSegmentID2));

        //equal
        Assert.assertFalse(getListSegmentCustomer(equalSegmentID1).contains(profileID), "%s".formatted(equalSegmentID1));
        Assert.assertTrue(getListSegmentCustomer(equalSegmentID2).contains(profileID), "%s".formatted(equalSegmentID2));
    }
}
