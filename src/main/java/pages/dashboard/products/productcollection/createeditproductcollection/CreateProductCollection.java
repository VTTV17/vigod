package pages.dashboard.products.productcollection.createeditproductcollection;

import api.dashboard.products.APIAllProducts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.home.HomePage;
import pages.dashboard.products.productcollection.productcollectionmanagement.ProductCollectionManagement;
import utilities.UICommonAction;
import utilities.data.DataGenerator;
import utilities.sort.SortData;

import java.text.ParseException;
import java.time.Duration;
import java.util.*;

public class CreateProductCollection extends HomePage {
    final static Logger logger = LogManager.getLogger(CreateProductCollection.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction common;
    CreateProductCollectionElement createCollectionUI;
    ProductCollectionManagement productCollectionManagement;
    DataGenerator generator;
    public static Map<String, Integer> productPriorityMap = new HashMap<>();
    public static int productSelectedNumber = 0;

    public CreateProductCollection(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonAction(driver);
        createCollectionUI = new CreateProductCollectionElement(driver);
        generator = new DataGenerator();
        PageFactory.initElements(driver, this);
    }

    public CreateProductCollection navigate(String languageDashboard) {
        waitTillSpinnerDisappear();
        navigateToPage("Products", "Product Collections");
        HomePage home = new HomePage(driver);
        home.selectLanguage(languageDashboard);
        home.hideFacebookBubble();
        productCollectionManagement = new ProductCollectionManagement(driver);
        return productCollectionManagement.clickOnCreateCollection();
    }

    public CreateProductCollection inputCollectionName(String collectioName) {
        common.inputText(createCollectionUI.COLLECTION_NAME_INPUT, collectioName);
        logger.info("Input collection name: " + collectioName);
        return this;
    }

    public CreateProductCollection uploadImages(String... fileNames) {
        common.uploadMultipleFile(createCollectionUI.IMAGE_INPUT, "productcollection_images", fileNames);
        logger.info("Upload multiple file: " + Arrays.toString(fileNames));
        return this;
    }

    /**
     * @param collectionType: Manual, Automated (ignore case)
     * @return CreateProductCollection
     * @throws Exception if param does not match.
     */
    public CreateProductCollection selectCollectionType(String collectionType) throws Exception {
        if (collectionType.equalsIgnoreCase("Manual")) {
            common.checkTheCheckBoxOrRadio(createCollectionUI.MANUAL_RADIO_VALUE, createCollectionUI.MANUAL_RADIO_ACTION);
        } else if (collectionType.equalsIgnoreCase("Automated")) {
            common.checkTheCheckBoxOrRadio(createCollectionUI.AUTOMATED_RADIO_VALUE, createCollectionUI.AUTOMATED_RADIO_ACTION);
        } else {
            throw new Exception("Input value does not match any of the accepted values: Manual/Automated");
        }
        logger.info("Select collection type: " + collectionType);
        return this;
    }

    public CreateProductCollection clickOnSelectProduct() {
        common.clickElement(createCollectionUI.SELECT_PRODUCT_BTN);
        logger.info("Click on Select product button.");
        return this;
    }

    public CreateProductCollection inputSearchKeyword(String keyword) {
        common.inputText(createCollectionUI.SEARCH_FOR_PRODUCT_INPUT, keyword);
        logger.info("Search with keyword: " + keyword);
        return this;
    }

    public CreateProductCollection selectAllProductInCurrentPage() {
        common.checkTheCheckBoxOrRadio(createCollectionUI.SELECT_ALL_CBX_VALUE, createCollectionUI.SELECT_ALL_CBX_ACTION);
        logger.info("Select all producr in current page");
        return this;
    }

    public CreateProductCollection clickOnOKBTN() {
        common.clickElement(createCollectionUI.OK_BTN);
        logger.info("Click on OK button");
        return this;
    }

    public CreateProductCollection selectProductWithKeyword(String... keywords) {
        if (keywords.length == 0) {
            logger.info("No select product.");
            return this;
        }
        clickOnSelectProduct();
        waitTillLoadingDotsDisappear();
        for (String productName : keywords) {
            inputSearchKeyword(productName);
            waitTillSpinnerDisappear();
            selectAllProductInCurrentPage();
        }
        clickOnOKBTN();
        productSelectedNumber = createCollectionUI.PRODUCT_NAME_LIST.size();
        logger.info("Select product: " + Arrays.toString(keywords));
        return this;
    }

    /**
     * @param conditionType: All conditions, Any condition (ignore case)
     * @return CreateProductCollection
     * @throws Exception if conditionType not match
     */
    public CreateProductCollection selectConditionType(String conditionType) throws Exception {
        if (conditionType.equalsIgnoreCase("All conditions")) {
            common.checkTheCheckBoxOrRadio(createCollectionUI.ALL_CONDITION_RADIO_VALUE, createCollectionUI.ALL_CONDITION_RADIO_ACTION);
        } else if (conditionType.equalsIgnoreCase("Any condition")) {
            common.checkTheCheckBoxOrRadio(createCollectionUI.ANY_CONDITION_RADIO_VALUE, createCollectionUI.ANY_CONDITION_RADIO_ACTION);
        } else {
            throw new Exception("Input value does not match any of the accepted values: All conditions/Any condition");
        }
        return this;
    }

    /**
     *
     * @param isInputAllProduct input priority for all product
     * @param canInputDuplicate can input same priority
     * @return
     */
    public Map<String, Integer> inputPriority(boolean isInputAllProduct, boolean canInputDuplicate) {
        Map<String, Integer> productPriorityMap = new HashMap<>();
        common.sleepInMiliSecond(1000);
        int productListSize = createCollectionUI.PRODUCT_NAME_LIST.size();
        List<Integer> priorityList;
        if (isInputAllProduct) {
            if (canInputDuplicate) {
                priorityList = generator.randomListNumberCanDuplicate(productListSize);
            } else {
                priorityList = generator.randomListNumberWithNoDuplicate(productListSize);
            }
        } else {
            if (canInputDuplicate) {
                priorityList = generator.randomListNumberCanDuplicate(productListSize - 2);
            } else {
                priorityList = generator.randomListNumberWithNoDuplicate(productListSize - 2);
            }
        }
        for (int i = 0; i < productListSize; i++) {
            if (i < priorityList.size()) {
                common.inputText(createCollectionUI.PRIORITIES_INPUT.get(i), String.valueOf(priorityList.get(i)));
                productPriorityMap.put(common.getText(createCollectionUI.PRODUCT_NAME_LIST.get(i)).toLowerCase(), priorityList.get(i));
            } else {
                productPriorityMap.put(common.getText(createCollectionUI.PRODUCT_NAME_LIST.get(i)).toLowerCase(), priorityList.size());
            }
        }
        logger.info("Input product priority: " + productPriorityMap);
        return productPriorityMap;
    }
    public Map<String, Integer> getProductPriorityMapBefore() {
        Map<String, Integer> productPriorityMap = new HashMap<>();
//        waitTillSpinnerDisappear();
//        common.sleepInMiliSecond(1000);
        int productListSize = createCollectionUI.PRODUCT_NAME_LIST.size();
        for (int i = 0; i < productListSize; i++) {
            String priority = common.getElementAttribute(createCollectionUI.PRIORITIES_INPUT.get(i),"value");
            if (priority!="") {
                productPriorityMap.put(common.getText(createCollectionUI.PRODUCT_NAME_LIST.get(i)).toLowerCase(),Integer.parseInt(priority));
            } else {
                productPriorityMap.put(common.getText(createCollectionUI.PRODUCT_NAME_LIST.get(i)).toLowerCase(), productListSize);
            }
        }
        return productPriorityMap;
    }
    /**
     * @param conditions: fortmat: condition-operator-value.
     * @param: condition:"Product title","Product price".
     * @param:operator for product title: "starts with", "ends with", "is equal to".
     * Operator for product price: "is greater than", "is less than", "is equal to"
     * @param:Example: Product title-contains-coffee
     * @param hasAvailable: has condition before, then add more condition
     */
    public CreateProductCollection selectCondition(boolean hasAvailable, String... conditions) {
        for (int i = 0; i < conditions.length; i++) {
            String[] conditionContent = conditions[i].split("-");
            String conditionValueInputed = common.getText(createCollectionUI.CONDITION_VALUE_INPUT.get(i));
            if (hasAvailable && !conditionValueInputed.equals("")){
                common.clickElement(createCollectionUI.ADD_MORE_CONDITION_BTN);
                continue;
            }
            for (int j = 0; j < 5; j++) {
                try {
                    common.selectByVisibleText(new CreateProductCollectionElement(driver).CONDITION_DROPDOWN.get(i), conditionContent[0].trim());
                    break;
                } catch (StaleElementReferenceException ex) {
                    logger.debug("StaleElementReferenceException caught when selecting condition \n" + ex);
                }
            }
            for (int j = 0; j < 5; j++) {
                try {
                    common.selectByVisibleText(new CreateProductCollectionElement(driver).OPERATOR_DROPDOWN.get(i), conditionContent[1].trim());
                    break;
                } catch (StaleElementReferenceException ex) {
                    logger.debug("StaleElementReferenceException caught when selecting operator \n" + ex);
                }
            }

            common.inputText(createCollectionUI.CONDITION_VALUE_INPUT.get(i), conditionContent[2].trim());
            if (i < conditions.length - 1) {
                common.clickElement(createCollectionUI.ADD_MORE_CONDITION_BTN);
            }
            logger.info("Input condition: %s".formatted(conditions[i]));
        }
        common.clickElement(createCollectionUI.AUTOMATED_RADIO_ACTION);//click outside
        return this;
    }

    public CreateProductCollection clickOnSaveBTN() {
        common.clickElement(createCollectionUI.SAVE_BTN);
        logger.info("Click on Save button");
        return this;
    }

    public ProductCollectionManagement clickOnClose() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        common.clickElement(wait.until(ExpectedConditions.visibilityOf(createCollectionUI.CLOSE_BTN)));
        logger.info("Click on Close button");
        return new ProductCollectionManagement(driver);
    }

