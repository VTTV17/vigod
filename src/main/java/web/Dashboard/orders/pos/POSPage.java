package web.Dashboard.orders.pos;

import api.Seller.login.Login;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;

import java.time.Duration;

import static utilities.links.Links.DOMAIN;

public class POSPage extends POSElement {

    final static Logger logger = LogManager.getLogger(POSPage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    AssertCustomize assertCustomize;


    public POSPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        assertCustomize = new AssertCustomize(driver);
    }

    public void inputProductSearchTerm(String searchTerm) {
        commonAction.sendKeys(SEARCH_PRODUCT_BOX, searchTerm);
        logger.info("Input '" + searchTerm + "' into Search Product box.");
    }

    /*Verify permission for certain feature*/
    public void verifyPermissionToUsePOS(String permission) {
        if (permission.contentEquals("A")) {
            commonAction.switchToWindow(1);
            new POSPage(driver).inputProductSearchTerm("Test Permission");
            commonAction.closeTab();
            commonAction.switchToWindow(0);
        } else if (permission.contentEquals("D")) {
            Assert.assertEquals(commonAction.getAllWindowHandles().size(), 1);
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
    }
    /*-------------------------------------*/

    /*
        Create POS order with:
        - Normal/IMEI product with/without variation
        - With/without delivery
        - Apply some promotion
     */
    void navigateToInStorePurchasePage() {
        driver.get(DOMAIN + "/order/instore-purchase");
        driver.navigate().refresh();
        commonAction.waitUrlLoaded();
        logger.info("Open POS page.");
    }

    void addProductToCart() {

    }


    /*-------------------------------------*/
    /* Check permission */
    // ticket: https://mediastep.atlassian.net/browse/BH-24814
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    AllPermissions permissions;
    CheckPermission checkPermission;
    LoginDashboardInfo staffLoginInfo;

    public POSPage(WebDriver driver, AllPermissions permissions) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
        this.permissions = permissions;
        checkPermission = new CheckPermission(driver);
    }

    public POSPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        staffLoginInfo = new Login().getInfo(staffLoginInformation);
        return this;
    }

    void checkCreateOrder() {
        // navigate to POS page by URL
        navigateToInStorePurchasePage();

        // check permission
        if (permissions.getOrders().getPOSInstorePurchase().isCreateOrder()) {
            // check can access to POS page by URL
            assertCustomize.assertTrue(driver.getCurrentUrl().contains("/order/instore-purchase"), "Can not access to POS page.");

            // check others permission
            checkAddDirectDiscount();
            checkApplyDiscountCode();
            checkCreateDebtOrder();
            checkNotApplyEarningPoint();
        } else {
            assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(), "Restricted page is not shown.");
        }
    }

    void checkAddDirectDiscount() {
        // navigate to POS page by URL
        navigateToInStorePurchasePage();

        // check permission
        if (permissions.getOrders().getPOSInstorePurchase().isAddDirectDiscount()) {

        } else {}
    }

    void checkApplyDiscountCode() {
        // navigate to POS page by URL
        navigateToInStorePurchasePage();

        // check permission
        if (permissions.getOrders().getPOSInstorePurchase().isApplyDiscountCode()) {

        } else {}
    }

    void checkCreateDebtOrder() {
        // navigate to POS page by URL
        navigateToInStorePurchasePage();

        // check permission
        if (permissions.getOrders().getPOSInstorePurchase().isCreateDebtOrder()) {

        } else {}
    }

    void checkNotApplyEarningPoint() {
        // navigate to POS page by URL
        navigateToInStorePurchasePage();

        // check permission
        if (permissions.getOrders().getPOSInstorePurchase().isNotApplyEarningPoint()) {

        } else {}

    }
}
