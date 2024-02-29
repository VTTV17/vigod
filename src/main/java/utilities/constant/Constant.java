package utilities.constant;

import utilities.utils.PropertiesUtil;

public class Constant {
    public static final String TEXT_101_CHAR="Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been thess";
    public static final String TEXT_201_CHAR="Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type an";
    public static final String TEXT_326_CHAR="Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic types";
    public static final String SERVICES_MENU_ITEM_NAME = "Services";
    public static final String RESERVATIONS_MENU_ITEM_NAME = "Reservations";
    public static final String TEXT_256_CHAR = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has sur";
    public static final String BUYLINK_MENU_ITEM_NAME = "Buy Link";
    public static final String MARKETING_MENU_ITEM_NAME = "Marketing";
    public static final int PAGE_SIZE_SF_COLLECTION = 20;
    public static final String VIETNAM;
    public static final String PRODUCT_PRICE;
    public static final String CONTAINS;
    public static final String PRODUCT_TITLE;
    public static final String EQUAL_TO_TITLE;
    public static final String STARTS_WITH;
    public static final String ENDS_WITH;
    public static final String GREATER_THAN;
    public static final String LESS_THAN;
    public static final String EQUAL_TO_PRICE;
    public static final String ALL_CONDITION;
    public static final String ANY_CONDITION;
    public static final String MANUAL_OPTION;
    public static final String AUTOMATED_OPTION;
    public static final String MANUALLY_MODE;
    public static final String AUTOMATED_MODE;
    public static final String PRODUCT_TYPE;
    public static final String SERVICE_TYPE;
    public static final String SERVICE_TITLE;
    static {
        try {
            VIETNAM = PropertiesUtil.getPropertiesValueBySFLang("buyerApp.country.vietnam");
            PRODUCT_TITLE = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.conditionOptions.productTitleTxt");
            PRODUCT_PRICE = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.conditionOptions.productPriceTxt");
            CONTAINS = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.containsTxt");
            EQUAL_TO_TITLE = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.productTitleIsEqualToTxt");
            STARTS_WITH = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.startsWithTxt");
            ENDS_WITH = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.endsWithTxt");
            GREATER_THAN = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.isGeaterThanTxt");
            LESS_THAN = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.isLessThanTxt");
            EQUAL_TO_PRICE = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.productPriceIsEqualToTxt");
            ALL_CONDITION = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.allConditionsTxt");
            ANY_CONDITION = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.anyConditionTxt");
            MANUAL_OPTION = PropertiesUtil.getPropertiesValueByDBLang("services.serviceCollections.create.collectionType.manual");
            AUTOMATED_OPTION = PropertiesUtil.getPropertiesValueByDBLang("services.serviceCollections.create.collectionType.automated");
            AUTOMATED_MODE = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.automatedModeTxt");
            MANUALLY_MODE = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.manuallyModeTxt");
            PRODUCT_TYPE = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.productTypeTxt");
            SERVICE_TYPE = PropertiesUtil.getPropertiesValueByDBLang("services.serviceCollections.management.table.serviceTypeTxt");
            SERVICE_TITLE = PropertiesUtil.getPropertiesValueByDBLang("");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
