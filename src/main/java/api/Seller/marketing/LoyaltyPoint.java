package api.Seller.marketing;

import api.Seller.login.Login;
import api.Seller.products.all_products.CreateProduct;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.marketing.loyaltyPoint.LoyaltyPointInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.Collections;

import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class LoyaltyPoint {
    final static Logger logger = LogManager.getLogger(LoyaltyPoint.class);

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
    public LoyaltyPointInfo getLoyaltyPointSetting(){
        Response response = new API().get(LOYALTY_POINT_PATH + loginInfo.getStoreID(),loginInfo.getAccessToken());
        response.then().statusCode(200);
        LoyaltyPointInfo info = new LoyaltyPointInfo();
        info.setId(response.jsonPath().getInt("id"));
        info.setStoreId(response.jsonPath().getInt("storeId"));
        info.setEnabled(response.jsonPath().getBoolean("enabled"));
        info.setEnableExpiryDate(response.jsonPath().getBoolean("enableExpiryDate"));
        info.setExchangeAmount(response.jsonPath().getLong("exchangeAmount"));
        info.setExchangePoint(response.jsonPath().getInt("exchangePoint"));
        info.setCheckout(response.jsonPath().getBoolean("checkouted"));
        info.setIntroduced(response.jsonPath().getBoolean("introduced"));
        info.setRefered(response.jsonPath().getBoolean("refered"));
        info.setRateAmount(response.jsonPath().getLong("rateAmount"));
        info.setRatePoint(response.jsonPath().getInt("ratePoint"));
        info.setPurchased(response.jsonPath().getBoolean("purchased"));
        info.setShowPoint(response.jsonPath().getBoolean("showPoint"));
        boolean containExpirySince = response.body().asString().toUpperCase().contains("expirysince");
        if(containExpirySince)
            info.setExpirySince(response.jsonPath().getInt("expirySince"));
        else info.setExpirySince(0);
        return info;
    }
    public void enableOrDisableProgram(boolean isEnable){
        LoyaltyPointInfo info = getLoyaltyPointSetting();
        if(info.isEnabled() == isEnable) {
            logger.info("Enable Point program is %s, so no need call api to set up.".formatted(info.isEnabled()));
            return;
        }
        String expirySinceInBody = info.getExpirySince() ==0?"": String.valueOf(info.getExpirySince());
        String body = """
                {
                    "clearPoint": false,
                    "settingData":{
                        "id": %s,
                        "storeId": %s,
                        "enabled": %s,
                        "expirySince": "%s",
                        "showPoint": %s,
                        "purchased": %s,
                        "ratePoint": %s,
                        "rateAmount": %s,
                        "refered": %s,
                        "introduced": %s,
                        "checkouted": %s,
                        "exchangePoint": %s,
                        "exchangeAmount": %s,
                        "enableExpiryDate": %s
                    }
                }
                """.formatted(info.getId(),info.getStoreId(),isEnable,expirySinceInBody,info.isShowPoint(),
                info.isPurchased(),info.getRatePoint(),info.getRateAmount(),info.isRefered(),info.isIntroduced(),
                info.isCheckout(),info.getExchangePoint(),info.getExchangeAmount(),info.isEnableExpiryDate());
        Response response = new API().put(LOYALTY_POINT_PATH + loginInfo.getStoreID(),loginInfo.getAccessToken(),body);
        response.then().statusCode(200);
        logger.info("Set up enable point program = "+isEnable);

    }

    public boolean isEnableLoyaltyPoint(){
        Response response = new API().get(LOYALTY_POINT_PATH + loginInfo.getStoreID(),loginInfo.getAccessToken());
        return (response.getStatusCode() != 403) && response.then().statusCode(200).extract().jsonPath().getBoolean("enabled");
    }
}
