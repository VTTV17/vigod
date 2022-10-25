package api.dashboard.marketing;

import utilities.api.API;

import java.util.Collections;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.storeID;
import static api.dashboard.products.CreateProduct.*;
import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class LoyaltyPoint {
    String LOYALTY_POINT_PATH = "/beehiveservices/api/loyalty-point-settings/store/";
    public static int ratePoint;
    public static int rateAmount;
    public static int exchangeAmount;

    public void changeLoyaltyPointSetting(int... setting) {
        int loyaltyPointID = new API().get(LOYALTY_POINT_PATH + storeID, accessToken).jsonPath().getInt("id");
        ratePoint = setting.length > 0 ? setting[0] : nextInt(10) + 1;
        rateAmount = setting.length > 1 ? setting[1] : (isVariation ? Collections.max(variationSellingPrice)/2 : withoutVariationSellingPrice/2);
        exchangeAmount = setting.length > 2 ? setting[2] : (isVariation ? Collections.max(variationSellingPrice)/2 : withoutVariationSellingPrice/2);
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
                }""".formatted(loyaltyPointID, storeID, ratePoint, rateAmount, exchangeAmount);

        new API().update(LOYALTY_POINT_PATH + storeID, accessToken, body);
    }
}
