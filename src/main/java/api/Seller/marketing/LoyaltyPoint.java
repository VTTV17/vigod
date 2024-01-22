package api.Seller.marketing;

import api.Seller.login.Login;
import api.Seller.products.CreateProduct;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.Collections;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class LoyaltyPoint {
    String LOYALTY_POINT_PATH = "/beehiveservices/api/loyalty-point-settings/store/";
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public  LoyaltyPoint (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public void changeLoyaltyPointSetting(int... setting) {
        int loyaltyPointID = new API().get(LOYALTY_POINT_PATH + loginInfo.getStoreID(), loginInfo.getAccessToken()).jsonPath().getInt("id");
        int apiRatePoint = setting.length > 0 ? setting[0] : nextInt(10) + 1;
        long apiRateAmount = setting.length > 1 ? setting[1] : (new CreateProduct(loginInformation).isHasModel() ? Collections.max(new CreateProduct(loginInformation).getProductSellingPrice()) / 2 : new CreateProduct(loginInformation).getProductSellingPrice().get(0) / 2);
        long apiExchangeAmount = setting.length > 2 ? setting[2] : (new CreateProduct(loginInformation).isHasModel() ? Collections.max(new CreateProduct(loginInformation).getProductSellingPrice()) / 2 : new CreateProduct(loginInformation).getProductSellingPrice().get(0) / 2);
        String body = """
                {
                    "clearPoint": false,
                    "settingData": {
                        "id": %s,
                        "storeId": %s,
                        "enabled": true,
                        "expirySince": 1,
                        "showPoint": true,accessToken
                        "purchased": true,
                        "ratePoint": %s,
                        "rateAmount": %s,
                        "refered": false,
                        "introduced": false,
                        "checkouted": true,
                        "exchangePoint": 1,
                        "exchangeAmount": %s,
                        "enableExpiryDate": true,
                        "toggle-enabled": true,
                        "checkbox-enable-expiry-date": true
                    }
                }""".formatted(loyaltyPointID, loginInfo.getStoreID(), apiRatePoint, apiRateAmount, apiExchangeAmount);

        new API().put(LOYALTY_POINT_PATH + loginInfo.getStoreID(),  loginInfo.getAccessToken(), body);
    }
}