    public ProductCollectionManagement createManualCollectionWithoutSEO_NoPriority(String collectionName, String[] productList) throws Exception {
        inputCollectionName(collectionName);
        uploadImages("AoG.png");
        selectCollectionType("Manual");
        selectProductWithKeyword(productList);
        clickOnSaveBTN();
        logger.info("Create manual collection without SEO, no priority successfully.");
        return clickOnClose();
    }

    public ProductCollectionManagement createManualCollectionWithoutSEO_HasPriority(String collectionName, String[] productList, boolean isSetPriorityForAll, boolean canSetDuplicatePriority) throws Exception {
        inputCollectionName(collectionName);
        uploadImages("AoG.png");
        selectCollectionType("Manual");
        selectProductWithKeyword(productList);
        productPriorityMap = inputPriority(isSetPriorityForAll, canSetDuplicatePriority);
        clickOnSaveBTN(); //click outside
        clickOnSaveBTN();
        logger.info("Create manual collection without SEO, has priority successfully.");
        return clickOnClose();
    }

    public ProductCollectionManagement createManualCollectionWithSEO(String collectionName, String[] productList, String SEOTitle, String SEODescription, String SEOKeyword, String SEOUrl) throws Exception {
        inputCollectionName(collectionName);
        uploadImages("AoG.png");
        selectCollectionType("Manual");
        selectProductWithKeyword(productList);
        inputSEOInfo(SEOTitle, SEODescription, SEOKeyword, SEOUrl);
        clickOnSaveBTN();
        logger.info("Create manual collection with SEO info successfully.");
        return clickOnClose();
    }

