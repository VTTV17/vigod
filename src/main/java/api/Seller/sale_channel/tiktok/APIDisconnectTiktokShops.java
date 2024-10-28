package api.Seller.sale_channel.tiktok;

import api.Seller.login.Login;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

/**
 * This class handles the disconnection of TikTok shops from a seller's store.
 * It uses the seller's credentials to authenticate and disconnect TikTok shops.
 */
public class APIDisconnectTiktokShops {

    private final LoginDashboardInfo loginInfo;

    /**
     * Constructor that initializes the APIDisconnectTiktokShops instance with the seller's login information.
     *
     * @param credentials The seller's login credentials.
     */
    public APIDisconnectTiktokShops(LoginInformation credentials) {
        this.loginInfo = new Login().getInfo(credentials);
    }

    /**
     * Represents the authorization information required to disconnect a TikTok shop.
     * It includes fields such as the shop ID, store ID, and branch ID.
     */
    @Data
    public static class TiktokShopAuth {
        private int id;
        private int bcStoreId;
        private String tiktokShopId;
        private int branchId;
        private String authCode = null;
        private boolean reAuth = false;
    }

    /**
     * Disconnects the TikTok shop from the seller's account if the shop is currently connected.
     *
     * @param tiktokShopAccount The TikTok shop account to be disconnected.
     */
    public void disconnectTiktokShop(APIGetTiktokShops.TiktokShopAccount tiktokShopAccount) {
        // Check if the shop is already disconnected
        if (tiktokShopAccount.getConnectStatus().equals("DISCONNECTED")) {
            return;
        }

        // Prepare authorization data for disconnecting the TikTok shop
        TiktokShopAuth tiktokShopAuth = new TiktokShopAuth();
        tiktokShopAuth.setId(tiktokShopAccount.getId());
        tiktokShopAuth.setBcStoreId(loginInfo.getStoreID());
        tiktokShopAuth.setTiktokShopId(tiktokShopAccount.getTiktokShopId());
        tiktokShopAuth.setBranchId(tiktokShopAccount.getBranchId());

        // Make API request to disconnect the TikTok shop
        new API().put("/tiktokservices/api/shops/disconnect", loginInfo.getAccessToken(), tiktokShopAccount)
                .then()
                .statusCode(200);
    }
}