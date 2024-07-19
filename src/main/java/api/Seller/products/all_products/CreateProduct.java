package api.Seller.products.all_products;

import api.Seller.login.Login;
import api.Seller.setting.BranchManagement;
import api.Seller.setting.StoreInformation;
import api.Seller.setting.VAT;
import lombok.Data;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.setting.Tax.TaxInfo;
import utilities.model.dashboard.setting.branchInformation.BranchInfo;
import utilities.model.dashboard.setting.storeInformation.StoreInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;

public class CreateProduct {
    String createProductPath = "/itemservice/api/items?fromSource=DASHBOARD";
    API api = new API();
    long epoch = Instant.now().toEpochMilli();
    TaxInfo taxInfo;
    BranchInfo branchInfo;
    StoreInfo storeInfo;
    LoginDashboardInfo loginInfo;
    private static String productName;
    private static String productDescription;
    private static boolean hasModel;
    private static int productId;
    private static List<Long> productSellingPrice;
    private static Map<String, List<Integer>> productStockQuantity;
    private static List<Integer> branchIds;
    ProductPayloadInfo info = new ProductPayloadInfo();

    public CreateProduct(LoginInformation loginInformation) {
        loginInfo = new Login().getInfo(loginInformation);
        taxInfo = new VAT(loginInformation).getInfo();
        branchInfo = new BranchManagement(loginInformation).getInfo();
        branchIds = branchInfo.getBranchID();
        storeInfo = new StoreInformation(loginInformation).getInfo();
    }

    @Data
    public static class ShippingInfo {
        private int weight;
        private int height;
        private int width;
        private int length;
    }

    @Data
    public static class BranchStock {
        private int branchId;
        private int stock;
    }

    @Data
    public static class ModelCodeInfo {
        private int branchId;
        private List<String> modelCodes;
    }

    @Data
    public static class StockQuantityInfo {
        private String variation;
        private List<BranchStock> branchStockList;
    }

    @Data
    public static class PriceInfo {
        private long listingPrice;
        private long sellingPrice;
    }

    @Data
    public static class SeoInfo {
        private String seoTitle;
        private String seoDescription;
        private String seoURL;
        private String seoKeywords;
    }

    @Data
    public static class ProductPayloadInfo {
        private boolean manageByIMEI;
        private String productName;
        private String productDescription;
        private String currency = "đ";
        private ShippingInfo shippingInfo;
        private String taxId;
        private boolean showOutOfStock = true;
        private boolean hideStock;
        private boolean lotAvailable;
        private boolean expiredQuality;
        private boolean onApp = true;
        private boolean onWeb = true;
        private boolean inStore = true;
        private boolean inGoSocial = true;
        private boolean enableListing;
        private String variationName;
        private List<String> variationValueList;
        private List<StockQuantityInfo> stockQuantityInfoList;
        private List<PriceInfo> priceInfoList;
        private SeoInfo seoInfo;
        private int priority;
    }

    public CreateProduct setShowOutOfStock(boolean showOutOfStock) {
        info.setShowOutOfStock(showOutOfStock);
        return this;
    }

    public CreateProduct setHideStock(boolean hideStock) {
        info.setHideStock(hideStock);
        return this;
    }

    ProductPayloadInfo initBasicInformation(boolean isManagedByIMEI) {
        // set manage inventory
        info.setManageByIMEI(isManagedByIMEI);

        // set product description
        info.setProductDescription("[%s] product description.".formatted(storeInfo.getDefaultLanguage()));

        // set shipping info
        ShippingInfo shippingInfo = new ShippingInfo();
        shippingInfo.setHeight(nextBoolean() ? 0 : 10);
        shippingInfo.setWeight(nextBoolean() ? 0 : 10);
        shippingInfo.setWidth(nextBoolean() ? 0 : 10);
        shippingInfo.setLength(nextBoolean() ? 0 : 10);
        info.setShippingInfo(shippingInfo);

        // set taxId
        String taxId = taxInfo.getTaxID().isEmpty() ? "" : String.valueOf(taxInfo.getTaxID().get(nextInt(taxInfo.getTaxID().size())));
        info.setTaxId(taxId);

        // init SEO data
        SeoInfo seoInfo = getSeoInfo();

        // set SEO info
        info.setSeoInfo(seoInfo);

        return info;
    }

