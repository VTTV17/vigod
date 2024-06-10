package api.Seller.customers;

import api.Buyer.login.LoginSF;
import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.customer.segment.SegmentList;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.gochat.facebook.GeneralAutomationCampaign;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;
import java.util.Optional;

import static java.lang.Thread.sleep;

public class APISegment {
    final static Logger logger = LogManager.getLogger(APISegment.class);

    String CREATE_SEGMENT_PATH = "/beehiveservices/api/segments/create/";
    String GET_SEGMENT_LIST = "/beehiveservices/api/segments/store/%s?page=0&size=50&name.contains=&sort=id,desc";
    String DELETE_SEGMENT_PATH = "/beehiveservices/api/segments/delete/%s/%s";
    API api = new API();

    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APISegment(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    @Getter
    private String segmentName;
    @Getter
    private int segmentID;
    public void createSegment() {
        segmentName = "Auto - Segment - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");
        String body = """
                {
                    "name": "%s",
                    "matchCondition": "ALL",
                    "conditions": [
                        {
                            "name": "Customer Data_Customer tag_is equal to",
                            "value": "%s"
                        }
                    ]
                }
                """.formatted(segmentName,
                Optional.ofNullable(new APIEditCustomer(loginInformation).getCustomerTag())
                        .orElse("AutoTag" + new DataGenerator().generateDateTime("ddMMHHmmss")));
        Response createSegment = api.post(CREATE_SEGMENT_PATH + loginInfo.getStoreID(), loginInfo.getAccessToken(), body);
        createSegment.then().statusCode(200);
        segmentID = createSegment.jsonPath().getInt("id");
    }

    public void createSegmentByAPI(String account, String password, String phoneCode) throws InterruptedException {
        // login SF to create new Customer in Dashboard
        new LoginSF(loginInformation).LoginToSF(account, password, phoneCode);

        // wait customer is added
        sleep(3000);

        // add tag and create segment by tag name
        new APIEditCustomer(loginInformation).addCustomerTagForMailCustomer(account);

        // create segment
        createSegment();
    }

    public JsonPath getAllSegmentJsonPath() {
        return api.get(GET_SEGMENT_LIST.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response()
                .jsonPath();
    }

    /**
     * This method retrieves the ID of a segment by its name.
     * @param segmentName The name of the segment whose ID is to be retrieved.
     * @return The ID of the specified segment.
     */
    public int getSegmentIdByName(String segmentName) {
        return getAllSegmentJsonPath().get("find { it.name == '%s' }.id".formatted(segmentName));
    }

    /**
     * This method deletes a customer segment with a specific ID.
     * @param segmentId The ID of the segment to be deleted.
     */
    public void deleteSegment(int segmentId) {
        api.delete(DELETE_SEGMENT_PATH.formatted(loginInfo.getStoreID(), segmentId), loginInfo.getAccessToken()).then().statusCode(200);
        logger.info("Deleted customer segment with id: " + segmentId);
    }

    public List<Integer> getListSegmentIdInStore() {
        return getAllSegmentJsonPath().getList("id");
    }
	public List<SegmentList> getSegmentList() {
		Response response = api.get(GET_SEGMENT_LIST.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken()).then().statusCode(200).extract().response();
		return response.jsonPath().getList(".", SegmentList.class);
	} 

}
