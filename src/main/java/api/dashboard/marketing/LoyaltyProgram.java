package api.dashboard.marketing;

import utilities.api.API;
import utilities.data.DataGenerator;

import static api.dashboard.customers.Customers.segmentID;
import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.storeID;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.*;

public class LoyaltyProgram {
    String CREATE_MEMBERSHIP_PATH = "/beehiveservices/api/memberships";
    public void createNewMembership() {
        String name = "Auto - Membership - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        String description = randomAlphabetic(nextInt(MAX_MEMBERSHIP_DESCRIPTION_LENGTH));
        int discountPercent = nextInt(MAX_PERCENT_DISCOUNT) + 1;
        int discountMaxAmount = nextInt(1000000) + 1;

        String body = """
                {
                    "name": "%s",
                    "description": "%s",
                    "segmentId": %s,
                    "sellerId": %s,
                    "priority": 1,
                    "enabledBenefit": true,
                    "discountPercent": "%s",
                    "discountMaxAmount": "%s",
                    "image": {
                        "urlPrefix": "",
                        "imageUUID": "",
                        "extension": ""
                    }
                }""".formatted(name, description, segmentID, storeID, discountPercent, discountMaxAmount);

        new API().post(CREATE_MEMBERSHIP_PATH, accessToken, body).then().statusCode(200);
    }
}
