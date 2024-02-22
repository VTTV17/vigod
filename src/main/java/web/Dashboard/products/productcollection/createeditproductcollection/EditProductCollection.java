package web.Dashboard.products.productcollection.createeditproductcollection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import web.Dashboard.home.HomePage;
import web.Dashboard.products.productcollection.productcollectionmanagement.ProductCollectionManagement;

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
        generator = new DataGenerator();
        home = new HomePage(driver);
    }

    HomePage home;
    public EditProductCollection navigateEditCollection(String collectioName, String languageDashboard) throws Exception {
        
        home.waitTillSpinnerDisappear();
        home.navigateToPage("Products", "Product Collections");
        home.selectLanguage(languageDashboard);
        home.hideFacebookBubble();
        productCollectionManagement = new ProductCollectionManagement(driver);
        return productCollectionManagement.goToEditProductCollection(collectioName);
    }

    public ProductCollectionManagement editProductPriorityInCollection() {
        home.waitTillSpinnerDisappear();
        CreateProductCollection.productPriorityMap = inputPriority(false, true);
        clickOnSaveBTN();//Click outside
        clickOnSaveBTN();
        logger.info("Edit priority of product.");
        return clickOnClose();
    }
    public ProductCollectionManagement editProductListInManualCollection(String[] newProductList, boolean hasDeleteProduct, boolean hasInputPriority){
        home.waitTillSpinnerDisappear();
        int productSize = common.getElements(loc_lst_btnDelete).size();
        System.out.println("productSize: "+productSize);
        if(hasDeleteProduct) {
           for(int i=0; i< productSize;i++){
               common.click(loc_lst_btnDelete,0);
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
        int conditionSize = common.getElements(loc_lst_txtConditionValue).size();
        for (int i=0; i<conditionSize;i++){
            String condition = common.getDropDownSelectedValue(loc_lst_ddlCondition,i);
            String operate = common.getDropDownSelectedValue(loc_lst_ddlOperator,i);
            String value = common.getAttribute(loc_lst_txtConditionValue,i,"value");
            String aCondition = condition+"-"+operate+"-"+value;
            conditionList.add(aCondition);
        }
        logger.info("Conditions selected before: "+conditionList);
        String[] conditions = new String[conditionList.size()];
        return conditionList.toArray(conditions);
    }
    public String[] EditAutomationCollection(String conditionType, String...conditions) throws Exception {
        home.waitTillSpinnerDisappear();
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
