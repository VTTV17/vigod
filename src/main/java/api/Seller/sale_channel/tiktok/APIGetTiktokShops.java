package api.Seller.sale_channel.tiktok;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

/**
 * This class is responsible for retrieving the list of TikTok shops associated with a seller's account.
 * It allows the seller to retrieve and filter connected TikTok shops based on their connection status.
 */
public class APIGetTiktokShops {
    private final LoginDashboardInfo loginInfo;

    /**
     * Constructor that initializes the APIGetTiktokShops instance with the seller's login information.
     *
     * @param credentials The seller's login credentials.
     */
    public APIGetTiktokShops(LoginInformation credentials) {
        this.loginInfo = new Login().getInfo(credentials);
    }

    /**
     * Represents a TikTok shop account that is associated with the seller's store.
     * It includes shop details such as shop ID, name, country, connection status, and access tokens.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TiktokShopAccount {
        private int id;
        private String tiktokShopId;
        private String shopName;
        private String country;
        private String shopStatus;
        private int bcStoreId;
        private int branchId;
        private String connectStatus;
        private String authCode;
        private String accessToken;
        private String refreshToken;
        private long accessTokenExpireTime;
        private long refreshTokenExpireTime;
        private String branchName;
        private boolean global;
    }

    /**
     * Retrieves the list of all TikTok shop accounts associated with the seller's store.
     *
     * @return A list of TikTokShopAccount objects, each representing a TikTok shop account.
     */
    public List<TiktokShopAccount> getListTiktokAccounts() {
        return new API().get("/tiktokservices/api/bc-stores/%d/all/shops".formatted(loginInfo.getStoreID()), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList(".", TiktokShopAccount.class);
    }

    /**
     * Filters and retrieves the list of TikTok shop accounts that are currently connected.
     *
     * @param tiktokShopAccounts The list of all TikTok shop accounts.
     * @return A filtered list containing only the connected TikTok shops.
     */
    public static List<TiktokShopAccount> getListConnectedShop(List<TiktokShopAccount> tiktokShopAccounts) {
        if (tiktokShopAccounts.isEmpty()) return List.of();

        return tiktokShopAccounts.stream()
                .filter(tiktokShopAccount -> tiktokShopAccount.getConnectStatus().equals("CONNECTED"))
                .toList();
    }
}