package api.Seller.affiliate.partnerinventory;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APIPartnerTransferDetail {
    String SHIP_GOOD_TRANSFER_PATH = "/itemservice/api/affiliate-transfers/ship/%s/%s";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APIPartnerTransferDetail(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public void shipGoodTransfer(int id){
        Response response = api.put(SHIP_GOOD_TRANSFER_PATH.formatted(loginInfo.getStoreID(),id),loginInfo.getAccessToken());
        response.then().statusCode(200);
    }
}
