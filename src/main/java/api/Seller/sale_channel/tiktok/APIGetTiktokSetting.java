package api.Seller.sale_channel.tiktok;

import api.Seller.login.Login;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

/**
 * APIGetTiktokSettings is responsible for retrieving the synchronization settings for TikTok products
 * linked to the seller's account in GoSELL. It utilizes the seller's login information for authentication.
 */
public class APIGetTiktokSetting {
    private final LoginDashboardInfo loginInfo;

    /**
     * Constructor to initialize APIGetTiktokSettings with the seller's login credentials.
     *
     * @param credentials The login information of the seller, used to retrieve dashboard login info.
     */
    public APIGetTiktokSetting(LoginInformation credentials) {
        this.loginInfo = new Login().getInfo(credentials);
    }

    /**
     * SyncTiktokSettings is a data model class that represents the synchronization settings
     * of TikTok products with fields such as createdBy, createdDate, auto-sync settings, etc.
     */
    @Data
    public static class SyncTiktokSettings {
        private String createdBy;
        private String createdDate;
        private String lastModifiedBy;
        private String lastModifiedDate;
        private int id;
        private int bcStoreId;
        private boolean autoSyncStock;
        private boolean sendFailedSyncStockWeb;
        private boolean sendFailedSyncStockMobile;
    }

    /**
     * Retrieves the TikTok synchronization settings for the current seller's store.
     *
     * @return SyncTiktokSettings containing the current TikTok settings for the store.
     */
    public SyncTiktokSettings getTiktokSettings() {
        // Format the URL for the API call to get TikTok settings
        String path = "/tiktokservices/api/tiktok-settings/store/%d".formatted(loginInfo.getStoreID());

        // Make the API call and return the settings as a SyncTiktokSettings object
        return new API().get(path, loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().as(SyncTiktokSettings.class);
    }
}
