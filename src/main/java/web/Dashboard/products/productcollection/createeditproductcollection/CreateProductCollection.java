package web.Dashboard.products.productcollection.createeditproductcollection;

import api.Seller.products.all_products.APIAllProducts;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.commons.UICommonAction;
import utilities.constant.Constant;
import utilities.data.DataGenerator;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.sort.SortData;
import utilities.utils.PropertiesUtil;
import web.Dashboard.home.HomePage;
import web.Dashboard.products.productcollection.productcollectionmanagement.ProductCollectionManagement;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CreateProductCollection extends CreateProductCollectionElement {
    final static Logger logger = LogManager.getLogger(CreateProductCollection.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction common;
    ProductCollectionManagement productCollectionManagement;
    HomePage home;
    DataGenerator generator;
    public static Map<String, Integer> productPriorityMap = new HashMap<>();
    public static int productSelectedNumber = 0;
    String productTitleTxt;
    String productPriceTxt;
    String containsOperateTxt;
    String equalToOperateProductTiteTxt;
    String equalToOperateProductPriceTxt;

    String startWithOperateTxt;
    String endsWithOperateTxt;
    String greaterThanTxt;
    String lessThanTxt;
    String allConditionTxt;
    String anyConditionTxt;

    public CreateProductCollection(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonAction(driver);
        generator = new DataGenerator();
        home = new HomePage(driver);
        try {
            productTitleTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.conditionOptions.productTitleTxt");
            productPriceTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.conditionOptions.productPriceTxt");
            containsOperateTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.containsTxt");
            equalToOperateProductTiteTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.productTitleIsEqualToTxt");
            startWithOperateTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.startsWithTxt");
            endsWithOperateTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.endsWithTxt");
            greaterThanTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.isGeaterThanTxt");
            lessThanTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.isLessThanTxt");
            equalToOperateProductPriceTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.productPriceIsEqualToTxt");
            allConditionTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.allConditionsTxt");
            anyConditionTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.anyConditionTxt");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        PageFactory.initElements(driver, this);
    }


    public CreateProductCollection navigate(String languageDashboard) throws Exception {
        home.waitTillSpinnerDisappear1();
//        home.navigateToPage("Products", "Product Collections");
        home.selectLanguage(languageDashboard);
        home.hideFacebookBubble();
        productCollectionManagement = new ProductCollectionManagement(driver);
        return productCollectionManagement.navigateToProductCollectionManagementPage().clickOnCreateCollection();
    }

    public CreateProductCollection inputCollectionName(String collectioName) {
        common.sendKeys(loc_txtCollectionName, collectioName);
        logger.info("Input collection name: " + collectioName);
        return this;
    }

    public CreateProductCollection uploadImages(String... fileNames) {
        common.uploadMultipleFile(common.getElement(loc_txtUploadImage), "productcollection_images", fileNames);
        logger.info("Upload multiple file: " + Arrays.toString(fileNames));
        return this;
    }

    /**
     * @param collectionType: Manual, Automated (ignore case)
     * @return CreateProductCollection
     * @throws Exception if param does not match.
     */
    public CreateProductCollection selectCollectionType(String collectionType) throws Exception {
        if (collectionType.equalsIgnoreCase(Constant.MANUAL_OPTION)) {
            common.checkTheCheckBoxOrRadio(loc_chbManualValue, loc_chbManualAction);
        } else if (collectionType.equalsIgnoreCase(Constant.AUTOMATED_OPTION)) {
            common.checkTheCheckBoxOrRadio(loc_chbAutomatedValue, loc_chbAutomatedAction);
        } else {
            throw new Exception("Input value does not match any of the accepted values: Manual/Automated");
        }
        logger.info("Select collection type: " + collectionType);
        return this;
    }

    public CreateProductCollection clickOnSelectProduct() {
        common.click(loc_btnSelectProduct);
        logger.info("Click on Select product button.");
        return this;
    }

    public CreateProductCollection inputSearchKeyword(String keyword) {
        common.sendKeys(loc_dlgSelectProduct_txtSearchForProduct, keyword);
        logger.info("Search with keyword: " + keyword);
        return this;
    }

    public CreateProductCollection selectAllProductInCurrentPage() {
        common.checkTheCheckBoxOrRadio(loc_dlgSelectProduct_cbxSelectAllValue, loc_dlgSelectProduct_cbxSelectAllAction);
        logger.info("Select all producr in current page");
        return this;
    }

    public CreateProductCollection clickOnOKBTN() {
        common.click(loc_dlgSelectProduct_btnOK);
        logger.info("Click on OK button");
        return this;
    }

    public CreateProductCollection clickOnCancelBTNONSelectProductModal() {
        common.click(loc_dlgSelectProduct_btnCancel);
        logger.info("Click on Cancel button on select product modal.");
        return this;
    }

    public CreateProductCollection selectProductWithKeyword(String... keywords) {
        if (keywords.length == 0) {
            logger.info("No select product.");
            return this;
        }
        clickOnSelectProduct();
        home.waitTillLoadingDotsDisappear();
        for (String productName : keywords) {
            inputSearchKeyword(productName);
            common.sleepInMiliSecond(1000, "Wait loading display.");
            home.waitTillSpinnerDisappear1();
            selectAllProductInCurrentPage();
        }
        clickOnOKBTN();
        productSelectedNumber = common.getElements(loc_lst_lblProductName).size();
        logger.info("Select product: " + Arrays.toString(keywords));
        return this;
    }

    /**
     * @param conditionType: All conditions, Any condition (ignore case)
     * @return CreateProductCollection
     * @throws Exception if conditionType not match
     */
    public CreateProductCollection selectConditionType(String conditionType) throws Exception {
        if (StringUtils.equalsIgnoreCase(conditionType, allConditionTxt)) {
            common.checkTheCheckBoxOrRadio(loc_chbAllConditionValue, loc_chbAllConditionAction);
        } else if (StringUtils.equalsIgnoreCase(conditionType, anyConditionTxt)) {
            common.checkTheCheckBoxOrRadio(loc_chbAnyConditionValue, loc_chbAnyConditionAction);
        } else {
            throw new Exception("Input value does not match any of the accepted values: all conditions/any condition");
        }
        return this;
    }

    /**
     * @param isInputAllProduct input priority for all product
     * @param canInputDuplicate can input same priority
     * @return
     */
    public Map<String, Integer> inputPriority(boolean isInputAllProduct, boolean canInputDuplicate) {
        Map<String, Integer> productPriorityMap = new HashMap<>();
        common.sleepInMiliSecond(1000);
        int productListSize = common.getElements(loc_lst_lblProductName).size();
        List<Integer> priorityList;
        if (isInputAllProduct) {
            if (canInputDuplicate) {
                priorityList = generator.randomListNumberCanDuplicate(productListSize);
            } else {
                priorityList = generator.randomListNumberWithNoDuplicate(productListSize);
            }
        } else {
            int random = generator.generatNumberInBound(1, productListSize - 1);
            if (canInputDuplicate) {
                priorityList = generator.randomListNumberCanDuplicate(productListSize - random);
            } else {
                priorityList = generator.randomListNumberWithNoDuplicate(productListSize - random);
            }
        }
        for (int i = 0; i < productListSize; i++) {
            if (i < priorityList.size()) {
                common.sleepInMiliSecond(1000);
                common.sendKeys(loc_lst_txtPriority, i, String.valueOf(priorityList.get(i)));
                productPriorityMap.put(common.getText(common.getElements(loc_lst_lblProductName).get(i)).toLowerCase(), priorityList.get(i));
            } else {
                productPriorityMap.put(common.getText(common.getElements(loc_lst_lblProductName).get(i)).toLowerCase(), priorityList.size());
            }
        }
        logger.info("Input product priority: " + productPriorityMap);
        return productPriorityMap;
    }

    public Map<String, Integer> getProductPriorityMapBefore() {
        Map<String, Integer> productPriorityMap = new HashMap<>();
//        waitTillSpinnerDisappear();
//        common.sleepInMiliSecond(1000);
        int productListSize = common.getElements(loc_lst_lblProductName).size();
        for (int i = 0; i < productListSize; i++) {
            String priority = common.getAttribute(loc_lst_txtPriority, i, "value");
            if (priority.isEmpty()) {
                productPriorityMap.put(common.getText(common.getElements(loc_lst_lblProductName).get(i)).toLowerCase(), productListSize);

            } else {
                productPriorityMap.put(common.getText(common.getElements(loc_lst_lblProductName).get(i)).toLowerCase(), Integer.parseInt(priority));

            }
        }
        return productPriorityMap;
    }

    /**
     * @param conditions:   fortmat: condition-operator-value. bao gom condition co san va new condition
     * @param hasAvailable: has condition before, then add more condition
     * @param: condition:"Product title","Product price".
     * @param:operator for product title: "starts with", "ends with", "is equal to".
     * Operator for product price: "is greater than", "is less than", "is equal to"
     * @param:Example: Product title-contains-coffee
     */
    public CreateProductCollection selectCondition(boolean hasAvailable, String... conditions) {
        for (int i = 0; i < conditions.length; i++) {
            String[] conditionContent = conditions[i].split("-");
            String conditionValueInputed = common.getText(loc_lst_txtConditionValue, i);
            if (hasAvailable && !conditionValueInputed.isEmpty()) {
                common.click(loc_btnAddMoreCondition);
                continue;
            }
            for (int j = 0; j < 5; j++) {
                try {
                    common.selectByVisibleText(common.getElements(loc_lst_ddlCondition).get(i), conditionContent[0].trim());
                    break;
                } catch (StaleElementReferenceException ex) {
                    logger.debug("StaleElementReferenceException caught when selecting condition \n" + ex);
                }
            }
            for (int j = 0; j < 5; j++) {
                try {
                    common.selectByVisibleText(common.getElements(loc_lst_ddlOperator).get(i), conditionContent[1].trim());
                    break;
                } catch (StaleElementReferenceException ex) {
                    logger.debug("StaleElementReferenceException caught when selecting operator \n" + ex);
                }
            }

            common.sendKeys(loc_lst_txtConditionValue, i, conditionContent[2].trim());
            if (i < conditions.length - 1) {
                common.click(loc_btnAddMoreCondition);
            }
            logger.info("Input condition: %s".formatted(conditions[i]));
        }
        common.click(loc_chbAutomatedAction);//click outside
        return this;
    }

    public CreateProductCollection clickOnSaveBTN() {
        common.click(loc_btnSave);
        logger.info("Click on Save button");
        return this;
    }

    public ProductCollectionManagement clickOnClose() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        common.clickElement(wait.until(ExpectedConditions.visibilityOf(common.getElement(loc_btnClose))));
        logger.info("Click on Close button");
        return new ProductCollectionManagement(driver);
    }

    public ProductCollectionManagement createManualCollectionWithoutSEO_NoPriority(String collectionName, String[] productList) throws Exception {
        inputCollectionName(collectionName);
        uploadImages("AoG.png");
        selectCollectionType(Constant.MANUAL_OPTION);
        selectProductWithKeyword(productList);
        clickOnSaveBTN();
        logger.info("Create manual collection without SEO, no priority successfully.");
        clickOnClose();
        home.waitTillSpinnerDisappear1();
        return new ProductCollectionManagement(driver);
    }

    public ProductCollectionManagement createManualCollectionWithoutSEO_HasPriority(String collectionName, String[] productList, boolean isSetPriorityForAll, boolean canSetDuplicatePriority) throws Exception {
        inputCollectionName(collectionName);
        uploadImages("AoG.png");
        selectCollectionType(Constant.MANUAL_OPTION);
        selectProductWithKeyword(productList);
        productPriorityMap = inputPriority(isSetPriorityForAll, canSetDuplicatePriority);
        clickOnSaveBTN(); //click outside
        clickOnSaveBTN();
        clickOnClose();
        logger.info("Create manual collection without SEO, has priority successfully.");
        home.waitTillSpinnerDisappear1();
        return new ProductCollectionManagement(driver);
    }

    public ProductCollectionManagement createManualCollectionWithSEO(String collectionName, String[] productList, String SEOTitle, String SEODescription, String SEOKeyword, String SEOUrl) throws Exception {
        inputCollectionName(collectionName);
        uploadImages("AoG.png");
        selectCollectionType(Constant.MANUAL_OPTION);
        selectProductWithKeyword(productList);
        inputSEOInfo(SEOTitle, SEODescription, SEOKeyword, SEOUrl);
        clickOnSaveBTN();
        logger.info("Create manual collection with SEO info successfully.");
        clickOnClose();
        home.waitTillSpinnerDisappear1();
        return new ProductCollectionManagement(driver);
    }

    public List<String> sortProductListByPriority(Map<String, Integer> productPriorityMap) {
        Map<String, Integer> sortedMap = SortData.sortMapByValue(productPriorityMap);
        logger.info("Sort product list by priority" + sortedMap.keySet().stream().toList());
        return sortedMap.keySet().stream().toList();
    }

    public static List<String> sortProductListByPriorityAndUpdatedDate(LoginInformation loginInformation, Map<String, Integer> productPriorityMap, int collectionID) throws ParseException {
        logger.debug("Sort start.");
        Map<String, Integer> sortedMap = SortData.sortMapByValue(productPriorityMap);
        List<String> sortedList = new ArrayList<>();
        List<Integer> values = sortedMap.values().stream().toList();
        Map<String, Date> productUpdatedMap = new HashMap<>();
        APIAllProducts apiAllProducts = new APIAllProducts(loginInformation);
        for (int i = 0; i < values.size(); i++) {
            String productKey1 = sortedMap.keySet().toArray()[i].toString();
            String productKey2;
            int value1 = values.get(i);
            int value2;
            if (i == values.size() - 1) {
                value2 = values.get(i - 1);
                productKey2 = sortedMap.keySet().toArray()[i - 1].toString();
            } else {
                value2 = values.get(i + 1);
                productKey2 = sortedMap.keySet().toArray()[i + 1].toString();
            }
            if (value1 == value2) {
                productUpdatedMap.putAll(apiAllProducts.getProductCreatedDateMapByProductName(collectionID, productKey1));
                productUpdatedMap.putAll(apiAllProducts.getProductCreatedDateMapByProductName(collectionID, productKey2));
                if (i == values.size() - 1) {
                    sortedList.addAll(apiAllProducts.getProductListCollection_SortNewest(productUpdatedMap));
                }
            } else if (productUpdatedMap.isEmpty()) {
                sortedList.add(productKey1);
            } else {
                sortedList.addAll(apiAllProducts.getProductListCollection_SortNewest(productUpdatedMap));
                productUpdatedMap = new HashMap<>();
            }

        }
        logger.debug("Get sorted list by priority and created date: " + sortedList);
        return sortedList;
    }

    public CreateProductCollection inputSEOTitle(String SEOTitle) {
        common.sendKeys(loc_txtSEOTitle, SEOTitle);
        logger.info("Input SEO title: " + SEOTitle);
        return this;
    }

    public CreateProductCollection inputSEODescription(String SEODes) {
        common.sendKeys(loc_txtSEODescription, SEODes);
        logger.info("Input SEO description: " + SEODes);
        return this;
    }

    public CreateProductCollection inputSEOKeywords(String SEOKeywords) {
        common.sendKeys(loc_txtSEOKeywords, SEOKeywords);
        logger.info("Input SEO keywords: " + SEOKeywords);
        return this;
    }

    public CreateProductCollection inputSEOUrl(String SEOUrl) {
        common.sendKeys(loc_txtSEOUrl, SEOUrl);
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
        selectCollectionType(Constant.AUTOMATED_OPTION);
        selectConditionType(conditionType);
        selectCondition(false, conditions);
        clickOnSaveBTN();
        logger.info("Create Automated collection without SEO info successfully.");
        clickOnClose();
        home.waitTillSpinnerDisappear1();
        return new ProductCollectionManagement(driver);
    }

    /**
     * @param condition: (Product price/Product title)-(is greater than/less than/is equal to/starts with/ends with/contains)-(value)
     * @return Map: productExpectedList, CountItem
     * @throws ParseException
     */
    public Map productsBelongCollectionExpected_OneCondition(LoginInformation loginInformation, String condition) throws Exception {
        APIAllProducts apiAllProducts = new APIAllProducts(loginInformation);
        String conditionField = condition.split("-")[0];
        String operater = condition.split("-")[1];
        String value = condition.split("-")[2];
        int countItemExpected = 0;
        Map productCollectionExpected = new HashMap<>();
        List<String> productExpectedList = new ArrayList<>();
        if (conditionField.equalsIgnoreCase(productTitleTxt)) {
            Map productCollection = apiAllProducts.getMapOfProductCreateDateMatchTitleCondition(operater, value);
            productExpectedList = apiAllProducts.getProductListCollection_SortNewest((Map) productCollection.get("productCreatedDateMap"));
            countItemExpected = (int) productCollection.get("CountItem");
        } else if (conditionField.equalsIgnoreCase(productPriceTxt)) {
            Map productCollection = apiAllProducts.getProductMatchPriceCondition(operater, Long.parseLong(value));
            productExpectedList = apiAllProducts.getProductListCollection_SortNewest((Map) productCollection.get("productCreatedDateMap"));
            countItemExpected = (int) productCollection.get("CountItem");
        }
        productCollectionExpected.put("ExpectedList", productExpectedList);
        productCollectionExpected.put("CountItem", countItemExpected);
        logger.info("Get product match 1 condition: " + productCollectionExpected);
        return productCollectionExpected;
    }

    /**
     * @param conditionType: All conditions, Any condition
     * @param conditions:    (Product price/Product title)-(is greater than/less than/is equal to/starts with/ends with/contains)-(value)
     * @return Map with keys: productExpectedList, CountItem
     * @throws ParseException
     */
    public Map productsBelongCollectionExpected_MultipleCondition(LoginInformation loginInformation, String conditionType, String... conditions) throws Exception {
        APIAllProducts apiAllProducts = new APIAllProducts(loginInformation);
        int countItemExpected = 0;
        Map mergeProductCountItemMap = new HashMap<>();
        Map compareProductMap = new HashMap<>();
        Map compareCountItemMap = new HashMap<>();
        Map mergeProductMap = new HashMap<>();
        for (int i = 0; i < conditions.length; i++) {
            String conditionField = conditions[i].split("-")[0];
            String operater = conditions[i].split("-")[1];
            String value = conditions[i].split("-")[2];
            Map productCreatedDateMap = new HashMap();
            Map productCountItemMap = new HashMap();
            if (conditionField.equalsIgnoreCase(Constant.PRODUCT_TITLE)) {
                Map productCollection = apiAllProducts.getMapOfProductCreateDateMatchTitleCondition(operater, value);
                productCreatedDateMap = (Map) productCollection.get("productCreatedDateMap");
                productCountItemMap = (Map) productCollection.get("productCountItemMap");
            } else if (conditionField.equalsIgnoreCase(Constant.PRODUCT_PRICE)) {
                Map productCollection = apiAllProducts.getProductMatchPriceCondition(operater, Long.parseLong(value));
                productCreatedDateMap = (Map) productCollection.get("productCreatedDateMap");
                productCountItemMap = (Map) productCollection.get("productCountItemMap");
            }
            if (conditionType.equalsIgnoreCase(Constant.ANY_CONDITION)) {
                compareProductMap.putAll(productCreatedDateMap);
                compareCountItemMap.putAll(productCountItemMap);
            } else if (conditionType.equalsIgnoreCase(Constant.ALL_CONDITION)) {
                if (i == 0) {
                    compareProductMap.putAll(productCreatedDateMap);
                    compareCountItemMap.putAll(productCountItemMap);
                } else {
                    for (Object key : productCreatedDateMap.keySet()) {
                        if (compareProductMap.containsKey(key)) {
                            mergeProductMap.put(key, productCreatedDateMap.get(key));
                            mergeProductCountItemMap.put(key, productCountItemMap.get(key));
                        }
                    }
                    compareProductMap = mergeProductMap;
                    compareCountItemMap = mergeProductCountItemMap;
                }
                mergeProductMap = new HashMap<>();
                mergeProductCountItemMap = new HashMap<>();
            }

        }
        Collection<Integer> values = compareCountItemMap.values();
        for (int v : values) {
            countItemExpected = countItemExpected + v;
        }
        List<String> productExpectedList = apiAllProducts.getProductListCollection_SortNewest(compareProductMap);
        Map productCollectInfoMap = new HashMap<>();
        productCollectInfoMap.put("productExpectedList", productExpectedList);
        productCollectInfoMap.put("CountItem", countItemExpected);
        logger.info("Get product match multiple condition: " + productCollectInfoMap);
        return productCollectInfoMap;
    }

    public void verifyTextOfPage() throws Exception {
        Assert.assertEquals(common.getText(loc_lblCreateProductCollectionTitle), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.pageTitle"));
        Assert.assertEquals(common.getText(loc_btnSave), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.saveBtn"));
        Assert.assertEquals(common.getText(loc_btnCancel), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.cancelBtn"));
        Assert.assertEquals(common.getText(loc_lblGeneralInformation), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.generalInformationTitle"));
        Assert.assertEquals(common.getText(loc_lblCollectionName), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.collectionNameLbl"));
        Assert.assertEquals(common.getText(loc_lblImages), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.imagesLbl"));
        Assert.assertEquals(common.getText(loc_lblDragAndDropPhoto), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.drapAndDropTxt"));
        Assert.assertEquals(common.getText(loc_lblCollectionType), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.collectionTypeTitle"));
        Assert.assertEquals(common.getText(loc_chbManualAction), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.manualOptionTxt"));
        Assert.assertEquals(common.getText(loc_lblManualDescription), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.manualDescription"));
        Assert.assertEquals(common.getText(loc_chbAutomatedAction), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automatedOptionTxt"));
        Assert.assertEquals(common.getText(loc_lblAutomatedDescription), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automatedDescription"));
        Assert.assertEquals(common.getText(loc_lblFilterSortOption), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.filterSortOptionTitle"));
        Assert.assertEquals(common.getText(loc_lblFilterOption), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.filterOptionLbl"));
        Assert.assertEquals(common.getText(loc_lblPriceRange), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.priceRangeTxt"));
        Assert.assertEquals(common.getText(loc_lblVariation), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.variationTxt"));
        Assert.assertEquals(common.getText(loc_lblPromotion), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.promotionTxt"));
        Assert.assertEquals(common.getText(loc_lblUnit), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.unitTxt"));
        Assert.assertEquals(common.getText(loc_lblBranch), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.branchTxt"));
        Assert.assertEquals(common.getText(loc_lblSortOption), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.sortOptionLbl"));
        Assert.assertEquals(common.getText(loc_lblPriceLowestToHighest), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.sortByPriceAscTxt"));
        Assert.assertEquals(common.getText(loc_lblPriceHighestToLowest), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.sortBypriceDescTxt"));
        Assert.assertEquals(common.getText(loc_lblNameAZ), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.sortByNameAZTxt"));
        Assert.assertEquals(common.getText(loc_lblNameZA), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.sortByNameZATxt"));
        Assert.assertEquals(common.getText(loc_lblOldestToNewest), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.sortByOldestToNewestTxt"));
        Assert.assertEquals(common.getText(loc_lblNewestToOldest), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.sortByNewestToOldestTxt"));
        Assert.assertEquals(common.getText(loc_lblBestSelling), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.sortByBestSellingTxt"));
        Assert.assertEquals(common.getText(loc_lblRatingLowestToHightest), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.sortByRatingAscTxt"));
        Assert.assertEquals(common.getText(loc_lblRatingHighestToLowest), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.sortByRatingDescTxt"));
        Assert.assertEquals(common.getText(loc_lblProductList), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.productListTitle"));
        Assert.assertEquals(common.getText(loc_btnSelectProduct), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.selectProductTxt"));
        Assert.assertEquals(common.getText(loc_lblNoProduct), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.noProductTxt"));
        Assert.assertEquals(common.getText(loc_lblSEOSetting), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.seoSettingsTitle"));
        Assert.assertEquals(common.getText(loc_lblLivePreview), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.livePreviewLbl"));
        Assert.assertEquals(common.getAttribute(loc_tltLivePreview, "data-original-title"), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.livePreviewTooltipTxt"));
        Assert.assertEquals(common.getText(loc_lblSEOTitle), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.seoTitleLbl"));
        Assert.assertEquals(common.getAttribute(loc_tltSEOTitle, "data-original-title"), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.seoTitleTooltipTxt"));
        Assert.assertEquals(common.getText(loc_lblSEODescription), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.seoDescriptionLbl"));
        Assert.assertEquals(common.getAttribute(loc_tltSEODescription, "data-original-title"), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.seoDescriptionTooltipTxt"));
        Assert.assertEquals(common.getText(loc_lblSEOKeywords), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.seoKeywordsLbl"));
        Assert.assertEquals(common.getAttribute(loc_tltSEOKeywords, "data-original-title"), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.seoKeywordsTooltipTxt"));
        Assert.assertEquals(common.getText(loc_lblUrlLink), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.urlLinkLbl"));
        clickOnSelectProduct();
        home.waitTillLoadingDotsDisappear();
        Assert.assertEquals(common.getText(loc_dlgSelectProduct_lblTitle), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.selectProductModal.selectProductTitle"));
        Assert.assertEquals(common.getAttribute(loc_dlgSelectProduct_txtSearchForProduct, "placeholder"), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.selectProductModal.searchProductHintTxt"));
        Assert.assertEquals(common.getText(loc_dlgSelectProduct_lblProductNameCol), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.selectProductModal.productNameTxt"));
        Assert.assertEquals(common.getText(loc_dlgSelectProduct_lblUnitCol), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.selectProductModal.unitTxt"));
        Assert.assertEquals(common.getText(loc_dlgSelectProduct_lblCostPriceCol), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.selectProductModal.costPriceTxt"));
        Assert.assertEquals(common.getText(loc_dlgSelectProduct_lblListingPriceCol), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.selectProductModal.listingPriceTxt"));
        Assert.assertEquals(common.getText(loc_dlgSelectProduct_lblSellingPriceCol), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.selectProductModal.sellingPriceTxt"));
        Assert.assertEquals(common.getText(loc_dlgSelectProduct_btnOK), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.selectProductModal.okBtn"));
        Assert.assertEquals(common.getText(loc_dlgSelectProduct_btnCancel), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.selectProductModal.cancelBtn"));
        clickOnCancelBTNONSelectProductModal();
        selectCollectionType(Constant.AUTOMATED_OPTION);
        Assert.assertEquals(common.getText(loc_lblConditions), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.condtionTitle"));
        Assert.assertEquals(common.getText(loc_lblProductMustMatch), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.productMusMatchTxt"));
        Assert.assertEquals(common.getText(loc_chbAllConditionAction), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.allConditionsTxt"));
        Assert.assertEquals(common.getText(loc_chbAnyConditionAction), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.anyConditionTxt"));
        Assert.assertEquals(common.getText(loc_btnAddMoreCondition), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.addMoreConditionBtn"));

        List<WebElement> conditionOptions = common.getAllOptionInDropDown(loc_lst_ddlCondition, 0);
        Assert.assertEquals(common.getText(conditionOptions.get(0)), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.conditionOptions.productTitleTxt"));
        Assert.assertEquals(common.getText(conditionOptions.get(1)), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.conditionOptions.productPriceTxt"));
        List<WebElement> operateOptions = common.getAllOptionInDropDown(loc_lst_ddlOperator, 0);
        Assert.assertEquals(common.getText(operateOptions.get(0)), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.containsTxt"));
        Assert.assertEquals(common.getText(operateOptions.get(1)), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.productTitleIsEqualToTxt"));
        Assert.assertEquals(common.getText(operateOptions.get(2)), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.startsWithTxt"));
        Assert.assertEquals(common.getText(operateOptions.get(3)), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.endsWithTxt"));
        for (int j = 0; j < 5; j++) {
            try {
                common.selectByIndex(common.getElements(loc_lst_ddlCondition).get(0), 1);
                break;
            } catch (StaleElementReferenceException ex) {
                logger.debug("StaleElementReferenceException caught when selecting condition \n" + ex);
            }
        }
        operateOptions = common.getAllOptionInDropDown(loc_lst_ddlOperator, 0);
        Assert.assertEquals(common.getText(operateOptions.get(0)), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.isGeaterThanTxt"));
        Assert.assertEquals(common.getText(operateOptions.get(1)), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.isLessThanTxt"));
        Assert.assertEquals(common.getText(operateOptions.get(2)), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.productPriceIsEqualToTxt"));
    }

    public void createCollectionForPermissionTest() {
        // collection name
        String collectionName = "Collections %d".formatted(Instant.now().toEpochMilli());

        // create collection
        this.inputCollectionName(collectionName)
                .clickOnSaveBTN();

        // close notification popup
        common.getElement(loc_dlgNotification);
        common.closePopup(loc_dlgNotification_btnClose);
        logger.info("Complete create product collection.");

    }


}
