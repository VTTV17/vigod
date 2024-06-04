package web.Dashboard.sales_channels.shopee.account_management;

import api.Seller.login.Login;
import api.Seller.sale_channel.shopee.APIDisconnectShopeeAccount;
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
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;

import java.time.Duration;

import static utilities.links.Links.DOMAIN;

public class AccountManagementPage extends AccountManagementElement {

    final static Logger logger = LogManager.getLogger(AccountManagementPage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    public AccountManagementPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }

    public void clickDisconnect() {
        commonAction.click(loc_icnDisconnect);
        logger.info("Clicked on 'Disconnect Shopee' button.");
    }

    public void clickDeleteConnectedAccount() {
        commonAction.click(loc_icnRemoveAccount);
        logger.info("Clicked on 'Delete Connected Account' button.");
    }

    public void clickDeleteSyncedProductCheckbox() {
        commonAction.clickJS(loc_chkDeleteSyncedProduct);
        logger.info("Clicked on 'Delete Synced Products' check box.");
    }

    public void clickDeleteBtnInConfirmationDialog() {
        commonAction.click(loc_btnDelete);
        logger.info("Clicked on 'Delete' button in Confirmation Dialog.");
    }

    public void deleteAccount() {
        new HomePage(driver).hideFacebookBubble();
        clickDisconnect();
        new ConfirmationDialog(driver).clickOKBtn();
        clickDeleteConnectedAccount();
        clickDeleteSyncedProductCheckbox();
        clickDeleteBtnInConfirmationDialog();
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

    public AccountManagementPage(WebDriver driver, AllPermissions permissions) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
        this.permissions = permissions;
        checkPermission = new CheckPermission(driver);
    }

    public AccountManagementPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        staffLoginInfo = new Login().getInfo(staffLoginInformation);
        apiShopeeManagementWithSellerToken = new APIShopeeManagement(sellerLoginInformation);
        return this;
    }

    void navigateToShopeeAccountManagementPage() {
        driver.get("%s/channel/shopee/account/management".formatted(DOMAIN));
        driver.navigate().refresh();
        logger.info("Navigate to Shopee account management page by URL.");
    }

    void checkConnectAccount() {
        // navigate to Shopee account management page
        navigateToShopeeAccountManagementPage();
        if (permissions.getShopee().isConnectAccount()) {
            assertCustomize.assertFalse(checkPermission.checkAccessRestricted(loc_btnAddAccount),
                    "Can not connect new Shopee account.");
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnAddAccount),
                    "Restricted popup is not shown.");
        }
    }

    void checkDisconnectAccount() {
        // disconnect Shopee account
        new APIReconnectShopeeAccount(sellerLoginInformation).reconnectShopeeAccount();

        // navigate to Shopee account management page
        navigateToShopeeAccountManagementPage();
        if (!commonAction.getListElement(loc_icnDisconnect).isEmpty()) {
            commonAction.clickJS(loc_icnDisconnect, 0);
            if (permissions.getShopee().isDisconnectAccount()) {
                assertCustomize.assertFalse(commonAction.getListElement(loc_dlgConfirmDisconnectShopee).isEmpty(),
                        "Can not open Confirm disconnect Shopee account.");
            } else {
                assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(),
                        "Restricted popup is not shown.");
            }
        } else logger.warn("Can not found any Shopee account with CONNECTED status.");
    }


    void checkViewAccountInformation() {
        if (apiShopeeManagementWithSellerToken.isConnectedShopee()) {
            if (permissions.getShopee().isViewAccountInformation()) {
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/channel/shopee/account/management".formatted(DOMAIN),
                                "/channel/shopee/account/management"),
                        "Can not access to Shopee account management page.");

                // check connect account
                checkConnectAccount();

                // check disconnect account
                checkDisconnectAccount();
            } else {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/channel/shopee/account/management".formatted(DOMAIN)),
                        "Restricted page is not shown.");
            }
        }
    }

    void checkRemoveAccount() {
        // disconnect Shopee account
        new APIDisconnectShopeeAccount(sellerLoginInformation).disconnectShopeeAccount();

        // navigate Shopee account management page
        commonAction.clickJS(loc_icnRemoveAccount);
        if (permissions.getShopee().isRemoveAccount()) {
            assertCustomize.assertFalse(commonAction.getListElement(loc_dlgConfirmRemoveAccount).isEmpty(),
                    "Can not remove Shopee account.");
        } else {
            assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(),
                    "Restricted popup is not shown.");
        }
    }
}
