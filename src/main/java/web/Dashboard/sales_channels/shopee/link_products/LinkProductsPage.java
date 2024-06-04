package web.Dashboard.sales_channels.shopee.link_products;

import java.time.Duration;

import api.Seller.login.Login;
import api.Seller.sale_channel.shopee.APIShopeeManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.assert_customize.AssertCustomize;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;
import web.Dashboard.sales_channels.shopee.synchronization.ShopeeSynchronizationPage;

import static utilities.links.Links.DOMAIN;

public class LinkProductsPage extends LinkProductsElement {

	final static Logger logger = LogManager.getLogger(LinkProductsPage.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    public LinkProductsPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }
    
	public LinkProductsPage inputSearchTerm(String searchTerm) {
		commonAction.inputText(loc_txtSearchBox, searchTerm);
        logger.info("Input '{}' into Search box.", searchTerm);
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
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

    public LinkProductsPage(WebDriver driver, AllPermissions permissions) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
        this.permissions = permissions;
        checkPermission = new CheckPermission(driver);
    }

    public LinkProductsPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        staffLoginInfo = new Login().getInfo(staffLoginInformation);
        apiShopeeManagementWithSellerToken  = new APIShopeeManagement(sellerLoginInformation);
        return this;
    }

    void checkViewProductLinking() {
        if (permissions.getShopee().isViewProductLinking()) {
            assertCustomize.assertTrue(checkPermission.checkAccessedSuccessfully("%s/channel/shopee/link-products".formatted(DOMAIN),
                            "shopee/link-products"),
                    "Can not access to Shopee product linking page.");
        } else {
            assertCustomize.assertTrue(checkPermission.checkAccessRestricted("%s/channel/shopee/link-products".formatted(DOMAIN)),
                    "Restricted page is not shown.");
        }
    }
    
}
