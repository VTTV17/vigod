package web.Dashboard.marketing.pushnotification;

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
import web.Dashboard.marketing.emailcampaign.EmailCampaignManagement;

import java.util.List;
import java.util.Properties;

public class PushNotificationManagement {

	final static Logger logger = LogManager.getLogger(PushNotificationManagement.class);

	WebDriver driver;
	UICommonAction commonAction;
	AllPermissions allPermissions;
	AssertCustomize assertCustomize;
	CreatePushNotification createPushNotification;
	public PushNotificationManagement(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		assertCustomize = new AssertCustomize(driver);
		createPushNotification = new CreatePushNotification(driver);
	}

    By loc_btnExploreNow = By.cssSelector(".notification-intro .gs-button__green");
    By loc_btnCreateCampaign = By.cssSelector(".notification-header button.gs-button__green"); //Temporary
    By loc_lst_lblCampaignName = By.cssSelector(".discount-name");
	By loc_ddlStatus = By.xpath("(//div[contains(@class,'dropdown-box')])[2]/button");
	By loc_ddvStatus = By.xpath("(//div[contains(@class,'dropdown-box')])[2]//button[contains(@class,'dropdown-item')]");
	By loc_lst_icnDelete = By.xpath("//i[contains(@class,'gs-action-button')][2]");
	By loc_lst_icnEdit = By.xpath("//i[contains(@class,'gs-action-button')][1]");

    public PushNotificationManagement clickExploreNow() {
    	commonAction.click(loc_btnExploreNow);
    	logger.info("Clicked on 'Explore Now' button.");
    	
    	//Sometimes the element is not present even after the loading icon has disappeared. The code below fixes this intermittent issue
    	for (int i=0; i<30; i++) {
    		if (commonAction.getElements(loc_btnCreateCampaign).size() >0) break;
    		commonAction.sleepInMiliSecond(500);
    	}
    	return this;
    }        
    
    public PushNotificationManagement clickCreateCampaign() {
    	commonAction.click(loc_btnCreateCampaign);
    	logger.info("Clicked on 'Create Campaign' button.");
    	return this;
    }    	