    public List<String> sortProductListByPriority(Map<String, Integer> productPriorityMap) {
        Map<String, Integer> sortedMap = SortData.sortMapByValue(productPriorityMap);
        logger.info("Sort product list by priority" + sortedMap.keySet().stream().toList());
        return sortedMap.keySet().stream().toList();
    }

    public static List<String> sortProductListByPriorityAndUpdatedDate(Map<String, Integer> productPriorityMap, String storeID, String token, int collectionID) throws ParseException {
        Map<String, Integer> sortedMap = SortData.sortMapByValue(productPriorityMap);
        List<String> sortedList = new ArrayList<>();
        List<Integer> values = sortedMap.values().stream().toList();
        Map<String, Date> productUpdatedMap = new HashMap<>();
        APIAllProducts apiAllProducts = new APIAllProducts();
        for (int i = 0; i < values.size(); i++) {
            String productKey1 = sortedMap.keySet().toArray()[i].toString();
            String productKey2 ;
            int value1 = values.get(i);
            int value2;
            if (i == values.size() - 1) {
                value2 = values.get(i - 1);
                productKey2 = sortedMap.keySet().toArray()[i-1].toString();
            } else {
                value2 = values.get(i + 1);
                productKey2 = sortedMap.keySet().toArray()[i+1].toString();
            }
            if (value1 == value2) {
                productUpdatedMap.putAll(apiAllProducts.getProductCreatedDateMapByProductName(storeID, token, collectionID, productKey1));
                productUpdatedMap.putAll(apiAllProducts.getProductCreatedDateMapByProductName(storeID, token, collectionID, productKey2));
                if(i == values.size()-1){
                    sortedList.addAll(apiAllProducts.getProductListCollection_SortNewest(productUpdatedMap));
                }
            }else if (productUpdatedMap.isEmpty()) {
                sortedList.add(productKey1);
            } else {
                sortedList.addAll(apiAllProducts.getProductListCollection_SortNewest(productUpdatedMap));
                productUpdatedMap = new HashMap<>();
            }

        }
        logger.info("Get sorted list by priority and created date: " + sortedList);
        return sortedList;
    }

