package web.Dashboard.sales_channels.shopee.link_products;

import static utilities.links.Links.DOMAIN;

import java.util.ArrayList;
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

public class LinkProductsPage extends LinkProductsElement {

	final static Logger logger = LogManager.getLogger(LinkProductsPage.class);
	
	
    WebDriver driver;
    UICommonAction commonAction;

    public LinkProductsPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }
 
    public LinkProductsPage navigateByURL() {
		driver.get(DOMAIN + "/channel/shopee/link-products");
		
		//TODO handle navigation with different domains (VN/BIZ) if time allows
		return this;
    }    
    
	public LinkProductsPage inputSearchTerm(String searchTerm) {
		commonAction.inputText(loc_txtSearchBox, searchTerm);
        logger.info("Input '{}' into Search box.", searchTerm);
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}
	
	/**
	 * Automatically assigns each Shopee product variation to the first available GoSELL product variation in the dropdown list
	 * @return A list of lists containing the mapped variation names, where each inner list contains the Shopee variation name and the corresponding GoSELL variation name - something like [["L|Blue", "L,Yellow"], ["M|Blue","M,Yellow"]]
	 */
	public List<List<String>> linkVariations() {
		var varRowCount = commonAction.getElements(loc_tblLinkVariationRow(-1)).size();
		
		List<List<String>> mappedVariations = new ArrayList<>();
		for (int i=1; i<=varRowCount; i++) {
			commonAction.click(loc_ddlGosellVariation(i));
			commonAction.click(loc_ddvGosellVariation(i));
			mappedVariations.add(List.of(commonAction.getText(loc_tblShopeeVariationRow(i)), commonAction.getText(loc_ddlGosellVariation(i))));
		}
		return mappedVariations;
	}
	/**
	 * Links variations of a product between Shopee and GoSELL
	 * @param shopeeItemId The ID of the item on Shopee to be linked with GoSELL
	 * @param gosellProductName The name of the product on GoSELL to search for
	 * @return A list of lists containing the linked variations between Shopee and GoSELL, Eg. [["L|Blue", "L,Yellow"], ["M|Blue","M,Yellow"]]
	 */
	public List<List<String>> linkVariationsBetweenShopeeGosell(String shopeeItemId, String gosellProductName) {
		//Input GoSELL product name to search
		commonAction.inputText(loc_txtSearchGosellProductNameByShopeeItemId(shopeeItemId), gosellProductName);
		
		//Select the first result matching the product name
		commonAction.click(loc_ddvSearchResult(gosellProductName));
		
		new HomePage(driver).waitTillLoadingDotsDisappear();
		
		var mappedVariations = linkVariations();
		
		new ConfirmationDialog(driver).clickOKBtn();
		
		UICommonAction.sleepInMiliSecond(1000, "Wait a little");

		return mappedVariations;
	}
	
	/**
	 * Links a Shopee product to a GoSELL product
	 * @param shopeeItemId The ID of the item on Shopee to be linked with GoSELL
	 * @param gosellProductName The name of the product on GoSELL to search for
	 */
	public void linkShopeeProductToGosellProduct(String shopeeItemId, String gosellProductName) {
		//Input GoSELL product name to search
		commonAction.inputText(loc_txtSearchGosellProductNameByShopeeItemId(shopeeItemId), gosellProductName);
		
		//Select the first result matching the product name
		commonAction.click(loc_ddvSearchResult(gosellProductName));
		
		new HomePage(driver).waitTillLoadingDotsDisappear();
		
		UICommonAction.sleepInMiliSecond(1000, "Wait a little");
		
	}

	public LinkProductsPage tickProductByShopeeProductId(String shopeeProductId) {
		commonAction.click(loc_chkShopeeProductId(shopeeProductId));
        logger.info("Ticked Shopee Product Id '{}'", shopeeProductId);
		return this;
	}
	public LinkProductsPage clickSelectAction() {
		commonAction.click(loc_lnkSelectAction);
		logger.info("Clicked Select Action link text");
		return this;
	}	
	public LinkProductsPage clickUnlinkOption() {
		commonAction.click(loc_ddvUnlink);
		logger.info("Clicked 'Unlink'");
		return this;
	}	
	public void unlinkShopeeProductFromGosellProduct(List<String> shopeeProductIds) {
		
		shopeeProductIds.stream().forEach(id -> tickProductByShopeeProductId(id));
		clickSelectAction().clickUnlinkOption();
		
		new ConfirmationDialog(driver).clickOKBtn_V2();
		UICommonAction.sleepInMiliSecond(1000, "Wait a little");
		
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
