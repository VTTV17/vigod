package api.Seller.sale_channel.tiktok;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.restassured.response.Response;
import lombok.Data;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * This class interacts with the TikTok API to fetch product information for a seller.
 * It uses login credentials to authenticate and retrieve the product list from the TikTok store.
 */
public class APIGetTikTokProducts {

    private final LoginDashboardInfo loginInfo;

    /**
     * Constructor that initializes the APIGetTikTokProducts instance with the seller's login information.
     *
     * @param credentials The seller's login credentials.
     */
    public APIGetTikTokProducts(LoginInformation credentials) {
        this.loginInfo = new Login().getInfo(credentials);
    }

    /**
     * Represents a TikTok product with various attributes like price, stock, and variations.
     * The class also contains an inner class representing variations of the product.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TikTokProduct {
        private int id;
        private String thirdPartyItemId;
        private String branchId;
        private String thirdPartyShopId;
        private String thirdPartyShopName;
        private String thumbnail;
        private String thirdPartyItemName;
        private String gosellStatus;
        private String bcItemName;
        private String bcItemId;
        private String bcItemThumbnail;
        private String currency;
        private Long price;
        private Integer stock;
        private String lastSyncDate;
        private Long updateTime;
        private List<Variation> variations;
        private Boolean hasVariation;
        private Boolean hasLinkErrorStatus;
        private int bcStoreId;

        /**
         * Represents a variation of a TikTok product, which can have attributes like price, stock, and SKU.
         */
        @Data
        public static class Variation {
            private int id;
            private String thirdPartyVariationId;
            private String variationSku;
            private String name;
            private String label;
            private Long price;
            private Integer stock;
            private String itemId;
            private String bcModelId;
            private Integer syncLastStock;
        }
    }

    /**
     * Fetches a response containing the list of TikTok products for the given page index.
     *
     * @param pageIndex The index of the page to retrieve.
     * @return Response containing the TikTok products for the specified page.
     */
    private Response getTikTokProductsResponse(int pageIndex) {
        String path = "/tiktokservices/api/items/bc-store/%d?page=%d&size=50&getBcItemName=true&sort=update_time,DESC"
                .formatted(loginInfo.getStoreID(), pageIndex);
        return new API().get(path, loginInfo.getAccessToken())
                .then().statusCode(200)
                .extract().response();
    }

    /**
     * Fetches all TikTok products from the store by iterating through the pages of results.
     *
     * @return A list of all TikTok products from the store.
     */
    public List<TikTokProduct> getTikTokProducts() {
        // Fetch the number of pages by retrieving the total product count from the headers.
        int numOfPages = Integer.parseInt(getTikTokProductsResponse(0).getHeader("X-Total-Count")) / 50;

        // Retrieve product data from all pages in parallel and merge into a single list.
        var jsonPaths = IntStream.rangeClosed(0, numOfPages)
                .parallel()
                .mapToObj(pageIndex -> getTikTokProductsResponse(pageIndex).jsonPath()).toList();

        return jsonPaths.stream().flatMap(jsonPath -> jsonPath.getList(".", TikTokProduct.class).stream())
                .toList();
    }

    /**
     * Filters the provided list of TikTok products to include only those that are linked (i.e., products
     * that are not marked as "UNLINK" and do not have a link error status).
     *
     * @param tikTokProducts A list of TikTok products to filter. This list may be empty.
     * @return A list of linked TikTok products. If no products are linked or if the input list is empty,
     *         an empty list is returned.
     */
    public static List<TikTokProduct> getLinkedTiktokProduct(List<TikTokProduct> tikTokProducts) {
        if (tikTokProducts.isEmpty()) return List.of();
        return tikTokProducts.parallelStream()
                .filter(tikTokProduct -> !tikTokProduct.getGosellStatus().equals("UNLINK") && !tikTokProduct.getHasLinkErrorStatus())
                .toList();
    }

    /**
     * Filters the provided list of TikTok products to include only those with a link error status.
     * <p>
     * This method processes each TikTok product in the input list and selects only those
     * products that are marked with a link error status. It returns a list of products
     * that require attention due to linking issues.
     * </p>
     *
     * @param tikTokProducts A list of TikTok products to filter. This list may be empty.
     * @return A list of TikTok products with a link error status. If no products have a link error
     *         or if the input list is empty, an empty list is returned.
     */
    public static List<TikTokProduct> getErrorTiktokProduct(List<TikTokProduct> tikTokProducts) {
        if (tikTokProducts.isEmpty()) return List.of();
        return tikTokProducts.parallelStream()
                .filter(TikTokProduct::getHasLinkErrorStatus)
                .toList();
    }

    /**
     * Filters the TikTok products to return only those that are unlinked.
     *
     * @param tikTokProducts A list of TikTok products to filter.
     * @return A list of unlinked TikTok products.
     */
    public static List<TikTokProduct> getUnLinkedTiktokProduct(List<TikTokProduct> tikTokProducts) {
        if (tikTokProducts.isEmpty()) return List.of();
        return tikTokProducts.parallelStream()
                .filter(tikTokProduct -> tikTokProduct.getGosellStatus().equals("UNLINK"))
                .toList();
    }

    @Data
    public static class ItemMapping {
        private int bc_store_id;
        private String branch_id;
        private String bc_item_id;
        private String bc_model_id;
        private String tt_item_id;
        private String tt_model_id;
        private String tt_model_name;
        private boolean hasLinkErrorStatus;
    }

    /**
     * Generates a list of {@link ItemMapping} objects from the provided TikTok products.
     * Each product can have multiple variations, and this method creates a mapping for
     * each variation by associating relevant fields from both the BigCommerce and TikTok platforms.
     * The list of item mappings is then sorted by {@code bc_item_id} and {@code bc_model_id}.
     *
     * @param linkedTiktokProducts A list of {@link TikTokProduct} objects, where each product
     *                             contains a list of variations. This method will map each variation
     *                             to an {@link ItemMapping}.
     * @return A sorted list of {@link ItemMapping} objects. If the input list is empty,
     * an empty list is returned.
     */
    public static List<ItemMapping> getItemMapping(List<TikTokProduct> linkedTiktokProducts) {
        // Return an empty list if there are no linked TikTok products
        if (linkedTiktokProducts.isEmpty()) return List.of();

        List<ItemMapping> itemMappings = new ArrayList<>();

        // For each product, generate item mappings for its variations
        linkedTiktokProducts.stream()
                .filter(tikTokProduct -> !tikTokProduct.getHasLinkErrorStatus())
                .forEach(tikTokProduct ->
                tikTokProduct.getVariations().forEach(variation -> {
                    ItemMapping itemMapping = new ItemMapping();
                    itemMapping.setBc_store_id(tikTokProduct.getBcStoreId());
                    itemMapping.setBranch_id(tikTokProduct.getBranchId());
                    itemMapping.setBc_item_id(tikTokProduct.getBcItemId());
                    itemMapping.setBc_model_id(variation.getBcModelId());
                    itemMapping.setTt_item_id(tikTokProduct.getThirdPartyItemId());
                    itemMapping.setTt_model_id(variation.getThirdPartyVariationId());
                    itemMapping.setTt_model_name(variation.getName());
                    itemMapping.setHasLinkErrorStatus(tikTokProduct.getHasLinkErrorStatus());

                    // Add the item mapping to the list
                    itemMappings.add(itemMapping);
                })
        );

        // Sort the item mappings by bc_item_id first, then by bc_model_id
        itemMappings.sort(
                Comparator.comparing(ItemMapping::getBc_item_id)
                        .thenComparing(ItemMapping::getBc_model_id)
        );

        // Return the sorted list of item mappings
        return itemMappings;
    }
}