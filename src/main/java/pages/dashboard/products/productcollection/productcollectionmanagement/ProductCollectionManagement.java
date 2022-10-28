package pages.dashboard.products.productcollection.productcollectionmanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import pages.dashboard.home.HomePage;
import pages.dashboard.products.productcollection.createproductcollection.CreateProductCollection;
import pages.storefront.checkout.checkoutstep1.CheckOutStep1;
import pages.storefront.checkout.checkoutstep1.CheckOutStep1Element;
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
    public CreateProductCollection clickOnCreateCollection(){
        commonAction.clickElement(collectionManagementUI.CREATE_PRODUCT_COLLECTION_BTN);
        waitTillSpinnerDisappear();
        logger.info("Click on Create Product Collection button.");
        return new CreateProductCollection(driver);
    }
    public ProductCollectionManagement verifyCollectionName(String expected){
        String actual = commonAction.getText(collectionManagementUI.COLLECTION_NAMES.get(0));
        Assert.assertEquals(actual,expected);
        logger.info("Verify collection name after created");
        return this;
    }
    public ProductCollectionManagement verifyType(String expected){
        String actual = commonAction.getText(collectionManagementUI.TYPES.get(0));
        Assert.assertEquals(actual,expected);
        logger.info("Verify type after collection created");
        return this;
    }
    public ProductCollectionManagement verifyMode(String expected){
        String actual = commonAction.getText(collectionManagementUI.MODES.get(0));
        Assert.assertEquals(actual,expected);
        logger.info("Verify mode after collection created");
        return this;
    }
    public ProductCollectionManagement verifyItem(String expected){
        String actual = commonAction.getText(collectionManagementUI.ITEMS.get(0));
        Assert.assertEquals(actual,expected);
        logger.info("Verify items after collection created");
        return this;
    }
    public ProductCollectionManagement verifyCollectionInfo(String collectionName, String type, String mode, String items){
        waitTillSpinnerDisappear();
        commonAction.sleepInMiliSecond(2000);
        verifyCollectionName(collectionName);
        verifyType(type);
        verifyMode(mode);
        verifyItem(items);
        return this;
    }


}