    /*Verify permission for certain feature*/
    public void verifyPermissionToCreatePushNotification(String permission, String url) {
		if (permission.contentEquals("A")) {
			clickExploreNow();
			commonAction.sleepInMiliSecond(1000); //Sometimes it navigates to Create Campaign screen. Temporary
			clickCreateCampaign();
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commonAction.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/       
    public boolean hasViewCampaignListPers(){
		return allPermissions.getMarketing().getPushNotification().isViewCampaignList();
	}
	public boolean hasViewCampaignDetailPers(){
		return allPermissions.getMarketing().getPushNotification().isViewCampaignDetail();
	}
	public boolean hasCreateCampaignPers(){
		return allPermissions.getMarketing().getPushNotification().isCreateCampaign();
	}
	public boolean hasEditCampaignPers(){
		return allPermissions.getMarketing().getPushNotification().isEditCampaign();
	}
	public boolean hasDeleteCampaign(){
		return allPermissions.getMarketing().getPushNotification().isDeleteCampaign();
	}
	public boolean hasViewCustomerSegmentListPers(){
		return allPermissions.getCustomer().getSegment().isViewSegmentList();
	}
	public boolean hasViewProductListPers(){
		return allPermissions.getProduct().getProductManagement().isViewProductList();
	}
	public boolean hasViewCreatedProductListPers(){
		return allPermissions.getProduct().getProductManagement().isViewCreatedProductList();
	}
	public boolean hasViewServiceListPers(){
		return allPermissions.getService().getServiceManagement().isViewListService();
	}
	public boolean hasViewCreatedServiceListPers(){
		return allPermissions.getService().getServiceManagement().isViewListCreatedService();
	}
	public boolean hasViewProductCollectionListPers(){
		return allPermissions.getProduct().getCollection().isViewCollectionList();
	}
	public boolean hasViewServiceCollectionListPers(){
		return allPermissions.getService().getServiceCollection().isViewCollectionList();
	}
	public boolean hasViewPageListPers(){
		return allPermissions.getOnlineStore().getPage().isViewPageList();
	}
	public void filterByStatus(String status){
		commonAction.click(loc_ddlStatus);
		switch (status) {
			case "Sending" -> commonAction.click(loc_ddvStatus, 1);
			case "Sent" -> commonAction.click(loc_ddvStatus, 2);
			case "Scheduled" -> commonAction.click(loc_ddvStatus, 3);
			case "Schedule failed" -> commonAction.click(loc_ddvStatus, 4);
			case "Active" -> commonAction.click(loc_ddvStatus, 5);
			default -> {
				commonAction.click(loc_ddvStatus, 0);
				logger.info("Select All statuses");
			}
		}
		commonAction.sleepInMiliSecond(500, "Wait in filter status.");
	}
	public void navigateByUrl(){
		String url = Links.DOMAIN + "/marketing/notification";
		commonAction.navigateToURL(url);
		commonAction.sleepInMiliSecond(500);
		logger.info("Navigate to url: "+url);
	}
	public void checkPermissionViewCampaignList(){
		List<WebElement> campaignNames = commonAction.getElements(loc_lst_lblCampaignName,3);
		if (hasViewCampaignListPers()) {
			assertCustomize.assertTrue(campaignNames.size() > 0, "[Failed] Notification campaign list should be shown");
		} else
			assertCustomize.assertTrue(campaignNames.isEmpty(), "[Failed] Notification campaign should not be shown");
		logger.info("Complete check View push noti campaign list permission.");
	}

	public void checkPermissionViewCampaignDetail(int notificationId){
		String detailUrl = Links.DOMAIN + "/marketing/notification/detail/"+notificationId;
		String editUrl = Links.DOMAIN +"/marketing/notification/push/edit/"+notificationId;
		if (hasViewCampaignDetailPers()) {
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessedSuccessfully(detailUrl,detailUrl),"Detail page not show");
//			assertCustomize.assertTrue(new CheckPermission(driver).checkValueShow(editUrl, createPushNotification.loc_txtCampaignName),
//						"[Failed] Campaign name should be shown value, but '%s' is shown".formatted(commonAction.getText(createPushNotification.loc_txtCampaignName)));
		} else {
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(detailUrl),
					"[Failed] Restricted page should be shown when go to detail page");
//			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(editUrl),
//					"[Failed] Restricted page should be shown when go to edit page");
		}
		logger.info("Complete check View Campaign detail permission.");
	}
	public void checkPermissionViewSegmentList(){
		createPushNotification.clickOnAddSegment();
		List<WebElement> segmentNames = commonAction.getElements(createPushNotification.log_dlgAddSegment_lstSegmentName,3);
		if(hasViewCustomerSegmentListPers()){
			assertCustomize.assertTrue(segmentNames.size()>0,"[Failed] Customer segment should be shown");
		}else assertCustomize.assertTrue(segmentNames.isEmpty(),"[Failed] Customer segment should not be shown.");
		logger.info("Complete check View customer segment list permission");
	}
	public void checkPermissionViewCollectionList(){
		createPushNotification.selectLinkToType(2);
		commonAction.click(createPushNotification.loc_ddlLinkValue);
		commonAction.sleepInMiliSecond(1000);
		List<WebElement> collectionNames = commonAction.getElements(createPushNotification.loc_ddvLinkValue);
		if(hasViewProductCollectionListPers()||hasViewServiceCollectionListPers()){
			assertCustomize.assertTrue(collectionNames.size()>0,"[Failed] Collection list should be shown");
		}else assertCustomize.assertTrue(collectionNames.isEmpty(),"[Failed] Collection list should not be shown.");
		logger.info("Complete check View collection list permission");
	}
	public void checkPermissionViewProductList(){
		createPushNotification.selectLinkToType(3);
		commonAction.click(createPushNotification.loc_lnkChooseAProductService);
		new HomePage(driver).waitTillLoadingDotsDisappear();
		List<WebElement> productNames = commonAction.getElements(createPushNotification.loc_dlgProductSelection_lstProductName,3);
		if (hasViewProductListPers()||hasViewCreatedProductListPers()){
			assertCustomize.assertTrue(productNames.size()>0,"[Failed] Product list should be shown");
		}else assertCustomize.assertTrue(productNames.isEmpty(),"[Failed] Product list should not be shown.");
		logger.info("Complete check View Product list permission");
	}
	public void checkPermissionViewServiceList(){
		createPushNotification.selectLinkToType(4);
		commonAction.click(createPushNotification.loc_lnkChooseAProductService);
		new HomePage(driver).waitTillLoadingDotsDisappear();
		List<WebElement> serviceNames = commonAction.getElements(createPushNotification.loc_dlgProductSelection_lstProductName,3);
		if (hasViewServiceListPers()||hasViewCreatedServiceListPers()){
			assertCustomize.assertTrue(serviceNames.size()>0,"[Failed] Service list should be shown");
		}else assertCustomize.assertTrue(serviceNames.isEmpty(),"[Failed] Service list should not be shown.");
		logger.info("Complete check View Service list permission");
	}
	public void checkPermissionViewPageList(){
		createPushNotification.selectLinkToType(1);
		commonAction.click(createPushNotification.loc_ddlLinkValue);
		commonAction.sleepInMiliSecond(1000);
		List<WebElement> pages = commonAction.getElements(createPushNotification.loc_ddvLinkValue);
		if(hasViewPageListPers()){
			assertCustomize.assertTrue(pages.size()>0,"[Failed] Page list should be shown");
		}else assertCustomize.assertTrue(pages.isEmpty(),"[Failed] Page list should not be shown.");
		logger.info("Complete check View page list permission");
	}
	public void checkPermissionCreatePushNotiCampaign(){
		navigateByUrl();
		if(hasCreateCampaignPers()){
			clickCreateCampaign();
			checkPermissionViewSegmentList();
			createPushNotification.navigateUrl();
			checkPermissionViewPageList();
			createPushNotification.navigateUrl();
			checkPermissionViewCollectionList();
			createPushNotification.navigateUrl();
			checkPermissionViewProductList();
			createPushNotification.navigateUrl();
			checkPermissionViewServiceList();
			createPushNotification.navigateUrl()
					.createSimpleCampaign();
			String toastMessage = new HomePage(driver).getToastMessage();
			try {
				assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("marketing.pushNotification.create.successMessage"),
						"[Failed] Created successfully message should be shown after create");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}else
			assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_btnCreateCampaign),
					"[Failed] Restricted page not show when click on Create campaign button.");
	}
	public void checPermissionEditCampaign(int campaignId){
		String editUrl = Links.DOMAIN +"/marketing/notification/push/edit/"+campaignId;
//		if(hasViewCampaignDetailPers()) {
			if (hasEditCampaignPers()) {
				if(hasViewCampaignListPers()){
					navigateByUrl();
					commonAction.click(loc_lst_icnEdit,0);
				}else commonAction.navigateToURL(editUrl);
				checkPermissionViewSegmentList();
				commonAction.navigateToURL(editUrl);
				checkPermissionViewPageList();
				commonAction.navigateToURL(editUrl);
				checkPermissionViewCollectionList();
				commonAction.navigateToURL(editUrl);
				checkPermissionViewProductList();
				commonAction.navigateToURL(editUrl);
				checkPermissionViewServiceList();
				commonAction.navigateToURL(editUrl);
				createPushNotification.clickOnCreateBtn();
				String toastMessage = new HomePage(driver).getToastMessage();
				try {
					assertCustomize.assertEquals(toastMessage, PropertiesUtil.getPropertiesValueByDBLang("marketing.pushNotification.update.successMessage"),
							"[Failed] Updated successfully message should be shown.");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				if(hasViewCampaignListPers()){
					navigateByUrl();
					assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnEdit,0),
							"[Failed] Restricted popup not show when click on edit icon.");
				}else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(editUrl),
						"[Failed] Restricted page not show when navigate to edit page");
			}
