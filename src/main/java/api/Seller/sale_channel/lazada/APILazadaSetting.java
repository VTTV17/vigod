package api.Seller.sale_channel.lazada;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.LocalDateTime;

public class APILazadaSetting {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    String GET_SETTING_PATH = "/lazadaservices/api/lazada-settings/store/%s";
    String SETTING_PATH = "/lazadaservices/api/lazada-settings";
    public APILazadaSetting(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public Response getSettingConfig(){
        Response response = api.get(GET_SETTING_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response;
    }
    public void turnOnAutoSync(){
        Response getSettingRs = getSettingConfig();
        if(!getSettingRs.jsonPath().getBoolean("autoSyncStock")) {
            String payLoad = """
                    {
                      "bcStoreId": "%s",
                      "autoSyncStock": true,
                      "sendFailedSyncStockWeb": false,
                      "sendFailedSyncStockMobile": true
                    }
                    """.formatted(loginInfo.getStoreID());
            Response response = api.post(SETTING_PATH, loginInfo.getAccessToken(), payLoad);
            response.then().statusCode(201);
        }
    }
}
