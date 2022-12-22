package pages.dashboard.products.productcollection.productcollectionmanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.home.HomePage;
import pages.dashboard.products.productcollection.createeditproductcollection.CreateProductCollection;
import pages.dashboard.products.productcollection.createeditproductcollection.EditProductCollection;
import utilities.UICommonAction;

import java.time.Duration;

public class ProductCollectionManagement extends HomePage {
    final static Logger logger = LogManager.getLogger(ProductCollectionManagement.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    ProductCollectionManagementElement collectionManagementUI;

    public ProductCollectionManagement(WebDriver driver) {
        super(driver);
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        collectionManagementUI = new ProductCollectionManagementElement(driver);
        PageFactory.initElements(driver, this);
    }
    public ProductCollectionManagement navigateToProductCollectionManagement(){
        waitTillSpinnerDisappear();
        navigateToPage("Products", "Product Collections");
        logger.info("Go to product collection management page.");
        return this;
    }
    public CreateProductCollection clickOnCreateCollection(){
        commonAction.clickElement(collectionManagementUI.CREATE_PRODUCT_COLLECTION_BTN);
        waitTillSpinnerDisappear();
        logger.info("Click on Create Product Collection button.");
        return new CreateProductCollection(driver);
    }
    public ProductCollectionManagement verifyCollectionName(String expected,int index){
        String actual = commonAction.getText(collectionManagementUI.COLLECTION_NAMES.get(index));
        Assert.assertEquals(actual,expected);
        logger.info("Verify collection name after created");
        return this;
    }
    public ProductCollectionManagement verifyType(String expected, int index){
        String actual = commonAction.getText(collectionManagementUI.TYPES.get(index));
        Assert.assertEquals(actual,expected);
        logger.info("Verify type after collection created");
        return this;
    }
    public ProductCollectionManagement verifyMode(String expected, int index){
        String actual = commonAction.getText(collectionManagementUI.MODES.get(index));
        Assert.assertEquals(actual,expected);
        logger.info("Verify mode after collection created");
        return this;
    }
    public ProductCollectionManagement verifyItem(String expected,int index){
        String actual = commonAction.getText(collectionManagementUI.ITEMS.get(index));
        Assert.assertEquals(actual,expected);
        logger.info("Verify items after collection created");
        return this;
    }
    public ProductCollectionManagement verifyCollectionInfoAfterCreated(String collectionName, String type, String mode, String items){
//        waitTillSpinnerDisappear();
//        commonAction.sleepInMiliSecond(2000);
        verifyCollectionName(collectionName,0);
        verifyType(type,0);
        verifyMode(mode,0);
        verifyItem(items,0);
        logger.info("Verify collection info after created.");
        return this;
    }
    public ProductCollectionManagement verifyCollectionInfoAfterUpdated(String collectionName, String type, String mode, String items) throws Exception {
//        waitTillSpinnerDisappear();
//        commonAction.sleepInMiliSecond(2000);
        boolean isSelected = false;
        for (int i=0 ; i< collectionManagementUI.COLLECTION_NAMES.size();i++) {
            String collectionNameInList = commonAction.getText(collectionManagementUI.COLLECTION_NAMES.get(i));
            if(collectionNameInList.equalsIgnoreCase(collectionName)){
                verifyType(type,i);
                verifyMode(mode,i);
                verifyItem(items,i);
                isSelected = true;
                break;
            }
        }
        if(!isSelected){
            throw new Exception("Collection not found! Check collection name again!");
        }
        logger.info("Verify collection info after updated.");
        return this;
    }
    public EditProductCollection goToEditProductCollection(String collectionName) throws Exception {
        commonAction.sleepInMiliSecond(1000);
        boolean isSelected = false;
        for (int i=0 ; i< collectionManagementUI.COLLECTION_NAMES.size();i++) {
            String collectionNameInList = commonAction.getText(collectionManagementUI.COLLECTION_NAMES.get(i));
            if(collectionNameInList.equalsIgnoreCase(collectionName)){
                commonAction.clickElement(collectionManagementUI.EDIT_BTN.get(i));
                isSelected = true;
                break;
            }
        }
        if(!isSelected){
            throw new Exception("Collection not found! Check collection name again!");
        }
        logger.info("Go to edit product collection: "+collectionName);
        return new EditProductCollection(driver);
    }
    public String getTheFirstCollectionName(){
        String name = commonAction.getText(collectionManagementUI.COLLECTION_NAMES.get(0));
        logger.info("Get the first collection name in list: "+name);
        return name;
    }
    public void verifyCollectNameNotDisplayInList(String collectionName){
        for (int i=0 ; i< collectionManagementUI.COLLECTION_NAMES.size();i++) {
            String collectionNameInList = commonAction.getText(collectionManagementUI.COLLECTION_NAMES.get(i));
            if (collectionNameInList.equalsIgnoreCase(collectionName)){
                Assert.assertTrue(false,collectionName+": still display in position "+i);
            }
        }
        logger.info("Verify collection: %s - not show in list after deleted".formatted(collectionName));
        Assert.assertTrue(true,collectionName+": not show in collection list");
    }
    public void deleteTheFirstCollection(){
        commonAction.clickElement(collectionManagementUI.DELETE_BTN.get(0));
        commonAction.clickElement(collectionManagementUI.OK_BTN_ON_MODAL);
        commonAction.sleepInMiliSecond(1000);
        commonAction.clickElement(collectionManagementUI.OK_BTN_ON_MODAL);
        waitTillSpinnerDisappear();
        logger.info("Delete the first collection");
    }
}
