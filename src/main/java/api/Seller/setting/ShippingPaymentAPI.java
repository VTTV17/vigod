package api.Seller.setting;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class ShippingPaymentAPI {
    String SELF_DELIVERY_PATH = "/storeservice/api/delivery-providers/self-delivery/storeId/%s";

    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    API api = new API();
    
    public ShippingPaymentAPI(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public JsonPath getInfo() {
        return api.get(SELF_DELIVERY_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).then().log().all().statusCode(200).extract().response().jsonPath();
    }
    
    public JsonPath toggleSelfDelivery(boolean switchedOn) {
        String body = getInfo().prettify().replaceAll("\"enabled\": \\w+", "\"enabled\": %s".formatted(switchedOn));
        return api.put(SELF_DELIVERY_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken(), body).then().log().all().statusCode(200).extract().response().jsonPath();
    }    
    
}
