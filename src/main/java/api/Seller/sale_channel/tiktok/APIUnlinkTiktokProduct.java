package api.Seller.sale_channel.tiktok;

import api.Seller.login.Login;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import sql.SQLGetInventoryMapping;
import utilities.api.API;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.sales_channels.tiktok.VerifyAutoSyncHelper;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;

/**
 * APIUnlinkTiktokProduct handles the functionality to unlink a TikTok product from the seller's account on GoSELL.
 * It uses the seller's credentials to authenticate the operation and calls the unlink API.
 */
public class APIUnlinkTiktokProduct {
    private final LoginDashboardInfo loginInfo;
    private final static Logger logger = LogManager.getLogger();

    /**
     * Constructor to initialize APIUnlinkTiktokProduct with seller's login credentials.
     *
     * @param credentials The login information of the seller, used to retrieve dashboard login info.
     */
    public APIUnlinkTiktokProduct(LoginInformation credentials) {
        this.loginInfo = new Login().getInfo(credentials);
    }

    /**
     * Unlinks the provided TikTok product from the seller's account in GoSELL.
     *
     * @param tikTokProduct The TikTok product that is to be unlinked.
     */
    private void unlinkTiktokProduct(APIGetTikTokProducts.TikTokProduct tikTokProduct) {
        // Format the URL for the unlink API request
        String path = "/tiktokservices/api/items/84411/unlink/%s?ids=%s"
                .formatted(tikTokProduct.getThirdPartyShopId(), tikTokProduct.getThirdPartyItemId());

        // Make the API call to unlink the TikTok product
        new API().get(path, loginInfo.getAccessToken()).then().statusCode(200);
    }

    /**
     * Links a list of TikTok products to GoSELL and records the start and end times of the action.
     * <p>
     * This method filters the provided list to retrieve unlinked TikTok products, then initiates
     * the linking process for each product. It records the start and end times of the linking action
     * in UTC format and waits for a specified delay to allow the linking action to complete.
     * </p>
     *
     * @param tikTokProducts The list of TikTok products to link to GoSELL.
     */
    public void unlinkTiktokProducts(List<APIGetTikTokProducts.TikTokProduct> tikTokProducts) {
        if (tikTokProducts.isEmpty()) return;
        List<APIGetTikTokProducts.TikTokProduct> linkedTiktokProducts = APIGetTikTokProducts.getLinkedTiktokProduct(tikTokProducts);
        if (linkedTiktokProducts.isEmpty()) return;

        // Link each unlinked TikTok product to GoSELL
        tikTokProducts.forEach(this::unlinkTiktokProduct);

        // Wait for the update to complete (60 seconds)
        UICommonAction.sleepInMiliSecond(60_000);
    }

    public static void verifyLinkProductsToGoSELL(List<APIGetTikTokProducts.TikTokProduct> originalTiktokProducts,
                                                  List<APIGetTikTokProducts.TikTokProduct> updatedTiktokProducts,
                                                  List<SQLGetInventoryMapping.InventoryMapping> originalInventoryMappings,
                                                  Connection connection) {

        // Retrieve list of TikTok products that were unlinked before actions
        List<APIGetTikTokProducts.TikTokProduct> linkedTiktokProducts = APIGetTikTokProducts.getLinkedTiktokProduct(originalTiktokProducts);

        // Verify each unlinked product has been successfully linked to GoSELL
        linkedTiktokProducts.forEach(unlinkedProduct -> verifyProductUnlinkedToGoSELL(unlinkedProduct, updatedTiktokProducts));

        // Retrieve the store ID from the first changed item mapping
        int storeId = originalTiktokProducts.get(0).getBcStoreId();

        // Verify that the original inventory mappings remain consistent with the newly created mappings
        VerifyAutoSyncHelper.verifyInventoryMapping(null, originalInventoryMappings, null, storeId, connection);
    }

    /**
     * Verifies if the given unlinked TikTok product has been successfully created in GoSELL.
     *
     * @param unlinkedProduct   The original unlinked TikTok product.
     * @param newTiktokProducts The list of TikTok products fetched after the linking action.
     */
    private static void verifyProductUnlinkedToGoSELL(APIGetTikTokProducts.TikTokProduct unlinkedProduct, List<APIGetTikTokProducts.TikTokProduct> newTiktokProducts) {
        // Attempt to find a matching product in the newly fetched products
        newTiktokProducts.stream()
                .filter(newProduct -> Objects.equals(newProduct.getThirdPartyItemId(), unlinkedProduct.getThirdPartyItemId()))
                .findFirst()
                .ifPresentOrElse(APIUnlinkTiktokProduct::assertProductUnLinked,
                        () -> logger.error("No matching product found in GoSELL for TikTok product ID: {}", unlinkedProduct.getThirdPartyItemId()));
    }

    /**
     * Asserts that the product's GoSELL status is "UNLINK", indicating it has been successfully unlinked.
     *
     * @param tikTokProduct The TikTok product to verify.
     */
    private static void assertProductUnLinked(APIGetTikTokProducts.TikTokProduct tikTokProduct) {
        Assert.assertEquals(tikTokProduct.getGosellStatus(), "UNLINK",
                "Product still linked to GoSELL, TikTok product ID: %s".formatted(tikTokProduct.getThirdPartyItemId()));
        logger.info("Verified product unlink to GoSELL for TikTok product ID: {}", tikTokProduct.getThirdPartyItemId());
    }
}