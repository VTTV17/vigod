package web.Dashboard.sales_channels.shopee.synchronization;

import api.Seller.login.Login;
import api.Seller.sale_channel.shopee.APIShopeeManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.sales_channels.shopee.account_information.AccountInformationPage;
import web.Dashboard.sales_channels.shopee.account_management.AccountManagementPage;

import java.time.Duration;

import static utilities.account.AccountTest.*;

public class ShopeeSynchronizationPage extends ShopeeSynchronizationElement {

    final static Logger logger = LogManager.getLogger(ShopeeSynchronizationPage.class);


    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();

    public ShopeeSynchronizationPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    public void waitTillPageFinishLoading() {
        for (int i = 0; i < 30; i++) {
            if (!commonAction.getElements(loc_imgShopeeIntroBackground).isEmpty()) break;
            commonAction.sleepInMiliSecond(500);
        }
    }

    public boolean isConnectShopeeBtnDisplayed() {
        commonAction.sleepInMiliSecond(500);
        return commonAction.isElementDisplay(loc_btnConnectShopee);
    }

    public void clickConnectShopee() {
        commonAction.click(loc_btnConnectShopee);
        logger.info("Clicked on 'Connect Shopee' button.");
    }

    public void verifyPermissionToConnectShopee(String permission) {
        if (permission.contentEquals("A")) {
            if (isConnectShopeeBtnDisplayed()) {
                clickConnectShopee();
                new utilities.thirdparty.Shopee(driver).performLogin(SHOPEE_COUNTRY, SHOPEE_USERNAME, SHOPEE_PASSWORD);
                new HomePage(driver).navigateToPage("Account Information");
                new AccountInformationPage(driver).clickDownloadShopeeProduct();
                new HomePage(driver).navigateToPage("Account Management");
                new AccountManagementPage(driver).deleteAccount();
            }
        } else if (permission.contentEquals("D")) {
            // Not reproducible
        } else {
            Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
        }
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

    public ShopeeSynchronizationPage(WebDriver driver, AllPermissions permissions) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
        this.permissions = permissions;
        checkPermission = new CheckPermission(driver);
    }

    public ShopeeSynchronizationPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        staffLoginInfo = new Login().getInfo(staffLoginInformation);
        apiShopeeManagementWithSellerToken = new APIShopeeManagement(sellerLoginInformation);
        return this;
    }

    public void checkShopeePermission() {
    }

    void checkConnectAccount() {
        if (!apiShopeeManagementWithSellerToken.isConnectedShopee()) {
            if (permissions.getShopee().isConnectAccount()) {
                assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully(loc_btnConnectShopee,
                        "https://account.seller.shopee.com/signin/oauth/"), "Can not access to add Shopee account page.");
            } else {
                assertCustomize.assertTrue(checkPermission.checkAccessRestricted(loc_btnConnectShopee),
                        "Restricted popup is not shown.");
            }
        }
    }

    void checkViewProducts() {

    }

    void checkViewProductLinking() {

    }

    void checkLinkProduct() {

    }

    void checkCreateShopeeProductToGosSELL() {

    }

    void checkUpdateShopeeProductToGoSELL() {

    }

    void checkPurchaseConnection() {

    }

    void checkRenewConnection() {

    }

    void checkUpgradeConnection() {

    }

    void checkUnlinkProduct() {

    }

    void checkSetting() {

    }
}
