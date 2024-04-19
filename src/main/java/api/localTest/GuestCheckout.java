package api.localTest;

import api.Seller.login.Login;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang.RandomStringUtils;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static io.restassured.RestAssured.given;

public class GuestCheckout {
    LoginDashboardInfo loginInfo;
    LoginInformation sellerLoginInformation;

    public GuestCheckout(LoginInformation sellerLoginInformation) {
        loginInfo = new Login().getInfo(sellerLoginInformation);
        this.sellerLoginInformation = sellerLoginInformation;
    }

    API api = new API();
    String guestToken;
    int userId;

    int branchId = 129160;
    int itemId = 1279974;

    void getGuestToken() {
        String body = """
                {
                    "langKey": "vi",
                    "locationCode": "vn"
                }""";
        JsonPath jsonPath = given().contentType(ContentType.JSON)
                .header("Authorization", "Basic aW50ZXJuYWw6TUtQZDVkUG1MZXg3b2hXcmxHeEpQR3htZ2ZTSFF0MXU=")
                .when()
                .body(body)
                .post("/beecowgateway/api/guest")
                .then().statusCode(201)
                .extract().jsonPath();
        guestToken = jsonPath.getString("accessToken");
        userId = jsonPath.getInt("id");
    }

    public void addProductToCart() {
        String addProductToCartPath = "/orderservices2/api/shop-carts/add-to-cart/v2/domain/gosell";
        String body = """
                [
                     {
                         "branchId": %s,
                         "itemId": %s,
                         "quantity": 1
                     }
                 ]
                """.formatted(branchId, itemId);
        api.post(addProductToCartPath, guestToken, body)
                .then()
                .statusCode(200);
    }

    int bcOrderGroupId;
    int bcOrderIds;

    public void initOrder() {
        String initPath = "/orderservice3/api/ssr/checkout/init";
        String body = """
                {
                    "branches": [
                        {
                            "branchId": %s,
                            "branchName": "Free Branch",
                            "index": 0,
                            "items": [
                                {
                                    "index": 0,
                                    "selected": true,
                                    "itemId": %s,
                                    "itemTotalDiscounts": [],
                                    "quantity": 1
                                }
                            ]
                        }
                    ],
                    "langKey": "vi",
                    "platform": "ANDROID",
                    "storeId": %s,
                    "userId": %s
                }""".formatted(branchId, itemId, loginInfo.getStoreID(), userId);
        JsonPath jsonPath = api.post(initPath, guestToken, body).then().statusCode(200).extract().jsonPath();
        bcOrderIds = jsonPath.getInt("bcOrderIds[0]");
        bcOrderGroupId = jsonPath.getInt("bcOrderGroupId");
    }

    int deliveryId;

    void getDeliveryId() {
        String getDeliveryIdPath = "/orderservice3/api/orders/%s/get-order-details";
        deliveryId = api.get(getDeliveryIdPath.formatted(bcOrderGroupId), guestToken).then().statusCode(200).extract().jsonPath().getInt("deliveryInfo.id");
    }

    int orderId;
    int buyerId;

    void completeOrder(String phoneCode, String phoneNumber) {
        String body = """
                {
                    "bcOrderGroupId": %s,
                    "clientType": "MOBILE",
                    "deliveryInfo": {
                        "address": "qtuq",
                        "address2": "",
                        "city": "",
                        "contactName": "guest_ijq39re8",
                        "countryCode": "VN",
                        "districtCode": "3703",
                        "email": "",
                        "id": %s,
                        "locationCode": "VN-31",
                        "phoneCode": "%s",
                        "phoneNumber": "%s",
                        "wardCode": "370305",
                        "zipCode": ""
                    },
                    "discount": {
                        "couponCodes": [],
                        "directDiscount": {}
                    },
                    "lstBcOrder": [
                        {
                            "bcOrderId": %s,
                            "deliveryServiceId": 3395483,
                            "deliveryServiceType": "selfdelivery"
                        }
                    ],
                    "partnerCode": "",
                    "paymentMethod": "COD",
                    "returnURL": "",
                    "storeId": %s
                }""".formatted(bcOrderGroupId, deliveryId, phoneCode, phoneNumber, bcOrderIds, loginInfo.getStoreID());
        Response response = api.post("/orderservice3/api/ssr/checkout/complete", guestToken, body).then().statusCode(200).extract().response();
        orderId = response.jsonPath().getInt("bcOrderGroup.orderIds[0]");
    }

    void getUserInfoInOrderDetail() {
        String orderDetailPath = "/orderservice3/api/gs/order-details/ids/%s?getLoyaltyEarningPoint=true&langKey=en".formatted(orderId);
        buyerId = 0;
        while (buyerId == 0) {
            try {
                buyerId = api.get(orderDetailPath, loginInfo.getAccessToken()).then().statusCode(200).extract().jsonPath().getInt("customerInfo.userId");
            } catch (NullPointerException ignored) {
            }
        }
    }

