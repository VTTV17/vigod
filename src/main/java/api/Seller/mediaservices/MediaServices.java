package api.Seller.mediaservices;

import java.util.ArrayList;
import java.util.List;

import api.Seller.login.Login;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class MediaServices {
    String GET_RECORD_LIST = "/mediaservices/api/export-data-history/list?page=0&size=300";
    
    API api = new API();
    LoginInformation loginInformation;
    LoginDashboardInfo loginInfo;

    public MediaServices(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    
    public JsonPath getExportHistoryJsonPath() {
    	Response response = api.get(GET_RECORD_LIST, loginInfo.getAccessToken());
    	response.then().statusCode(200);
    	return response.jsonPath();
    }    
    
    @Data
    public class ExportHistoryInfo {
        List<Integer> id = new ArrayList<>();
        List<String> fileName = new ArrayList<>();
        List<String> dataType = new ArrayList<>();
        List<Boolean> isExpired = new ArrayList<>();
        List<Integer> userId = new ArrayList<>();
        List<Integer> exportByStaffId = new ArrayList<>();
    }  

    public ExportHistoryInfo getExportHistoryInfo() {
    	JsonPath jsonResponse = getExportHistoryJsonPath();
    	ExportHistoryInfo info = new ExportHistoryInfo();
    	info.setId(jsonResponse.getList("content.id"));
    	info.setFileName(jsonResponse.getList("content.fileName"));
    	info.setDataType(jsonResponse.getList("content.dataType"));
    	info.setIsExpired(jsonResponse.getList("content.isExpired"));
    	info.setUserId(jsonResponse.getList("content.userId"));
    	info.setExportByStaffId(jsonResponse.getList("content.exportByStaffId"));
        return info;
    }       
}