    public CreateProductCollection inputSEOTitle(String SEOTitle) {
        common.inputText(createCollectionUI.SEO_TITLE, SEOTitle);
        logger.info("Input SEO title: " + SEOTitle);
        return this;
    }

    public CreateProductCollection inputSEODescription(String SEODes) {
        common.inputText(createCollectionUI.SEO_DESCRIPTION, SEODes);
        logger.info("Input SEO description: " + SEODes);
        return this;
    }

    public CreateProductCollection inputSEOKeywords(String SEOKeywords) {
        common.inputText(createCollectionUI.SEO_KEYWORD, SEOKeywords);
        logger.info("Input SEO keywords: " + SEOKeywords);
        return this;
    }

    public CreateProductCollection inputSEOUrl(String SEOUrl) {
        common.inputText(createCollectionUI.SEO_URL, SEOUrl);
        logger.info("Input SEO url: " + SEOUrl);
        return this;
    }

    public CreateProductCollection inputSEOInfo(String SEOTitle, String SEODes, String SEOKeyword, String SEOUrl) {
        inputSEOTitle(SEOTitle);
        inputSEODescription(SEODes);
        inputSEOKeywords(SEOKeyword);
        inputSEOUrl(SEOUrl);
        return this;
    }

    /**
     * @param collectionName
     * @param conditionType: "All conditions" or "Any condition
     * @param conditions
     * @return
     * @throws Exception
     */
    public ProductCollectionManagement createProductAutomationCollectionWithoutSEO(String collectionName, String conditionType, String... conditions) throws Exception {
        inputCollectionName(collectionName);
        uploadImages("AoG.png");
        selectCollectionType("Automated");
        selectConditionType(conditionType);
        selectCondition(false,conditions);
        clickOnSaveBTN();
        logger.info("Create Automated collection without SEO info successfully.");
        return clickOnClose();
    }
    /**
     * @param token
     * @param storeId
     * @param condition: (Product price/Product title)-(is greater than/less than/is equal to/starts with/ends with/contains)-(value)
     * @return Map: productExpectedList, CountItem
     * @throws ParseException
     */
    public Map productsBelongCollectionExpected_OneCondition(String token, String storeId, String condition) throws ParseException {
        APIAllProducts apiAllProducts = new APIAllProducts();
        String conditionField = condition.split("-")[0];
        String operater = condition.split("-")[1];
        String value = condition.split("-")[2];
        int countItemExpected = 0;
        Map productCollectionExpected = new HashMap<>();
        List<String> productExpectedList = new ArrayList<>();
        if (conditionField.equalsIgnoreCase("Product title")) {
            Map productCollection = apiAllProducts.getMapOfProductCreateDateMatchTitleCondition(token, storeId, operater, value);
            productExpectedList = apiAllProducts.getProductListCollection_SortNewest((Map) productCollection.get("productCreatedDateMap"));
            countItemExpected = (int) productCollection.get("CountItem");
        } else if (conditionField.equalsIgnoreCase("Product price")) {
            Map productCollection = apiAllProducts.getProductMatchPriceCondition(token, storeId, operater, Long.parseLong(value));
            productExpectedList = apiAllProducts.getProductListCollection_SortNewest((Map) productCollection.get("productCreatedDateMap"));
            countItemExpected = (int) productCollection.get("CountItem");
        }
        productCollectionExpected.put("ExpectedList", productExpectedList);
        productCollectionExpected.put("CountItem", countItemExpected);
        logger.info("Get product match 1 condition: "+productCollectionExpected);
        return productCollectionExpected;
    }

