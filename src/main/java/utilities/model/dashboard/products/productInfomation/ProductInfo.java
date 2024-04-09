package utilities.model.dashboard.products.productInfomation;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProductInfo {
    private int productID;
    private Map<String, String> variationNameMap;
    private Map<String, List<String>> variationListMap;
    private List<Long> productListingPrice;
    private List<Long> productSellingPrice;
    private List<Long> productCostPrice;
    private boolean hasModel;
    private boolean manageInventoryByIMEI;
    private List<String> variationModelList;
    private List<String> barcodeList;
    private List<String> variationStatus;
    private Map<String, String> defaultProductNameMap;
    private Map<String, Map<String, String>> productNameMap;
    private Map<String, String> defaultProductDescriptionMap;
    private Map<String, Map<String, String>> productDescriptionMap;
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
