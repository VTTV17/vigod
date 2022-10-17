package pages.storefront.header;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import pages.storefront.GeneralSF;
import pages.storefront.login.LoginPage;
import utilities.UICommonAction;

public class ChangePasswordDialog {

	final static Logger logger = LogManager.getLogger(ChangePasswordDialog.class);

	WebDriver driver;
	UICommonAction commonAction;

	public ChangePasswordDialog(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(id = "change-pwd-password")
	WebElement CURRENT_PASSWORD;

	@FindBy(id = "change-pwd-repassword")
	WebElement NEW_PASSWORD;

	@FindBy(css = "#frm-change-pwd .btn-submit")
	WebElement DONE_BTN;

	@FindBy(css = "#change-pwd-modal .close")
	WebElement CLOSE_BTN;

	@FindBy (id = "change-pwd-fail")
	WebElement CURRENT_PASSWORD_ERROR;
	
    @FindBy (id = "change-pwd-repassword-error")
    WebElement NEW_PASSWORD_ERROR;
	
	public ChangePasswordDialog inputCurrentPassword(String currentPassword) {
		commonAction.inputText(CURRENT_PASSWORD, currentPassword);
		logger.info("Input '" + currentPassword + "' into Current Password field.");
		return this;
	}

	public ChangePasswordDialog inputNewPassword(String newPassword) {
		commonAction.inputText(NEW_PASSWORD, newPassword);
		logger.info("Input '" + newPassword + "' into New Password field.");
		return this;
	}

	public ChangePasswordDialog clickDoneBtn() {
		commonAction.clickElement(DONE_BTN);
		logger.info("Clicked on 'Done' button");
		new GeneralSF(driver).waitTillLoaderDisappear();
		return this;
	}

	public ChangePasswordDialog clickCloseBtn() {
		commonAction.clickElement(CLOSE_BTN);
		logger.info("Clicked on 'Close' button");
		return this;
	}

	
	/**
	 * <p>
	 * Get error message for Current Password field when users input invalid data for the field.
	 * <p>
	 * For example, when users leave the field empty, an error will appear
	 * <p>
	 * @return an error saying what is wrong
	 */	
	public String getErrorForCurrentPasswordField() {
		String error = commonAction.getText(CURRENT_PASSWORD_ERROR);
		logger.info("Finished getting error message for 'Current Password' field");
		return error;
	}

	/**
	 * <p>
	 * Get error message for New Password field when users input invalid data for the field.
	 * <p>
	 * For example, when user's input to the field does not contain special characters, an error will appear
	 * <p>
	 * @return an error saying what is wrong
	 */
	public String getErrorForNewPasswordField() {
		String error = commonAction.getText(NEW_PASSWORD_ERROR);
		logger.info("Finished getting error message for 'New Password' field");
		return error;
	}
	
}
