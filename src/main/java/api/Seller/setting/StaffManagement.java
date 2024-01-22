package api.Seller.setting;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class StaffManagement {
    String GET_STAFF_LIST = "/storeservice/api/store-staffs/store/%s?isEnabledCC=false&page=0&size=100&sort=id,desc";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public StaffManagement(LoginInformation loginInformation){
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public JsonPath getAllStaffJsonPath() {
    	Response response = api.get(GET_STAFF_LIST.formatted(loginInfo.getStoreID()),  loginInfo.getAccessToken());
    	response.then().statusCode(200);
    	return response.jsonPath();
    }
    
    public List<String> getAllStaffNames() {
    	return getAllStaffJsonPath().getList("name");
    }
    public List<Integer> getAllStaffUserId() { return  getAllStaffJsonPath().getList("userId");}
    public List<String> getAllStaffPermissionCode() {
        return getAllStaffJsonPath().getList("permissionCode");}

    public int getStaffId(int userId) {
    	return  getAllStaffJsonPath().get("find { it.userId == %s }.id".formatted(userId));
    }
    
}