//		}else logger.info("Don't have View detail permission, so can't check Edit permission.");
	}
	public void checkPermissionDeleteCampaign(){
		if(hasViewCampaignListPers()){
			navigateByUrl();
			if(hasDeleteCampaign()){
				commonAction.click(loc_lst_icnDelete,0);
				String message = new ConfirmationDialog(driver).getPopUpContent();
				try {
					assertCustomize.assertEquals(message,PropertiesUtil.getPropertiesValueByDBLang("marketing.pushNotification.delete.confirmContent"),
							"[Failed] Delete confirmation popup not show.");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				new ConfirmationDialog(driver).clickOKBtn();
				String toastMessage = new HomePage(driver).getToastMessage();
				try {
					assertCustomize.assertEquals(toastMessage,PropertiesUtil.getPropertiesValueByDBLang("marketing.pushNotification.delete.successMessage"),
							"[Failed] Delete success message not show when click on OK button on delete confirm popup.");
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else assertCustomize.assertTrue(new CheckPermission(driver).checkAccessRestricted(loc_lst_icnDelete,0),
					"[Failed] Restricted popup not show when click on delete icon.");
		}else logger.info("Don't have permission ");
	}
	public PushNotificationManagement checkPushNotificationPermission(AllPermissions allPermissions, int campaignId){
		this.allPermissions = allPermissions;
		checkPermissionViewCampaignList();
		checkPermissionViewCampaignDetail(campaignId);
		checkPermissionCreatePushNotiCampaign();
		checPermissionEditCampaign(campaignId);
		checkPermissionDeleteCampaign();
		completeVerifyPushNotificationPermission();
		return this;
	}
	public PushNotificationManagement completeVerifyPushNotificationPermission() {
		logger.info("countFail = %s".formatted(assertCustomize.getCountFalse()));
		if (assertCustomize.getCountFalse() > 0) {
			Assert.fail("[Failed] Fail %d cases".formatted(assertCustomize.getCountFalse()));
		}
		return this;
	}
}
