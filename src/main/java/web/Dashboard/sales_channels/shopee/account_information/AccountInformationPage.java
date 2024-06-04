package web.Dashboard.sales_channels.shopee.account_information;

import api.Seller.login.Login;
import api.Seller.sale_channel.shopee.APIReconnectShopeeAccount;
import api.Seller.sale_channel.shopee.APIShopeeManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;

import java.time.Duration;

import static utilities.links.Links.DOMAIN;

public class AccountInformationPage extends AccountInformationElement {

    final static Logger logger = LogManager.getLogger(AccountInformationPage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    public AccountInformationPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }

    public void clickDownloadShopeeProduct() {
        commonAction.click(loc_btnDownloadShopeeProduct);
        logger.info("Clicked on 'Download Shopee Product' button.");
    }

    /*-------------------------------------*/
    /* Check permission */
    // ticket: https://mediastep.atlassian.net/browse/BH-24822
    LoginInformation staffLoginInformation;
    LoginInformation sellerLoginInformation;
    LoginDashboardInfo staffLoginInfo;
    AllPermissions permissions;
    CheckPermission checkPermission;
    AssertCustomize assertCustomize;
    APIShopeeManagement apiShopeeManagementWithSellerToken;

    public AccountInformationPage(WebDriver driver, AllPermissions permissions) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
        this.permissions = permissions;
        checkPermission = new CheckPermission(driver);
    }

    public AccountInformationPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        staffLoginInfo = new Login().getInfo(staffLoginInformation);
        apiShopeeManagementWithSellerToken = new APIShopeeManagement(sellerLoginInformation);
        return this;
    }

    void navigateToShopeeAccountInformationPage() {
        driver.get("%s/channel/shopee/account/information".formatted(DOMAIN));
        driver.navigate().refresh();
        logger.info("Navigate to Shopee account information page by URL.");
    }

    void checkViewAccountInformation() {
        if (apiShopeeManagementWithSellerToken.isConnectedShopee()) {
            if (permissions.getShopee().isViewAccountInformation()) {
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/channel/shopee/account/information".formatted(DOMAIN),
                                "/channel/shopee/account/information"),
                        "Can not access to Shopee account information page.");
            } else {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/channel/shopee/account/information".formatted(DOMAIN)),
                        "Restricted page is not shown.");
            }
        }
    }

    void checkDownloadProduct() {
        // reconnect Shopee account
        new APIReconnectShopeeAccount(sellerLoginInformation).reconnectShopeeAccount();

        // navigate to Shopee account information
        navigateToShopeeAccountInformationPage();

        // click download product
        commonAction.click(loc_btnDownloadShopeeProduct);

        // check permission
        if (permissions.getShopee().isDownloadProductsBulkIndividual()) {
            assertCustomize.assertFalse(commonAction.getListElement(loc_icnProductDownloading).isEmpty(),
                    "Can not download Shopee product.");
        } else {
            assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(),
                    "Restricted popup is not shown.");
        }
    }

    void checkSyncOrders() {
        // reconnect Shopee account
        new APIReconnectShopeeAccount(sellerLoginInformation).reconnectShopeeAccount();

        // navigate to Shopee account information
        navigateToShopeeAccountInformationPage();

        // click sync order
        commonAction.click(loc_btnSyncShopeeOrder);

        // check permission
        if (permissions.getShopee().isSyncOrders()) {
            assertCustomize.assertFalse(commonAction.getListElement(loc_icnOrderSyncing).isEmpty(),
                    "Can not sync Shopee order.");
        } else {
            assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(),
                    "Restricted popup is not shown.");
        }
    }
}
