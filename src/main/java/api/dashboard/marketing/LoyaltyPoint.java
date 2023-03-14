package api.dashboard.marketing;

import utilities.api.API;

import java.util.Collections;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;
import static api.dashboard.products.CreateProduct.*;
import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class LoyaltyPoint {
    String LOYALTY_POINT_PATH = "/beehiveservices/api/loyalty-point-settings/store/";
    public static int apiRatePoint;
    public static long apiRateAmount;
    public static long apiExchangeAmount;

    public void changeLoyaltyPointSetting(int... setting) {
        int loyaltyPointID = new API().get(LOYALTY_POINT_PATH + apiStoreID, accessToken).jsonPath().getInt("id");
        apiRatePoint = setting.length > 0 ? setting[0] : nextInt(10) + 1;
        apiRateAmount = setting.length > 1 ? setting[1] : (apiIsVariation ? Collections.max(apiProductSellingPrice)/2 : apiProductSellingPrice.get(0)/2);
        apiExchangeAmount = setting.length > 2 ? setting[2] : (apiIsVariation ? Collections.max(apiProductSellingPrice)/2 : apiProductSellingPrice.get(0)/2);
        String body = """
                {
                    "clearPoint": false,
                    "settingData": {
                        "id": %s,
                        "storeId": %s,
                        "enabled": true,
                        "expirySince": 1,
                        "showPoint": true,
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
                }""".formatted(loyaltyPointID, apiStoreID, apiRatePoint, apiRateAmount, apiExchangeAmount);

        new API().put(LOYALTY_POINT_PATH + apiStoreID, accessToken, body);
    }
}
