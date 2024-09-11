package api.Seller.products.all_products;

import api.Seller.login.Login;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import utilities.api.API;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class APIProductDetailV2 {
    String getDetailsOfProductPath = "/itemservice/api/beehive-items/%d";

    API api = new API();
    LoginDashboardInfo loginInfo;
    LoginInformation loginInformation;

    public APIProductDetailV2(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        loginInfo = new Login().getInfo(loginInformation);
    }

    public ProductInfoV2 getInfo(int productId) {
        // Logger
        LogManager.getLogger().info("===== STEP =====> [GetProductInfo] ProductId: {} ", productId);

        return api.get(getDetailsOfProductPath.formatted(productId), loginInfo.getAccessToken())
                .then()
                .statusCode(200)
                .extract()
                .as(ProductInfoV2.class)
                .analyzeData();
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductInfoV2 {
        private String lastModifiedDate;
        private int id;
        private String name;
        private String currency;
        private String description;
        private long orgPrice;
        private int discount;
        private long newPrice;
        private ShippingInfo shippingInfo;
        private boolean deleted = true;
        private List<Model> models = new ArrayList<>();
        private boolean hasModel;
        private boolean showOutOfStock;
        private String seoTitle;
        private String seoDescription;
        private String seoKeywords;
        private String barcode;
        private String seoUrl;
        private List<BranchStock> branches;
        private List<MainLanguage> languages;
        private List<ItemAttribute> itemAttributes;
        private int taxId;
        private String taxName;
        private double taxRate;
        private double taxAmount;
        private long costPrice;
        private boolean onApp;
        private boolean onWeb;
        private boolean inStore;
        private boolean inGoSocial;
        private boolean enabledListing;
        private boolean isHideStock;
        private String inventoryManageType;
        private String bhStatus;
        private boolean lotAvailable;
        private boolean expiredQuality;

        // Analyze data
        private List<Long> productListingPrice;
        private List<Long> productSellingPrice;
        private List<Long> productCostPrice;
        private List<Integer> variationModelList = new ArrayList<>();
        private List<String> barcodeList;
        private List<String> variationStatus;
        private Map<String, String> mainProductNameMap = new HashMap<>();
        private Map<Integer, Map<String, String>> versionNameMap = new HashMap<>();
        private Map<String, String> mainProductDescriptionMap = new HashMap<>();
        private Map<Integer, Map<String, String>> versionDescriptionMap = new HashMap<>();
        private Map<String, String> variationGroupNameMap = new HashMap<>();
        private Map<String, List<String>> variationValuesMap = new HashMap<>();
        private Map<Integer, List<Integer>> productStockQuantityMap = new HashMap<>();

        private ProductInfoV2 analyzeData() {
            // Get product name and description
            languages.forEach(language -> {
                this.mainProductNameMap.put(language.getLanguage(), language.getName());
                this.mainProductDescriptionMap.put(language.getLanguage(), language.getDescription());
            });

            // Get variation information
            if (hasModel) {
                // Get variation name map
                models.get(0).getLanguages().forEach(language -> this.variationGroupNameMap.put(language.getLanguage(), language.getLabel()));

                // Get others information
                models.forEach(model -> {
                    // Get variation listing price
                    this.productListingPrice = models.stream().map(Model::getOrgPrice).toList();

                    // Get variation selling price
                    this.productSellingPrice = models.stream().map(Model::getNewPrice).toList();

                    // Get variation cost price
                    this.productCostPrice = models.stream().map(Model::getCostPrice).toList();

                    // Get variation model list
                    this.variationModelList = models.stream().map(Model::getId).toList();

                    // Get barcode list
                    this.barcodeList = models.stream().map(Model::getBarcode).toList();

                    // Get variation status
                    this.variationStatus = models.stream().map(Model::getStatus).toList();

                    // Get variation stock
                    List<Integer> variationStock = new ArrayList<>();
                    model.getBranches().stream().map(branchStock -> branchStock.getTotalItem() - branchStock.getSoldItem()).forEach(variationStock::add);
                    this.productStockQuantityMap.put(model.getId(), variationStock);

                    // Get version name
                    this.versionNameMap.put(model.getId(), IntStream.range(0, model.getLanguages().size())
                            .boxed()
                            .collect(Collectors.toMap(languageIndex -> model.getLanguages().get(languageIndex).getLanguage(),
                                    languageIndex -> Optional.ofNullable(model.getLanguages().get(languageIndex).getVersionName()).orElse(name),
                                    (ignored, b) -> b)));

                    // Get version name
                    this.versionDescriptionMap.put(model.getId(), IntStream.range(0, model.getLanguages().size())
                            .boxed()
                            .collect(Collectors.toMap(languageIndex -> model.getLanguages().get(languageIndex).getLanguage(),
                                    languageIndex -> model.isUseProductDescription() ? description : Optional.ofNullable(model.getLanguages().get(languageIndex).getDescription()).orElse(description),
                                    (ignored, b) -> b)));

                    // Get variation value map
                    model.getLanguages().forEach(language -> {
                        List<String> variationList = new ArrayList<>();
                        if (this.variationValuesMap.get(language.getLanguage()) != null)
                            variationList.addAll(variationValuesMap.get(language.getLanguage()));
                        variationList.add(language.getName());
                        this.variationValuesMap.put(language.getLanguage(), variationList);
                    });
                });
            }
            // Get without variation information
            else {
                // Get product barcode
                this.barcodeList = List.of(barcode);

                // Get without variation stock
                this.productStockQuantityMap.put(id, branches.stream().map(branchStock -> branchStock.getTotalItem() - branchStock.getSoldItem()).toList());

                // Get model list
                this.variationModelList.add(null);
            }
            return this;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ShippingInfo {
            private int weight;
            private int width;
            private int height;
            private int length;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Model {
            private int id;
            private String name;
            private String sku;
            private long orgPrice;
            private long newPrice;
            private String label;
            private String orgName;
            private String description;
            private String barcode;
            private String versionName;
            private boolean useProductDescription;
            private boolean reuseAttributes;
            private String status;
            private List<BranchStock> branches;
            private List<VersionLanguage> languages;
            private List<ItemAttribute> modelAttributes;
            private long costPrice;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class VersionLanguage {
                private String language;
                private String name;
                private String label;
                private String description;
                private String versionName;
            }
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class BranchStock {
            private int branchId;
            private int totalItem;
            private int soldItem;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class MainLanguage {
            private String language;
            private String name;
            private String description;
            private String seoTitle;
            private String seoDescription;
            private String seoKeywords;
            private String seoUrl;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ItemAttribute {
            private String attributeName;
            private String attributeValue;
            private boolean isDisplay;
        }
    }
}
