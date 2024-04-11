package utilities.model.dashboard.products.productInfomation;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProductInfo {
    private int productId;
    private Map<String, String> variationGroupNameMap;
    private Map<String, List<String>> variationValuesMap;
    private List<Long> productListingPrice;
    private List<Long> productSellingPrice;
    private List<Long> productCostPrice;
    private boolean hasModel;
    private boolean manageInventoryByIMEI;
    private List<String> variationModelList;
    private List<String> barcodeList;
    private List<String> variationStatus;
    private Map<String, String> mainProductNameMap;
    private Map<String, Map<String, String>> versionNameMap;
    private Map<String, String> mainProductDescriptionMap;
    private Map<String, Map<String, String>> versionDescriptionMap;
    private Map<String, Map<String, String>> seoMap;
    private Map<String, List<Integer>> productStockQuantityMap;
    private boolean showOutOfStock;
    private boolean enabledListing;
    private boolean isHideStock;
    private String bhStatus;
    private boolean deleted;
    private boolean onApp;
    private boolean onWeb;
    private boolean inStore;
    private boolean inGoSocial;
    private List<Integer> collectionIdList;
    private Map<Integer, Map<String, String>> collectionNameMap;
    private double taxRate;
    private String taxName;
    private List<String> attributeGroups;
    private List<String> attributeValues;
    private List<Boolean> isDisplayAttributes;
    private Map<String, List<String>> variationAttributeGroups;
    private Map<String, List<String>> variationAttributeValues;
    private Map<String, List<Boolean>> isDisplayVariationAttributes;
}
