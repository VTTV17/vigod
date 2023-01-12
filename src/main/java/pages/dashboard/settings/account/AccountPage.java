package pages.dashboard.settings.account;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class AccountPage {
	
	final static Logger logger = LogManager.getLogger(AccountPage.class);
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    
    SoftAssert soft = new SoftAssert();
    
    public AccountPage (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "li:nth-child(1) > a.nav-link")
    WebElement ACCOUNT_TAB;
    
    @FindBy(css = ".see-plan button")
    WebElement SEEPLAN_BTN;
    
    @FindBy(css = ".current__plan_information .gs-button__green")
    WebElement RENEW_BTN;
    
    @FindBy (id = "firstName")
    WebElement FIRST_NAME;
    
    @FindBy (id = "lastName")
    WebElement LAST_NAME;
    
    @FindBy (id = "email")
    WebElement EMAIL;
    
    @FindBy (id = "phoneNumber")
    WebElement PHONE;
    
    @FindBy (css = ".account__information .setting_btn_save")
    WebElement ACCOUNT_INFO_SAVE_BTN;    
    
    @FindBy (id = "currentPassword")
    WebElement CURRENT_PASSWORD;
    
    @FindBy (id = "newPassword")
    WebElement NEW_PASSWORD;
    
    @FindBy (id = "confirmPassword")
    WebElement CONFIRM_PASSWORD;

    
    @FindBy (css = ".reset-password__information .setting_btn_save")
    WebElement RESET_PASSWORD_SAVE_BTN;

    @FindBy (css = "div.alert__wrapper")
    WebElement INVALID_USER_ERROR;	
    

    @FindBy (css = "input[name='key']")
    WebElement VERIFICATION_CODE;    
    
    public AccountPage navigate() {
    	clickAccountTab();
        return this;
    }

    public AccountPage clickAccountTab() {
    	commonAction.clickElement(ACCOUNT_TAB);
    	logger.info("Clicked on Account tab.");
        return this;
    }
    
    public AccountPage clickRenew() {
    	commonAction.clickElement(SEEPLAN_BTN);
    	logger.info("Clicked on 'See Plans' button.");
    	new HomePage(driver).waitTillSpinnerDisappear();
    	return this;
    }
    
    public AccountPage clickSeePlans() {
    	commonAction.clickElement(SEEPLAN_BTN);
    	logger.info("Clicked on 'See Plans' button.");
    	new HomePage(driver).waitTillSpinnerDisappear();
    	return this;
    }

    /* ***************** Account Info ***************** */
    public AccountPage inputFirstName(String firstName) {
    	commonAction.inputText(FIRST_NAME, firstName);
    	logger.info("Input '" + firstName + "' into First Name field.");
        return this;
    }
    
    public AccountPage inputLastName(String lastName) {
    	commonAction.inputText(LAST_NAME, lastName);
    	logger.info("Input '" + lastName + "' into Last Name field.");
    	return this;
    }
    
    public AccountPage inputEmail(String email) {
    	if (commonAction.isElementVisiblyDisabled(EMAIL)) {
    		Assert.assertFalse(new HomePage(driver).isMenuClicked(EMAIL));
    		return this;
    	}
    	commonAction.inputText(EMAIL, email);
    	logger.info("Input '" + email + "' into Email field.");
    	return this;
    }
    
    public AccountPage inputPhone(String phone) {
    	commonAction.inputText(PHONE, phone);
    	logger.info("Input '" + phone + "' into Phone field.");
    	return this;
    }

    public AccountPage clickAccountInfoSaveBtn() {
    	commonAction.clickElement(ACCOUNT_INFO_SAVE_BTN);
    	logger.info("Clicked on Account Info Save button.");
        return this;
    }       
    
    /* ***************** Reset Password ***************** */
    public AccountPage inputCurrentPassword(String password) {
    	commonAction.inputText(CURRENT_PASSWORD, password);
    	logger.info("Input '" + password + "' into Current Password field.");
        return this;
    }
    
    public AccountPage inputNewPassword(String password) {
    	commonAction.inputText(NEW_PASSWORD, password);
    	logger.info("Input '" + password + "' into New Password field.");
    	return this;
    }
    
    public AccountPage inputConfirmPassword(String password) {
    	commonAction.inputText(CONFIRM_PASSWORD, password);
    	logger.info("Input '" + password + "' into Confirm Password field.");
    	return this;
    }

    public AccountPage clickResetPasswordSaveBtn() {
    	commonAction.clickElement(RESET_PASSWORD_SAVE_BTN);
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
    /* ***************** */    
    
    public void completeVerify() {
        soft.assertAll();
    }    
    
    /*Verify permission for certain feature*/
    public void verifyPermissionToUseAccountTab(String permission) {
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
