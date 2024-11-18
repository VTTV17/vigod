package web.Dashboard.settings.staff_management;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import utilities.data.DataGenerator;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import utilities.screenshot.Screenshot;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;
import static utilities.links.Links.DOMAIN;

public class StaffPage extends StaffElement {
    Logger logger = LogManager.getLogger(StaffPage.class);
    HomePage homePage;
	UICommonAction commons;
	WebDriverWait wait;
    
    public StaffPage(WebDriver driver) {
        super(driver);
        homePage = new HomePage(driver);
		commons = new UICommonAction(driver);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public StaffPage clickStaffManagementTab() {
    	commons.clickElement(STAFF_MANAGEMENT_MENU);
    	logger.info("Clicked on 'Staff Management' Tab");
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	UICommonAction.sleepInMiliSecond(500);
    	return this;
    }
    
    public StaffPage clickOnTheAddStaffBtn() {

        wait.until(ExpectedConditions.elementToBeClickable(ADD_STAFF_BTN)).click();
        logger.info("Click on Add Staff button to open the Add staff popup");
        return this;
    }

    public StaffPage clickOnTheEditIcon() {
        commons.click(loc_btnEditIcon);
        logger.info("Click on the Edit icon to open the Edit staff popup");
        return this;
    }
    
    public StaffPage clickDeleteIcon() {
    	commons.click(loc_btnDeleteIcon);
    	logger.info("Clicked Delete icon");
    	return this;
    }

    public StaffPage inputStaffName(String staffName) {
        wait.until(ExpectedConditions.elementToBeClickable(STAFF_NAME)).clear();
        STAFF_NAME.sendKeys(staffName);
        logger.info("Input the staff name: %s".formatted(staffName));
        return this;
    }
    
    public StaffPage assignPermissionsToStaff(List<Integer> roleList) {
    	for (Integer role : roleList) {
    		if ((role < STAFF_PERMISSIONS_LABEL.size())) {
    			if ((role == 12) || (role == 13)) {
    				if (!STAFF_PERMISSIONS_CHECKBOX.get(0).isSelected()) {
    					commons.clickElement(STAFF_PERMISSIONS_LABEL.get(0));
    				}
    			}
    			commons.clickElement(STAFF_PERMISSIONS_LABEL.get(role));
    		}
    	}
    	return this;
    }

    public StaffPage selectBranch(List<Integer> branchList) {
        for (Integer branch : branchList) {
            wait.until(ExpectedConditions.elementToBeClickable(STAFF_BRANCH_LABEL.get(branch))).click();
            logger.info("Assign %s branch to new staff".formatted(STAFF_BRANCH_LABEL.get(branch).getText()));
        }
        return this;
    }

    public StaffPage clickDoneBtn() {
        wait.until(ExpectedConditions.elementToBeClickable(DONE_BTN)).click();
        logger.info("Click on the Done button to complete create new staff");
        return this;
    }

    public StaffPage clickCancelBtn() {
    	commons.clickElement(CANCEL_BTN);
    	logger.info("Clicked on 'Cancel' button");
    	return this;
    }    
    
    /*Verify permission for certain feature*/
    public void verifyPermissionToAddStaff(String permission) {
    	clickStaffManagementTab();
		if (permission.contentEquals("A")) {
			clickOnTheAddStaffBtn();
			inputStaffName("Staff A");
            List<Integer> staffRole = List.of(1);
            List<Integer> staffBranch = List.of(0);
            assignPermissionsToStaff(staffRole);
            selectBranch(staffBranch);
            clickCancelBtn();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/       

    StaffPage navigateByURL(String url) {
		driver.get(url);
		logger.info("Navigated to: " + url);
		commons.removeFbBubble();
		homePage.waitTillSpinnerDisappear1();
		return this;
	}		

	public StaffPage navigateToDetailScreenByURL() {
		navigateByURL(DOMAIN + "/setting?tabId=7");
//    	commons.sleepInMiliSecond(500, "Wait a little after navigation");
		return this;
	}

	//Temporary function, will think of a better way to do this
    public StaffPage selectAllGroups() {
    	commons.click(loc_ddlSelectGroupForm);
    	commons.click(loc_ddvSelectFirstGroupForm);
    	logger.info("Select Permission: All Groups");
    	return this;
    }
    
    public boolean isStaffActivated() {
    	return commons.getElement(new ByChained(loc_btnActivateStaffToggle, loc_tmpInputTag)).isSelected();
    }    

    //Temporary function, will think of a better way to do this
    public StaffPage clickActivateStaffToggle() {
    	commons.click(loc_btnActivateStaffToggle);
    	logger.info("Clicked Activate Staff toggle");
    	return this;
    }     
    
    //Temporary function, will think of a better way to do this
    public StaffPage activateStaff() {
    	if (isStaffActivated()) {
    		logger.info("Staff has already been activated");
    		return this;
    	}
    	clickActivateStaffToggle();
    	logger.info("Activated staff member: The first one");
    	return this;
    }  
    //Temporary function, will think of a better way to do this
    public StaffPage deactivateStaff() {
    	if (!isStaffActivated()) {
    		logger.info("Staff has already been deactivated");
    		return this;
    	}
    	clickActivateStaffToggle();
    	logger.info("Deactivated staff member: The first one");
    	return this;
    }  
	
	boolean isViewStaffListPermissionGranted(AllPermissions staffPermission) {
		return staffPermission.getSetting().getStaffManagement().isViewStaffList();
	}
	boolean isAddStaffPermissionGranted(AllPermissions staffPermission) {
		return staffPermission.getSetting().getStaffManagement().isAddStaff();
	}
	boolean isEditStaffPermissionGranted(AllPermissions staffPermission) {
		return staffPermission.getSetting().getStaffManagement().isEditStaff();
	}
	boolean isActivateStaffPermissionGranted(AllPermissions staffPermission) {
		return staffPermission.getSetting().getStaffManagement().isActiveDeactivateStaff();
	}
	boolean isDeleteStaffPermissionGranted(AllPermissions staffPermission) {
		return staffPermission.getSetting().getStaffManagement().isDeleteStaff();
	}
	boolean isStaffManagementPermissionProhibited(AllPermissions staffPermission) {
		boolean[] allStaffManagementPermisison = {
				isViewStaffListPermissionGranted(staffPermission),
				isAddStaffPermissionGranted(staffPermission),
				isEditStaffPermissionGranted(staffPermission),
				isActivateStaffPermissionGranted(staffPermission),
				isDeleteStaffPermissionGranted(staffPermission),
		};
	    for(boolean individualPermission : allStaffManagementPermisison) if (individualPermission) return false;
	    return true;
	}
	
    void checkPermissionToViewStaffList(AllPermissions staffPermission) {
    	navigateToDetailScreenByURL(); 
    	if (isStaffManagementPermissionProhibited(staffPermission)) {
    		logger.info("Staff does not have Staff Management permission. Skipping checkPermissionToViewStaffList");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	if (isViewStaffListPermissionGranted(staffPermission)) {
			Assert.assertTrue(commons.getElements(loc_lblStaffEmailTable).size()>0, "Number of staff email elements > 0");
		} else {
			Assert.assertFalse(commons.getElements(loc_lblStaffEmailTable).size()>0, "Number of staff email elements > 0");
		}
    	logger.info("Finished checkPermissionToViewStaffList");
    }    
    
    void checkPermissionToAddStaff(AllPermissions staffPermission) {
    	navigateToDetailScreenByURL(); 
    	
    	if (isStaffManagementPermissionProhibited(staffPermission)) {
    		logger.info("Staff does not have Staff Management permission. Skipping checkPermissionToAddStaff");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	
    	String randomNumber = new DataGenerator().randomNumberGeneratedFromEpochTime(5);
    	String name = "Luke Auto " + randomNumber;
    	String mail = "lukeauto" + randomNumber + "@mailnesia.com";
    	
    	clickOnTheAddStaffBtn();
    	
    	if (isAddStaffPermissionGranted(staffPermission)) {
//    		inputStaffName(name).inputStaffMail(mail).selectAllGroups().selectBranch(Arrays.asList(0)).clickDoneBtn();
//    		homePage.getToastMessage();
    		clickCancelBtn();
    	} else {
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    	}
    	logger.info("Finished checkPermissionToAddStaff");
    }   
    
    void checkPermissionToEditStaff(AllPermissions staffPermission) {
    	navigateToDetailScreenByURL(); 
    	
    	if (isStaffManagementPermissionProhibited(staffPermission)) {
    		logger.info("Staff does not have Staff Management permission. Skipping checkPermissionToEditStaff");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	
    	if (!isViewStaffListPermissionGranted(staffPermission)) {
    		logger.info("Staff does not have view staff list permission. Skipping checkPermissionToEditStaff");
    		return;
    	}
    	
    	String randomNumber = new DataGenerator().randomNumberGeneratedFromEpochTime(5);
    	String name = "Luke Auto " + randomNumber;
    	
    	clickOnTheEditIcon();
    	
    	if (isEditStaffPermissionGranted(staffPermission)) {
    		inputStaffName(name).clickDoneBtn();
    		homePage.getToastMessage();
    	} else {
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    	}
    	logger.info("Finished checkPermissionToEditStaff");
    }    
    
    void checkPermissionToActivateStaff(AllPermissions staffPermission) {
    	navigateToDetailScreenByURL(); 
    	
    	if (isStaffManagementPermissionProhibited(staffPermission)) {
    		logger.info("Staff does not have Staff Management permission. Skipping checkPermissionToActivateStaff");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	
    	if (!isViewStaffListPermissionGranted(staffPermission)) {
    		logger.info("Staff does not have view staff list permission. Skipping checkPermissionToActivateStaff");
    		return;
    	}
    	
    	clickActivateStaffToggle();
    	
    	if (isActivateStaffPermissionGranted(staffPermission)) {
    		//Bug: Toggle is still switched
    		System.out.println();
    	} else {
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    	}
    	logger.info("Finished checkPermissionToActivateStaff");
    }    
    
    void checkPermissionToDeleteStaff(AllPermissions staffPermission) {
    	navigateToDetailScreenByURL(); 
    	
    	if (isStaffManagementPermissionProhibited(staffPermission)) {
    		logger.info("Staff does not have Staff Management permission. Skipping checkPermissionToDeleteStaff");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    		return;
    	}
    	
    	if (!isViewStaffListPermissionGranted(staffPermission)) {
    		logger.info("Staff does not have view staff list permission. Skipping checkPermissionToDeleteStaff");
    		return;
    	}
    	
    	clickDeleteIcon();
    	
    	if (isDeleteStaffPermissionGranted(staffPermission)) {
//    		new ConfirmationDialog(driver).clickOKBtn();
//    		homePage.getToastMessage();
    		Assert.assertTrue(new ConfirmationDialog(driver).isConfirmationDialogDisplayed(), "Confirmation dialog appears");
    	} else {
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
    	}
    	logger.info("Finished checkPermissionToDeleteStaff");
    }    
    
    public void checkStaffManagementPermission(AllPermissions staffPermission, String adminPassword) {
    	checkPermissionToViewStaffList(staffPermission);
    	checkPermissionToAddStaff(staffPermission);
    	checkPermissionToEditStaff(staffPermission);
    	checkPermissionToActivateStaff(staffPermission);
    	checkPermissionToDeleteStaff(staffPermission);
    } 	
}
