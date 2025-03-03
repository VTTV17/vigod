package api.Seller.marketing;

import api.Seller.login.Login;
import api.Seller.products.all_products.APICreateProduct;
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
        long apiRateAmount = setting.length > 1 ? setting[1] : (new APICreateProduct(loginInformation).isHasModel() ? Collections.max(new APICreateProduct(loginInformation).getProductSellingPrice()) / 2 : new APICreateProduct(loginInformation).getProductSellingPrice().get(0) / 2);
        long apiExchangeAmount = setting.length > 2 ? setting[2] : (new APICreateProduct(loginInformation).isHasModel() ? Collections.max(new APICreateProduct(loginInformation).getProductSellingPrice()) / 2 : new APICreateProduct(loginInformation).getProductSellingPrice().get(0) / 2);
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
        LoyaltyPointInfo info = response.as(LoyaltyPointInfo.class);
        boolean containExpirySince = response.body().asString().toUpperCase().contains("expirysince");
        if(!containExpirySince) info.setExpirySince(0);
        return info;
    }
    public Response enableOrDisableProgram(boolean isEnable){
        Response response = null;
        LoyaltyPointInfo info = getLoyaltyPointSetting();
        if(info.getEnabled().booleanValue() == isEnable) {
            logger.info("Enable Point program is %s, so no need call api to set up.".formatted(info.getEnabled().booleanValue()));
            return response;
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
                info.isCheckouted(),info.getExchangePoint(),info.getExchangeAmount(),info.isEnableExpiryDate());
        response = new API().put(LOYALTY_POINT_PATH + loginInfo.getStoreID(),loginInfo.getAccessToken(),body);
        logger.info("Set up enable point program = "+isEnable);
        return response;
    }

    public boolean isEnableLoyaltyPoint(){
        Response response = new API().get(LOYALTY_POINT_PATH + loginInfo.getStoreID(),loginInfo.getAccessToken());
        return (response.getStatusCode() != 403) && response.then().statusCode(200).extract().jsonPath().getBoolean("enabled");
    }

    /**
     * To call API update loyalty point with information: enable or not, rate point, rate amount
     * exchange point, exchange amount
     * @param loyaltyPointInfo: set value into loyaltyPointInfo if need update
     * @return Response
     */
    public Response callAPIUpdateLoyaltyPoint(LoyaltyPointInfo loyaltyPointInfo){
        LoyaltyPointInfo infoCurrent = getLoyaltyPointSetting();
        boolean isEnable = loyaltyPointInfo.getEnabled()==null? infoCurrent.getEnabled(): loyaltyPointInfo.getEnabled().booleanValue();
        String expirySinceInBody = infoCurrent.getExpirySince() ==0?"": String.valueOf(infoCurrent.getExpirySince());
        int ratePoint = loyaltyPointInfo.getRatePoint()==null? infoCurrent.getRatePoint(): loyaltyPointInfo.getRatePoint();
        long rateAmount = loyaltyPointInfo.getRateAmount()==null? infoCurrent.getRateAmount(): loyaltyPointInfo.getRateAmount();
        int exchangePoint = loyaltyPointInfo.getExchangePoint()==null? infoCurrent.getExchangePoint(): loyaltyPointInfo.getExchangePoint();
        long exchangeAmount = loyaltyPointInfo.getExchangeAmount()==null? infoCurrent.getExchangeAmount(): loyaltyPointInfo.getExchangeAmount();

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
                """.formatted(infoCurrent.getId(),infoCurrent.getStoreId(),isEnable,expirySinceInBody,infoCurrent.isShowPoint(),
                infoCurrent.isPurchased(),ratePoint,rateAmount,infoCurrent.isRefered(),infoCurrent.isIntroduced(),
                infoCurrent.isCheckouted(),exchangePoint,exchangeAmount,infoCurrent.isEnableExpiryDate());
        Response response = new API().put(LOYALTY_POINT_PATH + loginInfo.getStoreID(),loginInfo.getAccessToken(),body);
        return response;
    }
}
