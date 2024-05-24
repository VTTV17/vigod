package web.Dashboard.orders.pos;

import api.Seller.login.Login;
import api.Seller.products.all_products.APISuggestionProduct;
import api.Seller.products.all_products.APISuggestionProduct.SuggestionProductsInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
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
import java.util.Optional;

import static org.apache.commons.lang.math.JVMRandom.nextLong;
import static utilities.links.Links.DOMAIN;
import static web.Dashboard.orders.pos.POSElement.SearchType.barcode;
import static web.Dashboard.orders.pos.POSElement.SearchType.getAllSearchType;

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
        commonAction.sendKeys(loc_txtSearchProduct, searchTerm);
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
        logger.info("Open POS page.");
    }

    void selectBranch(SuggestionProductsInfo productsInfo) {
        // open branch dropdown
        commonAction.clickJS(loc_ddvSelectedBranch);

        // select branch
        commonAction.clickJS(By.xpath(str_ddvBranches.formatted(productsInfo.getBranchName())));

        // confirm switch branch
        if (!commonAction.getListElement(loc_dlgConfirmChangeBranch).isEmpty()) {
            commonAction.click(loc_dlgConfirmChangeBranch_btnOK);
        }

        // log
        logger.info("Select branch: %s.".formatted(productsInfo.getBranchName()));
    }

    void addProductToCart(SuggestionProductsInfo productsInfo) {
        // open search type list
        commonAction.clickJS(loc_ddvSelectedSearchType);

        // change search type to search by barcode
        commonAction.clickJS(loc_ddlSearchType, getAllSearchType().indexOf(barcode));

        // search product
        String keyword = (productsInfo.getModelId() == 0)
                ? "%s".formatted(productsInfo.getItemId())
                : "%s-%s".formatted(productsInfo.getItemId(), productsInfo.getModelId());
        commonAction.sendKeys(loc_txtSearchProduct, keyword);
        logger.info("Search keyword: %s.".formatted(keyword));

        // select product
        commonAction.click(loc_ddlSearchResult, 0);

        // input quantity
        long quantity = nextLong(Math.max(Optional.ofNullable(productsInfo.getRemainingStock()).orElse(0L), 1L)) + 1L;
        logger.info("Input cart quantity: %,d.".formatted(quantity));

        // select lot if any
        if (!commonAction.getListElement(loc_tblCart_lnkSelectLot).isEmpty()) {
            // open select lot popup
            commonAction.clickJS(loc_tblCart_lnkSelectLot, 0);

            // select lot
            if (!commonAction.getListElement(loc_dlgSelectLot).isEmpty()) {
                if (!commonAction.getListElement(loc_dlgSelectLot_txtConfirmQuantity).isEmpty()) {
                    int bound = commonAction.getListElement(loc_dlgSelectLot_txtConfirmQuantity).size();
                    for (int lotIndex = 0; lotIndex < bound; lotIndex++) {
                        if (quantity > 0) {
                            // input quantity
                            long availableQuantity = Long.parseLong(commonAction.getText(loc_dlgSelectLot_lblAvailableQuantity));
                            commonAction.sendKeys(loc_dlgSelectLot_txtConfirmQuantity, String.valueOf(Math.min(quantity, availableQuantity)));
                            logger.info("Input lot quantity: %,d.".formatted(quantity));

                            // get current quantity
                            quantity -= Math.min(quantity, availableQuantity);
                        }
                    }

                    // save changes
                    commonAction.click(loc_dlgSelectLot_btnConfirm);
                } else logger.warn("Can not found lot.");
            }
        }
    }

    void checkAddStock() {
        // navigate to POS page
        navigateToInStorePurchasePage();

        // get product for add stock on POS
        SuggestionProductsInfo productsInfo = new APISuggestionProduct(staffLoginInformation).findProductInformationForAddStockOnPOS();

        if (productsInfo.getItemId() != 0) {
            // add product to cart
            addProductToCart(productsInfo);

            // check icon add stock show or not
            assertCustomize.assertFalse(commonAction.getListElement(loc_icnAddStock).isEmpty(), "Product out of stock, but add stock label does not show.");

            // check permission
            if (!commonAction.getListElement(loc_icnAddStock).isEmpty()) {
                if (permissions.getProduct().getInventory().isUpdateStock()) {
                    
                }
            }
        }
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

        } else {
        }
    }

    void checkApplyDiscountCode() {
        // navigate to POS page by URL
        navigateToInStorePurchasePage();

        // check permission
        if (permissions.getOrders().getPOSInstorePurchase().isApplyDiscountCode()) {

        } else {
        }
    }

    void checkCreateDebtOrder() {
        // navigate to POS page by URL
        navigateToInStorePurchasePage();

        // check permission
        if (permissions.getOrders().getPOSInstorePurchase().isCreateDebtOrder()) {

        } else {
        }
    }

    void checkNotApplyEarningPoint() {
        // navigate to POS page by URL
        navigateToInStorePurchasePage();

        // check permission
        if (permissions.getOrders().getPOSInstorePurchase().isNotApplyEarningPoint()) {

        } else {
        }

    }
}
