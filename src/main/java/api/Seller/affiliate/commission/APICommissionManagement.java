package api.Seller.affiliate.commission;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APICommissionManagement {
    String GET_COMMISSION_LIST_PATH = "affiliateservice/api/commissions/store/%s?page=0&size=50";
    String DELETE_COMMISSION_PATH = "affiliateservice/api/commissions/%s";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APICommissionManagement(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public List<Integer> getCommissionIdList(){
        Response response = api.get(GET_COMMISSION_LIST_PATH.formatted(loginInfo.getStoreID()),loginInfo.getAccessToken());
        response.then().statusCode(200);
        return response.jsonPath().getList("id");
    }
    public void deleteCommission(int id){
        api.delete(DELETE_COMMISSION_PATH.formatted(id),loginInfo.getAccessToken());
    }
}
