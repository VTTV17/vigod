package api.Seller.sale_channel.tiktok;

import api.Seller.login.Login;
import api.Seller.products.all_products.APICreateProduct;
import api.Seller.products.all_products.APIGetProductDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This class provides functionality to link TikTok products with the GoSELL platform.
 */
public class APILinkTiktokProductToGoSELL {
    private final LoginDashboardInfo loginInfo;
    private final LoginInformation credentials;
    private final static Logger logger = LogManager.getLogger();

    /**
     * Constructor to initialize API for linking products.
     *
     * @param credentials Login information for seller's account.
     */
    public APILinkTiktokProductToGoSELL(LoginInformation credentials) {
        this.credentials = credentials;
        this.loginInfo = new Login().getInfo(credentials);
    }


    @Data
    @AllArgsConstructor
    public static class ProductLinkRequest {
        private String bcItemId;
        private int itemId;
        private String tiktokShopId;
        private String branchId;
        private int bcStoreId;
        private List<String> bcTierVariations;
        private final List<String> tiktokTierVariations = List.of();
        private List<TiktokItemVariation> tiktokItemVariations;
        private final Boolean isManual = true;
        private List<BcItemVariation> bcItemVariations;

        @Data
        @AllArgsConstructor
        public static class TiktokItemVariation {
            private int id;
            private String value;
        }

        @Data
        @AllArgsConstructor
        public static class BcItemVariation {
            private int id;
            private String value;
        }
    }

    /**
     * Get GoSELL tier variations from the product information.
     *
     * @param productInfo Product information fetched from GoSELL.
     * @return List of tier variations for GoSELL.
     */
    private List<String> getBcTierVariations(APIGetProductDetail.ProductInformation productInfo) {
        if (!productInfo.isHasModel()) return List.of();
        return Arrays.stream(productInfo.getModels().getFirst().getLabel().split("\\|")).toList();
    }

    /**
     * Get TikTok item variations from the TikTok product information.
     *
     * @param tikTokProduct TikTok product information fetched via API.
     * @return List of TikTok item variations.
     */
    private List<ProductLinkRequest.TiktokItemVariation> getTiktokItemVariations(APIGetTikTokProducts.TikTokProduct tikTokProduct) {
        if (tikTokProduct.getVariations().isEmpty()) {
            return List.of();
        }

        return tikTokProduct.getVariations().stream().map(variation -> new ProductLinkRequest.TiktokItemVariation(variation.getId(), variation.getName())).toList();
    }

    /**
     * Get GoSELL item variations from the product information.
     *
     * @param productInfo Product information fetched from GoSELL.
     * @return List of GoSELL item variations.
     */
    private List<ProductLinkRequest.BcItemVariation> getBcItemVariations(APIGetProductDetail.ProductInformation productInfo) {

        if (!productInfo.isHasModel()) {
            return List.of();
        }
        return productInfo.getModels().stream().map(model -> new ProductLinkRequest.BcItemVariation(model.getId(), model.getName())).toList();
    }

    /**
     * Generate the request payload to link a TikTok product with GoSELL.
     *
     * @param tikTokProduct TikTok product information.
     * @return Request payload to link the TikTok product with GoSELL.
     */
    private ProductLinkRequest getPayload(APIGetTikTokProducts.TikTokProduct tikTokProduct) {
        // Create and link the product in GoSELL first, then retrieve its product ID
        int productId = new APICreateProduct(credentials).createAndLinkProductTo3rdPartyThenRetrieveId(tikTokProduct.getVariations().size());
        APIGetProductDetail.ProductInformation productInfo = new APIGetProductDetail(credentials).getProductInformation(productId);

        // Build and return the payload for the API request
        return new ProductLinkRequest(String.valueOf(productId),
                tikTokProduct.getId(),
                tikTokProduct.getThirdPartyShopId(),
                tikTokProduct.getBranchId(),
                tikTokProduct.getBcStoreId(),
                getBcTierVariations(productInfo),
                getTiktokItemVariations(tikTokProduct),
                getBcItemVariations(productInfo));
    }

