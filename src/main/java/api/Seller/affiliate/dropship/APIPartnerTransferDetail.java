package api.Seller.affiliate.dropship;

import api.Seller.affiliate.dropship.PartnerTransferManagement.PartnerTransferStatus;
import api.Seller.login.Login;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

public class APIPartnerTransferDetail {
    Logger logger = LogManager.getLogger(T.class);

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    public APIPartnerTransferDetail(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
    	loginInfo = new Login().getInfo(loginInformation);
    }

    String getPartnerTransferDetailPath = "/itemservice/api/transfers/detail/%s/%s";

    Response getTransferDetailResponse(int transferId) {
        return api.get(getPartnerTransferDetailPath.formatted(loginInfo.getStoreID(), transferId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    public List<Integer> getItemIds(int transferId) {
        return getTransferDetailResponse(transferId).jsonPath().getList("items.itemId");
    }

    public PartnerTransferStatus getTransferStatus(int transferId) {
        return PartnerTransferStatus.valueOf(getTransferDetailResponse(transferId).jsonPath().getString("status"));
    }
}
