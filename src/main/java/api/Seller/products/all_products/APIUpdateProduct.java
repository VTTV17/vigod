package api.Seller.products.all_products;

import api.Seller.login.Login;
import api.Seller.setting.BranchManagement;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.List;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;

/**
 * APIUpdateProduct handles the process of updating product details through a PUT request to the
 * item service API. It fetches necessary information such as login credentials, branch details,
 * and product-related information to construct a product payload and send it to the API.
 */
public class APIUpdateProduct {

    /**
     * API endpoint for updating a product.
     */
    private static final String UPDATE_PRODUCT_PATH = "/itemservice/api/items?fromSource=DASHBOARD";

    private final LoginInformation credentials;
    private LoginDashboardInfo loginInfo;
    private List<Integer> branchIds;
    private List<String> branchTypes;
    private ProductPayload payload;

    /**
     * Constructs an APIUpdateProduct instance with the given login credentials.
     * Initializes the product payload and fetches necessary information.
     *
     * @param credentials The login credentials used for authentication.
     */
    public APIUpdateProduct(LoginInformation credentials) {
        this.credentials = credentials;
        this.payload = new ProductPayload();
        fetchInformation();
    }

    /**
     * Fetches necessary information from various APIs, such as login info and branch details.
     * Sets up default language, VAT IDs, branch IDs, and branch types.
     */
    private void fetchInformation() {
        loginInfo = new Login().getInfo(credentials);
        var branchInfoList = new BranchManagement(credentials).getInfo();
        branchIds = branchInfoList.getBranchID();
        branchTypes = branchInfoList.getBranchType();
    }

    /**
     * The ProductPayload class represents the structure of the product data that is sent
     * to the API for updating the product.
     */
    @Data
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ProductPayload {
        private Integer id;
        private List<Category> categories;
        private String name;
        private Integer cateId;
        private String itemType;
        private String currency;
        private String description;
        private List<Image> images;
        private Integer totalItem;
        private Long costPrice;
        private Long orgPrice;
        private Long newPrice;
        private ShippingInfo shippingInfo;
        private String parentSku;
        private List<Model> models;
        private List<Object> itemAttributes = List.of();
        private List<Integer> itemAttributeDeleteIds = List.of();
        private String seoTitle;
        private String seoDescription;
        private String seoUrl;
        private String seoKeywords;
        private int priority;
        private Integer taxId;
        private Boolean quantityChanged;
        private Boolean showOutOfStock;
        private String barcode;
        private Boolean isHideStock;
        private Boolean lotAvailable;
        private Boolean expiredQuality;
        private String inventoryManageType;
        private Integer conversionUnitId;
        private Boolean onApp;
        private Boolean onWeb;
        private Boolean inStore;
        private Boolean inGoSocial;
        private Boolean enabledListing;
        private List<Inventory> lstInventory;
        private List<Object> itemModelCodeDTOS = List.of();
        private boolean selfDelivery;
        private List<Language> languages;

        @Data
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Category {
            private Integer id;
            private Integer level;
            private Integer cateId;
        }

        @Data
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Image {
            private String imageUUID;
            private String urlPrefix;
            private String extension;
            private Integer rank;
        }

        @Data
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class ShippingInfo {
            private int weight;
            private int height;
            private int length;
            private int width;
        }

        @Data
        @NoArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Model {
            private Integer id;
            private String name;
            private int totalItem;
            private int discount;
            private String sku;
            private Long orgPrice;
            private Long newPrice;
            private String label;
            private String orgName;
            private String description;
            private String barcode;
            private String versionName;
            private Boolean useProductDescription;
            private Boolean reuseAttributes;
            private String status;
            private List<Branch> lstInventory;
            private List<Language> languages;
            private List<Object> modelAttributes = List.of();
            private long costPrice;

            @Data
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @AllArgsConstructor
            public static class Branch {
                private int branchId;
                private String branchType;
                private int inventoryCurrent;
                private int inventoryStock;
                private String inventoryType;
                private String inventoryActionType;
                private String sku;
            }

            @Data
            @AllArgsConstructor
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public static class Language {
                private String language;
                private String name;
                private String label;
                private String description;
                private String versionName;
            }
        }

        @Data
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Inventory {
            private Integer branchId;
            private Integer inventoryCurrent;
            private Integer inventoryStock;
            private String inventoryType;
            private String sku;
        }

        @Data
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Language {
            private int id;
            private int itemId;
            private String language;
            private String name;
            private String description;
            private String seoTitle;
            private String seoDescription;
            private String seoKeywords;
        }
    }

