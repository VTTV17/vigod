package api.Seller.products.all_products;

import api.Seller.login.Login;
import api.Seller.products.all_products.APIProductDetail.ProductInformationEnum;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StoreInformation;
import api.Seller.setting.VAT;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.Tax.TaxInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;

public class APICreateProduct {
    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;
    TaxInfo taxInfo;
    BranchInfo branchInfo;
    StoreInfo storeInfo;

    private static final String createProductPath = "/itemservice/api/items?fromSource=DASHBOARD";
    private static int productId;

    public APICreateProduct(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
        taxInfo = new VAT(loginInformation).getInfo();
        branchInfo = new BranchManagement(loginInformation).getInfo();
        storeInfo = new StoreInformation(loginInformation).getInfo();
    }

    @Data
    @RequiredArgsConstructor
    public static class ProductPayload {
        private final List<Category> categories = List.of(new Category(null, 1, 1014), new Category(null, 2, 1680));
        private String name;
        private final int cateId = 1680;
        private final String itemType = "BUSINESS_PRODUCT";
        private final String currency = "đ";
        private String description;
        private final int discount = 0;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Long costPrice;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Long orgPrice;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Long newPrice;
        private final int totalComment = 0;
        private final int totalLike = 0;
        private final List<Image> images = List.of(new Image("28952be8-ef6b-4877-99fa-399c7ddd8c01", "https://d3a0f2zusjbf7r.cloudfront.net", "jpg", 0));
        private final int totalItem = 0;
        private final ShippingInfo shippingInfo = nextBoolean() ? new ShippingInfo(10, 10, 10, 10) : new ShippingInfo(0, 0, 0, 0);
        private String parentSku;
        private List<Model> models = new ArrayList<>();
        private List<Object> itemAttributes = new ArrayList<>();
        private List<Object> itemAttributeDeleteIds = new ArrayList<>();
        private String seoTitle;
        private String seoDescription;
        private String seoUrl;
        private String seoKeywords;
        private String priority;
        private String taxId;
        private final boolean quantityChanged = true;
        private final boolean isSelfDelivery = false;
        private boolean showOutOfStock = true;
        private String barcode;
        private Boolean isHideStock = false;
        private boolean lotAvailable;
        private boolean expiredQuality;
        private String inventoryManageType;
        private String conversionUnitId;
        private final boolean onApp = true;
        private final boolean onWeb = true;
        private final boolean inStore = true;
        private final boolean inGoSocial = true;
        private final boolean enabledListing = false;
        private List<Inventory> lstInventory = new ArrayList<>();
        private List<ItemModelCodeDTO> itemModelCodeDTOS = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    public static class Category {
        private Integer id;
        private int level;
        private int cateId;
    }

    @Data
    @AllArgsConstructor
    public static class Image {
        private String imageUUID;
        private String urlPrefix;
        private String extension;
        private int rank;
    }

    @Data
    @AllArgsConstructor
    public static class ShippingInfo {
        private int weight;
        private int height;
        private int length;
        private int width;
    }

    @Data
    public static class ItemModelCodeDTO {
        private int branchId;
        private String code;
        private final String status = "AVAILABLE";

        public ItemModelCodeDTO(int branchId, String branchName, String variation, int index) {
            this.branchId = branchId;
            this.code = "%s%s_%s".formatted(variation.isEmpty() ? "" : "%s_".formatted(variation), branchName, index);
        }
    }

    @Data
    @AllArgsConstructor
    public static class Inventory {
        private int branchId;
        private String branchType;
        private final String inventoryActionType = "FROM_CREATE_AT_ITEM_SCREEN";
        private final int inventoryCurrent = 0;
        private int inventoryStock;
        private final String inventoryType = "SET";
        private final String sku = "";
    }

    @Data
    @AllArgsConstructor
    public static class Model {
        private String name;
        private long orgPrice;
        private final int discount = 0;
        private long newPrice;
        private final int totalItem = 0;
        private String label;
        private final String sku = "";
        private final int newStock = 0;
        private final int costPrice = 0;
        private List<Inventory> lstInventory;
        private List<ItemModelCodeDTO> itemModelCodeDTOS;
    }
    private ProductPayload payload = new ProductPayload();

