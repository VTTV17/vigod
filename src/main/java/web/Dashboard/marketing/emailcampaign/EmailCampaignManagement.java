package web.Dashboard.marketing.emailcampaign;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import utilities.assert_customize.AssertCustomize;
import utilities.links.Links;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;
import web.Dashboard.marketing.landingpage.LandingPage;

import java.util.List;

public class EmailCampaignManagement {

	final static Logger logger = LogManager.getLogger(EmailCampaignManagement.class);

	WebDriver driver;
	UICommonAction commonAction;
	AllPermissions allPermissions;
	AssertCustomize assertCustomize;
	CreateEmailCampaign createEmailCampaign;

	public EmailCampaignManagement(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		assertCustomize = new AssertCustomize(driver);
		createEmailCampaign = new CreateEmailCampaign(driver);
	}

    By loc_btnCreateEmailCampaign = By.cssSelector(".marketing-email-campaign-list .gss-content-header--undefined button");	
    By loc_lst_lblCampaignName = By.xpath("//tr/td[1]//div");
	By loc_ddlStatus = By.xpath("(//div[@class='uik-select__valueWrapper'])[2]");
	By loc_ddvStatus = By.cssSelector(".uik-select__option");
	By loc_icnEdit = By.xpath("//i[contains(@class,'gs-action-button')][1]");
	By loc_icnDelete = By.xpath("//i[contains(@class,'gs-action-button')][3]");
    public EmailCampaignManagement clickCreateEmailCampaign() {
    	commonAction.click(loc_btnCreateEmailCampaign);
    	logger.info("Clicked on 'Create New Email Campaign' button.");
    	return this;
    }    	

    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateEmailCampaign(String permission, String url) {
		if (permission.contentEquals("A")) {
			clickCreateEmailCampaign();
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commonAction.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/     
    public boolean hasViewCampaignListPers(){
		return allPermissions.getMarketing().getEmailCampaign().isViewCampaignList();
	}
	public boolean hasCreateCampaignPers(){
		return allPermissions.getMarketing().getEmailCampaign().isCreateCampaign();
	}
	public boolean hasEditCampaignPers(){
		return allPermissions.getMarketing().getEmailCampaign().isEditCampaign();
	}
	public boolean hasDeleteCampaignPers(){
		return allPermissions.getMarketing().getEmailCampaign().isDeleteCampaign();
	}
	public boolean hasViewSegment(){
		return allPermissions.getCustomer().getSegment().isViewSegmentList();
	}
	public void navigateUrl(){
		String url = Links.DOMAIN + "/marketing/email/list";
		commonAction.navigateToURL(url);
		logger.info("Navigate to url: "+url);
		commonAction.sleepInMiliSecond(200);
	}
	public void filterByStatus(String status){
		commonAction.click(loc_ddlStatus);
		switch (status) {
			case "Draft" -> commonAction.click(loc_ddvStatus, 1);
			case "Sent" -> commonAction.click(loc_ddvStatus, 2);
			default -> {
				commonAction.click(loc_ddvStatus, 0);
				logger.info("Select All status");
			}
		}
		commonAction.sleepInMiliSecond(500, "Wait in filter status.");
	}
	public void checkPermissionViewCampaignList(){
		navigateUrl();
		List<WebElement> emailCampaignNames = commonAction.getElements(loc_lst_lblCampaignName);
		if (hasViewCampaignListPers()) {
			assertCustomize.assertTrue(emailCampaignNames.size() > 0, "[Failed] Email campaign list should be shown");
		} else
			assertCustomize.assertTrue(emailCampaignNames.isEmpty(), "[Failed] Email campaign should not be shown");
		logger.info("Complete check View email campaign list permission.");
	}
	public void checkPermissionCreateEmailCampaign(){
		if(hasCreateCampaignPers()){
			clickCreateEmailCampaign();
			checkPermissionViewSegment();
			navigateUrl();
			clickCreateEmailCampaign();
			createEmailCampaign.createAnEmailCampaign();
			String toastMessage = new HomePage(driver).getToastMessage();
			try {
				assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("marketing.emailCampaign.create.successMessage"),
						"[Failed] Create successfully message should be shown, but '%s' is shown.".formatted(toastMessage));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnCreateEmailCampaign),
				"[Failed] Restricted page not show when click on Create Email campaign button.");
	}
	public void checkPermissionViewSegment(){
		createEmailCampaign.selectSendTo(1)
				.clickOnSelectSegment();
		commonAction.sleepInMiliSecond(1000);
		List<WebElement> segmentNames = commonAction.getElements(createEmailCampaign.loc_dlgAddSegment_lstSegmentName);
		if(hasViewSegment()){
			assertCustomize.assertTrue(segmentNames.size() > 0, "[Failed] Segment list should be shown");
		} else
			assertCustomize.assertTrue(segmentNames.isEmpty(), "[Failed] Segment should not be shown");
		logger.info("Complete check View segment permission.");
	}
	public void checkPermissionEditCampaign(int draftId){
		String editUrl = Links.DOMAIN + "/marketing/email/edit/"+draftId;
		navigateUrl();
		filterByStatus("Draft");
		if(hasViewCampaignListPers()) {
			if (hasEditCampaignPers()) {
				commonAction.click(loc_icnEdit, 0);
				checkPermissionViewSegment();
				navigateUrl();
				commonAction.click(loc_icnEdit, 0);
				createEmailCampaign.clickOnSaveBtn();
				String toastMessage = new HomePage(driver).getToastMessage();
				try {
					assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("marketing.emailCampaign.update.successMessage"),
							"[Failed] Updated successfully message should be shown, but '%s' is shown.".formatted(toastMessage));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_icnEdit, 0),
					"[Failed] Restricted page not show when click on Edit Email campaign button.");
		}else {
			if (hasEditCampaignPers()) {
				commonAction.navigateToURL(editUrl);
				checkPermissionViewSegment();
				commonAction.navigateToURL(editUrl);
				createEmailCampaign.clickOnSaveBtn();
				String toastMessage = new HomePage(driver).getToastMessage();
				try {
					assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("marketing.emailCampaign.update.successMessage"),
							"[Failed] Updated successfully message should be shown, but '%s' is shown.".formatted(toastMessage));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(editUrl),
					"[Failed] Restricted page not show when click on Edit Email campaign button.");
		}
	}
	public void checkPermissionDeleteCampaign(){
		navigateUrl();
		if(hasViewCampaignListPers()) {
			if (hasDeleteCampaignPers()) {
				commonAction.click(loc_icnDelete, 0);
				String messageContent = new ConfirmationDialog(driver).getPopUpContent();
				try {
					assertCustomize.assertEquals(messageContent, PropertiesUtil.getPropertiesValueByDBLang("marketing.emailCampaign.delete.confirmContent"),
							"[Failed] Delete confirmation message should be shown, but '%s' is shown.".formatted(messageContent));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				new ConfirmationDialog(driver).clickGreenBtn();
				String toastMessage = new HomePage(driver).getToastMessage();
				try {
					assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("marketing.emailCampaign.delete.successMessage"),
							"[Failed] Deleted successfully should be shown, but '%s' is shown.".formatted(toastMessage));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else
				assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_icnDelete, 0),
						"[Failed] Restricted popup not show when click on delete icon.");
		}else logger.info("Don't have View email campaign list, so no need check Delete permission.");
	}
	public EmailCampaignManagement checkEmailCampaignPermission(AllPermissions allPermissions, int draftId){
		this.allPermissions = allPermissions;
		checkPermissionViewCampaignList();
		checkPermissionCreateEmailCampaign();
		checkPermissionEditCampaign(draftId);
		checkPermissionDeleteCampaign();
		completeVerifyEmailCampaignPermission();
		return this;
	}
	public EmailCampaignManagement completeVerifyEmailCampaignPermission() {
		logger.info("countFail = %s".formatted(assertCustomize.getCountFalse()));
		if (assertCustomize.getCountFalse() > 0) {
			Assert.fail("[Failed] Fail %d cases".formatted(assertCustomize.getCountFalse()));
		}
		return this;
	}
}
