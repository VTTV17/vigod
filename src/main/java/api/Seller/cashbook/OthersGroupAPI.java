package api.Seller.cashbook;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class OthersGroupAPI {
    String GET_OTHER_GROUP_LIST = "/cashbookservice/api/other-groups?storeId.equals=%s&page=0&size=100";
    API api = new API();
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;

    public OthersGroupAPI(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public JsonPath getAllOtherGroupJsonPath() {
    	Response response = api.get(GET_OTHER_GROUP_LIST.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
    	response.then().statusCode(200);
    	return response.jsonPath();
    }
    
    public List<String> getAllOtherGroupNames() {
    	return getAllOtherGroupJsonPath().getList("name");
    }

}
