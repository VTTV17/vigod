package api.Seller.sale_channel.tiktok;

import api.Seller.login.Login;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
public class APIDownloadIndividualTiktokProducts {
    private final LoginDashboardInfo loginInfo;

    /**
     * Constructs an instance of {@link APIDownloadIndividualTiktokProducts}.
     *
     * @param credentials The login credentials of the seller, used to authenticate with the TikTok API.
     */
    public APIDownloadIndividualTiktokProducts(LoginInformation credentials) {
        this.loginInfo = new Login().getInfo(credentials);
    }

    /**
     * Downloads product information from TikTok based on the specified TikTok shop ID and item ID.
     *
     * @param tiktokShopId The ID of the TikTok shop from which to download the product.
     * @param tiktokItemId The ID of the TikTok item to download.
     */
    public void downloadProduct(String tiktokShopId, String tiktokItemId) {
        String path = "/tiktokservices/api/item-download-informations/product/%s/%s/%s"
                .formatted(loginInfo.getStoreID(), tiktokShopId, tiktokItemId);
        new API().post(path, loginInfo.getAccessToken()).then().statusCode(200);
    }
}
