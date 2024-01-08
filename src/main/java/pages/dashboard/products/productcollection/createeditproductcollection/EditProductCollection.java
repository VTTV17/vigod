package pages.dashboard.products.productcollection.createeditproductcollection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.dashboard.home.HomePage;
import pages.dashboard.products.productcollection.productcollectionmanagement.ProductCollectionManagement;
import pages.dashboard.service.servicecollections.ServiceCollectionManagement;
import utilities.UICommonAction;
import utilities.data.DataGenerator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditProductCollection extends CreateProductCollection {
    WebDriver driver;
    ProductCollectionManagement productCollectionManagement;
    final static Logger logger = LogManager.getLogger(EditProductCollection.class);

    public EditProductCollection(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        common = new UICommonAction(driver);
        createCollectionUI = new CreateProductCollectionElement(driver);
        generator = new DataGenerator();
        PageFactory.initElements(driver, this);
    }

    public EditProductCollection navigateEditCollection(String collectioName, String languageDashboard) throws Exception {
        waitTillSpinnerDisappear();
        navigateToPage("Products", "Product Collections");
        HomePage home = new HomePage(driver);
        home.selectLanguage(languageDashboard);
        home.hideFacebookBubble();
        productCollectionManagement = new ProductCollectionManagement(driver);
        return productCollectionManagement.goToEditProductCollection(collectioName);
    }

    public ProductCollectionManagement editProductPriorityInCollection() {
        waitTillSpinnerDisappear();
        CreateProductCollection.productPriorityMap = inputPriority(false, true);
        clickOnSaveBTN();//Click outside
        clickOnSaveBTN();
        logger.info("Edit priority of product.");
        return clickOnClose();
    }
    public ProductCollectionManagement editProductListInManualCollection(String[] newProductList, boolean hasDeleteProduct, boolean hasInputPriority){
        waitTillSpinnerDisappear();
        int productSize = createCollectionUI.DELETE_BTN_LIST.size();
        System.out.println("productSize: "+productSize);
        if(hasDeleteProduct) {
           for(int i=0; i< productSize;i++){
               common.clickElement(createCollectionUI.DELETE_BTN_1);
               System.out.println("Deleted: "+i);
               common.sleepInMiliSecond(500);
           }
       }
       selectProductWithKeyword(newProductList);
        if(hasInputPriority){
           CreateProductCollection.productPriorityMap = inputPriority(false,true);
           clickOnSaveBTN();//Click outside
       }else {
            CreateProductCollection.productPriorityMap = getProductPriorityMapBefore();
        }
        clickOnSaveBTN();
        logger.info("Edit product list in manual collection.");
        return clickOnClose();
    }
    public String[] getCollectionConditionBefore(){
        List<String> conditionList = new ArrayList<>();
        int conditionSize = createCollectionUI.CONDITION_VALUE_INPUT.size();
        for (int i=0; i<conditionSize;i++){
            String condition = common.getDropDownSelectedValue(createCollectionUI.CONDITION_DROPDOWN.get(i));
            String operate = common.getDropDownSelectedValue(createCollectionUI.OPERATOR_DROPDOWN.get(i));
            String value = common.getElementAttribute(createCollectionUI.CONDITION_VALUE_INPUT.get(i),"value");
            String aCondition = condition+"-"+operate+"-"+value;
            conditionList.add(aCondition);
        }
        logger.info("Conditions selected before: "+conditionList);
        String[] conditions = new String[conditionList.size()];
        return conditionList.toArray(conditions);
    }
    public String[] EditAutomationCollection(String conditionType, String...conditions) throws Exception {
        waitTillSpinnerDisappear();
        common.sleepInMiliSecond(1000);
        selectConditionType(conditionType);
        String[] conditionsAvailable = getCollectionConditionBefore();
        List<String> allList = new ArrayList<>();
        allList.addAll(Arrays.stream(conditionsAvailable).toList());
        allList.addAll(Arrays.stream(conditions).toList());
        System.out.println("allList: "+allList);
        String[] allConditionArr = new String[allList.size()];
        allList.toArray(allConditionArr);
        selectCondition(true,allList.toArray(allConditionArr));
        clickOnSaveBTN();
        clickOnClose();
        logger.info("Update Automated collection successfully.");
        return allConditionArr;
    }
}
