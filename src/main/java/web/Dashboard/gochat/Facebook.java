package web.Dashboard.gochat;

import static utilities.links.Links.DOMAIN;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import utilities.model.gochat.facebook.ConnectedPages;
import utilities.model.gochat.facebook.CreatedAutomationCampaign;
import utilities.model.gochat.facebook.CreatedBroadcast;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;						

public class Facebook {
	WebDriver driver;
	UICommonAction commons;
	HomePage homePage;
	FacebookElement elements;

	final static Logger logger = LogManager.getLogger(Facebook.class);

	public Facebook(WebDriver driver) {
		this.driver = driver;
		commons = new UICommonAction(driver);
		homePage = new HomePage(driver);
		elements = new FacebookElement();
	}

	/**
	 * A temporary function that helps get rid of the annoying try catch block when reading text from property file
	 * @param propertyKey
	 */
	public String translateText(String propertyKey) {
		String translatedText = null;
		try {
			translatedText = PropertiesUtil.getPropertiesValueByDBLang(propertyKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return translatedText;
	}		
	
	Facebook navigateByURL(String url) {
		driver.get(url);
		logger.info("Navigated to: " + url);
		commons.removeFbBubble();
		homePage.waitTillSpinnerDisappear1();
		return this;
	}	
	void waitTillLoadingIconDisappear() {
		commons.sleepInMiliSecond(500, "Wait in waitTillLoadingIconDisappear()");
		//There's an issue here. Uncomment the line below when the issue is fixed
		commons.waitInvisibilityOfElementLocated(elements.loc_icnLoadMoreLoading);
		//There's an issue here. Delete them when the issue is fixed
//		for (int i=0; i<10; i++) {
//			if (!commons.getElements(elements.loc_lblFBUserName).isEmpty()) break;
//			if (!commons.getElements(elements.loc_lblNoResultFound).isEmpty()) break;
//			commons.sleepInMiliSecond(1000, "Wait in waitTillLoadingIconDisappear()");
//		}
		logger.info("Load more loading icon has disappeared");
		
	}	
	public Facebook navigateToConversationScreenByURL() {
		navigateByURL(DOMAIN + "/gosocial/facebook/conversations");
		waitTillLoadingIconDisappear();
		return this;
	}	
	public Facebook navigateToConfigurationScreenByURL() {
		navigateByURL(DOMAIN + "/gosocial/facebook/configuration");
		return this;
	}	
	public Facebook navigateToAutomationScreenByURL() {
		navigateByURL(DOMAIN + "/gosocial/facebook/automation");
		return this;
	}	
	public Facebook navigateToCreateAutomationScreenByURL() {
		navigateByURL(DOMAIN + "/gosocial/facebook/automation/create");
		return this;
	}	
	public Facebook navigateToAutomationDetailScreenByURL(int campaignId) {
		navigateByURL(DOMAIN + "/gosocial/facebook/automation/edit/" + campaignId);
		return this;
	}	
	public Facebook navigateToBroadcastScreenByURL() {
		navigateByURL(DOMAIN + "/gosocial/facebook/broadcast");
		return this;
	}	
	public Facebook navigateToCreateBroadcastScreenByURL() {
		navigateByURL(DOMAIN + "/gosocial/facebook/broadcast/create");
		return this;
	}	
	public Facebook navigateToBroadcastDetailScreenByURL(int campaignId) {
		navigateByURL(DOMAIN + "/gosocial/facebook/broadcast/edit/" + campaignId);
		return this;
	}	
	public Facebook clickConnectFacebook() {
		commons.click(elements.loc_btnConnectFacebook);
		logger.info("Clicked 'Connect Facebook' button.");
		return this;
	}
	public boolean isAddPageBtnDisplayed() {
		boolean isDisplayed = !commons.getElements(elements.loc_btnAddPage).isEmpty();
		logger.info("Is Add Page button displayed: " + isDisplayed);
		return isDisplayed;
	}
	public Facebook clickAddPageBtn() {
		commons.click(elements.loc_btnAddPage);
		logger.info("Clicked 'Add Page' button.");
		return this;
	}
	public Facebook clickAddPageLinkText() {
		commons.click(elements.loc_lnkAddPage);
		logger.info("Clicked 'Add pages or change permissions' link text.");
		return this;
	}
	public Facebook selectPageToConnect(String connectedPage) {
		commons.click(By.xpath(elements.loc_rdoConnectPageByName.formatted(connectedPage)));
		logger.info("Selected page: " + connectedPage);
		return this;
	}
	public Facebook clickConnectPageBtn() {
		//We deliberately click this button even though it's disabled to see if Connect action is prohibited on Backend side
		commons.clickJS(elements.loc_btnConnectPage);
		logger.info("Clicked Connect page button");
		return this;
	}
	public Facebook clickDisconnectPageBtn() {
		//We deliberately click this button even though it's disabled to see if Disconnect action is prohibited on Backend side
		commons.clickJS(elements.loc_btnDisconnectPage);
		logger.info("Clicked Disconnect page button");
		return this;
	}
	public String getPageStatus(String pageName) {
		String status = commons.getText(By.xpath(elements.loc_lblPageStatusByName));
		logger.info("Retrieved status of page '%s': %s".formatted(pageName, status));
		return status;
	}
	/**
	 * Get name of the page whose conversations are displayed
	 * @return pageName
	 */
	public String getDisplayedConversationPage() {
		String name = commons.getText(elements.loc_ddlSelectedPage);
		logger.info("Conversations of page '%s' are being displayed".formatted(name));
		return name;
	}
	public List<String> getFBUserFromConversation() {
		List<String> names = new ArrayList<>();
		for (int i=0; i<commons.getElements(elements.loc_lblFBUserName).size(); i++) {
			names.add(commons.getText(elements.loc_lblFBUserName, i));
		}
		logger.info("Retrieved FB Users from conversation: " + names);
		return names;
	}
	//Temporary functions, will think of a better way to handle this
	public Facebook clickFirstConversation() {
		commons.click(elements.loc_lblFBUserName);
		logger.info("Click the 1st conversation to see its detail");
		return this;
	}
	public Facebook clickAssignStaffDropdown() {
		commons.click(elements.loc_ddlAssignStaff);
		logger.info("Click Assign Staff dropdown");
		return this;
	}
	public String getCurrentlyAssignedStaff() {
		commons.sleepInMiliSecond(500, "Wait in getCurrentlyAssignedStaff()");
		String staff = commons.getText(elements.loc_ddlAssignStaff);
		logger.info("Retrieved currently assigned staff: " + staff);
		return staff;
	}
	//Temporary functions, will think of a better way to handle this
	public Facebook clickFirstAssignStaffDropdownOption() {
		commons.click(elements.loc_ddvAssignStaff);
		logger.info("Click the 1st Assign Staff dropdown option");
		return this;
	}
	public Facebook clickAssignToMeBtn() {
		commons.click(elements.loc_btnAssignToMe);
		logger.info("Click 'Assign To Me' button");
		return this;
	}
	public Facebook clickUnAssignBtn() {
		commons.click(elements.loc_btnUnAssign);
		logger.info("Click 'Unassign' button");
		return this;
	}
	public Facebook clickAddTagBtn() {
		commons.click(elements.loc_btnCreateTag);
		logger.info("Click 'Add Tag' button");
		return this;
	}
	public Facebook inputTagName(String name) {
		new Actions(driver).moveToElement(commons.getElement(elements.loc_txtTagNameInDialog)).click().sendKeys(name).build().perform();
		logger.info("Input tag name: " + name);
		return this;
	}
	public Facebook clickAddBtnInAddTagDialog() {
		commons.click(elements.loc_btnAddTagInDialog);
		logger.info("Clicked Add button in Add Tag dialog");
		return this;
	}
	public Facebook clickDeleteTagIcon(String tagName) {
		commons.click(By.xpath(elements.loc_icnDeleteTagByName.formatted(tagName)));
		logger.info("Clicked delete tag icon: " + tagName);
		commons.sleepInMiliSecond(500, "Wait in clickDeleteTagIcon");
		return this;
	}
	public Facebook clickHideTagIcon(String tagName) {
		commons.click(By.xpath(elements.loc_icnHideTagByName.formatted(tagName)));
		logger.info("Clicked the 1st delete tag icon");
		commons.sleepInMiliSecond(500, "Wait in clickHideTagIcon");
		return this;
	}
	public Facebook selectTagToAssign(String tagName) {
		commons.click(By.xpath(elements.loc_btnTagByName.formatted(tagName)));
		logger.info("Clicked tag to assign: " + tagName);
		return this;
	}
	public Facebook clickEditProfileBtn() {
		commons.click(elements.loc_icnEditProfile);
		logger.info("Click Edit profile button");
		return this;
	}
	public Facebook clickUnlinkCustomerIcon() {
		commons.click(elements.loc_icnUnlinkCustomer);
		logger.info("Click Unlink customer icon");
		commons.sleepInMiliSecond(500, "Wait a little in clickUnlinkCustomerIcon()");
		return this;
	}
	public Facebook selectCustomerToLink(String customerName) {
		clickEditProfileBtn();
		commons.inputText(elements.loc_txtSearchCustomer, customerName);
		commons.click(By.xpath(elements.loc_lblCustomerResultByName.formatted(customerName)));
		logger.info("Selected customer to link: " + customerName);
		return this;
	}
	public Facebook clickSaveProfileBtn() {
		homePage.hideFacebookBubble();
		commons.click(elements.loc_btnSaveProfile);
		logger.info("Clicked Save profile button");
		return this;
	}
	public Facebook clickCreateAutomationCampaignBtn() {
		commons.click(elements.loc_btnCreateAutomationCampaign);
		logger.info("Clicked Create Automation campaign button");
		return this;
	}
	public Facebook selectPage(String fbPageName) {
		commons.click(elements.loc_ddlSelectPage);
		commons.click(By.xpath(elements.loc_ddvSelectPageByName.formatted(fbPageName)));
		logger.info("Selected Page to create for campaign creation: " + fbPageName);
		return this;
	}
	public Facebook inputCampaignName(String name) {
		commons.inputText(elements.loc_txtAutomationCampaignName, name);
		logger.info("Input campaign name: " + name);
		return this;
	}
	
	public Facebook clickCreateBroadcastCampaignBtn() {
		commons.click(elements.loc_btnCreateBroadcastCampaign);
		logger.info("Clicked Create Broadcast campaign button");
		return this;
	}	
	public String getAutomationCampaignNameInDetailScreeen() {
		String name = "";
		for (int i=0; i<10; i++) {
			name = commons.getAttribute(elements.loc_txtAutomationCampaignName, "value");
			if (!name.isEmpty()) break;
			commons.sleepInMiliSecond(500, "Wait till automation campaign name gets rendered");
		}
		logger.info("Retrieved automation campaign name: " + name);
		return name;
	}	
	public Facebook clickEditAutomationCampaignIcon(String name) {
		commons.click(By.xpath(elements.loc_icnEditAutomationCampaignByName.formatted(name)));
		logger.info("Clicked Edit icon of campaign: " + name);
		return this;
	}
	public Facebook clickDeleteAutomationCampaignIcon(String name) {
		commons.click(By.xpath(elements.loc_icnDeleteAutomationCampaignByName.formatted(name)));
		logger.info("Clicked Delete icon of campaign: " + name);
		return this;
	}
	public String getBroadcastCampaignNameInDetailScreeen() {
		String name = "";
		for (int i=0; i<10; i++) {
			name = commons.getAttribute(elements.loc_txtAutomationCampaignName, "value");
			if (!name.isEmpty()) break;
			commons.sleepInMiliSecond(500, "Wait till broadcast campaign name gets rendered");
		}
		logger.info("Retrieved broadcast campaign name: " + name);
		return name;
	}	
	public Facebook clickEditBroadcastCampaignIcon(String name) {
		commons.click(By.xpath(elements.loc_icnEditBroadcastCampaignByName.formatted(name)));
		logger.info("Clicked Edit icon of campaign: " + name);
		return this;
	}
	public Facebook clickDeleteBroadcastCampaignIcon(String name) {
		commons.click(By.xpath(elements.loc_icnDeleteBroadcastCampaignByName.formatted(name)));
		logger.info("Clicked Delete icon of campaign: " + name);
		return this;
	}	

	/*Verify permission for certain feature*/
	public void verifyPermissionToConnectToFacebook(String permission) {
		if (permission.contentEquals("A")) {
			new Facebook(driver).clickConnectFacebook();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
	}

	/*-------------------------------------*/	

	/*-------------------------------------*/ 
	boolean isPermissionProhibited(AllPermissions staffPermission) {
		boolean[] allStaffPermisison = {
				staffPermission.getGoChat().getFacebook().isEditAutomationCampaign(),
				staffPermission.getGoChat().getFacebook().isConnectPage(),
				staffPermission.getGoChat().getFacebook().isDisconnectAccount(),
				staffPermission.getGoChat().getFacebook().isDisconnectPage(),
				staffPermission.getGoChat().getFacebook().isLinkCustomerWithFBUser(),
				staffPermission.getGoChat().getFacebook().isUnassignStaffFromConversation(),
				staffPermission.getGoChat().getFacebook().isDeleteBroadcastCampaign(),
				staffPermission.getGoChat().getFacebook().isCreateOrder(),
				staffPermission.getGoChat().getFacebook().isAddRemoveFBPage(),
				staffPermission.getGoChat().getFacebook().isAddTagToConversation(),
				staffPermission.getGoChat().getFacebook().isViewBroadcastCampaignList(),
				staffPermission.getGoChat().getFacebook().isCreateBroadcastCampaign(),
				staffPermission.getGoChat().getFacebook().isUnlinkCustomerWithFBUser(),
				staffPermission.getGoChat().getFacebook().isViewBroadcastCampaignDetail(),
				staffPermission.getGoChat().getFacebook().isHideTag(),
				staffPermission.getGoChat().getFacebook().isSendAMessage(),
				staffPermission.getGoChat().getFacebook().isDeleteTag(),
				staffPermission.getGoChat().getFacebook().isCreateNewTag(),
				staffPermission.getGoChat().getFacebook().isDeleteAutomationCampaign(),
				staffPermission.getGoChat().getFacebook().isConnectAccount(),
				staffPermission.getGoChat().getFacebook().isViewAutomationCampaignDetail(),
				staffPermission.getGoChat().getFacebook().isEditBroadcastCampaign(),
				staffPermission.getGoChat().getFacebook().isViewAllConversations(),
				staffPermission.getGoChat().getFacebook().isViewAutomationCampaignList(),
				staffPermission.getGoChat().getFacebook().isAssignStaffToConversation(),
				staffPermission.getGoChat().getFacebook().isRemoveTagFromConversation(),
				staffPermission.getGoChat().getFacebook().isCreateAutomationCampaign(),
				staffPermission.getGoChat().getFacebook().isViewAssignedConversation(),
		};
		for(boolean individualPermission : allStaffPermisison) if (individualPermission) return false;
		return true;
	}	    

	public void checkPermissionToConnectAccount(AllPermissions staffPermission) {
		navigateToConfigurationScreenByURL(); 
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToConnectAccount");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		if (staffPermission.getGoChat().getFacebook().isConnectAccount()) {
			//Temporarily skipped
		} else {
			//Temporarily skipped
		}
		logger.info("Finished checkPermissionToConnectAccount");
	}    
	public void checkPermissionToDisconnectAccount(AllPermissions staffPermission) {
		navigateToConfigurationScreenByURL(); 
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToDisconnectAccount");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		if (staffPermission.getGoChat().getFacebook().isDisconnectAccount()) {
			//Temporarily skipped
		} else {
			//Temporarily skipped
		}
		logger.info("Finished checkPermissionToDisconnectAccount");
	}    
	public void checkPermissionToAddRemovePages(AllPermissions staffPermission) {
		navigateToConfigurationScreenByURL(); 
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToAddRemovePages");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}

		clickAddPageLinkText();

		if (staffPermission.getGoChat().getFacebook().isAddRemoveFBPage()) {
			commons.switchToWindow(1);
			String title = "";
			for (int i=0; i<3; i++) {
				title = commons.getPageTitle();
				if (!title.isEmpty()) break;
				commons.sleepInMiliSecond(500, "Wait a little to get tab title");
			}
			commons.closeTab();
			commons.switchToWindow(0);
			Assert.assertTrue(title.contains("Facebook"), "Tab title contains Facebook");
			//Temporarily skipped
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		logger.info("Finished checkPermissionToAddRemovePages");
	}    
	public void checkPermissionToConnectPages(AllPermissions staffPermission, String connectedPage) {
		navigateToConfigurationScreenByURL(); 
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToConnectPages");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		
		selectPageToConnect(connectedPage);
		clickConnectPageBtn();
		if (staffPermission.getGoChat().getFacebook().isConnectPage()) {
			Assert.assertEquals(homePage.getToastMessage(), translateText("common.toast.somethingWrong"));
			logger.info("Staff can not connect pages");
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToConnectPages");
	}    
	public void checkPermissionToDisconnectPages(AllPermissions staffPermission, String disconnectedPage) {
		navigateToConfigurationScreenByURL(); 
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToDisconnectPages");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		
		selectPageToConnect(disconnectedPage);
		clickDisconnectPageBtn();
		if (staffPermission.getGoChat().getFacebook().isDisconnectPage()) {
			Assert.assertEquals(homePage.getToastMessage(), translateText("common.toast.somethingWrong"));
			logger.info("Staff can not disconnect pages");
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToDisconnectPages");
	}    
	/**
	 * This function checks permission to view all conversations and assigned conversations
	 * @param staffPermission
	 * @param assignedUser
	 * @param unassignedUser
	 */
	public void checkPermissionToViewConversations(AllPermissions staffPermission, String assignedUser, String unassignedUser) {
		navigateToConversationScreenByURL(); 
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToViewConversations");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		
		String assignedConvMsg = "Assigned conversations are displayed";
		String unassignedConvMsg = "Unassigned conversations are displayed";
		
		List<String> UINames = getFBUserFromConversation();
		if (staffPermission.getGoChat().getFacebook().isViewAllConversations()) {
    		Assert.assertTrue(UINames.contains(assignedUser), assignedConvMsg);
    		Assert.assertTrue(UINames.contains(unassignedUser), unassignedConvMsg);
		} else {
    		if (staffPermission.getGoChat().getFacebook().isViewAssignedConversation()) {
        		Assert.assertFalse(UINames.contains(unassignedUser), unassignedConvMsg);
        		Assert.assertTrue(UINames.contains(assignedUser), assignedConvMsg);
    		} else {
    			Assert.assertTrue(UINames.isEmpty(), "List of FB Users from UI is empty");
    		}
		}
		
		logger.info("Finished checkPermissionToViewConversations");
	}    
	public void checkPermissionToAssignConversations(AllPermissions staffPermission) {
		navigateToConversationScreenByURL(); 
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToAssignConversations");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		if(!staffPermission.getGoChat().getFacebook().isViewAllConversations() && !staffPermission.getGoChat().getFacebook().isViewAssignedConversation()) {
			logger.info("View conversations permissions not granted. Skipping checkPermissionToAssignConversations");
			return;
		}
		
		clickFirstConversation().clickAssignStaffDropdown().clickAssignToMeBtn();
		if (staffPermission.getGoChat().getFacebook().isAssignStaffToConversation()) {
			clickAssignStaffDropdown();
			//This implicitly means the Restricted Action popup is not displayed
			clickAssignToMeBtn();
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		clickAssignStaffDropdown();
		if (staffPermission.getSetting().getStaffManagement().isViewStaffList()) {
			clickFirstAssignStaffDropdownOption();
			if (staffPermission.getGoChat().getFacebook().isAssignStaffToConversation()) {
				//This implicitly means the Restricted Action popup is not displayed
				clickAssignStaffDropdown();
				clickFirstAssignStaffDropdownOption();
			} else {
				Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			}
		} else {
			Assert.assertTrue(commons.getElements(elements.loc_ddvAssignStaff).isEmpty(), "Staff list is empty");
		}
		
		logger.info("Finished checkPermissionToAssignConversations");
	}    
	public void checkPermissionToUnassignConversations(AllPermissions staffPermission) {
		navigateToConversationScreenByURL(); 
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToUnassignConversations");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		if(!staffPermission.getGoChat().getFacebook().isViewAllConversations() && !staffPermission.getGoChat().getFacebook().isViewAssignedConversation()) {
			logger.info("View conversations permissions not granted. Skipping checkPermissionToUnassignConversations");
			return;
		}
		
		clickFirstConversation().clickAssignStaffDropdown().clickUnAssignBtn();
		if (staffPermission.getGoChat().getFacebook().isUnassignStaffFromConversation()) {
			Assert.assertEquals(getCurrentlyAssignedStaff(), translateText("facebook.currentAssignedStaff"));
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToUnassignConversations");
	}
	public void checkPermissionToCreateTag(AllPermissions staffPermission) {
		navigateToConversationScreenByURL();
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToCreateTag");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		if(!staffPermission.getGoChat().getFacebook().isViewAllConversations() && !staffPermission.getGoChat().getFacebook().isViewAssignedConversation()) {
			logger.info("View conversations permissions not granted. Skipping checkPermissionToCreateTag");
			return;
		}
		
		String tagName = "AutoTag" + System.currentTimeMillis();
		
		clickFirstConversation().clickAddTagBtn().inputTagName(tagName).clickAddBtnInAddTagDialog();
		
		if (staffPermission.getGoChat().getFacebook().isCreateNewTag()) {
			commons.sleepInMiliSecond(500, "Wait after tag is created");
			Assert.assertFalse(commons.getElements(By.xpath(elements.loc_lblTagByName.formatted(tagName))).isEmpty(), "Created tag is not shown");
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToCreateTag");
	}  
	public void checkPermissionToDeleteTag(AllPermissions staffPermission, String deletedTag) {
		navigateToConversationScreenByURL();
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToDeleteTag");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		if(!staffPermission.getGoChat().getFacebook().isViewAllConversations() && !staffPermission.getGoChat().getFacebook().isViewAssignedConversation()) {
			logger.info("View conversations permissions not granted. Skipping checkPermissionToHideTag");
			return;
		}
		
		clickFirstConversation().clickAddTagBtn().clickDeleteTagIcon(deletedTag);
		
		if (staffPermission.getGoChat().getFacebook().isDeleteTag()) {
			//This implicitly means the tag is hidden
			Assert.assertTrue(commons.getElements(By.xpath(elements.loc_icnHideTagByName.formatted(deletedTag))).isEmpty(), "The tag is delete");
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToDeleteTag");
	}    
	public void checkPermissionToHideTag(AllPermissions staffPermission, String hiddenTag) {
		navigateToConversationScreenByURL();
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToHideTag");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		if(!staffPermission.getGoChat().getFacebook().isViewAllConversations() && !staffPermission.getGoChat().getFacebook().isViewAssignedConversation()) {
			logger.info("View conversations permissions not granted. Skipping checkPermissionToHideTag");
			return;
		}
		
		clickFirstConversation().clickAddTagBtn().clickHideTagIcon(hiddenTag);
		
		if (staffPermission.getGoChat().getFacebook().isHideTag()) {
			//This implicitly means the tag is hidden
			Assert.assertTrue(commons.getAttribute(By.xpath(elements.loc_icnHideTagByName.formatted(hiddenTag)), "class").contains("inactive"), "The tag is hidden");
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToHideTag");
	}    
	public void checkPermissionToAssignTag(AllPermissions staffPermission, String assignedTag) {
		navigateToConversationScreenByURL();
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToAssignTag");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		if(!staffPermission.getGoChat().getFacebook().isViewAllConversations() && !staffPermission.getGoChat().getFacebook().isViewAssignedConversation()) {
			logger.info("View conversations permissions not granted. Skipping checkPermissionToAssignTag");
			return;
		}
		
		clickFirstConversation().selectTagToAssign(assignedTag);
		
		if (staffPermission.getGoChat().getFacebook().isAddTagToConversation()) {
			commons.sleepInMiliSecond(500, "Wait after selecting tag");
			//This implicitly means the tag is assigned to the conversation
			Assert.assertFalse(commons.getAttribute(By.xpath(elements.loc_btnTagByName.formatted(assignedTag)), "style").contains("background-color: unset"), "Tag's style attribute containing 'background-color: unset'");
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToAssignTag");
	}    
	public void checkPermissionToUnassignTag(AllPermissions staffPermission, String unassignedTag) {
		navigateToConversationScreenByURL();
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToUnassignTag");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		if(!staffPermission.getGoChat().getFacebook().isViewAllConversations() && !staffPermission.getGoChat().getFacebook().isViewAssignedConversation()) {
			logger.info("View conversations permissions not granted. Skipping checkPermissionToUnassignTag");
			return;
		}
		
		clickFirstConversation().selectTagToAssign(unassignedTag);
		
		if (staffPermission.getGoChat().getFacebook().isRemoveTagFromConversation()) {
			commons.sleepInMiliSecond(500, "Wait after selecting tag");
			//This implicitly means the tag is unassigned from the conversation
			Assert.assertTrue(commons.getAttribute(By.xpath(elements.loc_btnTagByName.formatted(unassignedTag)), "style").contains("background-color: unset"), "Tag's style attribute containing 'background-color: unset'");
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToUnassignTag");
	}    
	public void checkPermissionToLinkCustomer(AllPermissions staffPermission, String fbConversation, String goSellCustomer) {
		navigateToConversationScreenByURL();
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToLinkCustomer");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		if(!staffPermission.getGoChat().getFacebook().isViewAllConversations() && !staffPermission.getGoChat().getFacebook().isViewAssignedConversation()) {
			logger.info("View conversations permissions not granted. Skipping checkPermissionToLinkCustomer");
			return;
		}
		
		clickFirstConversation().selectCustomerToLink(goSellCustomer).clickSaveProfileBtn();
		
		//Remember to update code to check list of assigned customers and all customers
		//Remember to update code to check permission to create customer
		
		if (staffPermission.getGoChat().getFacebook().isLinkCustomerWithFBUser()) {
			Assert.assertEquals(homePage.getToastMessage(), translateText("facebook.customerLinkedSuccessfully"));
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToLinkCustomer");
	}    
	public void checkPermissionToUnlinkCustomer(AllPermissions staffPermission) {
		navigateToConversationScreenByURL();
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToUnlinkCustomer");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		if(!staffPermission.getGoChat().getFacebook().isViewAllConversations() && !staffPermission.getGoChat().getFacebook().isViewAssignedConversation()) {
			logger.info("View conversations permissions not granted. Skipping checkPermissionToUnlinkCustomer");
			return;
		}
		
		clickFirstConversation().clickEditProfileBtn().clickUnlinkCustomerIcon();
		if (staffPermission.getGoChat().getFacebook().isUnlinkCustomerWithFBUser()) {
			Assert.assertTrue(commons.getElements(elements.loc_lblLinkedCustomer).isEmpty(), "Linked customer is removed");
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToUnlinkCustomer");
	}    
	public void checkPermissionToReplyToMessage(AllPermissions staffPermission) {
		/* Temporarily skipped
		navigateToConversationScreenByURL();
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToUnlinkCustomer");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		if(!staffPermission.getGoChat().getFacebook().isViewAllConversations() && !staffPermission.getGoChat().getFacebook().isViewAssignedConversation()) {
			logger.info("View conversations permissions not granted. Skipping checkPermissionToUnlinkCustomer");
			return;
		}
		logger.info("Finished checkPermissionToUnlinkCustomer");
		 */
	}    
	public void checkPermissionToPlaceOrder(AllPermissions staffPermission) {
		/* Temporarily skipped
		navigateToConversationScreenByURL();
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToUnlinkCustomer");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		if(!staffPermission.getGoChat().getFacebook().isViewAllConversations() && !staffPermission.getGoChat().getFacebook().isViewAssignedConversation()) {
			logger.info("View conversations permissions not granted. Skipping checkPermissionToUnlinkCustomer");
			return;
		}
		logger.info("Finished checkPermissionToUnlinkCustomer");
		 */
	}    
	public void checkPermissionToViewAutomationCampaign(AllPermissions staffPermission) {
		navigateToAutomationScreenByURL();
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToViewAutomationCampaign");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		
		if (staffPermission.getGoChat().getFacebook().isViewAutomationCampaignList()) {
			//This implicitly means the list is not empty
			Assert.assertFalse(commons.getText(By.xpath(elements.loc_lblAutomationCampaign)).isEmpty(), "Campaign list is empty");
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToViewAutomationCampaign");
	}  
	public void checkPermissionToViewAutomationCampaignDetail(AllPermissions staffPermission, CreatedAutomationCampaign campaign) {
		navigateToAutomationScreenByURL();
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToViewAutomationCampaignDetail");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		
		//Navigate to campaign detail screen by clicking on Edit icon
		if (staffPermission.getGoChat().getFacebook().isViewAutomationCampaignList()) {
			clickEditAutomationCampaignIcon(campaign.getCampaignName());
			if (staffPermission.getGoChat().getFacebook().isViewAutomationCampaignDetail()) {
				Assert.assertEquals(getAutomationCampaignNameInDetailScreeen(), campaign.getCampaignName());
			} else {
				Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			}
		}
		
		//Navigate to campaign detail screen by URL
		navigateToAutomationDetailScreenByURL(campaign.getId());
		if (staffPermission.getGoChat().getFacebook().isViewAutomationCampaignDetail()) {
			Assert.assertEquals(getAutomationCampaignNameInDetailScreeen(), campaign.getCampaignName());
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToViewAutomationCampaignDetail");
	}  
	public void checkPermissionToCreateAutomationCampaign(AllPermissions staffPermission, ConnectedPages page) {
		navigateToAutomationScreenByURL();
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToCreateAutomationCampaign");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		
		if (staffPermission.getGoChat().getFacebook().isViewAutomationCampaignList()) {
			//Navigate to create screen by clicking on Create Campaign button
			clickCreateAutomationCampaignBtn();
			if (staffPermission.getGoChat().getFacebook().isCreateAutomationCampaign()) {
				selectPage(page.getPageName()).inputCampaignName("Automation " + System.currentTimeMillis());
				commons.click(elements.loc_btnSelectPost);
				commons.click(elements.loc_chkPost);
				commons.click(elements.loc_btnSelect);
				commons.click(elements.loc_btnAddResponse);
				commons.click(elements.loc_btnSaveAutomationCampaign);
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.create.successMessage"));
			} else {
				Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			}
		} else {
			//Navigate to create screen by URL
			navigateToCreateAutomationScreenByURL();
			selectPage(page.getPageName()).inputCampaignName("Automation " + System.currentTimeMillis());
			commons.click(elements.loc_btnSelectPost);
			commons.click(elements.loc_chkPost);
			commons.click(elements.loc_btnSelect);
			commons.click(elements.loc_btnAddResponse);
			commons.click(elements.loc_btnSaveAutomationCampaign);
			if (staffPermission.getGoChat().getFacebook().isCreateAutomationCampaign()) {
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.create.successMessage"));
			} else {
				Assert.assertEquals(homePage.getToastMessage(), translateText("common.toast.somethingWrong"));
			}
		}
		
		logger.info("Finished checkPermissionToCreateAutomationCampaign");
	}  
	public void checkPermissionToEditAutomationCampaign(AllPermissions staffPermission, CreatedAutomationCampaign campaign) {
		navigateToAutomationDetailScreenByURL(campaign.getId());
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToEditAutomationCampaign");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		if (!staffPermission.getGoChat().getFacebook().isViewAutomationCampaignDetail()) {
			logger.info("View Campaign Detail permission not granted. Skipping checkPermissionToEditAutomationCampaign");
			return;
		}
		
		inputCampaignName("Automation " + System.currentTimeMillis());
		commons.click(elements.loc_btnSaveAutomationCampaign);
		if (staffPermission.getGoChat().getFacebook().isEditAutomationCampaign()) {
			Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		logger.info("Finished checkPermissionToEditAutomationCampaign");
	}  
	public void checkPermissionToDeleteAutomationCampaign(AllPermissions staffPermission, CreatedAutomationCampaign campaign) {
		navigateToAutomationScreenByURL();
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToDeleteAutomationCampaign");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}

		if (!staffPermission.getGoChat().getFacebook().isViewAutomationCampaignList()) {
			logger.info("View Automation Campaigns permission not granted. Skipping checkPermissionToDeleteAutomationCampaign");
			return;
		}		
		
		clickDeleteAutomationCampaignIcon(campaign.getCampaignName());
		if (staffPermission.getGoChat().getFacebook().isDeleteAutomationCampaign()) {
			new ConfirmationDialog(driver).clickOKBtn();
			Assert.assertEquals(homePage.getToastMessage(), translateText("marketing.landingPage.delete.successMessage"));
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToDeleteAutomationCampaign");
	}  
	public void checkPermissionToViewBroadcastList(AllPermissions staffPermission) {
		navigateToBroadcastScreenByURL();
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToViewBroadcastList");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		
		if (staffPermission.getGoChat().getFacebook().isViewBroadcastCampaignList()) {
			//This implicitly means the list is not empty
			Assert.assertFalse(commons.getText(By.xpath(elements.loc_lblBroadcastCampaign)).isEmpty(), "Campaign list is empty");
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToViewBroadcastList");
	}  
	public void checkPermissionToViewBroadcastCampaignDetail(AllPermissions staffPermission, CreatedBroadcast campaign) {
		navigateToBroadcastScreenByURL();
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToViewBroadcastCampaignDetail");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		
		//Navigate to campaign detail screen by clicking on Edit icon
		if (staffPermission.getGoChat().getFacebook().isViewBroadcastCampaignList()) {
			clickEditBroadcastCampaignIcon(campaign.getCampaignName());
			if (staffPermission.getGoChat().getFacebook().isViewBroadcastCampaignDetail()) {
				Assert.assertEquals(getBroadcastCampaignNameInDetailScreeen(), campaign.getCampaignName());
			} else {
				Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			}
		}
		
		//Navigate to campaign detail screen by URL
		navigateToBroadcastDetailScreenByURL(campaign.getId());
		if (staffPermission.getGoChat().getFacebook().isViewBroadcastCampaignDetail()) {
			Assert.assertEquals(getBroadcastCampaignNameInDetailScreeen(), campaign.getCampaignName());
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToViewBroadcastCampaignDetail");
	}  	
	public void checkPermissionToCreateBroadcastCampaign(AllPermissions staffPermission, ConnectedPages page) {
		navigateToBroadcastScreenByURL();
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToCreateBroadcastCampaign");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		
		//Check view segment list permission
		
		if (staffPermission.getGoChat().getFacebook().isViewBroadcastCampaignList()) {
			//Navigate to create screen by clicking on Create Campaign button
			clickCreateBroadcastCampaignBtn();
			if (staffPermission.getGoChat().getFacebook().isCreateBroadcastCampaign()) {
				selectPage(page.getPageName()).inputCampaignName("Broadcast " + System.currentTimeMillis());
				commons.click(elements.loc_lnkAddSegment);
				commons.click(elements.loc_chkSegment);
				commons.click(elements.loc_btnSelect);
				commons.click(elements.loc_btnAddResponse);
				commons.click(elements.loc_btnSaveBroadcastCampaign);
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.create.successMessage"));
			} else {
				Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			}
		} else {
			//Navigate to create screen by URL
			navigateToCreateBroadcastScreenByURL();
			selectPage(page.getPageName()).inputCampaignName("Broadcast " + System.currentTimeMillis());
			commons.click(elements.loc_lnkAddSegment);
			commons.click(elements.loc_chkSegment);
			commons.click(elements.loc_btnSelect);
			commons.click(elements.loc_btnAddResponse);
			commons.click(elements.loc_btnSaveBroadcastCampaign);
			if (staffPermission.getGoChat().getFacebook().isCreateBroadcastCampaign()) {
				Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.create.successMessage"));
			} else {
				Assert.assertEquals(homePage.getToastMessage(), translateText("common.toast.somethingWrong"));	
			}
		}
		logger.info("Finished checkPermissionToCreateBroadcastCampaign");
	}
	public void checkPermissionToEditBroadcastCampaign(AllPermissions staffPermission, CreatedBroadcast campaign) {
		navigateToBroadcastDetailScreenByURL(campaign.getId());
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToEditBroadcastCampaign");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		
		// Check view segment list permission
		if (!staffPermission.getGoChat().getFacebook().isViewBroadcastCampaignDetail()) {
			logger.info("View Campaign Detail permission not granted. Skipping checkPermissionToEditBroadcastCampaign");
			return;
		}
		
		inputCampaignName("Broadcast " + System.currentTimeMillis());
		commons.click(elements.loc_btnSaveBroadcastCampaign);
		if (staffPermission.getGoChat().getFacebook().isEditBroadcastCampaign()) {
			Assert.assertEquals(homePage.getToastMessage(), translateText("promotion.flashSale.edit.successMessage"));
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		logger.info("Finished checkPermissionToEditBroadcastCampaign");
	}
	public void checkPermissionToDeleteBroadcastCampaign(AllPermissions staffPermission, CreatedBroadcast campaign) {
		navigateToBroadcastScreenByURL();
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Facebook permission not granted. Skipping checkPermissionToDeleteBroadcastCampaign");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}

		if (!staffPermission.getGoChat().getFacebook().isViewBroadcastCampaignList()) {
			logger.info("View Campaigns permission not granted. Skipping checkPermissionToDeleteBroadcastCampaign");
			return;
		}		
		
		clickDeleteBroadcastCampaignIcon(campaign.getCampaignName());
		if (staffPermission.getGoChat().getFacebook().isDeleteBroadcastCampaign()) {
			new ConfirmationDialog(driver).clickOKBtn();
			Assert.assertEquals(homePage.getToastMessage(), translateText("marketing.landingPage.delete.successMessage"));
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToDeleteBroadcastCampaign");
	}  	
	public void checkFacebookPermission(AllPermissions staffPermission, String connectedPage, String disconnectedPage) {
//		checkPermissionToConnectAccount(staffPermission);
//		checkPermissionToDisconnectAccount(staffPermission);
//		checkPermissionToAddRemovePages(staffPermission);
//		checkPermissionToConnectPages(staffPermission, connectedPage);
//		checkPermissionToDisconnectPages(staffPermission, disconnectedPage);
	}     

}
