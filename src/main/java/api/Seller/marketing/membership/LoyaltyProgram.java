package api.Seller.marketing.membership;

import api.Seller.customers.APIEditCustomer;
import api.Seller.customers.APISegment;
import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.data.testdatagenerator.LoyaltyProgramTDG;
import utilities.model.dashboard.customer.CustomerInfoFull;
import utilities.model.dashboard.customer.segment.CreateSegment;
import utilities.model.dashboard.customer.segment.SegmentCondition;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.marketing.loyaltyProgram.LoyaltyProgramInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.account.AccountTest.BUYER_ACCOUNT_THANG;
import static utilities.account.AccountTest.BUYER_PASSWORD_THANG;
import static utilities.character_limit.CharacterLimit.MAX_MEMBERSHIP_DESCRIPTION_LENGTH;
import static utilities.character_limit.CharacterLimit.MAX_PERCENT_DISCOUNT;

public class LoyaltyProgram {
	
	final static Logger logger = LogManager.getLogger(LoyaltyProgram.class);
	
	String CREATE_MEMBERSHIP_PATH = "/beehiveservices/api/memberships";
	String GET_ALL_MEMBERSHIP_PATH = CREATE_MEMBERSHIP_PATH + "?sellerId=%s&sort=priority,asc&page=0&size=100";
	String DELETE_MEMBERSHIP_PATH = CREATE_MEMBERSHIP_PATH + "/%s?sellerId=%s";
    
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    API api = new API();
    public LoyaltyProgram (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public int createNewMembership(){
        String name = "Auto - Membership - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        String description = randomAlphabetic(nextInt(MAX_MEMBERSHIP_DESCRIPTION_LENGTH));
        int discountPercent = nextInt(MAX_PERCENT_DISCOUNT) + 1;
        int discountMaxAmount = nextInt(1000000) + 1;

        if (new APISegment(loginInformation).getSegmentID() == 0) {
            try {
                new APISegment(loginInformation).createSegmentByAPI(BUYER_ACCOUNT_THANG, BUYER_PASSWORD_THANG, "+84");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

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
                }""".formatted(name, description, new APISegment(loginInformation).getSegmentID(), loginInfo.getStoreID(), discountPercent, discountMaxAmount);
         Response response = new API().post(CREATE_MEMBERSHIP_PATH, loginInfo.getAccessToken(), body);
         response.then().statusCode(201);
         return response.jsonPath().getInt("id");
    }
    
    /**
     * Retrieves all membership data as a JSONPath object
     * @return JsonPath object representing all membership data
     */
    public JsonPath getAllMembershipJsonPath() {
    	Response response = new API().get(GET_ALL_MEMBERSHIP_PATH.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
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
    	new API().delete(DELETE_MEMBERSHIP_PATH.formatted(membershipId,loginInfo.getStoreID()), loginInfo.getAccessToken()).then().statusCode(200);
    	logger.info("Deleted membership with id: " + membershipId);
    }        
    public int getAMembershipId(){
        List<Integer> ids = getAllMembershipJsonPath().getList("id");
        if(ids.isEmpty()) return 0;
        return ids.get(0);
    }
    public List<Integer> getMembershipList(){
        List<Integer> ids = getAllMembershipJsonPath().getList("id");
        logger.info("Mebership list: "+ids);
        return ids;
    }

    /**
     *
     * @param membershipInfo: require: segmentId, optional: enabledBenefit, discountPercent, discountMaxAmount if any
     * @return
     */
    public LoyaltyProgramInfo createMembership(LoyaltyProgramInfo membershipInfo){
        membershipInfo.setSellerId(loginInfo.getStoreID());
        Response response = api.post(CREATE_MEMBERSHIP_PATH,loginInfo.getAccessToken(),membershipInfo);
        response.then().statusCode(201);
        return response.as(LoyaltyProgramInfo.class);
    }
    public LoyaltyProgramInfo getMembershipDetail(int id){
        LoyaltyProgramInfo detail = getAllMembershipJsonPath().getObject("find {it.id==%s}".formatted(id),LoyaltyProgramInfo.class);
        return detail;
    }
    public LoyaltyProgramInfo getRandomMembershipDetail(){
        JsonPath jsonDetail = getAllMembershipJsonPath();
        LoyaltyProgramInfo detai = new LoyaltyProgramInfo();
        if(jsonDetail.getList("id").size()>0){
            detai = jsonDetail.getObject("find {[0]}",LoyaltyProgramInfo.class);
        }else {
            detai = LoyaltyProgramTDG.generateLoyaltyProgram();
            if(new APISegment(loginInformation).getSegmentList().size()==0){
                new APISegment(loginInformation).createSegment();
            }
            int getSegmentId = new APISegment(loginInformation).getSegmentList().get(0).getId();
            detai.setSegmentId(getSegmentId);
            createMembership(detai);
        }
        System.out.println(detai);
        return detai;
    }
    public LoyaltyProgramInfo setUpMembershipForCustomer(int customerId){
        String tagValue = "membership" + new DataGenerator().randomNumberGeneratedFromEpochTime(5);
        new APIEditCustomer(loginInformation).addMoreTagForCustomer(customerId, List.of(tagValue));
        SegmentCondition condition = new SegmentCondition();
        condition.setName("Customer Data_Customer tag_is equal to");
        condition.setValue(tagValue);
        CreateSegment segmentdata = new CreateSegment();
        segmentdata.setName("Segment " + tagValue);
        segmentdata.setMatchCondition("ALL");
        segmentdata.setConditions(Arrays.asList(condition));
        Response responseSegment = new APISegment(loginInformation).createSegment(segmentdata);
        responseSegment.prettyPrint();
        int segmentId = responseSegment.jsonPath().getInt("id");
        System.out.println(segmentId);
        int membershipId = getAMembershipId();
        LoyaltyProgramInfo membershipInfo;
        if(membershipId == 0) {
            membershipInfo = LoyaltyProgramTDG.generateLoyaltyProgram();
            membershipInfo.setSegmentId(segmentId);
            membershipInfo.setName("Membership "+tagValue);
            membershipInfo = createMembership(membershipInfo);
        }else {
            membershipInfo = new APIEditLoyaltyProgram(loginInformation).getPayloadAsDefault(membershipId);
            membershipInfo.setSegmentId(segmentId);
            membershipInfo.setName("Membership "+tagValue);
            new APIEditLoyaltyProgram(loginInformation).editLoyaltyProgram(membershipInfo);
        }
        return membershipInfo;
    }
}
