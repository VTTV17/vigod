package pages.dashboard.settings.account;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;
import utilities.UICommonAction;

import java.time.Duration;

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
    
    public void completeVerify() {
        soft.assertAll();
    }    
    
}
