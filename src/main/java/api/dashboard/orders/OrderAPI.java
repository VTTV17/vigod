package api.dashboard.orders;

import static api.dashboard.login.Login.accessToken;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import utilities.api.API;

public class OrderAPI {
	final static Logger logger = LogManager.getLogger(OrderAPI.class);
	
	API api = new API();

	public static String CONFIRM_ORDER_PATH = "orderservices2/api/shop/bc-orders/confirm";
	public static String DELIVER_ORDER_PATH = "orderservices2/api/shop/bc-orders/%s/status/delivered";

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
		api.post(CONFIRM_ORDER_PATH, accessToken, body).then().statusCode(200);
		logger.info("Confirmed order: " + orderID);
	}

	public void deliverOrder(String orderID) {
		api.post(DELIVER_ORDER_PATH.formatted(orderID), accessToken, "{}").then().statusCode(200);
		logger.info("Delivered order: " + orderID);
	}

}