    /**
     * Initializes the product payload with data fetched from the APIGetProductDetail.
     * This includes mapping product details to the ProductPayload structure.
     *
     * @param productInfo  The product information obtained from the API.
     * @param variationNum The number of product variations.
     * @return The constructed ProductPayload object.
     */
    private ProductPayload initProductPayload(APIGetProductDetail.ProductInformation productInfo, int variationNum) {
        var payload = new ProductPayload();

        // Set basic product details
        setBasicProductDetails(payload, productInfo);

        // Set categories, images, and shipping info
        setProductDetails(payload, productInfo);

        // Set SEO and other settings
        setSEOAndSettings(payload, productInfo);

        // Set inventory and models
        setInventoryAndModels(payload, productInfo, variationNum);

        // Set pricing if no variations exist
        if (variationNum == 0) {
            setPricing(payload, productInfo);
        }

        return payload;
    }

    /**
     * Set basic product details such as id, name, category, etc.
     */
    private void setBasicProductDetails(ProductPayload payload, APIGetProductDetail.ProductInformation productInfo) {
        payload.setId(productInfo.getId());
        payload.setName(productInfo.getName());
        payload.setCateId(productInfo.getCateId());
        payload.setItemType(productInfo.getItemType());
        payload.setCurrency(productInfo.getCurrency());
        payload.setDescription(productInfo.getDescription());
        payload.setTotalItem(0);
        payload.setParentSku(payload.getParentSku());
    }

    /**
     * Set categories, images, and shipping information.
     */
    private void setProductDetails(ProductPayload payload, APIGetProductDetail.ProductInformation productInfo) {
        payload.setCategories(generateCategories(productInfo));
        payload.setImages(generateImages(productInfo));
        payload.setShippingInfo(generateShippingInfo(productInfo));
    }

    /**
     * Set SEO, URL, and other general settings for the product.
     */
    private void setSEOAndSettings(ProductPayload payload, APIGetProductDetail.ProductInformation productInfo) {
        payload.setSeoTitle(productInfo.getSeoTitle());
        payload.setSeoDescription(productInfo.getSeoDescription());
        payload.setSeoKeywords(productInfo.getSeoKeywords());
        payload.setSeoUrl(productInfo.getSeoUrl());
        payload.setPriority(productInfo.getPriority());
        if (productInfo.getTaxId() != 0) payload.setTaxId(productInfo.getTaxId());
        payload.setQuantityChanged(true);
        payload.setShowOutOfStock(productInfo.isShowOutOfStock());
        payload.setBarcode(productInfo.getBarcode());
        payload.setIsHideStock(productInfo.getIsHideStock());
        payload.setLotAvailable(productInfo.isLotAvailable());
        payload.setExpiredQuality(productInfo.isExpiredQuality());
        payload.setInventoryManageType(productInfo.getInventoryManageType());
        payload.setConversionUnitId(null);
        payload.setOnApp(productInfo.isOnApp());
        payload.setOnWeb(productInfo.isOnWeb());
        payload.setInGoSocial(productInfo.isInGosocial());
        payload.setInStore(productInfo.isInStore());
        payload.setEnabledListing(productInfo.isEnabledListing());
    }

    /**
     * Set the inventory and model details, including variations if applicable.
     */
    private void setInventoryAndModels(ProductPayload payload, APIGetProductDetail.ProductInformation productInfo, int variationNum) {
        payload.setModels(generateModels(variationNum));
        payload.setLstInventory(generateLstInventory(productInfo));
        payload.setLanguages(generateLanguages(productInfo));
    }

    /**
     * Set pricing for the product if no variations exist.
     */
    private void setPricing(ProductPayload payload, APIGetProductDetail.ProductInformation productInfo) {
        payload.setOrgPrice(productInfo.getOrgPrice());
        payload.setNewPrice(productInfo.getNewPrice());
        payload.setCostPrice(productInfo.getCostPrice());
    }


    /**
     * Generates the list of categories based on product information.
     *
     * @param productInfo The product information.
     * @return The list of ProductPayload.Category objects.
     */
    private List<ProductPayload.Category> generateCategories(APIGetProductDetail.ProductInformation productInfo) {
        return productInfo.getCategories().stream()
                .map(category -> new ProductPayload.Category(category.getId(), category.getLevel(), category.getCateId()))
                .toList();
    }

