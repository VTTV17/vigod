package api.Buyer.header;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APIHeader {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APIHeader(
    LoginInformation loginInformation){
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public static String GET_CURRENT_MENUS_PATH = "/ssrstorefront/api/store-pages/store/%s/get-current-header-menu?langKey=%s";
    public int getCurrentMenuId(){
        Response response = api.get(GET_CURRENT_MENUS_PATH.formatted(loginInfo.getStoreID(),""),loginInfo.getAccessToken());
        response.then().statusCode(200);
        int menuId = response.jsonPath().getInt("menuId[0]");
        return menuId;
    }
}
