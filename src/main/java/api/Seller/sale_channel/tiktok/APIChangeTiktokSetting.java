package api.Seller.sale_channel.tiktok;

import api.Seller.login.Login;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.Objects;

public class APIChangeTiktokSetting {
    private final LoginDashboardInfo loginInfo;

    public APIChangeTiktokSetting(LoginInformation credentials) {
        this.loginInfo = new Login().getInfo(credentials);
    }

    public void changeTiktokSetting(APIGetTiktokSetting.SyncTiktokSettings syncTiktokSettings, boolean isAutoSynced) {
        if (Objects.equals(syncTiktokSettings.isAutoSyncStock(), isAutoSynced)) return;
        syncTiktokSettings.setAutoSyncStock(isAutoSynced);
        new API().put("/tiktokservices/api/tiktok-settings", loginInfo.getAccessToken(), syncTiktokSettings)
                .then().statusCode(200);
    }
}