    int customerId;

    public boolean checkPhone(String phoneCode, String phoneNumber) {
        String body = """
                {
                    "phoneNumber": "%s",
                    "phoneCode": "%s"
                }""".formatted(phoneNumber, phoneCode);
        return api.post("/beehiveservices/api/customer-profiles/check-phone/%s".formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body).getBody().asString().equals("true");
    }

    String getPhoneNumber(String phoneCode) {
        String phoneNumber = String.valueOf(Instant.now().toEpochMilli());
        int count = 0;
        while (checkPhone(phoneCode, phoneNumber)) {
            count++;
            phoneNumber = String.valueOf(Instant.now().plus(count, ChronoUnit.MINUTES).toEpochMilli());
        }

        return phoneNumber;
    }

    public void sellerCreateCustomer(String phoneCode, String phoneNumber) {
        String createPOSUserPath = "/beehiveservices/api/customer-profiles/POS/%s".formatted(loginInfo.getStoreID());
        String body = """
                {
                    "name": "%s",
                    "phone": "%s",
                    "email": "",
                    "note": "",
                    "tags": [],
                    "address": "",
                    "locationCode": "",
                    "districtCode": "",
                    "wardCode": "",
                    "isCreateUser": true,
                    "gender": null,
                    "birthday": null,
                    "countryCode": "VN",
                    "geoLocation": {},
                    "phones": [
                        {
                            "phoneCode": "%s",
                            "phoneName": "231321",
                            "phoneNumber": "%s",
                            "phoneType": "MAIN"
                        }
                    ],
                    "storeName": "auto stg",
                    "langKey": "en",
                    "branchId": ""
                }""".formatted(RandomStringUtils.randomAlphabetic(5), phoneNumber, phoneCode, phoneNumber);
        customerId = 0;
        while (customerId == 0) {
            try {
                customerId = Integer.parseInt(api.post(createPOSUserPath, loginInfo.getAccessToken(), body).then().statusCode(200).extract().jsonPath().getString("userId"));
            } catch (AssertionError ignored) {
            }
        }
    }

    void createOrder(String phoneCode, String phoneNumber) {
        getGuestToken();
        addProductToCart();
        initOrder();
        getDeliveryId();
        completeOrder(phoneCode, phoneNumber);
        getUserInfoInOrderDetail();
    }

    DataGenerator dataGenerator = new DataGenerator();

    void addServiceToCart() {
        String body = """
                {
                    "date": "%s",
                    "itemId": 1280908,
                    "modelId": 1285619,
                    "price": 10000.0,
                    "quantity": 1
                }""".formatted(dataGenerator.generateDateTime("yyyy-MM-dd", 365));

        api.post("/orderservices2/api/shop-carts-booking/add-to-cart", guestToken, body).then().statusCode(200);
    }

    void makeBooking() {
        String body = """
                {
                    "bookingCartItemVMs": [
                        {
                            "date": "%s",
                            "itemId": 1280908,
                            "modelId": 1285619,
                            "price": 10000.0,
                            "quantity": 1
                        }
                    ],
                    "buyerId": %s,
                    "langKey": "vi",
                    "platform": "ANDROID",
                    "storeId": 129149,
                    "usePoint": 0.0
                }""".formatted(dataGenerator.generateDateTime("yyyy-MM-dd", 365), buyerId);
        JsonPath jsonPath = api.post("/orderservices2/api/bc-orders/booking/coupon", guestToken, body).then().statusCode(200).extract().jsonPath();
        bcOrderIds = jsonPath.getInt("orders[0].id");
        bcOrderGroupId = jsonPath.getInt("orders[0].bcOrderGroupId");
    }

    int bookingId;

    void completeBooking(String phoneCode, String phoneNumber) {
        String body = """
                {
                    "bcOrderGroupId": %s,
                    "clientType": "MOBILE",
                    "contactName": "%s",
                    "note": "",
                    "paymentMethod": "CASH",
                    "phoneCode": "%s",
                    "phoneNumber": "%s",
                    "orders": [
                        {
                            "bcOrderId": %s
                        }
                    ]
                }""".formatted(bcOrderGroupId, RandomStringUtils.randomAlphabetic(5), phoneCode, phoneNumber, bcOrderIds);
        bookingId = api.post("/orderservices2/api/bc-orders/confirm-cash/booking", guestToken, body).then().statusCode(200).extract().jsonPath().getInt("orders[0].id");
    }

