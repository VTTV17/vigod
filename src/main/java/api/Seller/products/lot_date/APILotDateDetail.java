package api.Seller.products.lot_date;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

public class APILotDateDetail {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APILotDateDetail(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    String getLotDateDetailPath(int storeId, int lotId) {
        return "/itemservice/api/lot-dates/store/%d/%d".formatted(storeId, lotId);
    }

    public LotInfo getInfo(int lotId) {
        return api.get(getLotDateDetailPath(loginInfo.getStoreID(), lotId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .as(LotInfo.class);
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LotInfo {
        private int id;
        private int storeId;
        private String lotName;
        private String lotCode;
        private String manufactureDate;
        private String expiryDate;
        private String expiredInValues;
        private boolean notifyWhenExpired;
        private int totalProduct;
        private String lastModifiedDate;
        private String lastModifiedBy;
        private boolean isDeleted;
    }
}
