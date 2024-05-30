package web.Dashboard.gochat;

import static utilities.links.Links.DOMAIN;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.utils.PropertiesUtil;
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
		commons.waitInvisibilityOfElementLocated(elements.loc_icnLoadMoreLoading);
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
	public Facebook navigateToBroadcastScreenByURL() {
		navigateByURL(DOMAIN + "/gosocial/facebook/broadcast");
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
		commons.inputText(elements.loc_txtTagNameInDialog, name);
		logger.info("Input tag name: " + name);
		return this;
	}
	public Facebook clickAddBtnInAddTagDialog() {
		commons.click(elements.loc_btnAddTagInDialog);
		logger.info("Clicked Add button in Add Tag dialog");
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
			logger.info("Facebook permission not granted. Skipping checkPermissionToViewAssignedConversations");
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
			logger.info("Facebook permission not granted. Skipping checkPermissionToAssignConversations");
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
			logger.info("Facebook permission not granted. Skipping checkPermissionToAssignConversations");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
			return;
		}
		if(!staffPermission.getGoChat().getFacebook().isViewAllConversations() && !staffPermission.getGoChat().getFacebook().isViewAssignedConversation()) {
			logger.info("View conversations permissions not granted. Skipping checkPermissionToCreateTag");
			return;
		}
		
		String tagName = "Auto Tag " + System.currentTimeMillis();
		
		clickFirstConversation().clickAddTagBtn().inputTagName(tagName).clickAddBtnInAddTagDialog();
		
		if (staffPermission.getGoChat().getFacebook().isCreateNewTag()) {
			Assert.assertFalse(commons.getElements(By.xpath(elements.loc_lblTagByName.formatted(tagName))).isEmpty(), "Created tag is not shown");
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		
		logger.info("Finished checkPermissionToCreateTag");
	}    

	public void checkFacebookPermission(AllPermissions staffPermission, String connectedPage, String disconnectedPage) {
//		checkPermissionToConnectAccount(staffPermission);
//		checkPermissionToDisconnectAccount(staffPermission);
//		checkPermissionToAddRemovePages(staffPermission);
//		checkPermissionToConnectPages(staffPermission, connectedPage);
//		checkPermissionToDisconnectPages(staffPermission, disconnectedPage);
	}     

}
