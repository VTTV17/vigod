package web.Dashboard.products.productcollection.productcollectionmanagement;

import api.Seller.products.product_collections.APIProductCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.products.productcollection.createeditproductcollection.CreateProductCollection;
import web.Dashboard.products.productcollection.createeditproductcollection.EditProductCollection;

import java.time.Duration;
import java.util.List;

import static utilities.links.Links.DOMAIN;

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

    // check permission
    // ticket: https://mediastep.atlassian.net/browse/BH-24652
    AllPermissions permissions;
    CheckPermission checkPermission;
    AssertCustomize assertCustomize;
    LoginInformation loginInformation;

    public ProductCollectionManagement getLoginInformation(LoginInformation loginInformation) {
        this.loginInformation = loginInformation;
        return this;
    }

    public void checkProductCollectionPermission(AllPermissions permissions, List<Integer> collectionIds) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // init assert customize
        assertCustomize = new AssertCustomize(driver);

        // check view collection list
        checkViewCollectionList();

        // check view collection detail
        new EditProductCollection(driver).checkViewCollectionsDetail(permissions, collectionIds);

        // check create collection
        checkCreateCollection();

        // check edit collection
        checkViewCollectionDetail();

        // check delete collection
        checkDeleteCollection();
    }

    void navigateToProductCollectionManagementPage() {
        if (!driver.getCurrentUrl().contains("/collection/list")) {
            driver.get("%s/collection/list".formatted(DOMAIN));
            logger.info("Navigate to product collection management.");
        }
    }

    void checkViewCollectionList() {
        int statusCode = new APIProductCollection(loginInformation).getCollectionListResponse(0).statusCode();
        if (permissions.getProduct().getCollection().isViewCollectionList()) {
            assertCustomize.assertTrue(statusCode == 200,
                    "No product collections shows.");
        } else {
            assertCustomize.assertTrue(statusCode == 403,
                    "All product collections still showing when no view list permission.");
        }

        // If staff don’t have permission “View collection list”
        // => don’t see any collection when access product collection page
        assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/collection/list".formatted(DOMAIN), "/collection/list"),
                "Product collection management page must be shown instead of %s.".formatted(driver.getCurrentUrl()));
    }

    void checkCreateCollection() {
        // navigate to product collection management page
        navigateToProductCollectionManagementPage();

        // check permission
        if (permissions.getProduct().getCollection().isCreateCollection()) {
            // check can create product collection page
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnCreateProductCollection, "/collection/create/product/PRODUCT"),
                    "Product collection detail page is not shown.");

            // when access is successful create new product collection
            // => create new product collection
            if (driver.getCurrentUrl().contains("/collection/create/product/PRODUCT")) {
                // create collection
                new CreateProductCollection(driver).createCollectionForPermissionTest();
            }
        } else {
            // Show restricted popup
            // when click on [Delete] icon in collection management
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnCreateProductCollection),
                    "Restricted page is not shown.");
        }
        logger.info("Check permission: Product >> Collection >> Create collection.");
    }

    void checkViewCollectionDetail() {
        // navigate to product collection management page
        navigateToProductCollectionManagementPage();

        // check permission
        if (!commonAction.getListElement(loc_lst_btnEdit).isEmpty()) {

            // check permission
            if (permissions.getProduct().getCollection().isViewCollectionDetail()) {
                // check can view product collection detail
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_lst_btnEdit, "/collection/edit/product/"),
                        "Product collection detail page must be shown instead of %s.".formatted(driver.getCurrentUrl()));
            } else {
                // Show restricted page
                // if staff don’t have permission “View collection detail”
                // when open collection detail page
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_lst_btnEdit),
                        "Restricted page must be shown instead of %s.".formatted(driver.getCurrentUrl()));
            }
        } else logger.info("Store does not have any product collections.");
        // log
        logger.info("Check permission: Product >> Collection >> View collection detail.");
    }

    void checkDeleteCollection() {
        // navigate to product collection management page
        navigateToProductCollectionManagementPage();

        // check permission
        if (!commonAction.getListElement(loc_lst_btnDelete).isEmpty()) {
            if (permissions.getProduct().getCollection().isDeleteCollection()) {
                // check can delete product collection
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_lst_btnDelete, 0, loc_dlgConfirmation),
                        "Confirm delete popup is not shown.");
                if (!commonAction.getListElement(loc_dlgConfirmation).isEmpty())
                    commonAction.closePopup(loc_dlgConfirmation_btnOK);

            } else {
                // Show restricted popup
                // when click on [Delete] icon in collection management
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_lst_btnDelete, 0),
                        "Restricted popup is not shown.");
            }
        } else logger.info("Store does not have any product collections.");
        logger.info("Check permission: Product >> Collection >> Delete collection.");
    }
}
