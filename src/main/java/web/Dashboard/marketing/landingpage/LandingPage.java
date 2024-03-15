package web.Dashboard.marketing.landingpage;

import java.awt.*;
import java.time.Duration;
import java.util.List;

import api.Seller.marketing.APILandingPage;
import com.github.dockerjava.api.model.Link;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import utilities.assert_customize.AssertCustomize;
import utilities.links.Links;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;
import web.Dashboard.promotion.discount.DiscountPage;
import web.Dashboard.promotion.discount.product_discount_campaign.ProductDiscountCampaignPage;

public class LandingPage extends LandingPageElement {
	final static Logger logger = LogManager.getLogger(LandingPage.class);
	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;
	LandingPageElement landingPageUI;
	AssertCustomize assertCustomize;
	AllPermissions allPermissions;
	CreateLandingPage createLandingPage;
	LoginInformation loginInfo;

	public LandingPage(WebDriver driver) {
		super(driver);
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		landingPageUI = new LandingPageElement(driver);
		assertCustomize = new AssertCustomize(driver);
		createLandingPage = new CreateLandingPage(driver);
		PageFactory.initElements(driver, this);
	}

	public LandingPage getLoginInformation(LoginInformation loginInfo) {
		this.loginInfo = loginInfo;
		return this;
	}

	public CreateLandingPage clickCreateLandingPage() {
		commonAction.clickElement(landingPageUI.CREATE_PAGE_LANDING_BTN);
		logger.info("Clicked on 'Create New Landing Page' button");
		return new CreateLandingPage(driver);
	}

	public boolean isPermissionModalDisplay() {
		commonAction.sleepInMiliSecond(500);
		return commonAction.isElementDisplay(landingPageUI.PERMISSION_MODAL);
	}

	public LandingPage closeModal() {
		commonAction.clickElement(landingPageUI.CLOSE_MODAL_BTN);
		return this;
	}

