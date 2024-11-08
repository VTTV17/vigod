package api.Seller.orders;

import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class OrderAPI {
	final static Logger logger = LogManager.getLogger(OrderAPI.class);
	
	API api = new API();
	LoginDashboardInfo loginInfo;
	LoginInformation loginInformation;
	public OrderAPI (LoginInformation loginInformation) {
		this.loginInformation = loginInformation;
		loginInfo = new Login().getInfo(loginInformation);
	}


	public static String CONFIRM_ORDER_PATH = "orderservices2/api/shop/bc-orders/confirm";
	public static String DELIVER_ORDER_PATH = "orderservices2/api/shop/bc-orders/%s/status/delivered";
	public static String CANCEL_ORDER_PATH = "orderservices2/api/shop/bc-orders/reject";

	public void confirmOrder(String orderID) {
		String body = """
				        {
				"orderId": "%s",
				"note": "",
				"length": 1,
				"width": 1,
				"height": 1,
				"itemIMEISerials": []
				        }""".formatted(orderID);
		api.post(CONFIRM_ORDER_PATH,  loginInfo.getAccessToken(), body).then().statusCode(200);
		logger.info("Confirmed order: " + orderID);
	}

	public void deliverOrder(String orderID) {
		api.post(DELIVER_ORDER_PATH.formatted(orderID),  loginInfo.getAccessToken(), "{}").then().statusCode(200);
		logger.info("Delivered order: " + orderID);
	}
	public void cancelOrder(int orderId){
		String body = """
				{
				  "orderId": "%s",
				  "reason": "thích",
				  "isCancelAllPackage": false,
				  "langKey": "en"
				}
				""".formatted(orderId);
		Response response = api.post(CANCEL_ORDER_PATH.formatted(),loginInfo.getAccessToken(), body);
		response.then().statusCode(200);
	}
}
