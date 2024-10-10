package api.Seller.orders.pos;

import api.Seller.customers.APIAllCustomers;
import api.Seller.customers.APICustomerDetail;
import api.Seller.login.Login;
import api.Seller.products.all_products.APIProductDetail;
import api.Seller.products.all_products.ConversionUnit;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StoreLanguageAPI;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.customer.CustomerInfo;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.orders.pos.CreatePOSOrderCondition;
import utilities.model.dashboard.products.productInfomation.ProductConversionInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static api.Seller.products.all_products.APIProductDetail.ProductInformationEnum.variation;

public class APICreateOrderPOS {
    String CREATE_ORDER_PATH = "/orderservice3/api/pos/checkout";
    String SEARCH_SUGGESTION = "/itemservice/api/store/%s/item-model/suggestion?langKey=vi&page=0&size=100&searchType=PRODUCT_NAME&keyword=&ignoreDeposit=true&branchId=%s&platform=IN_STORE&includeConversion=true";
    String GET_AVAILABLE_IMEI_PATH = "/itemservice/api/item-model-codes/store/%s/search?itemId=%s&modelId=%s&branchId=%s&keyword=&status=AVAILABLE&page=0&size=9999";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    List<ProductInfo> productInfoList;
    String CAL_SHIPING_PLAN_FEE = "/orderservice3/api/bc-orders/calc-shipping-plan-fee";
    int branchId;
    boolean hasDelivery;
    boolean isGuestCheckout;
    double receiveAmount;
    int customerId;
    CustomerInfo customerInfo;
    public APICreateOrderPOS(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    @Data
    public static class APICreatePOSCondition{
        boolean isGuestCheckout = false;
        boolean hasDelivery  = true;
        List<ProductInfo> productInfoList;
        int branchId = 0;
        int customerId = 0;
    }
    public APICreateOrderPOS getInfo(APICreatePOSCondition condition) {
        productInfoList = condition.getProductInfoList();
        branchId = condition.getBranchId();
        hasDelivery = condition.isHasDelivery();
        isGuestCheckout = condition.isGuestCheckout();
        customerId = condition.getCustomerId();
        if(!isGuestCheckout){
            if(customerId==0) customerId = new APIAllCustomers(loginInformation).getAllAccountCustomerId().get(0);
            customerInfo = new APICustomerDetail(loginInformation).getInfo(customerId);
        }
        if(branchId==0){
            branchId = new BranchManagement(loginInformation).getFreeBranch();
        }
        return this;
    }

    public String getProductWithoutVariationInfoForBody(int productId, int branchId) {
        System.out.println("ProductId: " + productId);
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
                """.formatted(branchId, productId, branchId, price);
        return productInfo;
    }
    public String getDeliveryInfoForBody(){
        String deliveryInfo;
        if(hasDelivery&&!isGuestCheckout){
            deliveryInfo = """
                    "deliveryInfo": {
                        "contactName": "%s",
                        "email": "%s",
                        "phoneNumber": "%s",
                        "phoneCode": "%s",
                        %s
                      },
                    """.formatted(customerInfo.getMainEmailName(),customerInfo.getMainEmail(),customerInfo.getMainPhoneNumber(),customerInfo.getMainPhoneCode(),getAddressInfo());
        }else {
            String random = new DataGenerator().randomNumberGeneratedFromEpochTime(8);
            deliveryInfo = """
                    "deliveryInfo": {
                        "contactName": "%s",
                        "email": "%s",
                        "phoneNumber": "%s",
                        "phoneCode": "%s",
                        %s
                      },
                    """.formatted("name "+random,random+"@mailnesia.com","09"+random,"+84",getAddressInfo());
        }
        return deliveryInfo;
    }
    public String getCustomerAndDeliveryInfoForBody(int customerId) {
        if (customerId == 0) {
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
        customerInfo = new APICustomerDetail(loginInformation).getInfo(customerId);
        String userId = customerInfo.getUserId();
        String contactName = customerInfo.getMainPhoneName();
        String email = customerInfo.getMainEmail();
        String phone = customerInfo.getMainPhoneNumber();
        String phoneCode = customerInfo.getMainPhoneCode();
        String customerInfoBody = """
                    "userId": "%s",
                    "profileId": %s,
                    "userGuest": true,
                    "customerId": %s,
                    "deliveryInfo": {
                        "contactName": "%s",
                        "email": "%s",
                        "phoneNumber": "%s",
                        "phoneCode": "%s",
                        %s
                      },
                      %s
                    "note": "",
                """.formatted(userId, customerId,customerId,contactName, email, phone, phoneCode,getAddressInfo(),getShippingPlanFeeBody());
        return customerInfoBody;
    }
    public String getShippingPlanFeeBody(){
        ShippingPlanFeeInfo shippingPlanFeeInfo = new ShippingPlanFeeInfo();
        if(hasDelivery){
            shippingPlanFeeInfo = getShippingPlanFee();
        }
        String body= """
                "deliveryServiceId": %s,
                "deliveryMethod": "%s",
                "receivedAmount": %s,
                """.formatted(shippingPlanFeeInfo.getDeliveryServiceId(),shippingPlanFeeInfo.getProviderName(),receiveAmount);
        return body;
    }
    public String getAddressInfo(){
        String address = """
                        "locationCode": "VN-59",
                        "districtCode": "6104",
                        "wardCode": "610412",
                        "geoLocation": null,
                        "address": "Address wHRHs",
                        "address2": "",
                        "zipCode": "",
                        "countryCode": "VN",
                        "city": "VN-59"
                """;
        return address;
    }

    /**
     * @param customerId 0: if create order for guess, customerId if create order for specific user.
     * @param productId
     * @return
     */
    public int createPOSOrder(int customerId, int... productId) {
        int branchId = new BranchManagement(loginInformation).getFreeBranch();
        int productIdToOder = productId.length > 0 ? productId[0] : getSuggestionProductIDInStock(branchId);
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
                """.formatted(getProductWithoutVariationInfoForBody(productIdToOder, branchId), getCustomerAndDeliveryInfoForBody(customerId));
        Map<String, Object> mapHeader = new HashMap<>();
        mapHeader.put("storeid", String.valueOf(loginInfo.getStoreID()));
        mapHeader.put("platform", "WEB");
        mapHeader.put("isinternational", false);
        mapHeader.put("langkey", "en");
        Response response = api.post(CREATE_ORDER_PATH, loginInfo.getAccessToken(), body, mapHeader);
        response.then().statusCode(200);
        return response.jsonPath().getInt("id");
    }

    public int getSuggestionProductIDInStock(int branchId) {
        Response response = api.get(SEARCH_SUGGESTION.formatted(loginInfo.getStoreID(), branchId), loginInfo.getAccessToken());
        return response.jsonPath().getInt("find {it.remainingStock > 0}.id");
    }

    public int createPOSOrderByBranch(int customerId, int branchId) {
        int productIdToOder = getSuggestionProductIDInStock(branchId);
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
                """.formatted(getProductWithoutVariationInfoForBody(productIdToOder, branchId), getCustomerAndDeliveryInfoForBody(customerId));
        Map<String, Object> mapHeader = new HashMap<>();
        mapHeader.put("storeid", String.valueOf(loginInfo.getStoreID()));
        mapHeader.put("platform", "WEB");
        mapHeader.put("isinternational", false);
        mapHeader.put("langkey", "en");
        Response response = api.post(CREATE_ORDER_PATH, loginInfo.getAccessToken(), body, mapHeader);
        response.then().statusCode(200);
        return response.jsonPath().getInt("id");
    }

    //Product no variation, product has variation, product has conversion, imei
    public String getProductInfoForRequestBody() {
        JsonArray itemArray = new JsonArray();
        for (int i = 0; i < productInfoList.size(); i++) {
            int productId = productInfoList.get(i).getProductId();
            String modelId = "";
            boolean hasModel = productInfoList.get(i).isHasModel();
            if (hasModel) modelId = productInfoList.get(i).getVariationModelList().get(0).split("-")[1];
            List<ProductConversionInfo> conversionProductInfo;
            if (hasModel)
                conversionProductInfo = new ConversionUnit(loginInformation).getProductConversionInfoHasModel(productId, Integer.parseInt(modelId));
            else
                conversionProductInfo = new ConversionUnit(loginInformation).getProductConversionInfoNoModel(productId);
            if (conversionProductInfo.size() > 0) {
                productId = conversionProductInfo.get(0).getItemCloneId();
                receiveAmount = conversionProductInfo.get(0).getNewPrice();
                if(hasModel) modelId = new APIProductDetail(loginInformation).getInfo(productId,variation).getVariationModelList().get(0).split("-")[1];
            }else receiveAmount = productInfoList.get(i).getProductSellingPrice().get(0).doubleValue();
            boolean isManageByIMEI = productInfoList.get(i).getManageInventoryByIMEI();
            JsonObject item = new JsonObject();
            item.addProperty("itemId", productId);
            item.addProperty("modelId", modelId);
            item.addProperty("quantity", 1);
            JsonArray itemTotalDiscountArray = new JsonArray();
            item.add("itemTotalDiscounts", itemTotalDiscountArray);
            JsonArray lotLocationArray = new JsonArray();
            item.add("lotLocations", lotLocationArray);
            item.addProperty("branchId", branchId);

            if (isManageByIMEI) {
                JsonArray imeiArray = new JsonArray();
                imeiArray.add(getAvailableIMEI(branchId, productId, Integer.parseInt(modelId)).get(0));
                item.add("imeiSerials", imeiArray);
            }
            itemArray.add(item);
        }
        String productInfo = """
                    {
                      "branchId": %s,
                      "items": %s
                    }
                """.formatted(branchId, itemArray.toString());
        return productInfo;
    }

    public List<String> getAvailableIMEI(int branchId, int productId, int modelId) {
        Response response = api.get(GET_AVAILABLE_IMEI_PATH.formatted(loginInfo.getStoreID(), productId, modelId, branchId), loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response.jsonPath().getList("code");
    }
    @Data
    public static class ShippingPlanFeeInfo {
        private int weight;
        private double originalFee;
        private double fee = 0;
        private int feeDeduction;
        private String currency;
        private long deliveryServiceId = 75;
        private String providerName = "";
        private String serviceName;
        private List<String> discounts;
    }
    public ShippingPlanFeeInfo getShippingPlanFee(){
        JsonArray itemsArray = new JsonArray();
        long price=0;
        for(int i = 0;i<productInfoList.size();i++){
                int productId = productInfoList.get(i).getProductId();
                String modelId="";
                int weight, height, length, width;
                boolean hasModel = productInfoList.get(i).isHasModel();
                if (hasModel) modelId = productInfoList.get(i).getVariationModelList().get(0).split("-")[1];
                List<ProductConversionInfo> conversionProductInfo;
                if (hasModel)
                    conversionProductInfo = new ConversionUnit(loginInformation).getProductConversionInfoHasModel(productId, Integer.parseInt(modelId));
                else
                    conversionProductInfo = new ConversionUnit(loginInformation).getProductConversionInfoNoModel(productId);
            JsonObject itemObject = new JsonObject();
            if (conversionProductInfo.size() > 0) {
                    price = (long) (price + conversionProductInfo.get(0).getNewPrice());
                    productId = conversionProductInfo.get(0).getItemCloneId();
                    weight = conversionProductInfo.get(0).getWeight();
                    height = conversionProductInfo.get(0).getHeight();
                    length = conversionProductInfo.get(0).getLength();
                    width = conversionProductInfo.get(0).getWidth();
                } else {
                    price = price + productInfoList.get(i).getProductSellingPrice().get(0);
                    weight = productInfoList.get(i).getWeight();
                    height = productInfoList.get(i).getHeight();
                    length = productInfoList.get(i).getLength();
                    width = productInfoList.get(i).getWidth();
                }
                itemObject.addProperty("id",productId);
                itemObject.addProperty("quantity",1);
                itemObject.addProperty("length",length);
                itemObject.addProperty("height",height);
                itemObject.addProperty("weight",weight);
                itemObject.addProperty("width",width);
                itemObject.addProperty("price",price);
                itemObject.addProperty("name",productInfoList.get(i).getMainProductNameMap().get(new StoreLanguageAPI(loginInformation).getDefaultLanguage().getLangCode()));
            itemsArray.add(itemObject);
        }
        String body = """
                {
                    "deliveryInfo":{
                    %s
                    },
                    "items": %s,
                    "storeId": "%s",
                    "branchId": %s,
                    "paymentMethod": "CASH",
                    "preTotalPrice": %s
                }
                """.formatted(getAddressInfo(),itemsArray.toString(),loginInfo.getStoreID(),branchId,price);
        Map<String, Object> mapHeader = new HashMap<>();
        mapHeader.put("storeid", String.valueOf(loginInfo.getStoreID()));
        Response response = api.post(CAL_SHIPING_PLAN_FEE,loginInfo.getAccessToken(),body,mapHeader);
        List<ShippingPlanFeeInfo> shippingPlanFeeInfo = Arrays.asList(response.as(ShippingPlanFeeInfo[].class));
        receiveAmount = price + shippingPlanFeeInfo.get(0).getFee();
        return shippingPlanFeeInfo.get(0);
    }
    public String getCustomerInfoForBody(){
        if(isGuestCheckout) return """
                "userGuest": true,
                """;
        return """
                    "userId": "%s",
                    "profileId": %s,
                    "userGuest": true,
                    "customerId": %s,
                """.formatted(customerInfo.getUserId(),customerId,customerId);
    }
    public int createPOSOrder() {
        branchId = branchId==0? new BranchManagement(loginInformation).getFreeBranch():branchId;
        String body = """
                {
                    "branches": [
                        %s
                      ],
                    %s%s%s"paymentCode": "",
                    "paymentMethod": "CASH",
                    "platform": "IN_STORE",
                    "inStore": true,
                    "isAllowEarningPoint": true,
                    "applyTax":true,
                    "taxAmount": 0,
                    "paymentMposId": 0,
                    "paymentMposDeviceCode": ""
                }
                """.formatted(getProductInfoForRequestBody(), getCustomerInfoForBody(),getDeliveryInfoForBody(),getShippingPlanFeeBody());
        Map<String, Object> mapHeader = new HashMap<>();
        mapHeader.put("storeid", String.valueOf(loginInfo.getStoreID()));
        mapHeader.put("platform", "WEB");
        mapHeader.put("isinternational", false);
        mapHeader.put("langkey", "en");
        Response response = api.post(CREATE_ORDER_PATH, loginInfo.getAccessToken(), body, mapHeader);
        response.then().statusCode(200);
        return response.jsonPath().getInt("id");
    }
}
