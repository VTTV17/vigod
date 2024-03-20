package api.Seller.promotion;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class BuyXGetY {
	String BUYX_GETY_ROOT_PATH = "/orderservices2/api/gs-bxgy";
	String CREATE_BUYX_GETY_PATH = BUYX_GETY_ROOT_PATH;
	String GET_BUYX_GETY_PATH = BUYX_GETY_ROOT_PATH + "/search/%s?page=0&size=10";
	String DELETE_BUYX_GETY_PATH = BUYX_GETY_ROOT_PATH + "/%s/%s";

	API api = new API();
	Logger logger = LogManager.getLogger(BuyXGetY.class);

	LoginDashboardInfo loginInfo;
	LoginInformation loginInformation;

	public BuyXGetY(LoginInformation loginInformation) {
		this.loginInformation = loginInformation;
		loginInfo = new Login().getInfo(loginInformation);
	}

	public JsonPath createBuyXGetYProgramJsonPath(int productId) {

		String programName = "Auto - Buy X Get Y - " + new DataGenerator().generateDateTime("dd/MM HH:mm:ss");

		String body = """
				{
				"name": "%s",
				"activeDate": "2024-03-13T17:00:00.000Z",
				"conditions": [{
				"conditionType": "CUSTOMER_SEGMENT",
				"conditionOption": "ALL_CUSTOMERS",
				"values": []
				}, {
				"conditionType": "GIFT_GIVEAWAY",
				"conditionOption": "SPECIFIC_PRODUCTS",
				"values": [{
				"conditionValue": "%s"
				}]
				}, {
				"conditionType": "APPLIED_BRANCHES",
				"conditionOption": "ALL_BRANCHES",
				"values": []
				}, {
				"conditionType": "PLATFORM",
				"values": [{
				"conditionValue": "WEB"
				}, {
				"conditionValue": "APP"
				}, {
				"conditionValue": "INSTORE"
				}, {
				"conditionValue": "GOSOCIAL"
				}]
				}, {
				"conditionType": "DISCOUNT_METHOD",
				"conditionOption": "FREE",
				"values": []
				}, {
				"conditionType": "DISCOUNT_METHOD",
				"conditionOption": "GIVE_AWAY_MAXIMUM_QUANTITY",
				"values": [{
				"conditionValue": "1"
				}]
				}, {
				"conditionType": "APPLIES_TO",
				"conditionOption": "COMBO",
				"values": [{
				"conditionValue": "%s|1"
				}]
				}],
				"expiryDate": "3024-03-15T17:00:00.000Z",
				"storeId": "%s"
				}
				""".formatted(programName, productId, productId, loginInfo.getStoreID());
		Response createRecord = api.post(CREATE_BUYX_GETY_PATH, loginInfo.getAccessToken(), body);
		createRecord.then().statusCode(201);
		return createRecord.jsonPath();
	}
	
	//Temporary function to create an IN_PROGRESS buy x get y program
	public int createBuyXGetYProgram(int productId) {
		return createBuyXGetYProgramJsonPath(productId).getInt("id");
	}

	public void deleteBuyXGetYProgram(int programId) {
		String path = DELETE_BUYX_GETY_PATH.formatted(loginInfo.getStoreID(), programId);
		Response response = api.delete(path, loginInfo.getAccessToken());
		response.then().statusCode(204);
		logger.info("Deleted Buy X Get Y program with id: " + programId);
	}    

	/**
	 * @param status EXPIRED/IN_PROGRESS/SCHEDULED/ALL
	 */
	public List<Integer> getProgramByStatus(String status) {
		String path = GET_BUYX_GETY_PATH.formatted(loginInfo.getStoreID());
		if (!status.contentEquals("ALL")) {
			path += "&status=" + status;
		}
		
		Response response = api.get(path, loginInfo.getAccessToken());
		response.then().statusCode(200);
		return response.jsonPath().getList("content.id");
	}

}