    public APICreateProduct setLotAvailable(boolean lotAvailable) {
        payload.setLotAvailable(lotAvailable);
        return this;
    }

    public APICreateProduct setShowOutOfStock(boolean showOutOfStock) {
        payload.setShowOutOfStock(showOutOfStock);
        return this;
    }

    public APICreateProduct setHideStock(boolean hideStock) {
        payload.setIsHideStock(hideStock);
        return this;
    }

    ProductPayload initBasicInformation(boolean isManagedByIMEI) {
        // set lot information
        payload.setLotAvailable(payload.isLotAvailable() && !isManagedByIMEI);

        // set manage inventory
        payload.setInventoryManageType((isManagedByIMEI) ? "IMEI_SERIAL_NUMBER" : "PRODUCT");

        // set product description
        payload.setDescription("[%s] product description.".formatted(storeInfo.getDefaultLanguage()));

        // set taxId
        String taxId = taxInfo.getTaxID().isEmpty() ? "" : taxInfo.getTaxID().get(nextInt(taxInfo.getTaxID().size())).toString();
        payload.setTaxId(taxId);

        // set SEO title
        String seoTitle = "[%s] Auto - SEO Title - %s".formatted(storeInfo.getDefaultLanguage(), Instant.now().toEpochMilli());
        payload.setSeoTitle(seoTitle);

        // set SEO description
        String seoDescription = "[%s] Auto - SEO Description - %s".formatted(storeInfo.getDefaultLanguage(), Instant.now().toEpochMilli());
        payload.setSeoDescription(seoDescription);

        // set SEO keywords
        String seoKeywords = "[%s] Auto - SEO Keyword - %s".formatted(storeInfo.getDefaultLanguage(), Instant.now().toEpochMilli());
        payload.setSeoKeywords(seoKeywords);

        // set SEO url
        String seoURL = "%s%s".formatted(storeInfo.getDefaultLanguage(), Instant.now().toEpochMilli());
        payload.setSeoUrl(seoURL);

        return payload;
    }

