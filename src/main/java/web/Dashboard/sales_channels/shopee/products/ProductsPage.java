package web.Dashboard.sales_channels.shopee.products;

import static utilities.links.Links.DOMAIN;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import api.Seller.login.Login;
import api.Seller.sale_channel.shopee.APIShopeeManagement;
import utilities.assert_customize.AssertCustomize;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;

public class ProductsPage extends ProductsElement{

	final static Logger logger = LogManager.getLogger(ProductsPage.class);
	
    WebDriver driver;
    UICommonAction commonAction;

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }
   
    public ProductsPage navigateByURL() {
		driver.get(DOMAIN + "/channel/shopee/product/list");
		
		//TODO handle navigation with different domains (VN/BIZ) if time allows
		return this;
    }
    
	public ProductsPage inputSearchTerm(String searchTerm) {
		commonAction.inputText(loc_txtSearchBox, searchTerm);
        logger.info("Input '{}' into Search box.", searchTerm);
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}
	
	public ProductsPage tickProductByShopeeProductId(String shopeeProductId) {
		commonAction.click(loc_chkShopeeProductId(shopeeProductId));
        logger.info("Ticked Shopee Product Id '{}'", shopeeProductId);
		return this;
	}
	public ProductsPage clickSelectAction() {
		commonAction.click(loc_lnkSelectAction);
		logger.info("Clicked Select Action link text");
		return this;
	}	
	public ProductsPage clickCreateProductToGosellBtn() {
		commonAction.click(loc_ddvCreateProductToGoSELL);
		logger.info("Clicked Create Product To Gosell button");
		return this;
	}	
	public ProductsPage clickUpdateProductToGosellBtn() {
		commonAction.click(loc_ddvUpdateProductToGoSELL);
		logger.info("Clicked Update Product To Gosell button");
		return this;
	}	
	public ProductsPage createProductToGosellBtn(List<String> shopeeProductIds) {
		
		shopeeProductIds.stream().forEach(id -> tickProductByShopeeProductId(id));
		
		clickSelectAction().clickCreateProductToGosellBtn();
		new ConfirmationDialog(driver).clickOKBtn();
		
		waitUntilSyncStatusIconDisappear();
		return this;
	}	
	public ProductsPage createProductToGosellBtn(String shopeeProductId) {
		return createProductToGosellBtn(List.of(shopeeProductId));
	}

	public ProductsPage updateProductToGosellBtn(List<String> shopeeProductIds) {
		
		shopeeProductIds.stream().forEach(id -> tickProductByShopeeProductId(id));
		
		clickSelectAction().clickUpdateProductToGosellBtn();
		new ConfirmationDialog(driver).clickOKBtn();
		
		waitUntilSyncStatusIconDisappear();
		return this;
	}	
	
	void waitUntilSyncStatusIconDisappear() {
		//TODO consider removing this sleep if possible
		UICommonAction.sleepInMiliSecond(1000, "Wait a little for sync status icon to appear");
		
		for (int attempt=0; attempt <30; attempt++) {
			if (commonAction.getElements(loc_icnSyncStatus).isEmpty()) {
				logger.debug("Sync status icon has disappeared"); break;
			}
			commonAction.refreshPage();
			UICommonAction.sleepInMiliSecond(6000, "Wait until sync status icon disappears");
		}
	}
	
	public ProductsPage downloadSpecificProduct(String shopeeProductId) {
		commonAction.click(loc_icnDownloadProduct(shopeeProductId));
        logger.info("Downloaded Shopee Product Id '{}'", shopeeProductId);
        new HomePage(driver).waitTillLoadingDotsDisappear();
		return this;
	}
	public ProductsPage downloadSpecificProduct(List<String> shopeeProductIds) {
		shopeeProductIds.stream().forEach(id -> downloadSpecificProduct(id));
		return this;
	}
	
    /*-------------------------------------*/
    /* Check permission */
    // ticket: https://mediastep.atlassian.net/browse/BH-24822
    LoginInformation staffLoginInformation, sellerLoginInformation;
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