    /**
     * @param token
     * @param storeId
     * @param conditionType: All conditions, Any condition
     * @param conditions:    (Product price/Product title)-(is greater than/less than/is equal to/starts with/ends with/contains)-(value)
     * @return Map with keys: productExpectedList, CountItem
     * @throws ParseException
     */
    public Map productsBelongCollectionExpected_MultipleCondition(String token, String storeId, String conditionType, String... conditions) throws ParseException {
        APIAllProducts apiAllProducts = new APIAllProducts();
        int countItemExpected = 0;
        Map mergeProductMap = new HashMap<>();
        Map mergeProductCountItemMap = new HashMap<>();
        Map compareProductMap = new HashMap<>();
        Map compareCountItemMap = new HashMap<>();
        for (String condition : conditions) {
            String conditionField = condition.split("-")[0];
            String operater = condition.split("-")[1];
            String value = condition.split("-")[2];
            Map productCreatedDateMap = new HashMap();
            Map productCountItemMap = new HashMap();
            if (conditionField.equalsIgnoreCase("Product title")) {
                Map productCollection = apiAllProducts.getMapOfProductCreateDateMatchTitleCondition(token, storeId, operater, value);
                productCreatedDateMap = (Map) productCollection.get("productCreatedDateMap");
                productCountItemMap = (Map) productCollection.get("productCountItemMap");
            } else if (conditionField.equalsIgnoreCase("Product price")) {
                Map productCollection = apiAllProducts.getProductMatchPriceCondition(token, storeId, operater, Long.parseLong(value));
                productCreatedDateMap = (Map) productCollection.get("productCreatedDateMap");
                productCountItemMap = (Map) productCollection.get("productCountItemMap");
            }
            if (conditionType.equalsIgnoreCase("Any condition")) {
                mergeProductMap.putAll(productCreatedDateMap);
                mergeProductCountItemMap.putAll(productCountItemMap);
            } else if (conditionType.equalsIgnoreCase("All conditions")) {
                if (compareProductMap.isEmpty()) {
                    compareProductMap.putAll(productCreatedDateMap);
                    compareCountItemMap.putAll(productCountItemMap);
                } else {
                    for (Object key : productCreatedDateMap.keySet()) {
                        if (compareProductMap.containsKey(key)) {
                            mergeProductMap.put(key, productCreatedDateMap.get(key));
                            mergeProductCountItemMap.put(key, productCountItemMap.get(key));
                        }
                    }
                }
            }
        }
        Collection<Integer> values = mergeProductCountItemMap.values();
        System.out.println("values: " + values);
        for (int v : values) {
            countItemExpected = countItemExpected + v;
        }
        List<String> productExpectedList = apiAllProducts.getProductListCollection_SortNewest(mergeProductMap);
        Map productCollectInfoMap = new HashMap<>();
        productCollectInfoMap.put("productExpectedList", productExpectedList);
        productCollectInfoMap.put("CountItem", countItemExpected);
        logger.info("Get product match multiple condition: "+productCollectInfoMap);
        return productCollectInfoMap;
    }
}
