package web.Dashboard.settings.permission.management;

import static utilities.links.Links.DOMAIN;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.settings.permission.create.CreatePermissionPage;
import web.Dashboard.settings.permission.permissionassignment.AssignPermissionPage;

public class PermissionManagementPage {

	final static Logger logger = LogManager.getLogger(PermissionManagementPage.class);

	WebDriver driver;
	UICommonAction commonAction;
	HomePage homePage;
	PermissionManagementPageElement elements;

	public PermissionManagementPage(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		homePage = new HomePage(driver);
		elements = new PermissionManagementPageElement();
	}

	PermissionManagementPage navigateByURL(String url) {
		driver.get(url);
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		homePage.waitTillSpinnerDisappear1();
		return this;
	}		

	public PermissionManagementPage navigateToManagementScreenByURL() {
		navigateByURL(DOMAIN + "/setting?tabId=permissions-management");
    	commonAction.sleepInMiliSecond(500, "Wait a little after navigation");
		return this;
	}		
	
	public PermissionManagementPage navigateToCreateScreenByURL() {
		navigateByURL(DOMAIN + "/setting?tabId=create-group-permission");
		return this;
	}		
	
	public PermissionManagementPage navigateToEditScreenByURL(int groupId) {
		navigateByURL(DOMAIN + "/setting?tabId=edit-group-permission&id=" + groupId);
		return this;
	}		
	
	public PermissionManagementPage navigateToAssignmentScreenByURL(int groupId) {
		navigateByURL(DOMAIN + "/setting?tabId=group-staff-management&id=" + groupId);
		return this;
	}		
	
	public void deletePermissionGroup(int groupId) {
		commonAction.click(By.xpath(elements.loc_btnSpecificDeleteIcon.formatted(groupId)));
	}
	
	boolean isPermissionProhibited(AllPermissions staffPermission) {
		boolean[] allStaffManagementPermisison = {
				staffPermission.getSetting().getPermission().isViewPermissionGroupList(),
				staffPermission.getSetting().getPermission().isCreatePermissionGroup(),
				staffPermission.getSetting().getPermission().isEditPermissionGroup(),
				staffPermission.getSetting().getPermission().isAddStaffToPermissionGroup(),
				staffPermission.getSetting().getPermission().isDeletePermissionGroup(),
				staffPermission.getSetting().getPermission().isRemoveStaffFromPermissionGroup()
		};
	    for(boolean individualPermission : allStaffManagementPermisison) if (individualPermission) return false;
	    return true;
	}	    
    
