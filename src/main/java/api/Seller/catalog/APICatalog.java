package api.Seller.catalog;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.catalog.CityTree;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APICatalog {
	final static Logger logger = LogManager.getLogger(APICatalog.class);

	String cityTreePath = "/catalogservices/api/country/<country>/cities/tree";

	API api = new API();
	LoginDashboardInfo loginInfo;

	LoginInformation loginInformation;
	public APICatalog(LoginInformation loginInformation) {
		this.loginInformation = loginInformation;
		loginInfo = new Login().getInfo(loginInformation);
	}
	
    public List<CityTree> getCityTree(String countryCode) {
    	String basePath = cityTreePath.replaceAll("<country>", countryCode);
    	String token = loginInfo.getAccessToken();
    	
    	Response response = api.get(basePath, token).then().statusCode(200).extract().response();
    	return response.jsonPath().getList(".", CityTree.class);
    }  
}
