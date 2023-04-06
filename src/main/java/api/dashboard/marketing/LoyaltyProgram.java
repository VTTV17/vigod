package api.dashboard.marketing;

import api.dashboard.customers.Customers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.data.DataGenerator;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static api.dashboard.customers.Customers.apiSegmentID;
import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;
import static api.dashboard.products.CreateProduct.apiVariationList;
import static api.dashboard.setting.BranchManagement.apiBranchName;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.account.AccountTest.BUYER_ACCOUNT_THANG;
import static utilities.account.AccountTest.BUYER_PASSWORD_THANG;
import static utilities.character_limit.CharacterLimit.*;

public class LoyaltyProgram {
	
	final static Logger logger = LogManager.getLogger(LoyaltyProgram.class);
	
	String CREATE_MEMBERSHIP_PATH = "/beehiveservices/api/memberships";
	String GET_ALL_MEMBERSHIP_PATH = CREATE_MEMBERSHIP_PATH + "?sellerId=%s&sort=priority,asc&page=0&size=100";
	String DELETE_MEMBERSHIP_PATH = CREATE_MEMBERSHIP_PATH + "/%s?sellerId=%s";
	
    public static Map<String, List<String>> apiMembershipStatus;
    public void createNewMembership() throws InterruptedException {
        String name = "Auto - Membership - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        String description = randomAlphabetic(nextInt(MAX_MEMBERSHIP_DESCRIPTION_LENGTH));
        int discountPercent = nextInt(MAX_PERCENT_DISCOUNT) + 1;
        int discountMaxAmount = nextInt(1000000) + 1;

        if (apiSegmentID == 0) new Customers().createSegmentByAPI(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG, "+84");

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
    
    /**
     * Retrieves all membership data as a JSONPath object
     * @return JsonPath object representing all membership data
     */
    public JsonPath getAllMembershipJsonPath() {
    	Response response = new API().get(GET_ALL_MEMBERSHIP_PATH.formatted(apiStoreID), accessToken);
    	response.then().statusCode(200);
    	return response.jsonPath();
    }
    
    /**
     * Retrieves the ID of a membership by its name.
     * @param membershipName name of membership to retrieve ID for
     * @return integer value representing the ID of the membership with the given name
     */
    public int getMembershipIdByName(String membershipName) {
    	return getAllMembershipJsonPath().get("find { it.name == '%s' }.id".formatted(membershipName));
    }    
  
    /**
     * Deletes the membership with the given ID using the API
     * @param membershipId the ID of the membership to be deleted
     */
    public void deleteMembership(int membershipId) {
    	new API().delete(DELETE_MEMBERSHIP_PATH.formatted(membershipId,apiStoreID), accessToken).then().statusCode(200);
    	logger.info("Deleted membership with id: " + membershipId);
    }        
    
}