    ProductPayload getWithoutVariationPayload(boolean isManagedByIMEI, int... branchStock) {
        ProductPayload payload = initBasicInformation(isManagedByIMEI);

        // product name
        String productName = "[%s] %s%s".formatted(storeInfo.getDefaultLanguage(), isManagedByIMEI ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - "), new DataGenerator().generateDateTime("dd/MM HH:mm:ss"));
        payload.setName(productName);

        // set price payload list
        payload.setOrgPrice(nextLong(MAX_PRICE));
        payload.setNewPrice(nextLong(payload.getOrgPrice()));
        payload.setCostPrice(nextLong(payload.getNewPrice()));

        // set stock quantity payload list
        List<Inventory> lstInventory = IntStream.range(0, branchInfo.getBranchID().size())
                .mapToObj(branchIndex -> new Inventory(branchInfo.getBranchID().get(branchIndex),
                        branchInfo.getBranchType().get(branchIndex),
                        (!payload.isLotAvailable() && (branchStock.length > branchIndex)) ? branchStock[branchIndex] : 0))
                .toList();
        payload.setLstInventory(lstInventory);

        if (payload.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
            List<ItemModelCodeDTO> itemModelCodeDTOS = new ArrayList<>();
            payload.getLstInventory().forEach(inventory -> IntStream.range(0, inventory.getInventoryStock())
                    .mapToObj(index -> new ItemModelCodeDTO(inventory.getBranchId(), branchInfo.getBranchName().get(branchInfo.getBranchID().indexOf(inventory.getBranchId())), "", index))
                    .forEach(itemModelCodeDTOS::add));
            payload.setItemModelCodeDTOS(itemModelCodeDTOS);
        } else payload.setItemModelCodeDTOS(List.of());

        return payload;
    }

    ProductPayload getWithVariationPayload(boolean isManagedByIMEI, int increaseNum, int... branchStock) {
        ProductPayload payload = initBasicInformation(isManagedByIMEI);

        // set product name
        String productName = "[%s] %s%s".formatted(storeInfo.getDefaultLanguage(), isManagedByIMEI ? ("Auto - IMEI - variation - ") : ("Auto - Normal - variation - "), new DataGenerator().generateDateTime("dd/MM HH:mm:ss"));
        payload.setName(productName);

        // generate variation map
        Map<String, List<String>> variationMap = new DataGenerator().randomVariationMap(storeInfo.getDefaultLanguage());

        // set variation name
        String variationName = variationMap.keySet().toString().replaceAll("[\\[\\]\\s]", "").replaceAll(",", "|");

        // set variation value list
        List<String> variationList = new DataGenerator().getVariationList(variationMap);

        // set models value
        List<Model> models = new ArrayList<>();
        for (int varIndex = 0; varIndex < variationList.size(); varIndex++) {
            long listingPrice = nextLong(MAX_PRICE);
            long sellingPrice = nextLong(listingPrice);
            int finalVarIndex = varIndex;
            List<Inventory> lstInventory = IntStream.range(0, branchInfo.getBranchID().size())
                    .mapToObj(branchIndex -> new Inventory(branchInfo.getBranchID().get(branchIndex),
                            branchInfo.getBranchType().get(branchIndex),
                            (!payload.isLotAvailable() && (branchStock.length > branchIndex)) ? (branchStock[branchIndex] + (finalVarIndex * increaseNum)) : 0))
                    .toList();

            List<ItemModelCodeDTO> itemModelCodeDTOS = new ArrayList<>();
            if (payload.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
                lstInventory.forEach(inventory -> IntStream.range(0, inventory.getInventoryStock())
                        .mapToObj(index -> new ItemModelCodeDTO(inventory.getBranchId(), branchInfo.getBranchName().get(branchInfo.getBranchID().indexOf(inventory.getBranchId())), variationList.get(finalVarIndex), index))
                        .forEach(itemModelCodeDTOS::add));
            }
            models.add(new Model(variationList.get(finalVarIndex), listingPrice, sellingPrice, variationName, lstInventory, itemModelCodeDTOS));
        }
        payload.setModels(models);

        return payload;
    }

    ProductPayload getWithVariationPayload(int variationNum, int... branchStock) {
        ProductPayload payload = initBasicInformation(false);

        // set product name
        String productName = "[%s] %s%s".formatted(storeInfo.getDefaultLanguage(), ("Auto - Normal - variation - "), new DataGenerator().generateDateTime("dd/MM HH:mm:ss"));
        payload.setName(productName);

        // generate variation map
        Map<String, List<String>> variationMap = new DataGenerator().randomVariationMap(variationNum, storeInfo.getDefaultLanguage());

        // set variation name
        String variationName = variationMap.keySet().toString().replaceAll("[\\[\\]\\s]", "").replaceAll(",", "|");

        // set variation value list
        List<String> variationList = new DataGenerator().getVariationList(variationMap);

        // set models value
        List<Model> models = new ArrayList<>();
        for (int varIndex = 0; varIndex < variationList.size(); varIndex++) {
            long listingPrice = nextLong(MAX_PRICE);
            long sellingPrice = nextLong(listingPrice);
            int finalVarIndex = varIndex;
            List<Inventory> lstInventory = IntStream.range(0, branchInfo.getBranchID().size())
                    .mapToObj(branchIndex -> new Inventory(branchInfo.getBranchID().get(branchIndex),
                            branchInfo.getBranchType().get(branchIndex),
                            (!payload.isLotAvailable() && (branchStock.length > branchIndex)) ? (branchStock[branchIndex] + (finalVarIndex * 0)) : 0))
                    .toList();

            List<ItemModelCodeDTO> itemModelCodeDTOS = new ArrayList<>();
            if (payload.getInventoryManageType().equals("IMEI_SERIAL_NUMBER")) {
                lstInventory.forEach(inventory -> IntStream.range(0, inventory.getInventoryStock())
                        .mapToObj(index -> new ItemModelCodeDTO(inventory.getBranchId(), branchInfo.getBranchName().get(branchInfo.getBranchID().indexOf(inventory.getBranchId())), variationList.get(finalVarIndex), index))
                        .forEach(itemModelCodeDTOS::add));
            }
            models.add(new Model(variationList.get(finalVarIndex), listingPrice, sellingPrice, variationName, lstInventory, itemModelCodeDTOS));
        }
        payload.setModels(models);

        return payload;
    }

    public APICreateProduct createWithoutVariationProduct(boolean isManagedByIMEI, int... branchStock) {
        // Log
        LogManager.getLogger().info("===== STEP =====> [CreateWithoutVariationProduct] START...");

        // Get product payload
        ProductPayload productPayload = getWithoutVariationPayload(isManagedByIMEI, branchStock);

        // Renew product payload for next API
        payload = new ProductPayload();

        // post without variation product
        productId = api.post(createProductPath, loginInfo.getAccessToken(), productPayload)
                .then()
                .statusCode(201)
                .extract()
                .response()
                .jsonPath()
                .getInt("id");

        // Log
        LogManager.getLogger().info("===== STEP =====> [CreateWithoutVariationProduct] DONE!!!");
        return this;
    }

    public APICreateProduct createVariationProduct(boolean isManagedByIMEI, int increaseNum, int... branchStock) {
        // Log
        LogManager.getLogger().info("===== STEP =====> [CreateVariationProduct] START...");

        // Get product payload
        ProductPayload productPayload = getWithVariationPayload(isManagedByIMEI, increaseNum, branchStock);

        // Renew product payload for next API
        payload = new ProductPayload();

        // post without variation product
        productId = api.post(createProductPath, loginInfo.getAccessToken(), productPayload)
                .then()
                .statusCode(201)
                .extract()
                .response()
                .jsonPath()
                .getInt("id");

        // Log
        LogManager.getLogger().info("===== STEP =====> [CreateVariationProduct] DONE!!!");
        return this;
    }

    /**
     * Creates a product with or without variations, posts it to a third-party system,
     * and retrieves the product ID.
     *
     * @param variationNum  The number of variations for the product. If 0, a product without variations is created.
     * @param branchStock   An array of branch stock values to set for the product.
     * @return              The ID of the created product.
     */
    public int createProductTo3rdPartyThenRetrieveId(int variationNum, int... branchStock) {
        // Log the start of the product creation process
        LogManager.getLogger().info("===== STEP =====> [CreateAndLinkProductTo3rdPartyThenRetrieveId] START...");

        // Prepare the product payload depending on whether the product has variations
        ProductPayload productPayload = variationNum != 0
                ? getWithVariationPayload(variationNum, branchStock)
                : getWithoutVariationPayload(false, branchStock);

        // Renew the product payload for subsequent API use
        payload = new ProductPayload();

        // Post the product (with or without variations) and extract the product ID from the response
        productId = api.post(createProductPath, loginInfo.getAccessToken(), productPayload)
                .then()
                .statusCode(201)
                .extract()
                .response()
                .jsonPath()
                .getInt("id");

        // Log the successful completion of the product creation process
        LogManager.getLogger().info("===== STEP =====> [CreateAndLinkProductTo3rdPartyThenRetrieveId] DONE!!!");

        return productId;
    }

    public int getProductID() {
        return productId;
    }

    public String getProductName() {
        return new APIProductDetailV2(loginInformation).getInfo(productId).getName();
    }

    public String getProductDescription() {
        return new APIProductDetailV2(loginInformation).getInfo(productId).getDescription();
    }

    public boolean isHasModel() {
        return new APIProductDetailV2(loginInformation).getInfo(productId).isHasModel();
    }

    public List<Long> getProductSellingPrice() {
        APIProductDetailV2.ProductInfoV2 v2 = new APIProductDetailV2(loginInformation).getInfo(productId);
        return v2.isHasModel() ? v2.getProductSellingPrice() : List.of(v2.getNewPrice());
    }

    public Map<String, List<Integer>> getProductStockQuantity() {
        return new APIProductDetail(loginInformation).getInfo(productId, ProductInformationEnum.stockQuantity).getProductStockQuantityMap();
    }

    public List<Integer> getBranchIds() {
        return branchInfo.getBranchID();
    }
}