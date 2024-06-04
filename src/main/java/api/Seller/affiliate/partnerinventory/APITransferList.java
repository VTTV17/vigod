package api.Seller.affiliate.partnerinventory;

import api.Seller.login.Login;
import io.restassured.response.Response;
import utilities.api.API;
import utilities.enums.TransferStatus;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APITransferList {
    String GET_TRANSFER_LIST_PATH = "/itemservice/api/transfers/store/%s?transferType=PARTNER&page=0&size=100&sort=id,Cdesc%s";
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APITransferList(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }
    public Response getTransferList(String...filter){
        Response response;
        if(filter.length>0){
            response = api.get(GET_TRANSFER_LIST_PATH.formatted(loginInfo.getStoreID(),""),loginInfo.getAccessToken());
        }else response = api.get(GET_TRANSFER_LIST_PATH.formatted(loginInfo.getStoreID(),filter[0]),loginInfo.getAccessToken());
        return response;
    }
    public List<Integer> getTransferByOriginBranch(int originBranch, TransferStatus...transferStatus){
        String filterBranch = (originBranch>=0)? "&originBranchId=%s".formatted(originBranch): "";
        String filterStatus = (transferStatus.length>0)? "&status=%s".formatted(transferStatus[0]):"";
        Response response = getTransferList(filterBranch+filterStatus);
        return response.jsonPath().getList("id");
    }
}