    private SeoInfo getSeoInfo() {
        SeoInfo seoInfo = new SeoInfo();

        // set SEO title
        String seoTitle = "[%s] Auto - SEO Title - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        seoInfo.setSeoTitle(seoTitle);

        // set SEO description
        String seoDescription = "[%s] Auto - SEO Description - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        seoInfo.setSeoDescription(seoDescription);

        // set SEO keywords
        String seoKeywords = "[%s] Auto - SEO Keyword - %s".formatted(storeInfo.getDefaultLanguage(), epoch);
        seoInfo.setSeoKeywords(seoKeywords);

        // set SEO url
        String seoURL = "%s%s".formatted(storeInfo.getDefaultLanguage(), epoch);
        seoInfo.setSeoURL(seoURL);
        return seoInfo;
    }

    ProductPayloadInfo initWithoutVariationInfo(boolean isManagedByIMEI, int... branchStock) {
        ProductPayloadInfo info = initBasicInformation(isManagedByIMEI);

        // product name
        String productName = "[%s] %s%s".formatted(storeInfo.getDefaultLanguage(), isManagedByIMEI ? ("Auto - IMEI - without variation - ") : ("Auto - Normal - without variation - "), new DataGenerator().generateDateTime("dd/MM HH:mm:ss"));
        info.setProductName(productName);

        // set price info list
        PriceInfo priceInfo = new PriceInfo();
        priceInfo.setListingPrice(nextLong(MAX_PRICE));
        priceInfo.setSellingPrice(nextLong(priceInfo.getListingPrice()));
        info.setPriceInfoList(List.of(priceInfo));

        // set stock quantity info list
        List<BranchStock> branchStockList = new ArrayList<>();
        IntStream.range(0, branchInfo.getBranchID().size()).forEach(branchIndex -> {
            BranchStock brStock = new BranchStock();
            brStock.setBranchId(branchInfo.getBranchID().get(branchIndex));
            brStock.setStock((branchStock.length > branchIndex) ? branchStock[branchIndex] : 0);
            branchStockList.add(brStock);
        });
        StockQuantityInfo stockQuantityInfo = new StockQuantityInfo();
        stockQuantityInfo.setBranchStockList(branchStockList);
        info.setStockQuantityInfoList(List.of(stockQuantityInfo));

        return info;
    }

    ProductPayloadInfo initVariationProductInfo(boolean isManagedByIMEI, int increaseNum, int... branchStock) {
        ProductPayloadInfo info = initBasicInformation(isManagedByIMEI);

        // set product name
        String productName = "[%s] %s%s".formatted(storeInfo.getDefaultLanguage(), isManagedByIMEI ? ("Auto - IMEI - variation - ") : ("Auto - Normal - variation - "), new DataGenerator().generateDateTime("dd/MM HH:mm:ss"));
        info.setProductName(productName);

        // generate variation map
        Map<String, List<String>> variationMap = new DataGenerator().randomVariationMap("");

        // set variation name
        List<String> varName = new ArrayList<>(variationMap.keySet());
        String variationName = (varName.size() > 1) ? "%s_%s|%s_%s".formatted(storeInfo.getDefaultLanguage(), varName.get(0), storeInfo.getDefaultLanguage(), varName.get(1))
                : "%s_%s".formatted(varName.get(0), storeInfo.getDefaultLanguage());
        info.setVariationName(variationName);

        // set variation value list
        List<List<String>> varValue = new ArrayList<>(variationMap.values());
        List<String> variationList = new ArrayList<>(varValue.get(0).stream().map(var -> "%s_%s".formatted(storeInfo.getDefaultLanguage(), var)).toList());
        if (varValue.size() > 1) {
            variationList = new DataGenerator().mixVariationValue(variationList, varValue.get(1));
        }
        info.setVariationValueList(variationList);

        // set price info list
        List<PriceInfo> priceInfoList = new ArrayList<>();
        IntStream.range(0, variationList.size()).mapToObj(varIndex -> new PriceInfo()).forEach(priceInfo -> {
            priceInfo.setListingPrice(nextLong(MAX_PRICE));
            priceInfo.setSellingPrice(nextLong(priceInfo.getListingPrice()));
            priceInfoList.add(priceInfo);
        });
        info.setPriceInfoList(priceInfoList);

        // set stock quantity info list
        List<StockQuantityInfo> stockQuantityInfoList = new ArrayList<>();
        for (int varIndex = 0; varIndex < variationList.size(); varIndex++) {
            StockQuantityInfo stockQuantityInfo = new StockQuantityInfo();
            List<BranchStock> branchStockList = getBranchStocks(increaseNum, branchStock, varIndex);
            stockQuantityInfo.setVariation(variationList.get(varIndex));
            stockQuantityInfo.setBranchStockList(branchStockList);
            stockQuantityInfoList.add(stockQuantityInfo);
        }
        info.setStockQuantityInfoList(stockQuantityInfoList);

        return info;
    }

