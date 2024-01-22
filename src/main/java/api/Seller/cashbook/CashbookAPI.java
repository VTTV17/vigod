package api.Seller.cashbook;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class CashbookAPI {
    String GET_RECORD_LIST = "/cashbookservice/api/cash-books/store/%s?page=0&size=1000000&sort=createdDate,desc";
    API api = new API();
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;

    public CashbookAPI(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    
    public JsonPath getAllRecordJsonPath() {
    	Response response = api.get(GET_RECORD_LIST.formatted(loginInfo.getStoreID()), loginInfo.getAccessToken());
    	response.then().statusCode(200);
    	return response.jsonPath();
    }
    
    public List<String> getAllTransactionCodes() {
    	return getAllRecordJsonPath().getList("transactionCode");
    }

}