    void getUserInfoInBookingDetail() {
        String getServiceDetailPath = "/orderservices2/api/gs/booking-details/ids/%s".formatted(bookingId);
        buyerId = 0;
        while (buyerId == 0) {
            try {
                buyerId = api.get(getServiceDetailPath, loginInfo.getAccessToken()).then().statusCode(200).extract().jsonPath().getInt("customerInfo.customerId");
            } catch (NullPointerException ignored) {
            }
        }
    }

    public void createBooking(String phoneCode, String phoneNumber) {
        getGuestToken();
        addServiceToCart();
        makeBooking();
        completeBooking(phoneCode, phoneNumber);
        getUserInfoInBookingDetail();
    }

    String makeLDPOrderPath = "/orderservices2/api/bc-orders/checkout/gosell/landing-page";

    public void createLDPOrder(String phoneCode, String phoneNumber) {
        String body = """
                {
                    "deliveryInfo": {
                        "address": "12312",
                        "address2": "",
                        "wardCode": "630603",
                        "districtCode": "6306",
                        "locationCode": "VN-72",
                        "countryCode": "VN",
                        "city": "",
                        "zipCode": "",
                        "contactName": "%s",
                        "email": null,
                        "phoneNumber": "%s",
                        "phoneCode": "%s"
                    },
                    "branchId": "129160",
                    "cartItemVMs": [
                        {
                            "branchId": "129160",
                            "itemId": "1279974",
                            "quantity": 1
                        }
                    ],
                    "langKey": "vi",
                    "note": "",
                    "storeId": "%s",
                    "landingPageId": "100402",
                    "landingPageKey": "cNTvjprjvdDUrSygwpRAVtrvwSTMtW",
                    "partnerCode": null
                }""".formatted(RandomStringUtils.randomAlphabetic(5), phoneNumber, phoneCode, loginInfo.getStoreID());
        orderId = 0;
        while (orderId == 0) {
            try {
                orderId = api.post(makeLDPOrderPath, body).then().statusCode(200).extract().jsonPath().getInt("orders[0].id");
            } catch (AssertionError ignored) {
            }
        }
    }

    void createLDP(String phoneCode, String phoneNumber) {
        createLDPOrder(phoneCode, phoneNumber);
        getUserInfoInOrderDetail();
    }

    public void checkOutByGuest() {
        List<String> countryList = dataGenerator.getCountryList();
        countryList.forEach(countryName -> {
            String phoneCode = dataGenerator.getPhoneCode(countryName);
            String phoneNumber = getPhoneNumber(phoneCode);
            System.out.println(phoneCode + ":0" + phoneNumber);
            sellerCreateCustomer(phoneCode, "0" + phoneNumber);

//            // order
//            createOrder(phoneCode, phoneNumber);
//            if (customerId != buyerId) {
//                System.out.printf("[Order] Phone number: %s:%s, orderId: %s%n", phoneCode, phoneNumber, orderId);
//                System.out.printf("[Order]customerId: %s, guestId: %s%n", customerId, buyerId);
//            }
//
//            createOrder(phoneCode, "0" + phoneNumber);
//            if (customerId != buyerId) {
//                System.out.printf("[Order] Phone number: %s:0%s, orderId: %s%n", phoneCode, phoneNumber, orderId);
//                System.out.printf("[Order] customerId: %s, guestId: %s%n", customerId, buyerId);
//            }
//
//            // booking
//            createBooking(phoneCode, phoneNumber);
//            if (customerId != buyerId) {
//                System.out.printf("[Booking] Phone number: %s:%s, reservationId: %s%n", phoneCode, phoneNumber, bookingId);
//                System.out.printf("[Booking] customerId: %s, guestId: %s%n", customerId, buyerId);
//            }
//            createBooking(phoneCode, "0" + phoneNumber);
//            if (customerId != buyerId) {
//                System.out.printf("[Booking] Phone number: %s:0%s, reservationId: %s%n", phoneCode, phoneNumber, bookingId);
//                System.out.printf("[Booking] customerId: %s, guestId: %s%n", customerId, buyerId);
//            }

            // ldp
            createLDP(phoneCode, phoneNumber);
            if (customerId != buyerId) {
                System.out.printf("[LDP] Phone number: %s:%s, orderId: %s%n", phoneCode, phoneNumber, orderId);
                System.out.printf("[LDP]customerId: %s, guestId: %s%n", customerId, buyerId);
            }

            createLDP(phoneCode, "0" + phoneNumber);
            if (customerId != buyerId) {
                System.out.printf("[LDP] Phone number: %s:0%s, orderId: %s%n", phoneCode, phoneNumber, orderId);
                System.out.printf("[LDP] customerId: %s, guestId: %s%n", customerId, buyerId);
            }
        });
    }
}
