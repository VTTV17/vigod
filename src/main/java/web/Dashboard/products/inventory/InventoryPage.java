package web.Dashboard.products.inventory;

import api.Seller.products.all_products.CreateProduct;
import api.Seller.products.inventory.Inventory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.products.inventory.history.InventoryHistoryPage;

import java.util.HashSet;
import java.util.List;

import static utilities.links.Links.DOMAIN;

public class InventoryPage extends InventoryElement {
    WebDriver driver;
    UICommonAction commons;
    HomePage homePage;

    final static Logger logger = LogManager.getLogger(InventoryPage.class);

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        commons = new UICommonAction(driver);
        homePage = new HomePage(driver);
    }

    public InventoryPage navigate() {
        homePage.navigateToPage("Products", "Inventory");
        return this;
    }

    public InventoryHistoryPage clickInventoryHistory() {
        commons.click(loc_btnInventoryHistory);
        logger.info("Clicked on 'Inventory History' button.");
        homePage.waitTillSpinnerDisappear1();
        return new InventoryHistoryPage(driver);
    }

    void navigateToInventoryPage() {
        if (!driver.getCurrentUrl().contains("/inventory/list")) {
            driver.get(DOMAIN + "/inventory/list");
        }
    }

    public void navigateToInventoryHistoryPage() {
        if (!driver.getCurrentUrl().contains("/product/export-history")) {
            navigateToInventoryPage();
            commons.click(loc_btnInventoryHistory);
        }
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToSeeInventoryHistory(String permission, String url) {
        clickInventoryHistory();
        if (permission.contentEquals("A")) {
            Assert.assertTrue(commons.getCurrentURL().contains(url));
        } else if (permission.contentEquals("D")) {
            // Not reproducible
        } else {
            Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
        }
    }

    /*-------------------------------------*/
    // check permission
    // https://mediastep.atlassian.net/browse/BH-13841
    AllPermissions permissions;
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    AssertCustomize assertCustomize;
    CheckPermission checkPermission;

    void checkViewProductInventory(AllPermissions permissions, int notCreatedProductId, int createdProductId) {
        // navigate to inventory page
        navigateToInventoryPage();

        // get list productId in inventory
        List<Integer> dbProductList = new Inventory(staffLoginInformation).getInventoryInformation().getProductIds();

        // GET the product inventory from API.
        if (permissions.getProduct().getInventory().isViewProductInventory()) {
            List<Integer> checkData = (createdProductId != 0)
                    ? List.of(createdProductId, notCreatedProductId)
                    : List.of(notCreatedProductId);
            assertCustomize.assertTrue(new HashSet<>(dbProductList).containsAll(checkData), "List inventory must be contains: %s, but found list product: %s.".formatted(checkData.toString(), dbProductList.toString()));
        } else if (permissions.getProduct().getInventory().isViewCreatedProductInventory()) {
            if (createdProductId != 0)
                assertCustomize.assertTrue(new HashSet<>(dbProductList).contains(createdProductId), "List inventory must be contains: %s, but found list product: %s.".formatted(createdProductId, dbProductList.toString()));
            assertCustomize.assertFalse(new HashSet<>(dbProductList).contains(notCreatedProductId), "List inventory must not contains: %s, but found list product: %s.".formatted(notCreatedProductId, dbProductList.toString()));
        } else {
            assertCustomize.assertTrue(dbProductList.isEmpty(), "All inventory must be hidden, but found: %s.".formatted(dbProductList.toString()));
        }
        logger.info("Check permission: Product >> Inventory >> View product inventory.");
        logger.info("Check permission: Product >> Product management >> View created product inventory.");
    }

    void checkViewInventoryHistory() {
        navigateToInventoryPage();
        if (permissions.getProduct().getInventory().isViewInventoryHistory()) {
            // navigate to inventory history page
            navigateToInventoryHistoryPage();

            // check export/download export inventory history
            new InventoryHistoryPage(driver).checkExportInventoryHistory(permissions, assertCustomize, checkPermission, this);
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnInventoryHistory), "Restricted page is not shown.");
        }
        logger.info("Check permission: Product >> Inventory >> View Inventory history.");
    }

    void checkUpdateStock() {
        // get list productId in inventory
        List<Integer> dbProductList = new Inventory(staffLoginInformation).getInventoryInformation().getProductIds();

        if (!dbProductList.isEmpty()) {
            navigateToInventoryPage();
            if (permissions.getProduct().getInventory().isUpdateStock()) {
                commons.openPopupJS(loc_lnkRemainingStock, 0, loc_dlgUpdateStock);
                commons.closePopup(loc_dlgUpdateStock_btnCancel);
            } else {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_lnkRemainingStock, 0), "Restricted popup is not shown.");
            }
        }
        logger.info("Check permission: Product >> Inventory >> Update stock.");
    }

    public InventoryPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.staffLoginInformation = staffLoginInformation;
        this.sellerLoginInformation = sellerLoginInformation;
        return this;
    }


    public void checkInventoryPermission(AllPermissions permissions) {
        // get staff permission
        this.permissions = permissions;

        // init commons check no permission
        checkPermission = new CheckPermission(driver);

        // init customize assert
        this.assertCustomize = new AssertCustomize(driver);

        // get productId is created by staff
        int createdProductId = permissions.getProduct().getProductManagement().isCreateProduct()
                ? new CreateProduct(staffLoginInformation).createWithoutVariationProduct(false, 1000).getProductID()
                : 0;

        // get productId is created by seller
        int notCreatedProductId = new CreateProduct(sellerLoginInformation).createWithoutVariationProduct(false, 1000).getProductID();

        // check view product inventory
        checkViewProductInventory(permissions,  notCreatedProductId, createdProductId);

        // check view inventory history
        checkViewInventoryHistory();

        // check update stock
        checkUpdateStock();
    }

}