    public void checkPermissionToViewPermissionGroupList(AllPermissions staffPermission) {
    	navigateToManagementScreenByURL(); 
    	if (isPermissionProhibited(staffPermission)) {
    		logger.info("Staff does not have Set Permission permission. Skipping checkPermissionToViewPermissionList");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	
    	boolean flag = commonAction.getElements(elements.loc_btnDeleteIcon).size()>0;
    	String error = "Number of permission groups > 0";
    	if (staffPermission.getSetting().getPermission().isViewPermissionGroupList()) {
    		Assert.assertTrue(flag, error);
		} else {
			Assert.assertFalse(flag, error);
		}
    	logger.info("Finished checkPermissionToViewPermissionList");
    }    
    
    public void checkPermissionToCreatePermissionGroups(AllPermissions staffPermission) {
    	navigateToCreateScreenByURL(); 
    	if (isPermissionProhibited(staffPermission)) {
    		logger.info("Staff does not have Set Permission permission. Skipping checkPermissionToCreatePermissionGroups");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	
    	if (staffPermission.getSetting().getPermission().isCreatePermissionGroup()) {
        	String groupName = "Auto Permission " + new DataGenerator().randomNumberGeneratedFromEpochTime(5);
        	new CreatePermissionPage(driver).inputGroupName(groupName).clickFullPermissionCheckbox().clickSaveBtn();
        	homePage.getToastMessage();
    	} else {
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
    	}
    	logger.info("Finished checkPermissionToCreatePermissionGroups");
    }    
    
    public void checkPermissionToEditPermissionGroup(AllPermissions staffPermission, int groupId) {
    	navigateToEditScreenByURL(groupId); 
    	if (isPermissionProhibited(staffPermission)) {
    		logger.info("Staff does not have Set Permission permission. Skipping checkPermissionToEditPermissionGroup");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	if (staffPermission.getSetting().getPermission().isEditPermissionGroup()) {
    		String description = "Auto Description " + new DataGenerator().randomNumberGeneratedFromEpochTime(5);
    		new CreatePermissionPage(driver).inputDescription(description).clickSaveBtn();
    		homePage.getToastMessage();
    	} else {
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
    	}
    	logger.info("Finished checkPermissionToEditPermissionGroup");
    }    
    
    public void checkPermissionToGrantPermissionGroup(AllPermissions staffPermission, int groupId, String staffMail) {
    	navigateToAssignmentScreenByURL(groupId); 
    	
    	if (isPermissionProhibited(staffPermission)) {
    		logger.info("Staff does not have Set Permission permission. Skipping checkPermissionToGrantPermissionGroup");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
    		return;
    	}
    	if (staffPermission.getSetting().getPermission().isAddStaffToPermissionGroup()) {
    		new AssignPermissionPage(driver).clickAddStaffBtn().inputSelectStaffSearchTerm(staffMail).selectFirstStaff().clickAddBtn();
    		homePage.getToastMessage();
    	} else {
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
    	}
    	logger.info("Finished checkPermissionToGrantPermissionGroup");
    }    
    
    public void checkPermissionToRevokePermissionGroup(AllPermissions staffPermission, int groupId, String staffMail) {
    	navigateToAssignmentScreenByURL(groupId); 
    	
    	if (isPermissionProhibited(staffPermission)) {
    		logger.info("Staff does not have Set Permission permission. Skipping checkPermissionToRevokePermissionGroup");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
    		return;
    	}
    	
    	//Not sure if this is a bug
    	if (!staffPermission.getSetting().getPermission().isAddStaffToPermissionGroup()) {
    		logger.info("Staff does not have permission to add staff to permission group. Skipping checkPermissionToRevokePermissionGroup");
    		return;
    	}
    	
		new AssignPermissionPage(driver).removeStaffFromAssignmentList(staffMail);
		new ConfirmationDialog(driver).clickOKBtn();
    	if (staffPermission.getSetting().getPermission().isRemoveStaffFromPermissionGroup()) {
    		homePage.getToastMessage();
    	} else {
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted popup appears");
    	}
    	logger.info("Finished checkPermissionToRevokePermissionGroup");
    }    
    
    public void checkPermissionToDeletePermissionGroup(AllPermissions staffPermission, int groupId) {
    	navigateToManagementScreenByURL(); 
    	
    	if (isPermissionProhibited(staffPermission)) {
    		logger.info("Staff does not have Set Permission permission. Skipping checkPermissionToDeletePermissionGroup");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
    		return;
    	}
    	
    	if (!staffPermission.getSetting().getPermission().isViewPermissionGroupList()) {
    		logger.info("Staff does not have to view permission list. Skipping checkPermissionToDeletePermissionGroup");
    		return;
    	}
    	
		deletePermissionGroup(groupId);
		new ConfirmationDialog(driver).clickOKBtn();
    	if (staffPermission.getSetting().getPermission().isDeletePermissionGroup()) {
    		homePage.getToastMessage();
    	} else {
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted action popup appears");
    	}
    	logger.info("Finished checkPermissionToDeletePermissionGroup");
    }    
    
    public void checkSetPermissionPermission(AllPermissions staffPermission, int groupId, String grantedStaffMail, String revokedStaffMail, int deletedGroupId) {
    	checkPermissionToViewPermissionGroupList(staffPermission);
    	checkPermissionToCreatePermissionGroups(staffPermission);
    	checkPermissionToEditPermissionGroup(staffPermission, groupId);
    	checkPermissionToGrantPermissionGroup(staffPermission, groupId, grantedStaffMail);
    	checkPermissionToRevokePermissionGroup(staffPermission, groupId, revokedStaffMail);
    	checkPermissionToDeletePermissionGroup(staffPermission, deletedGroupId);
    } 
    
}
