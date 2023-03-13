package api.dashboard.marketing;

import utilities.api.API;
import utilities.data.DataGenerator;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static api.dashboard.customers.Customers.apiSegmentID;
import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;
import static api.dashboard.products.CreateProduct.apiVariationList;
import static api.dashboard.setting.BranchManagement.apiBranchName;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.*;

public class LoyaltyProgram {
    String CREATE_MEMBERSHIP_PATH = "/beehiveservices/api/memberships";
    public static Map<String, List<String>> apiMembershipStatus;
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
                }""".formatted(name, description, apiSegmentID, apiStoreID, discountPercent, discountMaxAmount);

        new API().post(CREATE_MEMBERSHIP_PATH, accessToken, body).then().statusCode(200);

        apiBranchName.forEach(brName -> apiMembershipStatus
                .put(brName, IntStream.range(0, apiVariationList.size())
                        .mapToObj(i -> "IN_PROGRESS").toList()));
    }
}