	/*Verify permission for certain feature*/
	public void verifyPermissionToCreateLandingPage(String permission, String url) {
		if (permission.contentEquals("A")) {
			clickCreateLandingPage().clickCancelBtn();
			new ConfirmationDialog(driver).clickOKBtn();
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commonAction.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
	}

	public void verifyPermissionToAddAnalyticsToLandingPage(String permission, String url) {
		if (permission.contentEquals("A")) {
			clickCreateLandingPage().inputGoogleAnalyticsId("123456");
			new HomePage(driver).waitTillLoadingDotsDisappear();
			new CreateLandingPage(driver).clickCancelBtn();
			new ConfirmationDialog(driver).clickOKBtn();
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commonAction.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
	}

	public void verifyPermissionToAddSEOToLandingPage(String permission, String url) {
		if (permission.contentEquals("A")) {
			clickCreateLandingPage().inputSEOTitle("Test Permission").clickCancelBtn();
			new ConfirmationDialog(driver).clickOKBtn();
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commonAction.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
	}

	public void verifyPermissionToAddCustomerTagToLandingPage(String permission, String url) {
		if (permission.contentEquals("A")) {
			clickCreateLandingPage().inputCustomerTag("Test Permission").clickCancelBtn();
			new ConfirmationDialog(driver).clickOKBtn();
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commonAction.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
	}

	public void verifyPermissionToCustomDomain(String permission, String url) {
		if (permission.contentEquals("A")) {
			clickCreateLandingPage().inputSubDomain("testdomain@gmail.com").clickCancelBtn();
			new ConfirmationDialog(driver).clickOKBtn();
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commonAction.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
	}

	/*-------------------------------------*/
	private boolean hasViewLandingPageListPers() {
		return allPermissions.getMarketing().getLandingPage().isViewLandingPageList();
	}

	private boolean hasCreateLandingPagePers() {
		return allPermissions.getMarketing().getLandingPage().isCreateLandingPage();
	}

	private boolean hasEditLandingPagePers() {
		return allPermissions.getMarketing().getLandingPage().isEditLandingPage();
	}

	private boolean hasPublishLandingPagePers() {
		return allPermissions.getMarketing().getLandingPage().isPublishLandingPage();
	}

	private boolean hasUnPublishLandingPagePers() {
		return allPermissions.getMarketing().getLandingPage().isUnpublishLandingPage();
	}

	private boolean hasDeleteLandingPagePers() {
		return allPermissions.getMarketing().getLandingPage().isDeleteLandingPage();
	}

	private boolean hasViewDetailLandingPagePers() {
		return allPermissions.getMarketing().getLandingPage().isViewDetailLandingPage();
	}

	private boolean hasCloneLandingPagePers() {
		return allPermissions.getMarketing().getLandingPage().isCloneLandingPage();
	}

	private boolean hasViewProductListPers() {
		return allPermissions.getProduct().getProductManagement().isViewProductList();
	}

	private boolean hasViewCreatedProductListPers() {
		return allPermissions.getProduct().getProductManagement().isCreateProduct();
	}

	public void navigateUrl() {
		String url = Links.DOMAIN + "/marketing/landing-page/list";
		commonAction.navigateToURL(url);
		logger.info("Navigate to url: " + url);
		commonAction.sleepInMiliSecond(200);
	}

	public void filterStatus(String status) {
		commonAction.click(loc_ddlStatus);
		switch (status) {
			case "Published" -> commonAction.click(loc_ddvStatus, 1);
			case "Draft" -> commonAction.click(loc_ddvStatus, 2);
			default -> {
				commonAction.click(loc_ddvStatus, 0);
				logger.info("Select All status");
			}
		}
		commonAction.sleepInMiliSecond(500, "Wait in filter status.");
	}
	public void checkPermissionViewLandingPageList() {
		navigateUrl();
		List<WebElement> landingPageNames = commonAction.getElements(loc_lst_lblName);
		if (hasViewLandingPageListPers()) {
			assertCustomize.assertTrue(landingPageNames.size() > 0, "[Failed]Landing page list should be shown");
		} else
			assertCustomize.assertTrue(landingPageNames.isEmpty(), "[Failed] Landing page should not be shown");
		logger.info("Complete check View landing page list permission.");
	}

	public void checkPermissionCreateLandingPage(String productNameOfShopOwner, String productNameOfStaff) {
		navigateUrl();
		if (hasCreateLandingPagePers()) {
			clickCreateLandingPage();
			checkPermissionViewProductList(productNameOfShopOwner, productNameOfStaff);
			createLandingPage.createLandingPage();
			String toastMessage = new HomePage(driver).getToastMessage();
			try {
				assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("marketing.landingPage.create.successMessage"),
						"[Failed]Create succeessfull message should be show, but '%s' is shown".formatted(toastMessage));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnCreateLandingPage),
					"[Failed]Restricted page should be shown when click Create landing page.");
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(Links.DOMAIN + "/marketing/landing-page/create"),
					"[Failed]Restricted page should be shown when click navigate to create landing page url.");
			navigateUrl();
			commonAction.click(loc_icnShowMoreAction);
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_ddvAction, 2),
					"[Failed]Restricted page should be shown when click Clone landing page.");
		}
		logger.info("Complete check Create landing page permission.");
	}

	public void checkPermissionViewProductList(String productNameOfShopOwner, String productNameOfStaff) {
		createLandingPage.selectCheckoutTemplate();
		if (hasViewProductListPers()) {
			assertCustomize.assertTrue(createLandingPage.isProductShowWhenSearch(productNameOfShopOwner),
					"[Failed]Product is created by shop owner: '%s' should be shown".formatted(productNameOfShopOwner));
			assertCustomize.assertTrue(createLandingPage.isProductShowWhenSearch(productNameOfStaff),
					"[Failed]Product is created by staff: '%s' should be shown".formatted(productNameOfStaff));
		} else if (hasViewCreatedProductListPers()) {
			assertCustomize.assertFalse(createLandingPage.isProductShowWhenSearch(productNameOfShopOwner),
					"[Failed]Product is created by shop owner: '%s' should not be shown".formatted(productNameOfShopOwner));
			assertCustomize.assertTrue(createLandingPage.isProductShowWhenSearch(productNameOfStaff),
					"[Failed]Product is created by: '%s' should be shown".formatted(productNameOfStaff));
		} else {
			assertCustomize.assertFalse(createLandingPage.isProductShowWhenSearch(productNameOfShopOwner),
					"[Failed]Product is created by shop owner: '%s' should not be shown".formatted(productNameOfShopOwner));
			assertCustomize.assertFalse(createLandingPage.isProductShowWhenSearch(productNameOfStaff),
					"[Failed]Product is created by staff: '%s' should not be shown".formatted(productNameOfStaff));
		}
		logger.info("Complete check View product commission.");
	}