    /**
     * Function to link a TikTok product to GoSELL.
     * This sends a PUT request to link the product based on the TikTok product information.
     *
     * @param tikTokProduct TikTok product information.
     */
    private void linkTiktokProductToGoSELL(APIGetTikTokProducts.TikTokProduct tikTokProduct) {
        new API().put("/tiktokservices/api/items/link", loginInfo.getAccessToken(), getPayload(tikTokProduct))
                .then().statusCode(200);
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
     * @return A String array containing the start and end times of the action in UTC format, or
     *         null if there are no unlinked products.
     */
    public String[] linkTiktokProductsToGoSELL(List<APIGetTikTokProducts.TikTokProduct> tikTokProducts) {
        if (tikTokProducts.isEmpty()) return null;
        List<APIGetTikTokProducts.TikTokProduct> unlinkedTiktokProducts = APIGetTikTokProducts.getUnLinkedTiktokProduct(tikTokProducts);
        if (unlinkedTiktokProducts.isEmpty()) return null;

        // Array to store the start and end times of the link action
        String[] actionsTime = new String[2];

        // Record the start time of the action in UTC format
        actionsTime[0] = LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

        // Link each unlinked TikTok product to GoSELL
        tikTokProducts.forEach(this::linkTiktokProductToGoSELL);

        // Wait for the update to complete (60 seconds)
        UICommonAction.sleepInMiliSecond(60_000);

        // Record the end time of the action in UTC format
        actionsTime[1] = LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

        // Return the start and end times of the link action
        return actionsTime;
    }

    /**
     * Verifies that TikTok products have been successfully linked to GoSELL.
     * <p>
     * This method first identifies the unlinked products in the original list, verifies that
     * they have been linked in the updated list, and checks that inventory events are created
     * correctly based on synchronization status. Finally, it confirms that inventory mappings
     * have been accurately updated.
     * </p>
     *
     * @param originalTiktokProducts    The list of original TikTok products before the linking actions.
     * @param updatedTiktokProducts     The list of updated TikTok products after the linking actions.
     * @param originalInventoryMappings The original inventory mappings before the linking actions.
     * @param actionTime                Array with start and end times of the linking action.
     * @param connection                Database connection for verifying inventory events and mappings.
     * @param isAutoSynced              Specifies whether inventory syncing is enabled.
     */
    public static void verifyLinkProductsToGoSELL(List<APIGetTikTokProducts.TikTokProduct> originalTiktokProducts,
                                           List<APIGetTikTokProducts.TikTokProduct> updatedTiktokProducts,
                                           List<SQLGetInventoryMapping.InventoryMapping> originalInventoryMappings,
                                           String[] actionTime,
                                           Connection connection,
                                           boolean isAutoSynced) {
        // Retrieve list of TikTok products that were unlinked before actions
        List<APIGetTikTokProducts.TikTokProduct> unlinkedTiktokProducts = APIGetTikTokProducts.getUnLinkedTiktokProduct(originalTiktokProducts);

        // Verify each unlinked product has been successfully linked to GoSELL
        unlinkedTiktokProducts.forEach(unlinkedProduct -> verifyProductLinkedToGoSELL(unlinkedProduct, updatedTiktokProducts));

        // Retrieve list of product IDs for newly linked TikTok products
        List<String> unlinkedTiktokProductIds = unlinkedTiktokProducts.stream()
                .map(APIGetTikTokProducts.TikTokProduct::getThirdPartyItemId)
                .toList();

        // Filter updated TikTok products to include only those that were newly linked in GoSELL
        List<APIGetTikTokProducts.TikTokProduct> newLinkedTiktokProducts = updatedTiktokProducts.stream()
                .filter(tiktokProduct -> unlinkedTiktokProductIds.contains(tiktokProduct.getThirdPartyItemId()))
                .toList();

        // Get item mappings for newly linked TikTok products
        List<APIGetTikTokProducts.ItemMapping> changedItemMappings = APIGetTikTokProducts.getItemMapping(newLinkedTiktokProducts);

        // Retrieve the store ID from the first changed item mapping
        int storeId = originalTiktokProducts.getFirst().getBcStoreId();

        // Verify the inventory event based on sync status and action times
        VerifyAutoSyncHelper.verifyInventoryEvent(isAutoSynced, changedItemMappings, actionTime, storeId, connection, "GS_TIKTOK_SYNC_ITEM_EVENT");

        // Verify that the original inventory mappings remain consistent with the newly created mappings
        VerifyAutoSyncHelper.verifyInventoryMapping(originalInventoryMappings, null, changedItemMappings, storeId, connection);
    }

    /**
     * Verifies if the given unlinked TikTok product has been successfully created in GoSELL.
     *
     * @param unlinkedProduct   The original unlinked TikTok product.
     * @param newTiktokProducts The list of TikTok products fetched after the linking action.
     */
    private static void verifyProductLinkedToGoSELL(APIGetTikTokProducts.TikTokProduct unlinkedProduct, List<APIGetTikTokProducts.TikTokProduct> newTiktokProducts) {
        // Attempt to find a matching product in the newly fetched products
        newTiktokProducts.stream()
                .filter(newProduct -> Objects.equals(newProduct.getThirdPartyItemId(), unlinkedProduct.getThirdPartyItemId()))
                .findFirst()
                .ifPresentOrElse(APILinkTiktokProductToGoSELL::assertProductLinked,
                        () -> logger.error("No matching product found in GoSELL for TikTok product ID: {}", unlinkedProduct.getThirdPartyItemId()));
    }

    /**
     * Asserts that the product's GoSELL status is not "UNLINK", indicating it has been successfully linked.
     *
     * @param tikTokProduct The TikTok product to verify.
     */
    private static void assertProductLinked(APIGetTikTokProducts.TikTokProduct tikTokProduct) {
        Assert.assertNotEquals(tikTokProduct.getGosellStatus(), "UNLINK",
                "Product is not linked to GoSELL, TikTok product ID: %s".formatted(tikTokProduct.getThirdPartyItemId()));
        logger.info("Verified product link to GoSELL for TikTok product ID: {}", tikTokProduct.getThirdPartyItemId());
    }
}