    private List<BranchStock> getBranchStocks(int increaseNum, int[] branchStock, int varIndex) {
        List<BranchStock> branchStockList = new ArrayList<>();
        // set branch stock
        IntStream.range(0, branchInfo.getBranchID().size()).forEach(branchIndex -> {
            BranchStock brStock = new BranchStock();
            brStock.setBranchId(branchInfo.getBranchID().get(branchIndex));
            brStock.setStock((branchStock.length > branchIndex) ? (branchStock[branchIndex] + (varIndex * increaseNum)) : 0);
            branchStockList.add(brStock);
        });
        return branchStockList;
    }

    private String getCategories() {
        return """
                [
                 	{
                 		"id": null,
                 		"level": 1,
                 		"cateId": 1014
                 	},
                 	{
                 		"id": null,
                 		"level": 2,
                 		"cateId": 1680
                 	}
                 ]""";
    }

    private String getImages() {
        return """
                [
                	{
                		"imageUUID": "28952be8-ef6b-4877-99fa-399c7ddd8c01",
                		"urlPrefix": "https://d3a0f2zusjbf7r.cloudfront.net",
                		"extension": "jpg",
                		"rank": 0
                	}
                ]""";
    }

    private String getShippingInfo(ShippingInfo shippingInfo) {
        return """
                {
                	"weight": %s,
                	"height": %s,
                	"length": %s,
                	"width": %s
                }""".formatted(shippingInfo.getWeight(),
                shippingInfo.getHeight(),
                shippingInfo.getLength(),
                shippingInfo.getWidth());
    }

    private String getInventory(BranchStock info) {
        return """
                {
                	"branchId": %s,
                	"inventoryActionType": "FROM_CREATE_AT_ITEM_SCREEN",
                	"inventoryCurrent": 0,
                	"inventoryStock": %s,
                	"inventoryType": "SET",
                	"sku": ""
                }""".formatted(info.getBranchId(), info.getStock());
    }

    private List<String> getListInventory(List<BranchStock> infoList) {
        return infoList.stream().map(this::getInventory).toList();
    }

    private String getModelCode(int branchId, String code) {
        return """
                {
                	"branchId": %s,
                	"code": "%s",
                	"status": "AVAILABLE"
                }""".formatted(branchId, code);
    }

    private String getBranchName(int branchId) {
        return branchInfo.getBranchName().get(branchInfo.getBranchID().indexOf(branchId));
    }

    private String getIMEICode(int branchId, int index, String... variation) {
        return "%s%s_IMEI_%s_%s".formatted((variation.length > 0) ? "%s_".formatted(variation[0]) : "", getBranchName(branchId), epoch, index);
    }

    private List<String> getItemModelCodeDTOS(List<BranchStock> infoList, String... variation) {
        List<String> itemModelCodes = new ArrayList<>();
        infoList.forEach(info -> IntStream.range(0, info.getStock()).mapToObj(index -> getModelCode(info.getBranchId(), getIMEICode(info.getBranchId(), index, variation))).forEach(itemModelCodes::add));
        return itemModelCodes;
    }

    String getVariationModel(String varName, PriceInfo priceInfo, StockQuantityInfo stockQuantityInfo, boolean isManagedByIMEI) {
        return """
                {
                	"name": "%s",
                	"orgPrice": %s,
                	"discount": 0,
                	"newPrice": %s,
                	"totalItem": 0,
                	"label": "%s",
                	"sku": "",
                	"newStock": 0,
                	"costPrice": 0,
                	"lstInventory": %s,
                	"itemModelCodeDTOS": %s
                }""".formatted(stockQuantityInfo.getVariation(),
                priceInfo.getListingPrice(),
                priceInfo.getSellingPrice(),
                varName,
                getListInventory(stockQuantityInfo.getBranchStockList()),
                isManagedByIMEI ? getItemModelCodeDTOS(stockQuantityInfo.getBranchStockList(), stockQuantityInfo.getVariation()) : "[]");
    }

    private List<String> getVariationModels(String varName, List<PriceInfo> priceInfoList, List<StockQuantityInfo> stockQuantityInfoList, boolean isManagedByIMEI) {
        return IntStream.range(0, stockQuantityInfoList.size()).mapToObj(varIndex -> getVariationModel(varName, priceInfoList.get(varIndex), stockQuantityInfoList.get(varIndex), isManagedByIMEI)).toList();
    }

