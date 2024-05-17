package web.Dashboard.settings.branch_management;

import static utilities.links.Links.DOMAIN;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import utilities.enums.PaymentMethod;
import utilities.model.staffPermission.AllPermissions;
import utilities.permission.CheckPermission;
import web.Dashboard.confirmationdialog.ConfirmationDialog;
import web.Dashboard.home.HomePage;
import web.Dashboard.settings.plans.PlansPage;

public class BranchPage extends BranchElement {
	final static Logger logger = LogManager.getLogger(BranchPage.class);
	
    WebDriverWait wait;
	UICommonAction commonAction;
	HomePage homePage;
    
    public BranchPage(WebDriver driver) {
        super(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        homePage = new HomePage(driver);
    }

	public BranchPage navigate() {
		commonAction.clickElement(BRANCH_MANAGEMENT_MENU);
		logger.info("Navigated to Branch Management tab.");
		return this;
	}

	public BranchPage clickAddBranch() {
		if (commonAction.isElementVisiblyDisabled(ADD_BRANCH_BTN)) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(ADD_BRANCH_BTN));
			return this;
		}
		commonAction.clickElement(ADD_BRANCH_BTN);
		logger.info("Clicked on Add Branch button.");
		return this;
	}	
	
    private String removeCountry(String address) {
        String[] s = address.split(",");
        return "%s,%s,%s,%s".formatted(s[0], s[1], s[2], s[3]);
    }

    public Map<String, String> getBranchNameAndAddress() {
        Map<String, String> branchInfo = new HashMap<>();
        new HomePage(driver).navigateToSettingsPage();
        wait.until(ExpectedConditions.elementToBeClickable(BRANCH_MANAGEMENT_MENU)).click();
        waitElementList(BRANCH_NAME_LIST);
        for (int i = 0; i < BRANCH_NAME_LIST.size(); i++) {
            branchInfo.put(wait.until(ExpectedConditions.visibilityOf(BRANCH_NAME_LIST.get(i))).getText(),
                    removeCountry(wait.until(ExpectedConditions.visibilityOf(BRANCH_ADDRESS_LIST.get(i))).getText()));
        }
        return branchInfo;
    }

	/**
	 * <p>
	 * Get info of free branch
	 * <p>
	 * Example: List<String> info = getFreeBranchInfo()
	 * @return a List with the 0-indexed element containing the branch name, 1-indexed element denoting branch code,
	 * 2-indexed element denoting branch location, 3-indexed element denoting branch status.
	 */	
    public List<String> getFreeBranchInfo() {
    	waitElementList(BRANCH_NAME_LIST);
    	List<String> info = new ArrayList<>();
    	for (WebElement el:FREE_BRANCH_INFO) {
    		String rawInfo = commonAction.getText(el);
    		if (rawInfo.length()>0) info.add(rawInfo);
    	}
    	return info;
    }    
    
    private void waitElementList(List<WebElement> elementList) {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) driver -> {
            assert driver != null;
            return elementList.size() > 0;
        });
    }
    
    /*Verify permission for certain feature*/
    public void verifyPermissionToAddBranch(String permission) {
    	navigate();
    	commonAction.sleepInMiliSecond(3000);
//    	waitElementList(BRANCH_NAME_LIST);
    	clickAddBranch();
    	boolean flag = new ConfirmationDialog(driver).isConfirmationDialogDisplayed();
		if (permission.contentEquals("A")) {
			new ConfirmationDialog(driver).clickCancelBtn();
			Assert.assertTrue(flag);
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(flag);
		} else {
			Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/   

    BranchPage navigateByURL(String url) {
		driver.get(url);
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		homePage.waitTillSpinnerDisappear1();
		return this;
	}		

	public BranchPage navigateToManagementScreenByURL() {
		navigateByURL(DOMAIN + "/setting?tabId=8");
		return this;
	}    

	public BranchPage clickEditBtn() {
		commonAction.click(loc_btnEdit);
		logger.info("Clicked Edit button");
		return this;
	}
	
	public String getBranchCodeInDetailDialog() {
		String code = commonAction.getAttribute(loc_lblBranchCodeInDialog, "value");
		logger.info("Retrieved branch code in detail dialog: " + code);
		return code;
	}
	
	public BranchPage clickShowBranchOnSFCheckbox() {
		commonAction.click(loc_chkShowBranchOnSF);
		logger.info("Clicked 'Show Branch On SF' checkbox");
		return this;
	}

	public BranchPage clickAddOrUpdateBtn() {
		commonAction.click(loc_btnUpdate);
		logger.info("Clicked Add/Update button");
		return this;
	}	
	
	public BranchPage clickRenewExpiredBranchBtn() {
		commonAction.click(loc_btnRenewExpiredBranch);
		logger.info("Clicked 'Renew' button");
		return this;
	}	
	
	public BranchPage clickUpgradeBranchBtn() {
		commonAction.click(loc_btnRenewExpiredBranch);
		logger.info("Clicked 'Upgrade' button");
		return this;
	}	
	
	//Temparory function, will think of a better to handle it
	public BranchPage clickActivateBranchToggleBtn() {
		commonAction.click(loc_btnActivateBranchToggle);
		logger.info("Clicked toggle to activate/deactivate the first paid branch");
		return this;
	}	
	
	boolean isPermissionProhibited(AllPermissions staffPermission) {
		boolean[] allStaffManagementPermisison = {
				staffPermission.getSetting().getBranchManagement().isViewBranchInformation(),
				staffPermission.getSetting().getBranchManagement().isActiveDeactivateBranch(),
				staffPermission.getSetting().getBranchManagement().isAddBranch(),
				staffPermission.getSetting().getBranchManagement().isPurchaseBranch(),
				staffPermission.getSetting().getBranchManagement().isRenewBranch(),
				staffPermission.getSetting().getBranchManagement().isUpdateBranchInformation(),
				staffPermission.getSetting().getBranchManagement().isUpgradeBranch(),
		};
	    for(boolean individualPermission : allStaffManagementPermisison) if (individualPermission) return false;
	    return true;
	}		
	
	void checkPermissionToViewBranchDetail(AllPermissions staffPermission) {
		navigateToManagementScreenByURL(); 

    	if (isPermissionProhibited(staffPermission)) {
    		logger.info("Staff does not have Branch permission. Skipping checkPermissionToViewBranchDetail");
    		Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
    		return;
    	}
		
    	clickEditBtn();
		if (staffPermission.getSetting().getBranchManagement().isViewBranchInformation()) {
			Assert.assertFalse(getBranchCodeInDetailDialog().isEmpty(), "Branch code from detail dialog is empty");
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Action popup appears");
		}
		logger.info("Finished checkPermissionToViewBranchDetail");
	}
	
	void checkPermissionToUpdateBranchDetail(AllPermissions staffPermission) {
		navigateToManagementScreenByURL(); 
		
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Staff does not have Branch permission. Skipping checkPermissionToUpdateBranchDetail");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
			return;
		}
		
		if (!staffPermission.getSetting().getBranchManagement().isViewBranchInformation()) {
			logger.info("Staff does not have View Branch Info permission. Skipping checkPermissionToUpdateBranchDetail");
			return;
		}
		
		clickEditBtn();
		clickShowBranchOnSFCheckbox();
		clickShowBranchOnSFCheckbox();
		clickAddOrUpdateBtn();
		
		if (staffPermission.getSetting().getBranchManagement().isUpdateBranchInformation()) {
			homePage.getToastMessage();
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Action popup appears");
		}
		logger.info("Finished checkPermissionToUpdateBranchDetail");
	}
	
	void checkPermissionToAddBranch(AllPermissions staffPermission) {
		navigateToManagementScreenByURL(); 
		
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Staff does not have Branch permission. Skipping checkPermissionToAddBranch");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
			return;
		}
		
		clickAddBranch();
		
		ConfirmationDialog confirmationDlg = new ConfirmationDialog(driver);
		
		if (staffPermission.getSetting().getBranchManagement().isAddBranch()) {
			if(confirmationDlg.isConfirmationDialogDisplayed()) {
				logger.info("The number of branches has reached the allowed limit. Skipping checkPermissionToAddBranch");
				return;
			} else {
				Assert.assertTrue(commonAction.getElements(loc_btnEdit).size() >0, "Add button appears in Add dialog");
			}
			//We won't actually add a new branch for now as there's no way to delete it later on
		} else {
			if(confirmationDlg.isConfirmationDialogDisplayed()) {
				logger.info("The number of branches has reached the allowed limit. Skipping checkPermissionToAddBranch");
				return;
			} else {
				clickAddOrUpdateBtn();
				Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Action popup appears");
			}
		}
		logger.info("Finished checkPermissionToAddBranch");
	}
	
	void checkPermissionToPurchaseBranch(AllPermissions staffPermission) {
		navigateToManagementScreenByURL(); 
		
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Staff does not have Branch permission. Skipping checkPermissionToPurchaseBranch");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
			return;
		}
		
		clickAddBranch();
		
		ConfirmationDialog confirmationDlg = new ConfirmationDialog(driver);
		
		if (confirmationDlg.isConfirmationDialogDisplayed()) {
			confirmationDlg.clickOKBtn();
			
			if (staffPermission.getSetting().getBranchManagement().isPurchaseBranch()) {
				PlansPage planPage = new PlansPage(driver);
				planPage.selectPaymentMethod(PaymentMethod.BANKTRANSFER);
				planPage.completePayment(PaymentMethod.BANKTRANSFER);
				Assert.assertTrue(!planPage.getOrderId().isEmpty(), "OrderId is not empty");
				//We won't actually purchase branches as there's no way to delete branches
			} else {
				Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Action popup appears");
			}
		} else {
			logger.info("The number of branches has not reached the allowed limit. Skipping checkPermissionToPurchaseBranch");
			return;
		}
		logger.info("Finished checkPermissionToPurchaseBranch");
	}
	
	void checkPermissionToRenewExpiredBranch(AllPermissions staffPermission) {
		navigateToManagementScreenByURL(); 
		
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Staff does not have Branch permission. Skipping checkPermissionToRenewExpiredBranch");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
			return;
		}
		
		if (!(commonAction.getElements(loc_blkExpiredBranch).size()>0)) {
			logger.info("There are no expired branches. Skipping checkPermissionToRenewExpiredBranch");
			return;
		}
		
		clickRenewExpiredBranchBtn();
		PlansPage planPage = new PlansPage(driver);
		planPage.selectPaymentMethod(PaymentMethod.BANKTRANSFER);
		
		if (staffPermission.getSetting().getBranchManagement().isRenewBranch()) {
			Assert.assertTrue(!planPage.getOrderId().isEmpty(), "OrderId is not empty");
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Action popup appears");
		}
		logger.info("Finished checkPermissionToRenewExpiredBranch");
	}
	
	void checkPermissionToUpgradeBranch(AllPermissions staffPermission) {
		navigateToManagementScreenByURL(); 
		
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Staff does not have Branch permission. Skipping checkPermissionToUpgradeBranch");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
			return;
		}
		
		if (commonAction.getElements(loc_blkExpiredBranch).size()>0) {
			logger.info("There are expired branches. Skipping checkPermissionToUpgradeBranch");
			return;
		}
		
		clickUpgradeBranchBtn();
		PlansPage planPage = new PlansPage(driver);
		planPage.selectPaymentMethod(PaymentMethod.BANKTRANSFER);
		
		if (staffPermission.getSetting().getBranchManagement().isUpgradeBranch()) {
			Assert.assertTrue(!planPage.getOrderId().isEmpty(), "OrderId is not empty");
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Action popup appears");
		}
		logger.info("Finished checkPermissionToUpgradeBranch");
	}
	
	void checkPermissionToActivateBranch(AllPermissions staffPermission) {
		navigateToManagementScreenByURL(); 
		
		if (isPermissionProhibited(staffPermission)) {
			logger.info("Staff does not have Branch permission. Skipping checkPermissionToActivateBranch");
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Page appears");
			return;
		}
		
		if (commonAction.getElements(loc_blkExpiredBranch).size()>0) {
			logger.info("There are expired branches. Skipping checkPermissionToActivateBranch");
			return;
		}
		
		clickActivateBranchToggleBtn();
		
		if (staffPermission.getSetting().getBranchManagement().isActiveDeactivateBranch()) {
			ConfirmationDialog confirmationDlg = new ConfirmationDialog(driver);
			confirmationDlg.clickOKBtn();
			homePage.getToastMessage();
			clickActivateBranchToggleBtn();
			confirmationDlg.clickOKBtn();
			homePage.getToastMessage();
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent(), "Restricted Action popup appears");
		}
		logger.info("Finished checkPermissionToActivateBranch");
	}
    
	public void checkBranchPermission(AllPermissions staffPermission) {
		checkPermissionToViewBranchDetail(staffPermission);
		checkPermissionToUpdateBranchDetail(staffPermission);
		checkPermissionToAddBranch(staffPermission);
		checkPermissionToPurchaseBranch(staffPermission);
		checkPermissionToRenewExpiredBranch(staffPermission);
		checkPermissionToUpgradeBranch(staffPermission);
		checkPermissionToActivateBranch(staffPermission);
	}	
}