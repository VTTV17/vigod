package api.Seller.products.lot_date;

import api.Seller.login.Login;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class APICreateLotDate {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    String createLotDatePath = "/itemservice/api/lot-dates";
    public APICreateLotDate (LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
    	loginInfo = new Login().getInfo(loginInformation);
    }

    @Data
    @AllArgsConstructor
    public static class CreateLotPayload {
        private final String expiryDate = OffsetDateTime.now().plus(Duration.ofDays(365)).toString();
        private final String manufactureDate = OffsetDateTime.now().toString();
        private final String lotCode = String.valueOf(Instant.now().toEpochMilli());
        private final String lotName = "Lot name " + LocalDateTime.now();
        private final boolean isDeleted = false;
        private final boolean notifyWhenExpired = false;
        private final String expiredInValues = "";
        private int storeId;
    }

    @SneakyThrows
    public int createLotDateAndGetLotId() {
        CreateLotPayload payload = new CreateLotPayload(loginInfo.getStoreID());
        return api.post(createLotDatePath, loginInfo.getAccessToken(), payload)
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");
    }
}
