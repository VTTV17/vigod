package web.Dashboard.sales_channels.shopee.products;

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
import web.Dashboard.sales_channels.shopee.link_products.LinkProductsPage;

public class ProductsPage extends ProductsElement{

	final static Logger logger = LogManager.getLogger(ProductsPage.class);
	
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;


    public ProductsPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
    }
    
	public ProductsPage inputSearchTerm(String searchTerm) {
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

    public ProductsPage(WebDriver driver, AllPermissions permissions) {
        this.driver = driver;
        assertCustomize = new AssertCustomize(driver);
        commonAction = new UICommonAction(driver);
        this.permissions = permissions;
        checkPermission = new CheckPermission(driver);
    }

    public ProductsPage getLoginInformation(LoginInformation sellerLoginInformation, LoginInformation staffLoginInformation) {
        this.sellerLoginInformation = sellerLoginInformation;
        this.staffLoginInformation = staffLoginInformation;
        staffLoginInfo = new Login().getInfo(staffLoginInformation);
        apiShopeeManagementWithSellerToken  = new APIShopeeManagement(sellerLoginInformation);
        return this;
    }

    void checkDownloadProduct() {
        if (!commonAction.getListElement(loc_icnDownloadProduct).isEmpty()) {
            // click download product icon
            commonAction.clickJS(loc_icnDownloadProduct, 0);
            if (permissions.getShopee().isDownloadProductsBulkIndividual()) {
                assertCustomize.assertFalse(commonAction.getListElement(loc_spnLoading).isEmpty(),
                        "Can not download Shopee product.");
            } else {
                assertCustomize.assertTrue(checkPermission.isAccessRestrictedPresent(),
                        "Restricted popup is not shown.");
            }
        } else logger.warn("Can not found any Shopee product to check download.");
    }
}
