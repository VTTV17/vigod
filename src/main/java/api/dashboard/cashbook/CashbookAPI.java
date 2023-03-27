package api.dashboard.cashbook;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;

import java.util.List;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;

public class CashbookAPI {
    String GET_RECORD_LIST = "/cashbookservice/api/cash-books/store/%s?page=0&size=1000000&sort=createdDate,desc";
    API api = new API();
    
    public JsonPath getAllRecordJsonPath() {
    	Response response = api.get(GET_RECORD_LIST.formatted(apiStoreID), accessToken);
    	response.then().statusCode(200);
    	return response.jsonPath();
    }
    
    public List<String> getAllTransactionCodes() {
    	return getAllRecordJsonPath().getList("transactionCode");
    }

}
