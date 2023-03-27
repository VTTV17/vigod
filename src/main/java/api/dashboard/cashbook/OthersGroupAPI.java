package api.dashboard.cashbook;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;

import java.util.List;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;

public class OthersGroupAPI {
    String GET_OTHER_GROUP_LIST = "/cashbookservice/api/other-groups?storeId.equals=%s&page=0&size=100";
    API api = new API();
    
    public JsonPath getAllOtherGroupJsonPath() {
    	Response response = api.get(GET_OTHER_GROUP_LIST.formatted(apiStoreID), accessToken);
    	response.then().statusCode(200);
    	return response.jsonPath();
    }
    
    public List<String> getAllOtherGroupNames() {
    	return getAllOtherGroupJsonPath().getList("name");
    }

}