	public void checkPermissionEditLandingPage(String productNameOfShopOwner, String productNameOfStaff,int landingPageId) {
		String editUrl = Links.DOMAIN + "/marketing/landing-page/edit/" + landingPageId;
		if (hasViewDetailLandingPagePers()) {
			if (hasViewLandingPageListPers()) {
				navigateUrl();
				filterStatus("Draft");
				commonAction.click(loc_icnEdit);
				if (hasEditLandingPagePers()) {
					commonAction.sleepInMiliSecond(1000);
					commonAction.click(createLandingPage.loc_btnSave);
					String toastMessage = new HomePage(driver).getToastMessage();
					try {
						assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("marketing.landingPage.edit.successMessage"),
								"[Failed] Edit successfull message should be shown, but '%s' is shown.".formatted(toastMessage));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				} else {
					assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(createLandingPage.loc_btnSave),
							"[Failed] Restricted page should be shown when click on edit landing page.");
				}
			} else {
				if (hasEditLandingPagePers()) {
					commonAction.navigateToURL(editUrl);
					commonAction.click(createLandingPage.loc_btnSave);
					String toastMessage = new HomePage(driver).getToastMessage();
					try {
						assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("marketing.landingPage.edit.successMessage"),
								"[Failed] Edit successfull message should be shown, but '%s' is shown.".formatted(toastMessage));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				} else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(editUrl),
						"[Failed] Restricted page should be shown when navigate ti edit url: " + editUrl);
			}

		} else logger.info("Don't have View detail permission, so no need check Edit permission.");
		logger.info("Complete check Edit landing page permission.");
	}

	public void checkPermissionViewLandingPageDetail(int landingPageId) {
		String editUrl = Links.DOMAIN + "/marketing/landing-page/edit/" + landingPageId;
		if (hasViewDetailLandingPagePers()) {
			//staff has view landing page list > click on edit button to check
			if (hasViewLandingPageListPers())
				assertCustomize.assertTrue(new CheckPermission(driver).checkValueShow(loc_icnEdit, createLandingPage.loc_txtTitleName),
						"[Failed] Title name '%s' should be shown on landing page detail.".formatted(commonAction.getText(createLandingPage.loc_txtTitleName)));
			else
				//staff has not view landing page list > navigate url to check
				assertCustomize.assertTrue(new CheckPermission(driver).checkValueShow(editUrl, createLandingPage.loc_txtTitleName),
						"[Failed] Title name '%s' should be shown on landing page detail.".formatted(commonAction.getText(createLandingPage.loc_txtTitleName)));
		} else {
			if (hasViewLandingPageListPers())
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_icnEdit),
						"[Failed] Restricted page not show when click on Edit button.");
			else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(editUrl),
					"[Failed] Restricted page not show when navigate to landing page detail.");
		}
		logger.info("Complete check View landing page detail permission.");
	}
	public void checkPermissionPublishLandingPage(int landingPageDraftId){
		String editUrl = Links.DOMAIN + "/marketing/landing-page/edit/" + landingPageDraftId;
		if(hasViewDetailLandingPagePers()){
			if(hasPublishLandingPagePers()){
				commonAction.navigateToURL(editUrl);
				createLandingPage.clickOnPublishBtn();
				String messageContent = new ConfirmationDialog(driver).getPopUpContent();
				try {
					assertCustomize.assertEquals(messageContent,PropertiesUtil.getPropertiesValueByDBLang("marketing.landingPage.publish.comfirmMessage"),
							"[Failed] Confirm publish page message should be shown, but '%s' is shown".formatted(messageContent));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				// click OK button on Confirmation pupop to publish
				new ConfirmationDialog(driver).clickOKBtn();
				commonAction.sleepInMiliSecond(1000);
				String toastMessage = new ConfirmationDialog(driver).getPopUpTitle();
				try {
					assertCustomize.assertTrue(toastMessage.contains(PropertiesUtil.getPropertiesValueByDBLang("marketing.landingPage.detail.publish.successMessage")),
							"[Failed] The website published success message should be shown, but '%s' is shown".formatted(toastMessage));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else {
				commonAction.navigateToURL(editUrl);
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(createLandingPage.loc_btnPublish),
						"[Failed] Restricted page not show when click on Publish button on detail page.");
			}
		}else logger.info("Don't have View landing page detail permission, so no need check Publish landing page on detail page.");

		//Check publish on landing page list
		if(hasViewLandingPageListPers()){
			navigateUrl();
			filterStatus("Draft");
			commonAction.click(loc_icnShowMoreAction);
			if(hasPublishLandingPagePers()){
				//Click on publish button
				commonAction.click(loc_ddvAction,0);
				String messageContent = new ConfirmationDialog(driver).getPopUpContent();
				try {
					assertCustomize.assertEquals(messageContent,PropertiesUtil.getPropertiesValueByDBLang("marketing.landingPage.publish.comfirmMessage"),
							"[Failed] Confirm publish page message should be shown, but '%s' is shown".formatted(messageContent));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				// click OK button on Confirmation pupop to publish
				new ConfirmationDialog(driver).clickOKBtn();
				String toastMessage = new HomePage(driver).getToastMessage();
				try {
					assertCustomize.assertEquals(toastMessage,PropertiesUtil.getPropertiesValueByDBLang("marketing.landingPage.publish.successMessage"),
							"[Failed] Publish success message should be shown, but '%s' is shown".formatted(toastMessage));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else{
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_ddvAction,0),
						"[Failed] Restricted page not show when click on publish button on list.");
			}
		}else logger.info("Don't have View landing page list permission, so no need check Publish landing page on list.");
		logger.info("Complete check Publish permission.");
	}
	public void checkPermissionUnpublishLandingPage(int landingPagePublishedId){
		String editUrl = Links.DOMAIN + "/marketing/landing-page/edit/" + landingPagePublishedId;
		//Check Unpublish permission on detail page
		if(hasViewDetailLandingPagePers()){
			if(hasUnPublishLandingPagePers()){
				commonAction.navigateToURL(editUrl);
				createLandingPage.clickOnUnPublishBtn();
				String messageContent = new ConfirmationDialog(driver).getPopUpContent();
				try {
					assertCustomize.assertEquals(messageContent,PropertiesUtil.getPropertiesValueByDBLang("marketing.landingPage.unpublish.comfirmMessage"),
							"[Failed] Confirm unpublish page message should be shown, but '%s' is shown".formatted(messageContent));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				// click OK button on Confirmation pupop to unpublish
				new ConfirmationDialog(driver).clickOKBtn();
				String toastMessage = new HomePage(driver).getToastMessage();
				try {
					assertCustomize.assertEquals(toastMessage,PropertiesUtil.getPropertiesValueByDBLang("marketing.landingPage.publish.successMessage"),
							"[Failed] UnPublish success message should be shown, but '%s' is shown".formatted(toastMessage));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else {
				commonAction.navigateToURL(editUrl);
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(createLandingPage.loc_btnUnpublish),
						"[Failed] Restricted page not show when click on UnPublish button on detail page.");
			}
		}else logger.info("Don't have View landing page detail permission, so no need check UnPublish landing page on detail page.");

		// Check Unpublish permission on list.
		if(hasViewLandingPageListPers()){
			navigateUrl();
			filterStatus("Published");
			commonAction.click(loc_icnShowMoreAction);
			if(hasUnPublishLandingPagePers()){
				//Click on publish button
				commonAction.click(loc_ddvAction,1);
				String messageContent = new ConfirmationDialog(driver).getPopUpContent();
				try {
					assertCustomize.assertEquals(messageContent,PropertiesUtil.getPropertiesValueByDBLang("marketing.landingPage.unpublish.comfirmMessage"),
							"[Failed] Confirm unpublish page message should be shown, but '%s' is shown".formatted(messageContent));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				// click OK button on Confirmation pupop to unpublish
				new ConfirmationDialog(driver).clickOKBtn();
				String toastMessage = new HomePage(driver).getToastMessage();
				try {
					assertCustomize.assertEquals(toastMessage,PropertiesUtil.getPropertiesValueByDBLang("marketing.landingPage.publish.successMessage"),
							"[Failed] Publish success message should be shown, but '%s' is shown".formatted(toastMessage));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else{
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_ddvAction,1),
						"[Failed] Restricted page not show when click on publish button on list.");
			}
		}else logger.info("Don't have View landing page list permission, so no need check Unpublish landing page on list.");
		logger.info("Complete check Unpublish permission.");
	}
	public void checkPermissionCloneLandingPage(){
		if(hasViewLandingPageListPers()){
			navigateUrl();
			commonAction.click(loc_icnShowMoreAction);
			if(hasCreateLandingPagePers()) {
				if (hasCloneLandingPagePers()) {
					//Click on publish button
					commonAction.click(loc_ddvAction, 2);
					String messageContent = new ConfirmationDialog(driver).getPopUpContent();
					try {
						assertCustomize.assertTrue(messageContent.contains(PropertiesUtil.getPropertiesValueByDBLang("marketing.landingPage.clone.confirmMessage")),
								"[Failed] Confirm Clone page message should be shown, but '%s' is shown".formatted(messageContent));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}

					// click OK button on Confirmation pupop to clone
					new ConfirmationDialog(driver).clickOKBtn();
					commonAction.sleepInMiliSecond(1000);
					String popUpMessage = new ConfirmationDialog(driver).getPopUpContent();
					try {
						assertCustomize.assertEquals(popUpMessage, PropertiesUtil.getPropertiesValueByDBLang("marketing.landingPage.clone.successMessage"),
								"[Failed] Clone success message should be shown, but '%s' is shown".formatted(popUpMessage));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				} else {
					assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_ddvAction, 2),
							"[Failed] Restricted page not show when click on clone button on list.");
				}
			}else{
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_ddvAction, 2),
						"[Failed]Restricted page should be shown when click Clone landing page.");
				logger.info("Don't has Create landing page permission, so staff don't have Clone permission.");
			}
		}else logger.info("Don't has View landing page list permission, so no need check Clone permission.");
		logger.info("Complete check Clone permission.");
	}
	public void checkPermissionDeleteLandingPage(){
		if(hasViewLandingPageListPers()){
			navigateUrl();
			filterStatus("Draft");
			commonAction.click(loc_icnShowMoreAction);
			if(hasDeleteLandingPagePers()){
				//Click on publish button
				commonAction.click(loc_ddvAction,3);
				String messageContent = new ConfirmationDialog(driver).getPopUpContent();
				try {
					assertCustomize.assertTrue(messageContent.contains(PropertiesUtil.getPropertiesValueByDBLang("marketing.landingPage.delete.confirmMessage")),
							"[Failed] Confirm Delete page message should be shown, but '%s' is shown".formatted(messageContent));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				// click OK button on Confirmation pupop to delete
				new ConfirmationDialog(driver).clickGreenBtn();
				commonAction.sleepInMiliSecond(500);
				String notifyMessage = new ConfirmationDialog(driver).getPopUpContent();
				try {
					assertCustomize.assertEquals(notifyMessage,PropertiesUtil.getPropertiesValueByDBLang("marketing.landingPage.delete.successMessage"),
							"[Failed] Delete success message should be shown, but '%s' is shown".formatted(notifyMessage));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else{
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_ddvAction,3),
						"[Failed] Restricted page not show when click on delete button on list.");
			}
		}else logger.info("Don't has View landing page list permission, so no need check Delete permission.");
		logger.info("Complete check Delete permission.");
	}
	public LandingPage verifyLandingPagePermission(AllPermissions allPermissions, int landingPageDraftId, int landingPagePublishedId, String productNameCreatedShopOwner, String productNameCreatedStaff ){
		this.allPermissions = allPermissions;
		checkPermissionViewLandingPageList();
		checkPermissionViewLandingPageDetail(landingPageDraftId);
		checkPermissionCreateLandingPage(productNameCreatedShopOwner,productNameCreatedStaff);
		checkPermissionEditLandingPage(productNameCreatedShopOwner,productNameCreatedStaff,landingPageDraftId);
		checkPermissionPublishLandingPage(landingPageDraftId);
		checkPermissionUnpublishLandingPage(landingPagePublishedId);
		checkPermissionCloneLandingPage();
		checkPermissionDeleteLandingPage();
		return this;
	}
	public LandingPage completeVerifyLandingPagePermission() {
		logger.info("countFail = %s".formatted(assertCustomize.getCountFalse()));
		if (assertCustomize.getCountFalse() > 0) {
			Assert.fail("[Failed] Fail %d cases".formatted(assertCustomize.getCountFalse()));
		}
		return this;
	}
}
