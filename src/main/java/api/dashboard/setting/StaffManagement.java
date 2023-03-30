package api.dashboard.setting;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;

import java.util.List;

import static api.dashboard.login.Login.accessToken;
import static api.dashboard.login.Login.apiStoreID;

public class StaffManagement {
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
    public List<Integer> getAllStaffUserId() { return  getAllStaffJsonPath().getList("userId");}
    public List<String> getAllStaffPermissionCode() {
        return getAllStaffJsonPath().getList("permissionCode");}

}
