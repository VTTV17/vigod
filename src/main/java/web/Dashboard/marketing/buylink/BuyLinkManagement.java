package web.Dashboard.marketing.buylink;

import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

import api.Seller.login.Login;
import api.Seller.setting.BranchManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import utilities.assert_customize.AssertCustomize;
import utilities.enums.Domain;
import utilities.links.Links;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;
import web.Dashboard.marketing.landingpage.LandingPage;
import web.Dashboard.promotion.discount.product_discount_campaign.ProductDiscountCampaignPage;

import static utilities.links.Links.*;
import static utilities.links.Links.LOGIN_PATH;

public class BuyLinkManagement extends HomePage{

	final static Logger logger = LogManager.getLogger(BuyLinkManagement.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;
	Domain domain;
	SoftAssert soft = new SoftAssert();
	AssertCustomize assertCustomize;
	AllPermissions allPermissions;
	CreateBuyLink createBuyLink;
	LoginInformation loginInformation;
	public BuyLinkManagement(WebDriver driver) {
		super(driver);
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		assertCustomize = new AssertCustomize(driver);
		createBuyLink = new CreateBuyLink(driver);
	}
	public BuyLinkManagement(WebDriver driver, Domain domain) {
		super(driver);
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		assertCustomize = new AssertCustomize(driver);
		createBuyLink = new CreateBuyLink(driver);
		this.domain = domain;
	}
	By loc_btnExploreNow = By.cssSelector(".buylink-intro button");
	By loc_btnCreateBuyLink = By.cssSelector(".buylink button");
	By loc_lblBuyLinkManagementTitle = By.cssSelector("[role='heading']");
	By loc_lblUrlCol = By.xpath("//section[contains(@class,'gs-table-header-item')][2]/span");
	By loc_lblCouponCol = By.xpath("//section[contains(@class,'gs-table-header-item')][3]/span");
	By loc_lblCreateDateCol = By.xpath("//section[contains(@class,'gs-table-header-item')][4]/span");
	By loc_lblActionCol = By.xpath("//section[contains(@class,'gs-table-header-item')][5]/span");
	By loc_btnCopyLink = By.xpath("(//i[contains(@class,'gs-action-button')])[1]");
	By loc_btnEditLink = By.xpath("(//i[contains(@class,'gs-action-button')])[2]");
	By loc_btnDelete = By.xpath("(//i[contains(@class,'gs-action-button')])[3]");
	By loc_lst_lblUrl = By.xpath("//div[@class='gs-table-body-item text-truncate']");
	By loc_tltCopyLink = By.xpath("(//div[contains(@class,'gs-table-body-item action')])[1]/div[1]");
	By loc_tltEditLink = By.xpath("(//div[contains(@class,'gs-table-body-item action')])[1]/div[2]");
	By loc_tltDeleteLink = By.xpath("(//div[contains(@class,'gs-table-body-item action')])[1]/div[3]");
	By loc_dlgConfirmation_btnDelete = By.xpath("//div[@class='modal-footer']/button[2]");

	public BuyLinkManagement getLoginInfo(LoginInformation loginInformation){
		this.loginInformation = loginInformation;
		return this;
	}
	public BuyLinkManagement clickExploreNow() {
		commonAction.sleepInMiliSecond(500);
    	commonAction.click(loc_btnExploreNow);
    	logger.info("Clicked on 'Explore Now' button.");
    	return this;
    }    
    
    public CreateBuyLink clickCreateBuyLink() {
    	commonAction.click(loc_btnCreateBuyLink);
    	logger.info("Clicked on 'Create Buy Link' button.");
		commonAction.sleepInMiliSecond(500,"Wait popup show.");
    	return new CreateBuyLink(driver);
    }    	

    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateBuyLink(String permission) {
		if (permission.contentEquals("A")) {
			clickExploreNow().clickCreateBuyLink();
			boolean flag =  new CreateBuyLink(driver).isProductSelectionDialogDisplayed();
			commonAction.navigateBack();
			Assert.assertTrue(flag);
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }

    /*-------------------------------------*/
	public BuyLinkManagement VerifyText() throws Exception {
		Assert.assertEquals(commonAction.getText(loc_lblBuyLinkManagementTitle), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.pagetitle"));
		Assert.assertEquals(commonAction.getText(loc_btnCreateBuyLink), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.createBuyLinkBtn"));
		Assert.assertEquals(commonAction.getText(loc_lblUrlCol), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.urlCol"));
		Assert.assertEquals(commonAction.getText(loc_lblCouponCol), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.couponCol"));
		Assert.assertEquals(commonAction.getText(loc_lblCreateDateCol), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.createDate"));
		Assert.assertEquals(commonAction.getText(loc_lblActionCol), PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.actions"));
		commonAction.sleepInMiliSecond(500);
		Assert.assertEquals(commonAction.getAttribute(loc_tltCopyLink,"data-original-title"),PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.copyLinkTooltip"));
		Assert.assertEquals(commonAction.getAttribute(loc_tltEditLink,"data-original-title"),PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.editLinkTooltip"));
		Assert.assertEquals(commonAction.getAttribute(loc_tltDeleteLink,"data-original-title"),PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.table.deleteLinkTooltip"));
		return this;
	}
	public BuyLinkManagement clickOnCopyLink(){
		commonAction.click(loc_btnCopyLink);
		logger.info("Click on Copy link of the newest link (on the top)");
		return this;
	}
	public String getNewestBuyLinkURL(){
		commonAction.sleepInMiliSecond(1500);
		String URL = commonAction.getText(loc_lst_lblUrl,0);
		return URL;
	}
	public BuyLinkManagement verifyCreateBuyLinkSuccessfulMessage() throws Exception {
		waitTillLoadingDotsDisappear();
		Assert.assertEquals(getToastMessage(),PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.createSuccessfullyMessage"));
		return this;
	}
	public BuyLinkManagement verifyCopiedLink(String expectedLink) throws IOException, UnsupportedFlavorException {
		String copiedLink = commonAction.getCopiedText(loc_btnCopyLink);
		Assert.assertEquals(copiedLink,expectedLink);
		logger.info("Verify copied link");
		return this;
	}
	public BuyLinkManagement verifyCopiedMessage() throws Exception {
		Assert.assertEquals(getToastMessage(),PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.copySuccessfullyMessage"));
		logger.info("Verify toast message Copy successfully.");
		return this;
	}
	public CreateBuyLink clickEditNewestBuyLink(){
		commonAction.click(loc_btnEditLink);
		logger.info("Click on edit newest buy link");
		return new CreateBuyLink(driver);
	}
	public BuyLinkManagement verifyUpdateBuyLinkSuccessfulMessage() throws Exception {
		waitTillLoadingDotsDisappear();
		Assert.assertEquals(getToastMessage(),PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.updateSuccessfullyMessage"));
		return this;
	}
	public BuyLinkManagement clickDeleteNewestBuyLink(){
		commonAction.click(loc_btnDelete);
		logger.info("Click on delete newest buy link.");
		return this;
	}
	public BuyLinkManagement clickDeleteBtnOnModal(){
		commonAction.click(loc_dlgConfirmation_btnDelete);
		logger.info("Click on delete button on Delete Confirmation modal.");
		return this;
	}
	public BuyLinkManagement verifyAfterDeleteBuyLink(String linkBefore){
		waitTillLoadingDotsDisappear();
		Assert.assertNotEquals(getNewestBuyLinkURL(),linkBefore);
		logger.info("Verify newest buy link after deleted.");
		return this;
	}
	public boolean hasViewBuyLinkPers(){
		return allPermissions.getMarketing().getBuyLink().isViewBuyLinkList();
	}
	public boolean hasCreateBuyLinkPers(){
		return allPermissions.getMarketing().getBuyLink().isCreateBuyLink();
	}
	public boolean hasEditBuyLinkPers(){
		return allPermissions.getMarketing().getBuyLink().isEditBuyLink();
	}
	public boolean hasDeleteBuyLinkPers(){
		return allPermissions.getMarketing().getBuyLink().isDeleteBuyLink();
	}
	private boolean hasViewProductListPers() {
		return allPermissions.getProduct().getProductManagement().isViewProductList();
	}
	private boolean hasViewDiscountCodeListPers() {
		return allPermissions.getPromotion().getDiscountCode().isViewProductDiscountCodeList();
	}
	private boolean hasViewCreatedProductListPers() {
		return allPermissions.getProduct().getProductManagement().isCreateProduct();
	}
	public BuyLinkManagement navigateUrl(){
		var url = switch (domain) {
			case VN -> DOMAIN + BUY_LINK_PATH;
			case BIZ -> DOMAIN_BIZ + BUY_LINK_PATH;
			default -> throw new IllegalArgumentException("Unexpected value: " + domain);
		};
		commonAction.navigateToURL(url);
		logger.info("Navigate to url: "+url);
		commonAction.sleepInMiliSecond(500);
		return this;
	}
	public void checkPermissionViewBuyLinkList(){
		commonAction.sleepInMiliSecond(500);
		List<WebElement> buyLinkList = commonAction.getElements(loc_lst_lblUrl);
		if (hasViewBuyLinkPers()) {
			assertCustomize.assertTrue(buyLinkList.size() > 0, "[Failed] Buy link list should be shown");
		} else
			assertCustomize.assertTrue(buyLinkList.isEmpty(), "[Failed] Buy link list should not be shown");
		logger.info("Complete check View buy link list permission.");
	}
	public BuyLinkManagement checkViewBranchPermission(){
		List<Integer> branchIds = new Login().getInfo(loginInformation).getAssignedBranchesIds();
		List<String> branchNamesAssigned = new BranchManagement(loginInformation).getBranchNameById(branchIds);
		createBuyLink.clickOnBranchDropdown();
		List<String> branchListActual = new CreateBuyLink(driver).getBranchList();
		assertCustomize.assertEquals(branchListActual,branchNamesAssigned,
				"[Failed] Branch list expected: %s \nBranch list actual: %s".formatted(branchNamesAssigned,branchListActual));
		logger.info("Verified View Branch list permission.");
		return this;
	}
	public void checkPermissionCreateBuyLink(String productNameOfShopOwner, String productNameOfStaff){
		navigateUrl();
		if(hasCreateBuyLinkPers()){
			clickCreateBuyLink();
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(loc_btnCreateBuyLink,createBuyLink.loc_dlgProductSelection),
					"[Failed] Select product dialog not show when click on Create buy link button.");
			navigateUrl();
			clickCreateBuyLink();
			checkViewBranchPermission();
			checkPermissionViewProductList(productNameOfShopOwner,productNameOfStaff);
			navigateUrl();
			clickCreateBuyLink();
			checkPermissionViewDiscountList(productNameOfStaff);
			if(hasViewProductListPers()){
				navigateUrl();
				clickCreateBuyLink();
				createBuyLink.createASimpleBuyLink(productNameOfStaff);
				String toastMessage = new HomePage(driver).getToastMessage();
				try {
					assertCustomize.assertEquals(toastMessage,PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.createSuccessfullyMessage"),
							"[Failed] Create successfully message should be shown, but '%s' is shown.".formatted(toastMessage));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}else
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnCreateBuyLink),
					"[Failed] Restricted page not show when click on create buy link button.");
	}
	public void checkPermissionViewProductList(String productNameOfShopOwner, String productNameOfStaff) {
		if (hasViewProductListPers()) {
			assertCustomize.assertTrue(createBuyLink.isProductShowWhenSearch(productNameOfShopOwner),
					"[Failed]Product is created by shop owner: '%s' should be shown".formatted(productNameOfShopOwner));
			assertCustomize.assertTrue(createBuyLink.isProductShowWhenSearch(productNameOfStaff),
					"[Failed]Product is created by staff: '%s' should be shown".formatted(productNameOfStaff));
		} else if (hasViewCreatedProductListPers()) {
			assertCustomize.assertFalse(createBuyLink.isProductShowWhenSearch(productNameOfShopOwner),
					"[Failed]Product is created by shop owner: '%s' should not be shown".formatted(productNameOfShopOwner));
			assertCustomize.assertTrue(createBuyLink.isProductShowWhenSearch(productNameOfStaff),
					"[Failed]Product is created by: '%s' should be shown".formatted(productNameOfStaff));
		} else {
			assertCustomize.assertFalse(createBuyLink.isProductShowWhenSearch(productNameOfShopOwner),
					"[Failed]Product is created by shop owner: '%s' should not be shown".formatted(productNameOfShopOwner));
			assertCustomize.assertFalse(createBuyLink.isProductShowWhenSearch(productNameOfStaff),
					"[Failed]Product is created by staff: '%s' should not be shown".formatted(productNameOfStaff));
		}
		logger.info("Complete check View product commission.");
	}
	public void checkPermissionViewDiscountList(String productName){
		if(hasViewProductListPers()){
			createBuyLink.searchAndSelectProduct(productName)
					.clickOnNextBtn();
			List<WebElement> couponList = commonAction.getElements(createBuyLink.loc_lst_lblCouponName);
			if(hasViewDiscountCodeListPers()){
				assertCustomize.assertTrue(couponList.size() > 0, "[Failed] Discount code list should be shown");
			}else
				assertCustomize.assertTrue(couponList.isEmpty(), "[Failed] Discount code list should not be shown");
		}else logger.info("Don't has View product list permission, so can't check View discount code permission.");
	}
	public void checkPermissionEditBuyLink(String productNameOfShopOwner, String productNameOfStaff) {
		navigateUrl();
		if (hasViewBuyLinkPers()) {
			if (hasEditBuyLinkPers()) {
				clickEditNewestBuyLink();
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(loc_btnEditLink, createBuyLink.loc_dlgProductSelection),
						"[Failed] Select product dialog not show when click on Edit buy link button.");
				navigateUrl();
				clickEditNewestBuyLink();
				checkPermissionViewProductList(productNameOfShopOwner, productNameOfStaff);
				navigateUrl();
				clickEditNewestBuyLink();
				checkPermissionViewDiscountList(productNameOfStaff);
				if (hasViewProductListPers()) {
					navigateUrl();
					clickEditNewestBuyLink()
							.clickOnNextBtn()
							.clickOnFinishBTN();
					String toastMessage = new HomePage(driver).getToastMessage();
					try {
						assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.updateSuccessfullyMessage"),
								"[Failed] Updated successfully message should be shown, but '%s' is shown.".formatted(toastMessage));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			} else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnEditLink),
						"[Failed] Restricted page not show when click on edit buy link button.");
		}else logger.info("No permission View buy link list, so can't check Edit permission.");
	}
	public void checkPermissionDelete(){
		if(hasViewBuyLinkPers()){
			if(hasDeleteBuyLinkPers()){
				clickDeleteNewestBuyLink();
				String messagePopup = new ConfirmationDialog(driver).getPopUpContent();
				try {
					assertCustomize.assertEquals(messagePopup,PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.delete.popUpMessage"),
							"[Failed] Delete confirm message should be shown, but '%s' is shown.".formatted(messagePopup));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				clickDeleteBtnOnModal();
				String toastMessage = new HomePage(driver).getToastMessage();
				try {
					assertCustomize.assertEquals(toastMessage,PropertiesUtil.getPropertiesValueByDBLang("marketing.buyLink.management.updateSuccessfullyMessage"),
							"[Failed] Update successful mesage not show when delete");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnDelete),
						"[Failed] Restricted popup not show when click on detele icon.");
		}else logger.info("Don't have View buy link permission, so no need check Delete buy link permission.");
	}
	public BuyLinkManagement checkBuyLinkPermission(AllPermissions allPermissions,String productNameOfShopOwner, String productNameOfStaff){
		this.allPermissions = allPermissions;
		checkPermissionViewBuyLinkList();
		checkPermissionCreateBuyLink(productNameOfShopOwner, productNameOfStaff);
		checkPermissionEditBuyLink(productNameOfShopOwner, productNameOfStaff);
		checkPermissionDelete();
		AssertCustomize.verifyTest();;
		return this;
	}
}
