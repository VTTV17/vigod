package api.dashboard.setting;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;

import java.util.List;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;

public class StaffAPI {
    String GET_STAFF_LIST = "/storeservice/api/store-staffs/store/%s?isEnabledCC=false&page=0&size=100&sort=id,desc";
    API api = new API();
    
    public JsonPath getAllStaffJsonPath() {
    	Response response = api.get(GET_STAFF_LIST.formatted(apiStoreID), accessToken);
    	response.then().statusCode(200);
    	return response.jsonPath();
    }
    
    public List<String> getAllStaffNames() {
    	return getAllStaffJsonPath().getList("name");
    }

}
