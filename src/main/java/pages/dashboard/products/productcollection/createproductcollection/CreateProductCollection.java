package pages.dashboard.products.productcollection.createproductcollection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.home.HomePage;
import pages.dashboard.products.productcollection.productcollectionmanagement.ProductCollectionManagement;
import utilities.UICommonAction;
import utilities.data.DataGenerator;
import utilities.sort.SortData;

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
    public static List<String> productSortByPriority = new ArrayList<>();
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

    public CreateProductCollection navigate() {
        waitTillSpinnerDisappear();
        navigateToPage("Products", "Product Collections");
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
            common.checkTheCheckBoxOrRadio(createCollectionUI.MANUAL_RADIO_BTN);
        } else if (collectionType.equalsIgnoreCase("Automated")) {
            common.checkTheCheckBoxOrRadio(createCollectionUI.AUTOMATED_RADIO_BTN);
        } else {
            throw new Exception("Input value does not match any of the accepted values: Manual/Automated");
        }
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
     * @return
     * @throws Exception
     */
    public CreateProductCollection selectConditionType(String conditionType) throws Exception {
        if (conditionType.equalsIgnoreCase("All conditions")) {
            common.checkTheCheckBoxOrRadio(createCollectionUI.ALL_CONDITION_RADIO_BTN);
        } else if (conditionType.equalsIgnoreCase("Any condition")) {
            common.checkTheCheckBoxOrRadio(createCollectionUI.ANY_CONDITION_RADIO_BTN);
        } else {
            throw new Exception("Input value does not match any of the accepted values: All conditions/Any condition");
        }
        return this;
    }

    public Map<String, Integer> inputPriority() {
        Map<String, Integer> productPriorityMap = new HashMap<>();
        int productListSize = createCollectionUI.PRODUCT_NAME_LIST.size();
        List<Integer> priorityList = generator.randomListNumberWithNoDulicate(productListSize);
        for (int i = 0; i < createCollectionUI.PRODUCT_NAME_LIST.size(); i++) {
            common.inputText(createCollectionUI.PRIORITIES_INPUT.get(i), String.valueOf(priorityList.get(i)));
            productPriorityMap.put(common.getText(createCollectionUI.PRODUCT_NAME_LIST.get(i)).toLowerCase(), priorityList.get(i));
        }
        logger.info("Input product priority: " + productPriorityMap);
        return productPriorityMap;
    }

    /**
     * @param conditions: fortmat: condition-operator-value. Example: Product title-contains-coffee
     * @return
     */
    public CreateProductCollection selectCondition(String... conditions) {
        for (int i = 0; i < conditions.length; i++) {
            String[] conditionContent = conditions[i].split("-");
            common.selectByVisibleText(createCollectionUI.CONDITION_DROPDOWN.get(i), conditionContent[0].trim());
            common.selectByVisibleText(createCollectionUI.OPERATOR_DROPDOWN.get(i), conditionContent[1].trim());
            common.inputText(createCollectionUI.CONDITION_VALUE_INPUT.get(i), conditionContent[2].trim());
            if (i < conditions.length - 1) {
                common.clickElement(createCollectionUI.ADD_MORE_CONDITION_BTN);
            }
            logger.info("Input condition: %s".formatted(conditions[i]));
        }
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

    public ProductCollectionManagement createManualCollectionWithoutSEO(String collectionName, String[] productList, boolean setPriority) throws Exception {
        inputCollectionName(collectionName);
        uploadImages("AoG.png");
        selectCollectionType("Manual");
        selectProductWithKeyword(productList);
        if (setPriority) {
            Map<String, Integer> productPriorityMap = inputPriority();
            productSortByPriority = sortProductListByPriority(productPriorityMap);
            clickOnSaveBTN();
        }
        clickOnSaveBTN();
        logger.info("Create manual collection without SEO info successfully.");
        return clickOnClose();
    }
    public ProductCollectionManagement createManualCollectionWithSEO(String collectionName, String[] productList, boolean setPriority, String SEOTitle, String SEODescription, String SEOKeyword, String SEOUrl) throws Exception {
        inputCollectionName(collectionName);
        uploadImages("AoG.png");
        selectCollectionType("Manual");
        selectProductWithKeyword(productList);
        if (setPriority) {
            Map<String, Integer> productPriorityMap = inputPriority();
            productSortByPriority = sortProductListByPriority(productPriorityMap);
        }
        inputSEOInfo(SEOTitle,SEODescription,SEOKeyword,SEOUrl);
        clickOnSaveBTN();
        logger.info("Create manual collection with SEO info successfully.");
        return clickOnClose();
    }
    public List<String> sortProductListByPriority(Map<String, Integer> productPriorityMap) {
        Map<String, Integer> sortedMap = SortData.sortMapByValue(productPriorityMap);
        logger.info("Sort product list by priority" + sortedMap.keySet().stream().toList());
        return sortedMap.keySet().stream().toList();
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
        logger.info("Input SEO keywords: "+SEOKeywords);
        return this;
    }
    public CreateProductCollection inputSEOUrl (String SEOUrl){
        common.inputText(createCollectionUI.SEO_URL, SEOUrl);
        logger.info("Input SEO url: "+SEOUrl);
        return this;
    }
    public CreateProductCollection inputSEOInfo(String SEOTitle, String SEODes, String SEOKeyword, String SEOUrl){
        inputSEOTitle(SEOTitle);
        inputSEODescription(SEODes);
        inputSEOKeywords(SEOKeyword);
        inputSEOUrl(SEOUrl);
        return this;
    }
}
