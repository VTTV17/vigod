package web.Dashboard.settings.account;

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

public class AccountPage {

	final static Logger logger = LogManager.getLogger(AccountPage.class);

	WebDriver driver;
	UICommonAction commonAction;
	HomePage homePage;
	AccountPageElement elements;

	public AccountPage (WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		homePage = new HomePage(driver);
		elements = new AccountPageElement();
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
	
	public AccountPage navigate() {
		clickAccountTab();
		homePage.waitTillSpinnerDisappear1();
		commonAction.sleepInMiliSecond(500);
		return this;
	}

	AccountPage navigateByURL(String url) {
		driver.get(url);
		logger.info("Navigated to: " + url);
		commonAction.removeFbBubble();
		homePage.waitTillSpinnerDisappear1();
		return this;
	}		

	public AccountPage navigateToAccountTabByURL() {
		navigateByURL(DOMAIN + "/setting?tabId=1");
		return this;
	}		
	
	public AccountPage clickAccountTab() {
		commonAction.click(elements.loc_tabAccount);
		logger.info("Clicked on Account tab.");
		return this;
	}

	public AccountPage clickRenew() {
		commonAction.click(elements.loc_btnSeePlan);
		logger.info("Clicked on 'See Plans' button.");
		homePage.waitTillSpinnerDisappear();
		return this;
	}

	public List<List<String>> getPlanInfo() {
		List<List<String>> table = new ArrayList<>();
        for (int i = 0; i < commonAction.getElements(By.xpath(elements.loc_tmpRecords)).size(); i++) {
            List<String> rowData = new ArrayList<>();
            rowData.add(commonAction.getText(elements.loc_lblSubscriptionStartDate, i));
            rowData.add(commonAction.getText(elements.loc_lblPlanExpiryDate, i));
            rowData.add(commonAction.getText(elements.loc_lblPlanName, i));
            rowData.add(commonAction.getText(elements.loc_lblPlanStatus,i));
            table.add(rowData);
        }
		logger.info("Retrieved plan info: {}", table);
		return table;
	}    
	
	public List<String> getPlanInfo(String plan) {
		List<List<String>> table = getPlanInfo();
		for (List<String> row: table) {
			if (row.contains(plan)) return row;
		}
		return null;
	}    

	public AccountPage clickSeePlans() {
		commonAction.click(elements.loc_btnSeePlan);
		logger.info("Clicked on 'See Plans' button.");
		homePage.waitTillSpinnerDisappear();
		return this;
	}

	/* ***************** Account Info ***************** */
	public AccountPage inputFirstName(String firstName) {
		commonAction.sendKeys(elements.loc_txtFirstName, firstName);
		logger.info("Input '" + firstName + "' into First Name field.");
		return this;
	}

	public AccountPage inputLastName(String lastName) {
		commonAction.sendKeys(elements.loc_txtLastName, lastName);
		logger.info("Input '" + lastName + "' into Last Name field.");
		return this;
	}

	public AccountPage inputEmail(String email) {
		if (commonAction.isElementVisiblyDisabled(elements.loc_txtEmail)) {
			Assert.assertFalse(homePage.isElementClicked(elements.loc_txtEmail));
			return this;
		}
		commonAction.sendKeys(elements.loc_txtEmail, email);
		logger.info("Input '" + email + "' into Email field.");
		return this;
	}

	public AccountPage inputPhone(String phone) {
		commonAction.sendKeys(elements.loc_txtPhone, phone);
		logger.info("Input '" + phone + "' into Phone field.");
		return this;
	}

	public AccountPage clickAccountInfoSaveBtn() {
		commonAction.click(elements.loc_btnSaveAccountInfo);
		logger.info("Clicked on Account Info Save button.");
		return this;
	}       

	/* ***************** Reset Password ***************** */
	public AccountPage inputCurrentPassword(String password) {
		commonAction.sendKeys(elements.loc_txtCurrentPassword, password);
		logger.info("Input '" + password + "' into Current Password field.");
		return this;
	}

	public AccountPage inputNewPassword(String password) {
		commonAction.sendKeys(elements.loc_txtNewPassword, password);
		logger.info("Input '" + password + "' into New Password field.");
		return this;
	}

	public AccountPage inputConfirmPassword(String password) {
		commonAction.sendKeys(elements.loc_txtConfirmPassword, password);
		logger.info("Input '" + password + "' into Confirm Password field.");
		return this;
	}

	public AccountPage clickResetPasswordSaveBtn() {
		commonAction.click(elements.loc_btnSaveResetPassword);
		logger.info("Clicked on Reset Password Save button.");
		return this;
	}    

	public AccountPage changePassword(String currentPassword, String newPassword, String confirmPassword) {
		inputCurrentPassword(currentPassword);
		inputNewPassword(newPassword);
		inputConfirmPassword(confirmPassword);
		clickResetPasswordSaveBtn();
		return this;
	} 

	/*Verify permission for certain feature*/
	public void verifyPermissionToUseAccountTab(String permission) {
		navigate();
		if (permission.contentEquals("A")) {
			clickAccountInfoSaveBtn();
			homePage.getToastMessage();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(homePage.verifySalePitchPopupDisplay(), 0);
		}
	}
	/*-------------------------------------*/        
	void checkPermissionToViewAccountDetail(AllPermissions staffPermission) {
		navigateToAccountTabByURL(); 

		if (staffPermission.getSetting().getAccount().isViewAccountDetail()) {
			inputFirstName("Test Permission First Name");
			inputCurrentPassword("testpass!1word");
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		logger.info("Finished checkPermissionToViewAccountDetail");
	}
	
	void checkPermissionToUpdateAccountInfo(AllPermissions staffPermission) {
		navigateToAccountTabByURL(); 
		
		if (!staffPermission.getSetting().getAccount().isViewAccountDetail()) {
			logger.info("Permission to update account info is not granted. Skipping this check");
			return;
		}
		
		clickAccountInfoSaveBtn();
		
		if (staffPermission.getSetting().getAccount().isUpdateAccountInformation()) {
			Assert.assertEquals(homePage.getToastMessage(), translateText("settings.account.saveAccountSuccess"));
		} else {
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		logger.info("Finished checkPermissionToUpdateAccountInfo");
	}
	
	void checkPermissionToResetPassword(AllPermissions staffPermission, String currentPassword) {
		if (!staffPermission.getSetting().getAccount().isViewAccountDetail()) {
			logger.info("Permission to reset password is not granted. Skipping this check");
			return;
		}
		
		navigateToAccountTabByURL(); 
		
		String originalPassword = currentPassword;
		
		if (staffPermission.getSetting().getAccount().isResetPassword()) {
			// Change password back to the first password
			for (int i = 0; i < 6; i++) {
				String newPassword = (i != 5) ? originalPassword + System.currentTimeMillis() + "!" : originalPassword;
				changePassword(currentPassword, newPassword, newPassword);
				currentPassword = newPassword;
				Assert.assertEquals(homePage.getToastMessage(), translateText("settings.account.changePasswordSuccess"));
			}			
		} else {
			clickResetPasswordSaveBtn();
			Assert.assertTrue(new CheckPermission(driver).isAccessRestrictedPresent());
		}
		logger.info("Finished checkPermissionToResetPassword");
	}
	
	public void checkAccountSettingPermission(AllPermissions staffPermission, String currentPassword) {
		checkPermissionToViewAccountDetail(staffPermission);
		checkPermissionToUpdateAccountInfo(staffPermission);
		checkPermissionToResetPassword(staffPermission, currentPassword);
		//Purchase/Renew package will be handled in the future
	}	

}
