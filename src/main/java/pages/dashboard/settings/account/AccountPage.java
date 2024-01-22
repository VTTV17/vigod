package pages.dashboard.settings.account;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class AccountPage {
	
	final static Logger logger = LogManager.getLogger(AccountPage.class);
	
    WebDriver driver;
    UICommonAction commonAction;
    
    public AccountPage (WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_tabAccount = By.cssSelector("li:nth-child(1) > a.nav-link");
    By loc_btnSeePlan = By.cssSelector(".see-plan button");
    By loc_btnRenew = By.cssSelector(".current__plan_information .gs-button__green");
    By loc_blkPlanInfo = By.cssSelector(".current__plan_information .setting__account");
    By loc_txtFirstName = By.id("firstName");
    By loc_txtLastName = By.id("lastName");
    By loc_txtEmail = By.id("email");
    By loc_txtPhone = By.id("phoneNumber");
    By loc_btnSaveAccountInfo = By.cssSelector(".account__information .setting_btn_save");
    By loc_txtCurrentPassword = By.id("currentPassword");
    By loc_txtNewPassword = By.id("newPassword");
    By loc_txtConfirmPassword = By.id("confirmPassword");
    By loc_btnSaveResetPassword = By.cssSelector(".reset-password__information .setting_btn_save");
    
    public AccountPage navigate() {
    	clickAccountTab();
    	new HomePage(driver).waitTillSpinnerDisappear1();
    	commonAction.sleepInMiliSecond(500);
        return this;
    }

    public AccountPage clickAccountTab() {
    	commonAction.click(loc_tabAccount);
    	logger.info("Clicked on Account tab.");
        return this;
    }
    
    public AccountPage clickRenew() {
    	commonAction.click(loc_btnSeePlan);
    	logger.info("Clicked on 'See Plans' button.");
    	new HomePage(driver).waitTillSpinnerDisappear();
    	return this;
    }

    public List<List<String>> getPlanInfo() {
    	List<List<String>> table = new ArrayList<>();
    	for (WebElement row: commonAction.getListElement(loc_blkPlanInfo)) {
    		List<String> rowOfData = new ArrayList<>();
    		for (WebElement el:row.findElements(By.xpath(".//*[contains(@class,'account__line2')]"))) {
    			rowOfData.add(el.getText());
    		}
    		table.add(rowOfData);
    	}
    	logger.info("Retrieved current plan info.");
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
    	commonAction.click(loc_btnSeePlan);
    	logger.info("Clicked on 'See Plans' button.");
    	new HomePage(driver).waitTillSpinnerDisappear();
    	return this;
    }

    /* ***************** Account Info ***************** */
    public AccountPage inputFirstName(String firstName) {
    	commonAction.sendKeys(loc_txtFirstName, firstName);
    	logger.info("Input '" + firstName + "' into First Name field.");
        return this;
    }
    
    public AccountPage inputLastName(String lastName) {
    	commonAction.sendKeys(loc_txtLastName, lastName);
    	logger.info("Input '" + lastName + "' into Last Name field.");
    	return this;
    }
    
    public AccountPage inputEmail(String email) {
    	if (commonAction.isElementVisiblyDisabled(commonAction.getElement(loc_txtEmail))) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(commonAction.getElement(loc_txtEmail)));
    		return this;
    	}
    	commonAction.sendKeys(loc_txtEmail, email);
    	logger.info("Input '" + email + "' into Email field.");
    	return this;
    }
    
    public AccountPage inputPhone(String phone) {
    	commonAction.sendKeys(loc_txtPhone, phone);
    	logger.info("Input '" + phone + "' into Phone field.");
    	return this;
    }

    public AccountPage clickAccountInfoSaveBtn() {
    	commonAction.click(loc_btnSaveAccountInfo);
    	logger.info("Clicked on Account Info Save button.");
        return this;
    }       
    
    /* ***************** Reset Password ***************** */
    public AccountPage inputCurrentPassword(String password) {
    	commonAction.sendKeys(loc_txtCurrentPassword, password);
    	logger.info("Input '" + password + "' into Current Password field.");
        return this;
    }
    
    public AccountPage inputNewPassword(String password) {
    	commonAction.sendKeys(loc_txtNewPassword, password);
    	logger.info("Input '" + password + "' into New Password field.");
    	return this;
    }
    
    public AccountPage inputConfirmPassword(String password) {
    	commonAction.sendKeys(loc_txtConfirmPassword, password);
    	logger.info("Input '" + password + "' into Confirm Password field.");
    	return this;
    }

    public AccountPage clickResetPasswordSaveBtn() {
    	commonAction.click(loc_btnSaveResetPassword);
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
			new HomePage(driver).getToastMessage();
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/        
    
}
