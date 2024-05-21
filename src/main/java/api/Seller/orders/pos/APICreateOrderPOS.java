package api.Seller.orders.pos;

import api.Seller.customers.Customers;
import api.Seller.login.Login;
import api.Seller.products.all_products.APIAllProducts;
import api.Seller.products.all_products.APIProductDetail;
import api.Seller.products.all_products.CreateProduct;
import api.Seller.setting.BranchManagement;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.HashMap;
import java.util.Map;

import static utilities.character_limit.CharacterLimit.MAX_PRICE;

public class APICreateOrderPOS {
    String CREATE_ORDER_PATH = "/orderservice3/api/pos/checkout";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APICreateOrderPOS(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public String getProductWithoutVariationInfoForBody(int productId, int branchId){
        System.out.println("ProductId: "+productId);
        long price = new APIProductDetail(loginInformation).getInfo(productId, APIProductDetail.ProductInformationEnum.price).getProductSellingPrice().get(0);
        String productInfo = """
                "branches": [
                    {
                      "branchId": %s,
                      "items": [
                        {
                          "itemId": %s,
                          "modelId": "",
                          "quantity": 1,
                          "itemTotalDiscounts": [],
                          "lotLocations": [],
                          "branchId": %s
                        }
                      ]
                    }
                  ],
                  "receivedAmount": %s,
                """.formatted(branchId,productId,branchId,price);
        return productInfo;
    }
    public String getCustomerAndDeliveryInfoForBody(int customerId){
        if(String.valueOf(customerId).isEmpty()){
            return """
                    "userGuest": true,
                      "deliveryInfo": {
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
                      "deliveryMethod": "",
                      "note": "",
                    """;
        }
        String userId = new Customers(loginInformation).getInfo(customerId).getUserId();
        String contactName = new Customers(loginInformation).getInfo(customerId).getMainPhoneName();
        String email = new Customers(loginInformation).getInfo(customerId).getMainEmail();
        String phone = new Customers(loginInformation).getInfo(customerId).getMainPhoneNumber();
        String phoneCode = new Customers(loginInformation).getInfo(customerId).getMainPhoneCode();
        String customerInfo = """
                    "userId": "%s",
                    "profileId": %s,
                    "userGuest": true,
                    "deliveryInfo": {
                        "contactName": "%s",
                        "email": "%s",
                        "phoneNumber": "%s",
                        "phoneCode": "%s",
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
                    "deliveryMethod": "",
                    "note": "",
                    "customerId": %s,
                """.formatted(userId,customerId,contactName,email,phone,phoneCode,customerId);
        return customerInfo;
    }
    public int CreatePOSOrder(int customerId,int productId){
        int branchId = new BranchManagement(loginInformation).getFreeBranch();
        String body = """
                {
                    %s%s"paymentCode": "",
                    "paymentMethod": "CASH",
                    "platform": "IN_STORE",
                    "selfDeliveryFee": 0,
                    "inStore": true,
                    "isAllowEarningPoint": true,
                    "taxAmount": 0,
                    "paymentMposId": 0,
                    "paymentMposDeviceCode": ""
                }
                """.formatted(getProductWithoutVariationInfoForBody(productId,branchId),getCustomerAndDeliveryInfoForBody(customerId));
        Map<String,Object> mapHeader = new HashMap<>();
        mapHeader.put("storeid",String.valueOf(loginInfo.getStoreID()));
        mapHeader.put("platform","WEB");
        mapHeader.put("isinternational",false);
        mapHeader.put("langkey","en");
        Response response = api.post(CREATE_ORDER_PATH,loginInfo.getAccessToken(),body, mapHeader);
        response.then().statusCode(200);
        return response.jsonPath().getInt("id");
    }
}