    private String getPayload(ProductPayloadInfo productInfo) {
        productName = productInfo.getProductName();
        productDescription = productInfo.getProductDescription();
        productSellingPrice = productInfo.getPriceInfoList().stream().map(PriceInfo::getSellingPrice).toList();
        productStockQuantity = new HashMap<>();
        productInfo.getStockQuantityInfoList().forEach(info -> productStockQuantity.put(info.getVariation(), info.getBranchStockList().stream().map(BranchStock::getStock).toList()));

        return """
                {
                    "categories": %s,
                    "name": "%s",
                    "cateId": 1680,
                    "itemType": "BUSINESS_PRODUCT",
                    "currency": "%s",
                    "description": "%s",
                    "discount": 0,
                    %s
                    "totalComment": 0,
                    "totalLike": 0,
                    "images": %s,
                    "totalItem": 0,
                    "shippingInfo": %s,
                    "models": %s,
                    "itemAttributes": [],
                    "itemAttributeDeleteIds": [],
                    "seoTitle": "%s",
                    "seoDescription": "%s",
                    "seoUrl": "%s",
                    "seoKeywords": "%s",
                    "priority": "%s",
                    "taxId": %s,
                    "quantityChanged": true,
                    "bcoin": 0,
                    "isSelfDelivery": false,
                    "showOutOfStock": %s,
                    "barcode": null,
                    "isHideStock": %s,
                    "lotAvailable": %s,
                    "expiredQuality": %s,
                    "inventoryManageType": "%s",
                    "conversionUnitId": null,
                    "onApp": %s,
                    "onWeb": %s,
                    "inStore": %s,
                    "inGosocial": %s,
                    "enabledListing": %s,
                    "lstInventory": %s
                }""".formatted(getCategories(),
                productInfo.getProductName(),
                productInfo.getCurrency(),
                productInfo.getProductDescription(),
                (productInfo.getVariationName() != null) ? "" : """
                        "orgPrice": %s,
                        "newPrice": %s,
                        "costPrice": 0,""".formatted(productInfo.getPriceInfoList().get(0).getListingPrice(),
                        productInfo.getPriceInfoList().get(0).getSellingPrice()),
                getImages(),
                getShippingInfo(productInfo.getShippingInfo()),
                (productInfo.getVariationName() != null)
                        ? getVariationModels(productInfo.getVariationName(),
                        productInfo.getPriceInfoList(),
                        productInfo.getStockQuantityInfoList(),
                        productInfo.isManageByIMEI())
                        : "[]",
                productInfo.getSeoInfo().getSeoTitle(),
                productInfo.getSeoInfo().getSeoDescription(),
                productInfo.getSeoInfo().getSeoURL(),
                productInfo.getSeoInfo().getSeoKeywords(),
                productInfo.getPriority(),
                productInfo.getTaxId().isEmpty() ? "\"\"" : productInfo.getTaxId(),
                productInfo.isShowOutOfStock(),
                productInfo.isHideStock(),
                productInfo.isLotAvailable(),
                productInfo.isExpiredQuality(),
                productInfo.isManageByIMEI() ? "IMEI_SERIAL_NUMBER" : "PRODUCT",
                productInfo.isOnApp(),
                productInfo.isOnWeb(),
                productInfo.isInStore(),
                productInfo.isInGoSocial(),
                productInfo.isEnableListing(),
                getListInventory(productInfo.getStockQuantityInfoList().get(0).getBranchStockList()));
    }

    public CreateProduct createWithoutVariationProduct(boolean isManagedByIMEI, int... branchStock) {
        hasModel = false;
        ProductPayloadInfo info = initWithoutVariationInfo(isManagedByIMEI, branchStock);

        // post without variation product
        productId = api.post(createProductPath, loginInfo.getAccessToken(), getPayload(info))
                .then()
                .statusCode(201)
                .extract()
                .response()
                .jsonPath()
                .getInt("id");
        return this;
    }

    public CreateProduct createVariationProduct(boolean isManagedByIMEI, int increaseNum, int... branchStock) {
        hasModel = true;
        ProductPayloadInfo info = initVariationProductInfo(isManagedByIMEI, increaseNum, branchStock);

        // post without variation product
        productId = api.post(createProductPath, loginInfo.getAccessToken(), getPayload(info))
                .then()
                .statusCode(201)
                .extract()
                .response()
                .jsonPath()
                .getInt("id");
        return this;
    }

    public int getProductID() {
        return CreateProduct.productId;
    }

    public String getProductName() {
        return CreateProduct.productName;
    }

    public String getProductDescription() {
        return CreateProduct.productDescription;
    }

    public boolean isHasModel() {
        return CreateProduct.hasModel;
    }

    public List<Long> getProductSellingPrice() {
        return productSellingPrice;
    }

    public Map<String, List<Integer>> getProductStockQuantity() {
        return CreateProduct.productStockQuantity;
    }

    public List<Integer> getBranchIds() {
        return CreateProduct.branchIds;
    }

}