    /**
     * Generates the list of images based on product information.
     *
     * @param productInfo The product information.
     * @return The list of ProductPayload.Image objects.
     */
    private List<ProductPayload.Image> generateImages(APIGetProductDetail.ProductInformation productInfo) {
        return productInfo.getImages().stream()
                .map(image -> new ProductPayload.Image(image.getImageUUID(), image.getUrlPrefix(), image.getExtension(), image.getRank()))
                .toList();
    }

    /**
     * Generates the shipping info based on product information.
     *
     * @param productInfo The product information.
     * @return The generated ShippingInfo object.
     */
    private ProductPayload.ShippingInfo generateShippingInfo(APIGetProductDetail.ProductInformation productInfo) {
        var shippingInfo = productInfo.getShippingInfo();
        return new ProductPayload.ShippingInfo(shippingInfo.getWeight(), shippingInfo.getHeight(), shippingInfo.getLength(), shippingInfo.getWidth());
    }

    /**
     * Generates the models for the product variations if any exist.
     *
     * @param variationNum The number of product variations.
     * @return A list of ProductPayload.Model objects.
     */
    private List<ProductPayload.Model> generateModels(int variationNum) {
        if (variationNum == 0) return List.of();

        var variationMap = new DataGenerator().randomVariationMapWithoutLangKey(variationNum);
        String variationLabel = variationMap.keySet().toString().replaceAll("[\\[\\]\\s]", "").replaceAll(",", "|");
        List<String> variationList = new DataGenerator().getVariationList(variationMap);
        return variationList.stream().map(variationName -> createModel(variationLabel, variationName))
                .toList();
    }

    /**
     * Creates a model object for a given variation.
     *
     * @param variationLabel The label for the variation.
     * @param variationName  The name of the variation.
     * @return The ProductPayload.Model object.
     */
    private ProductPayload.Model createModel(String variationLabel, String variationName) {
        long listingPrice = nextLong(MAX_PRICE);
        long sellingPrice = nextLong(listingPrice);

        // Create inventory list with stock values
        List<ProductPayload.Model.Branch> inventoryList = branchIds.stream().map(branchId -> new ProductPayload.Model.Branch(branchId, getBranchType(branchId), 5, 0, "CHANGE", "FROM_CREATE_AT_ITEM_SCREEN", ""))
                .toList();

        var model = new ProductPayload.Model();
        model.setName(variationName);
        model.setLabel(variationLabel);
        model.setOrgPrice(listingPrice);
        model.setNewPrice(sellingPrice);
        model.setLstInventory(inventoryList);
        return model;
    }

    /**
     * Generates the inventory list for the product based on its branches.
     *
     * @param productInfo The product information.
     * @return A list of ProductPayload.Inventory objects.
     */
    private List<ProductPayload.Inventory> generateLstInventory(APIGetProductDetail.ProductInformation productInfo) {
        return productInfo.getBranches()
                .stream().map(branch -> new ProductPayload.Inventory(branch.getBranchId(), branch.getTotalItem(), branch.getSoldItem(), "CHANGE", branch.getSku()))
                .toList();
    }

    /**
     * Retrieves the branch type for the given branch ID.
     *
     * @param branchId The branch ID.
     * @return The branch type.
     */
    private String getBranchType(int branchId) {
        return branchTypes.get(branchIds.indexOf(branchId));
    }

    /**
     * Generates the list of languages based on product information.
     *
     * @param productInfo The product information.
     * @return A list of ProductPayload.Language objects.
     */
    private List<ProductPayload.Language> generateLanguages(APIGetProductDetail.ProductInformation productInfo) {
        return productInfo.getLanguages().stream()
                .map(language -> new ProductPayload.Language(language.getId(), productInfo.getId(), language.getLanguage(), language.getName(), language.getDescription(), language.getSeoTitle(), language.getSeoDescription(), language.getSeoKeywords()))
                .toList();
    }

    /**
     * Updates a product by sending a PUT request with the updated product data.
     * The product information is re-initialized after the request.
     *
     * @param productInfo  The product information.
     * @param variationNum The number of variations for the product.
     */
    public void updateProductVariations(APIGetProductDetail.ProductInformation productInfo, int variationNum) {
        payload = initProductPayload(productInfo, variationNum);

        // Send PUT request to update the product
        new API().put(UPDATE_PRODUCT_PATH, loginInfo.getAccessToken(), payload)
                .then().statusCode(200);

        // Reset payload for future use
        payload = new ProductPayload();
    }
}