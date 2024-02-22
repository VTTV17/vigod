package web.Dashboard.products.productcollection.productcollectionmanagement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.commons.UICommonAction;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.products.productcollection.createeditproductcollection.CreateProductCollection;
import web.Dashboard.products.productcollection.createeditproductcollection.EditProductCollection;

import java.time.Duration;
import java.util.List;

public class ProductCollectionManagement extends ProductCollectionManagementElement {
    final static Logger logger = LogManager.getLogger(ProductCollectionManagement.class);
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    HomePage home;

    public ProductCollectionManagement(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        home = new HomePage(driver);
    }

    public ProductCollectionManagement navigateToProductCollectionManagement() {
//        waitTillSpinnerDisappear();
        home.navigateToPage("Products", "Product Collections");
        logger.info("Go to product collection management page.");
        return this;
    }

    public CreateProductCollection clickOnCreateCollection() {
        commonAction.click(loc_btnCreateProductCollection);
        home.waitTillSpinnerDisappear1();
        logger.info("Click on Create Product Collection button.");
        return new CreateProductCollection(driver);
    }

    public ProductCollectionManagement verifyCollectionName(String expected, int index) {
        String actual = commonAction.getText(loc_lst_lblCollectionName, index);
        Assert.assertEquals(actual, expected);
        logger.info("Verify collection name after created");
        return this;
    }

    public ProductCollectionManagement verifyType(String expected, int index) {
        String actual = commonAction.getText(loc_lst_lblTypes, index);
        Assert.assertEquals(actual, expected);
        logger.info("Verify type after collection created");
        return this;
    }

    public ProductCollectionManagement verifyMode(String expected, int index) {
        String actual = commonAction.getText(loc_lst_lblModes, index);
        Assert.assertEquals(actual, expected);
        logger.info("Verify mode after collection created");
        return this;
    }

    public ProductCollectionManagement verifyItem(String expected, int index) {
        String actual = commonAction.getText(loc_lst_lblItems, index);
        Assert.assertEquals(actual, expected);
        logger.info("Verify items after collection created");
        return this;
    }

    public ProductCollectionManagement verifyCollectionInfoAfterCreated(String collectionName, String type, String mode, String items) {
        commonAction.refreshPage();
        home.waitTillSpinnerDisappear1();
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
        home.waitTillSpinnerDisappear1();
        boolean isSelected = false;
        List<WebElement> collectionNameElement = commonAction.getElements(loc_lst_lblCollectionName);
        for (int i = 0; i < collectionNameElement.size(); i++) {
            String collectionNameInList = commonAction.getText(loc_lst_lblCollectionName, i);
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
        List<WebElement> collectionNameElement = commonAction.getElements(loc_lst_lblCollectionName);
        for (int i = 0; i < collectionNameElement.size(); i++) {
            String collectionNameInList = commonAction.getText(loc_lst_lblCollectionName, i);
            if (collectionNameInList.equalsIgnoreCase(collectionName)) {
                commonAction.click(loc_lst_btnEdit, i);
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
        String name = commonAction.getText(loc_lst_lblCollectionName, 0);
        logger.info("Get the first collection name in list: " + name);
        return name;
    }

    public void verifyCollectNameNotDisplayInList(String collectionName) {
        List<WebElement> collectionNameElement = commonAction.getElements(loc_lst_lblCollectionName);
        for (int i = 0; i < collectionNameElement.size(); i++) {
            String collectionNameInList = commonAction.getText(loc_lst_lblCollectionName, i);
            if (collectionNameInList.equalsIgnoreCase(collectionName)) {
                Assert.fail(collectionName + ": still display in position " + i);
            }
        }
        logger.info("Verify collection: %s - not show in list after deleted".formatted(collectionName));
        Assert.assertTrue(true, collectionName + ": not show in collection list");
    }

    public void deleteTheFirstCollection() {
        commonAction.click(loc_lst_btnDelete, 0);
        commonAction.click(loc_dlgConfirmation_btnOK);
        commonAction.sleepInMiliSecond(1000);
        commonAction.click(loc_dlgConfirmation_btnOK);
        home.waitTillSpinnerDisappear();
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
            Assert.assertEquals(home.verifySalePitchPopupDisplay(), 0);
        }
    }

    /*-------------------------------------*/

    public void verifyTextOfPage() throws Exception {
        Assert.assertEquals(commonAction.getText(loc_ttlPageTitleAndTotalNumber).split("\n")[0], PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.pageTitle"));
        Assert.assertEquals(commonAction.getText(loc_btnCreateProductCollection), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.createProductCollectionBtn"));
        Assert.assertEquals(commonAction.getAttribute(loc_txtSearch, "placeholder"), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.searchHintTxt"));
        Assert.assertEquals(commonAction.getText(loc_lblThumbnailCol), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.thumbnailColTxt"));
        Assert.assertEquals(commonAction.getText(loc_lblCollectionName), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.collectionNameColTxt"));
        Assert.assertEquals(commonAction.getText(loc_lblTypeCol), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.typeColTxt"));
        Assert.assertEquals(commonAction.getText(loc_lblModeCol), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.modeColTxt"));
        Assert.assertEquals(commonAction.getText(loc_lblItemsCol), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.itemsColTxt"));
        Assert.assertEquals(commonAction.getText(loc_lblActionsCol), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.table.actionsColTxt"));
        commonAction.click(loc_lst_btnDelete, 0);
        Assert.assertEquals(commonAction.getText(loc_dlgConfirmation_lblTitle), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.deleteModal.title"));
        Assert.assertEquals(commonAction.getText(loc_dlgConfirmation_lblMessage), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.deleteModal.content"));
        Assert.assertEquals(commonAction.getText(loc_dlgConfirmation_btnOK), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.deleteModal.OKBtn"));
        Assert.assertEquals(commonAction.getText(loc_dlgConfirmation_btnCancel), PropertiesUtil.getPropertiesValueByDBLang("products.productCollections.management.deleteModal.cancelBtn"));
    }

    public void waitToUpdateCollection(int second) {
        commonAction.sleepInMiliSecond(1000L * second);
    }

    public void clickLogout() {
        home.clickLogout();
    }
}
