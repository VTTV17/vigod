package pages.sellerapp.product;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.products.productcollection.createeditproductcollection.CreateProductCollectionElement;
import pages.sellerapp.SellerGeneral;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;
import utilities.data.DataGenerator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerCreateCollection {
    final static Logger logger = LogManager.getLogger(SellerCreateCollection.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile common;
    DataGenerator generator;
    String allConditionTxt;
    String anyConditionTxt;

    public SellerCreateCollection (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonMobile(driver);
        generator = new DataGenerator();
        try {
            allConditionTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.allConditionsTxt");
            anyConditionTxt = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.anyConditionTxt");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    By COLLECTION_NAME_INPUT = By.xpath("//*[ends-with(@resource-id,'edtCollectionName')]");
    By COLLECTION_NAME_LBL = By.xpath("//*[ends-with(@resource-id,'edtCollectionName')]/parent::android.widget.FrameLayout/preceding-sibling::android.widget.TextView");
    By COLLECTION_NAME_ERROR = By.xpath("//*[ends-with(@resource-id,'tvErrorCollectionName')]");
    By MANUALLY_TAB = By.xpath("(//*[ends-with(@resource-id,'inputTypeTabLayout')]//android.widget.TextView)[1]");
    By AUTOMATED_TAB = By.xpath("(//*[ends-with(@resource-id,'inputTypeTabLayout')]//android.widget.TextView)[2]");
    By GUIDE_MANUALLY = By.xpath("//*[ends-with(@resource-id,'tvGuideInputTypeManual')]");
    By ADD_PRODUCT_BTN = By.xpath("//*[ends-with(@resource-id,'tvAddProduct')]");
    By PRODUCT_LIST_LBL_MANUALLY = By.xpath("//*[ends-with(@resource-id,'btnAddProductManual')]/preceding-sibling::android.widget.TextView");
    By EMPTY_PRODUCT_LBL = By.xpath("//*[ends-with(@resource-id,'tvEmptyProduct')]");
    By SEARCH_PRODUCT_NAME_INPUT = By.xpath("//*[ends-with(@resource-id,'search_src_text')]");
    By GUIDE_AUTOMATED = By.xpath("//*[ends-with(@resource-id,'tvGuideInputTypeAutomated')]");
    By SEE_DETAIL_BTN = By.xpath("//*[ends-with(@resource-id,'tvSeeProductListAutomated')]");
    By PRODUCT_LIST_LBL_AUTOMATED = By.xpath("//*[ends-with(@resource-id,'btnSeeProductListAutomated')]/preceding-sibling::android.widget.TextView");
    By PRODUTC_MUST_MATCH_LBL = By.xpath("//*[ends-with(@resource-id,'rgConditionMatchType')]/preceding-sibling::android.widget.TextView");
    By ALL_CONDITIONS_OPTION = By.xpath("//*[ends-with(@resource-id,'rbtAllConditions')]");
    By ANY_CONDITION_OPTION = By.xpath("//*[ends-with(@resource-id,'rbtAnyCondition')]");
    By CONDITION_DROPDOWN = By.xpath("//*[ends-with(@resource-id,'btnConditionField')]");
    By OPERATE_DROPDOWN = By.xpath("//*[ends-with(@resource-id,'btnConditionOperand')]");
    By INPUT_A_STRING = By.xpath("//*[ends-with(@resource-id,'edtConditionValue')]");
    By INPUT_STRING_ERROR = By.xpath("//*[ends-with(@resource-id,'edtConditionValueError')]");
    By ADD_MORE_CONDITION_BTN = By.xpath("//*[ends-with(@resource-id,'tvAddCondition')]");
    By DELETE_CONDITION_ICON = By.xpath("//*[ends-with(@resource-id,'btnDelete')]");
    By PRODUCT_NAME_LIST = By.xpath("//*[ends-with(@resource-id,'tvProductName')]");
    By NO_PRODUCT_MESSAGE_AUTOMATED = By.xpath("//*[ends-with(@resource-id,'llActionBarContainer')]/parent::android.widget.RelativeLayout/following-sibling::android.widget.RelativeLayout/android.widget.TextView");
    By EDIT_PRIORITY_LIST = By.xpath("//*[ends-with(@resource-id,'edtPriority')]");

    public SellerCreateCollection verifyText() throws Exception {
        String pageTitle = new SellerGeneral(driver).getHeaderTitle();
        Assert.assertEquals(pageTitle, PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.pageTitle"));
        Assert.assertEquals(common.getText(COLLECTION_NAME_LBL),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.collectionNameLbl"));
        Assert.assertEquals(common.getText(COLLECTION_NAME_ERROR),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.emptyError"));
        Assert.assertEquals(common.getText(MANUALLY_TAB),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.manuallyLbl"));
        Assert.assertEquals(common.getText(AUTOMATED_TAB),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.automatedLbl"));
        Assert.assertEquals(common.getText(GUIDE_MANUALLY),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.manually.description"));
        Assert.assertEquals(common.getText(PRODUCT_LIST_LBL_MANUALLY),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.manually.productListLbl"));
        Assert.assertEquals(common.getText(ADD_PRODUCT_BTN),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.manually.addProductBtn"));
        Assert.assertEquals(common.getText(EMPTY_PRODUCT_LBL),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.manually.noProductMessage"));
        tapAddProductBtn();
        String addProductPageTitle = new SellerGeneral(driver).getHeaderTitle();
        Assert.assertEquals(addProductPageTitle,PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.manually.addProduct.pageTitle"));
        Assert.assertEquals(common.getText(SEARCH_PRODUCT_NAME_INPUT),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.manually.addProduct.searchHint"));
        new SellerGeneral(driver).tapHeaderLeftIcon();
        tapAutomatedTab();
        Assert.assertEquals(common.getText(GUIDE_AUTOMATED),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.automated.description"));
        Assert.assertEquals(common.getText(PRODUCT_LIST_LBL_AUTOMATED),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.automated.productListLbl"));
        Assert.assertEquals(common.getText(SEE_DETAIL_BTN),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.automated.seeDetailBtn"));
        Assert.assertEquals(common.getText(PRODUTC_MUST_MATCH_LBL),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.automated.productMustMatchLbl"));
        Assert.assertEquals(common.getText(ALL_CONDITIONS_OPTION),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.automated.allConditionsLbl"));
        Assert.assertEquals(common.getText(ANY_CONDITION_OPTION),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.automated.anyCondition"));
        Assert.assertEquals(common.getText(CONDITION_DROPDOWN),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.automated.condition.productTitle"));
        Assert.assertEquals(common.getText(OPERATE_DROPDOWN),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.automated.operator.contains"));
        Assert.assertEquals(common.getText(INPUT_A_STRING),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.automated.inputStringHint"));
        Assert.assertEquals(common.getText(INPUT_STRING_ERROR),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.automated.inputStringError"));
        Assert.assertEquals(common.getText(ADD_MORE_CONDITION_BTN),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.automated.addMoreConditionBtn"));
        tapSeeDetail();
        String productListPageTitle = new SellerGeneral(driver).getHeaderTitle();
        Assert.assertEquals(productListPageTitle,PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.automated.productList.pageTitle"));
        Assert.assertEquals(common.getText(NO_PRODUCT_MESSAGE_AUTOMATED),PropertiesUtil.getPropertiesValueByDBLang("seller.productCollection.create.automated.productList.noProductMessage"));

        return this;
    }
    public SellerCreateCollection tapAddProductBtn(){
        common.clickElement(ADD_PRODUCT_BTN);
        logger.info("Tap on Add product button.");
        return this;
    }
    public  SellerCreateCollection tapAutomatedTab(){
        common.clickElement(AUTOMATED_TAB);
        logger.info("Tap on Automated tab.");
        return this;
    }
    public  SellerCreateCollection tapSeeDetail(){
        common.clickElement(SEE_DETAIL_BTN);
        logger.info("Tap on See detail button.");
        return this;
    }
    public SellerCreateCollection inputCollectionName(String collectionName){
        common.inputText(COLLECTION_NAME_INPUT,collectionName);
        logger.info("Input Collection name: "+collectionName);
        return this;
    }
    public SellerCreateCollection selectImage(){
        new SellerGeneral(driver).selectImage();
        logger.info("Select image.");
        return new SellerCreateCollection(driver);
    }
    public SellerProductCollection tapSaveIcon(){
        new SellerGeneral(driver).tapHeaderRightIcon();
        logger.info("Tap on Save icon.");
        return new SellerProductCollection(driver);
    }
    public SellerCreateCollection tapAddProduct(){
        common.clickElement(ADD_PRODUCT_BTN);
        logger.info("Tap on Add product button.");
        return this;
    }
    public SellerCreateCollection selectProductsWithKeyword(String...keywords){
        tapAddProduct();
        if (keywords.length == 0) {
            logger.info("No select product.");
            return this;
        }
        for (int i=0;i<keywords.length;i++){
            inputSearchKeyword(keywords[i]);
            common.sleepInMiliSecond(500);
            selectAllProductDisplay();
        }
        new SellerGeneral(driver).tapHeaderRightIcon();
        return this;
    }
    public SellerCreateCollection selectAllProductDisplay(){
        List<WebElement> productNameList = common.getElements(PRODUCT_NAME_LIST);
        for (WebElement el:productNameList) {
            common.clickElement(el);
        }
        return this;
    }
    public SellerCreateCollection inputSearchKeyword(String keyword){
        common.inputText(SEARCH_PRODUCT_NAME_INPUT,keyword);
        logger.info("Input keyword: "+keyword);
        return this;
    }
    public Map<String, Integer> inputPriority(boolean isInputAllProduct, boolean canInputDuplicate) {
        Map<String, Integer> productPriorityMap = new HashMap<>();
        common.sleepInMiliSecond(1000);
        int productListSize = common.getElements(PRODUCT_NAME_LIST).size();
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
                common.sleepInMiliSecond(1000);
                common.inputText(common.getElements(EDIT_PRIORITY_LIST),i, String.valueOf(priorityList.get(i)));
                productPriorityMap.put(common.getText(common.getElements(EDIT_PRIORITY_LIST).get(i)).toLowerCase(), priorityList.get(i));
            } else {
                productPriorityMap.put(common.getText(common.getElements(EDIT_PRIORITY_LIST).get(i)).toLowerCase(), priorityList.size());
            }
        }
        logger.info("Input product priority: " + productPriorityMap);
        return productPriorityMap;
    }
    public SellerCreateCollection verifyCollectionName(String collectionName){
        Assert.assertEquals(common.getText(COLLECTION_NAME_INPUT),collectionName);
        logger.info("Verify collection name.");
        return this;
    }
    public SellerCreateCollection inputConditionKeyword(String keyword){
        common.inputText(INPUT_A_STRING,keyword);
        logger.info("Input a keyword to condition.");
        return this;
    }
    public void selectProductConditionDropDown(String productPriceOrTitle) throws Exception {
        String conditionCurrent = common.getText(CONDITION_DROPDOWN);
        if(!conditionCurrent.equalsIgnoreCase(productPriceOrTitle)){
            common.clickElement(CONDITION_DROPDOWN);
            List<String> conditionList = new ArrayList<>();
            try {
                conditionList.add(PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.conditionOptions.productTitleTxt"));
                conditionList.add(PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.conditionOptions.productPriceTxt"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            boolean isSelected = false;
            for (int i=0;i<conditionList.size();i++){
                if(conditionList.get(i).equalsIgnoreCase(productPriceOrTitle)){
                    common.selectDropdownOption(common.getElement(CONDITION_DROPDOWN),i+1);
                    isSelected = true;
                    break;
                }
            }
            if(!isSelected){
                throw new Exception("Product condition type not found.");
            }
        }
        logger.info("Select product condition option: "+productPriceOrTitle);
    }
    public void selectOperateDropDown(String conditionSelected, String operate) throws Exception {
        String conditionCurrent = common.getText(OPERATE_DROPDOWN);
        if(!conditionCurrent.equalsIgnoreCase(operate)){
            common.clickElement(OPERATE_DROPDOWN);
            List<String> operateList = new ArrayList<>();
            try {
                String productTitleText = PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.conditionOptions.productTitleTxt");
                if(conditionSelected.equalsIgnoreCase(productTitleText)){
                    operateList.add(PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.containsTxt"));
                    operateList.add(PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.productTitleIsEqualToTxt"));
                    operateList.add(PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.startsWithTxt"));
                    operateList.add(PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.endsWithTxt"));
                }else {
                    operateList.add(PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.isGeaterThanTxt"));
                    operateList.add(PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.isLessThanTxt"));
                    operateList.add(PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.create.automated.operateOptions.productPriceIsEqualToTxt"));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            boolean isSelected = false;
            for (int i=0;i<operateList.size();i++){
                if(operateList.get(i).equalsIgnoreCase(operate)){
                    common.selectDropdownOption(common.getElement(OPERATE_DROPDOWN),i+1);
                    isSelected = true;
                    break;
                }
            }
            if(!isSelected){
                throw new Exception("Operate not found.");
            }
        }
        logger.info("Select operate: "+operate);
    }
    public SellerCreateCollection selectCondition(boolean hasAvailable, String...conditions) throws Exception {
        List<WebElement> inputStringList = common.getElements(INPUT_A_STRING);
        for (int i = 0; i < conditions.length; i++) {
            String[] conditionContent = conditions[i].split("-");
            String conditionValueInputed = common.getText(inputStringList.get(i));
            if (hasAvailable && !conditionValueInputed.equals("")){
                common.clickElement(ADD_MORE_CONDITION_BTN);
                continue;
            }
            selectProductConditionDropDown(conditionContent[0]);
            selectOperateDropDown(conditionContent[0],conditionContent[1]);
            common.sleepInMiliSecond(1000);
            inputConditionKeyword(conditionContent[2]);
            if (i < conditions.length - 1) {
                common.clickElement(ADD_MORE_CONDITION_BTN);
            }
            logger.info("Input condition: %s".formatted(conditions[i]));
        }
        return this;
    }
    public void selectCollectionType(String collectionType){
        if(collectionType.equalsIgnoreCase(allConditionTxt)){
            if(common.getElementAttribute(common.getElement(ALL_CONDITIONS_OPTION),"checked").equals("false")){
                common.clickElement(ALL_CONDITIONS_OPTION);
            }
        }else if (collectionType.equalsIgnoreCase(anyConditionTxt)){
            if(common.getElementAttribute(common.getElement(ANY_CONDITION_OPTION),"checked").equals("false")){
                common.clickElement(ALL_CONDITIONS_OPTION);
            }
        }else try {
            throw new Exception("CollectionType not found.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public SellerProductCollection createAutomatedCollection(String collectionName, String conditionType, String...conditions) {
        inputCollectionName(collectionName);
        selectImage();
        tapAutomatedTab();
        selectCollectionType(conditionType);
        try {
            selectCondition(false,conditions);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tapSaveIcon();
    }
}
