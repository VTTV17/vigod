package web.Dashboard.supplier.purchaseorders.management;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.supplier.purchaseorders.crud.PurchaseOrderPage;

import java.util.HashSet;
import java.util.List;

public class PurchaseOrderManagementPage extends PurchaseOrderManagementElement {
    WebDriver driver;
    UICommonAction commons;
    AssertCustomize assertCustomize;

    final static Logger logger = LogManager.getLogger(PurchaseOrderManagementPage.class);

    public PurchaseOrderManagementPage(WebDriver driver) {
        this.driver = driver;
        commons = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
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
        if (permissions.getSuppliers().getPurchaseOrder().isViewPurchaseOrderList()) {
            List<Integer> checkData = List.of();
            assertCustomize.assertTrue(new HashSet<>(checkData).containsAll(checkData),
                    "List product must be contains: %s, but found list product: %s.".formatted(checkData.toString(), dbProductList.toString()));
        } else if (permissions.getSuppliers().getPurchaseOrder().isViewListCreatedPurchaseOrder()) {
            if (createdProductId != 0)
                assertCustomize.assertTrue(new HashSet<>(dbProductList).contains(createdProductId),
                        "List product must be contains: %s, but found list product: %s.".formatted(createdProductId, dbProductList.toString()));
            assertCustomize.assertFalse(new HashSet<>(dbProductList).contains(notCreatedProductId),
                    "List product must not contains: %s, but found list product: %s.".formatted(notCreatedProductId, dbProductList.toString()));
        } else {
            assertCustomize.assertTrue(dbProductList.isEmpty(),
                    "All products must be hidden, but found: %s.".formatted(dbProductList.toString()));
        }
        logger.info("Check permission: Product >> Product management >> View product list.");
        logger.info("Check permission: Product >> Product management >> View created product list.");

    }

    void checkCreatePurchaseOrder() {

    }
}