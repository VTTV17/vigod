package web.Dashboard.supplier.purchaseorders.management;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utilities.commons.UICommonAction;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.supplier.purchaseorders.crud.PurchaseOrderPage;

public class PurchaseOrderManagementPage extends PurchaseOrderManagementElement {
    WebDriver driver;
    UICommonAction commons;

    final static Logger logger = LogManager.getLogger(PurchaseOrderManagementPage.class);

    public PurchaseOrderManagementPage(WebDriver driver) {
        this.driver = driver;
        commons = new UICommonAction(driver);
    }

    public PurchaseOrderManagementPage inputSearchTerm(String searchTerm) {
        commons.sendKeys(loc_txtSearch, searchTerm);
        logger.info("Input '" + searchTerm + "' into Search box.");
        new HomePage(driver).waitTillSpinnerDisappear();
        return this;
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToManagePurchaseOrders(String permission, String url) {
        if (permission.contentEquals("A")) {
            Assert.assertTrue(commons.getCurrentURL().contains(url));
        } else if (permission.contentEquals("D")) {
            Assert.assertFalse(commons.getCurrentURL().contains(url));
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }

    /*-------------------------------------*/

    /*-------------------------------------*/
    // check permission
    // https://mediastep.atlassian.net/browse/BH-13849
    AllPermissions permissions;
    CheckPermission checkPermission;
    PurchaseOrderPage supplierPage;


    public void checkSupplierPermission(AllPermissions permissions) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // init crud purchase order POM
    }

    void checkViewPurchaseOrderList() {

    }

    void checkCreatePurchaseOrder() {

    }
}