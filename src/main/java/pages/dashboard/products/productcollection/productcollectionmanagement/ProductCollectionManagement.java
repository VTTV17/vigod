package pages.dashboard.products.productcollection.productcollectionmanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.dashboard.confirmationdialog.ConfirmationDialog;
import pages.dashboard.home.HomePage;
import pages.dashboard.products.productcollection.createeditproductcollection.CreateProductCollection;
import pages.dashboard.products.productcollection.createeditproductcollection.EditProductCollection;
import pages.dashboard.service.servicecollections.ServiceCollectionManagement;
import utilities.PropertiesUtil;
import utilities.UICommonAction;

import java.time.Duration;
import java.util.List;

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

    public ProductCollectionManagement navigateToProductCollectionManagement() {
//        waitTillSpinnerDisappear();
        navigateToPage("Products", "Product Collections");
        logger.info("Go to product collection management page.");
        return this;
    }

    public CreateProductCollection clickOnCreateCollection() {
        commonAction.click(collectionManagementUI.loc_btnCreateProductCollection);
        waitTillSpinnerDisappear1();
        logger.info("Click on Create Product Collection button.");
        return new CreateProductCollection(driver);
    }

    public ProductCollectionManagement verifyCollectionName(String expected, int index) {
        String actual = commonAction.getText(collectionManagementUI.loc_lst_lblCollectionName,index);
        Assert.assertEquals(actual, expected);
        logger.info("Verify collection name after created");
        return this;
    }

    public ProductCollectionManagement verifyType(String expected, int index) {
        String actual = commonAction.getText(collectionManagementUI.loc_lst_lblTypes,index);
        Assert.assertEquals(actual, expected);
        logger.info("Verify type after collection created");
        return this;
    }

    public ProductCollectionManagement verifyMode(String expected, int index) {
        String actual = commonAction.getText(collectionManagementUI.loc_lst_lblModes,index);
        Assert.assertEquals(actual, expected);
        logger.info("Verify mode after collection created");
        return this;
    }

    public ProductCollectionManagement verifyItem(String expected, int index) {
        String actual = commonAction.getText(collectionManagementUI.loc_lst_lblItems,index);
        Assert.assertEquals(actual, expected);
        logger.info("Verify items after collection created");
        return this;
    }

    public ProductCollectionManagement verifyCollectionInfoAfterCreated(String collectionName, String type, String mode, String items) {
        commonAction.refreshPage();
        waitTillSpinnerDisappear1();
        verifyCollectionName(collectionName, 0);
        verifyType(type, 0);
        verifyMode(mode, 0);
        verifyItem(items, 0);
        logger.info("Verify collection info after created.");
        return this;
    }

    public ProductCollectionManagement verifyCollectionInfoAfterUpdated(String collectionName, String type, String mode, String items) throws Exception {
        commonAction.sleepInMiliSecond(5000);
        commonAction.refreshPage();
        waitTillSpinnerDisappear1();
        boolean isSelected = false;
        List<WebElement> collectionNameElement = commonAction.getElements(collectionManagementUI.loc_lst_lblCollectionName);
        for (int i = 0; i < collectionNameElement.size(); i++) {
            String collectionNameInList = commonAction.getText(collectionManagementUI.loc_lst_lblCollectionName,i);
            if (collectionNameInList.equalsIgnoreCase(collectionName)) {
                verifyType(type, i);
                verifyMode(mode, i);
                verifyItem(items, i);
                isSelected = true;
                break;
            }
        }
        if (!isSelected) {
            throw new Exception("Collection not found! Check collection name again!");
        }
        logger.info("Verify collection info after updated.");
        return this;
    }

    public EditProductCollection goToEditProductCollection(String collectionName) throws Exception {
        commonAction.sleepInMiliSecond(1000);
        boolean isSelected = false;
        List<WebElement> collectionNameElement = commonAction.getElements(collectionManagementUI.loc_lst_lblCollectionName);
        for (int i = 0; i < collectionNameElement.size(); i++) {
            String collectionNameInList = commonAction.getText(collectionManagementUI.loc_lst_lblCollectionName,i);
            if (collectionNameInList.equalsIgnoreCase(collectionName)) {
                commonAction.click(collectionManagementUI.loc_lst_btnEdit,i);
                isSelected = true;
                break;
            }
        }
        if (!isSelected) {
            throw new Exception("Collection not found! Check collection name again!");
        }
        logger.info("Go to edit product collection: " + collectionName);
        return new EditProductCollection(driver);
    }

    public String getTheFirstCollectionName() {
        String name = commonAction.getText(collectionManagementUI.loc_lst_lblCollectionName,0);
        logger.info("Get the first collection name in list: " + name);
        return name;
    }

    public void verifyCollectNameNotDisplayInList(String collectionName) {
        List<WebElement> collectionNameElement = commonAction.getElements(collectionManagementUI.loc_lst_lblCollectionName);
        for (int i = 0; i < collectionNameElement.size(); i++) {
            String collectionNameInList = commonAction.getText(collectionManagementUI.loc_lst_lblCollectionName,i);
            if (collectionNameInList.equalsIgnoreCase(collectionName)) {
                Assert.assertTrue(false, collectionName + ": still display in position " + i);
            }
        }
        logger.info("Verify collection: %s - not show in list after deleted".formatted(collectionName));
        Assert.assertTrue(true, collectionName + ": not show in collection list");
    }

    public void deleteTheFirstCollection() {
        commonAction.click(collectionManagementUI.loc_lst_btnDelete,0);
        commonAction.click(collectionManagementUI.loc_dlgConfirmation_btnOK);
        commonAction.sleepInMiliSecond(1000);
        commonAction.click(collectionManagementUI.loc_dlgConfirmation_btnOK);
        waitTillSpinnerDisappear();
        logger.info("Delete the first collection");
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateProductCollection(String permission) {
		if (permission.contentEquals("A")) {
			new ProductCollectionManagement(driver).clickOnCreateCollection().inputCollectionName("Test Permission");
			commonAction.navigateBack();
			new ConfirmationDialog(driver).clickOKBtn();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }

    /*-------------------------------------*/      
    
    public void verifyTextOfPage() throws Exception {
        Assert.assertEquals(commonAction.getText(collectionManagementUI.loc_ttlPageTitleAndTotalNumber).split("\n")[0], PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.pageTitle"));
        Assert.assertEquals(commonAction.getText(collectionManagementUI.loc_btnCreateProductCollection), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.createProductCollectionBtn"));
        Assert.assertEquals(commonAction.getAttribute(collectionManagementUI.loc_txtSearch, "placeholder"), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.searchHintTxt"));
        Assert.assertEquals(commonAction.getText(collectionManagementUI.loc_lblThumbnailCol), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.thumbnailColTxt"));
        Assert.assertEquals(commonAction.getText(collectionManagementUI.loc_lblCollectionName), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.collectionNameColTxt"));
        Assert.assertEquals(commonAction.getText(collectionManagementUI.loc_lblTypeCol), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.typeColTxt"));
        Assert.assertEquals(commonAction.getText(collectionManagementUI.loc_lblModeCol), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.modeColTxt"));
        Assert.assertEquals(commonAction.getText(collectionManagementUI.loc_lblItemsCol), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.itemsColTxt"));
        Assert.assertEquals(commonAction.getText(collectionManagementUI.loc_lblActionsCol), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.actionsColTxt"));
        commonAction.click(collectionManagementUI.loc_lst_btnDelete,0);
        Assert.assertEquals(commonAction.getText(collectionManagementUI.loc_dlgConfirmation_lblTitle), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.deleteModal.title"));
        Assert.assertEquals(commonAction.getText(collectionManagementUI.loc_dlgConfirmation_lblMessage), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.deleteModal.content"));
        Assert.assertEquals(commonAction.getText(collectionManagementUI.loc_dlgConfirmation_btnOK), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.deleteModal.OKBtn"));
        Assert.assertEquals(commonAction.getText(collectionManagementUI.loc_dlgConfirmation_btnCancel), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.deleteModal.cancelBtn"));
    }
    public ProductCollectionManagement waitToUpdateCollection(int second){
        commonAction.sleepInMiliSecond(1000*second);
        return this;
    }
}
