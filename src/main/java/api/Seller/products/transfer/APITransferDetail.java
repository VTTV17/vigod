package api.Seller.products.transfer;

import api.Seller.login.Login;
import api.Seller.products.transfer.TransferManagement.TransferStatus;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APITransferDetail {
    String getTransferDetailPath = "/itemservice/api/transfers/detail/%s/%s";
    Logger logger = LogManager.getLogger(APITransferDetail.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APITransferDetail(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    Response getTransferDetailResponse(int transferId) {
        return api.get(getTransferDetailPath.formatted(loginInfo.getStoreID(), transferId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public List<Integer> getItemIds(int transferId) {
        return getTransferDetailResponse(transferId).jsonPath().getList("items.itemId");
    }

    public TransferStatus getTransferStatus(int transferId) {
        return TransferStatus.valueOf(getTransferDetailResponse(transferId).jsonPath().getString("status"));
    }
}
