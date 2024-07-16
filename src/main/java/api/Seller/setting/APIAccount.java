package api.Seller.setting;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.plan.PlanStatus;
import utilities.model.sellerApp.login.LoginInformation;

public class APIAccount {
	final static Logger logger = LogManager.getLogger(APIAccount.class);

	String changePasswordPath = "/api/account/change_password";
	String packagePlanListPath = "/beehiveservices/api/bundle-user-features/see-plans/user-id/%s";

	API api = new API();
	LoginDashboardInfo loginInfo;

	LoginInformation loginInformation;
	public APIAccount(LoginInformation loginInformation) {
		this.loginInformation = loginInformation;
		loginInfo = new Login().getInfo(loginInformation);
	}
	
	public List<PlanStatus> getAvailablePlanInfo() {
		
		Response response = api.get(packagePlanListPath.formatted(loginInfo.getUserId()), loginInfo.getAccessToken()).then().statusCode(200).extract().response();
		
		List<PlanStatus> info = response.jsonPath().getList(".", PlanStatus.class);
		
		logger.info("Retrieved available plan info: {}", info);
		return info;
	} 	
	
	public void changePassword(String currentPassword, String newPassword) {
		String body = """
				{
				"newPassword": "%s",
				"currentPassword": "%s"
				}""".formatted(newPassword, currentPassword);
		
		Response response = api.put(changePasswordPath, loginInfo.getAccessToken(), body);
		response.then().statusCode(200);
		logger.info("Password has been changed to: " + newPassword);
	} 	
}
