package web.StoreFront.header;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import web.StoreFront.GeneralSF;
import utilities.commons.UICommonAction;

public class ChangePasswordDialog {

	final static Logger logger = LogManager.getLogger(ChangePasswordDialog.class);

	WebDriver driver;
	UICommonAction commonAction;

	public ChangePasswordDialog(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}
	By loc_txtCurrentPassword = By.id("change-pwd-password");
	By loc_txtNewPassword = By.id("change-pwd-repassword");
	By loc_btnDone = By.cssSelector("#frm-change-pwd .btn-submit");
	By loc_btnClose = By.cssSelector("#change-pwd-modal .close");
	By loc_lblCurrentPasswordError = By.id("change-pwd-fail");
	By loc_lblNewPasswordError = By.id("change-pwd-repassword-error");
	
	public ChangePasswordDialog inputCurrentPassword(String currentPassword) {
		commonAction.inputText(loc_txtCurrentPassword, currentPassword);
		logger.info("Input '" + currentPassword + "' into Current Password field.");
		return this;
	}

	public ChangePasswordDialog inputNewPassword(String newPassword) {
		commonAction.inputText(loc_txtNewPassword, newPassword);
		logger.info("Input '" + newPassword + "' into New Password field.");
		return this;
	}

	public ChangePasswordDialog clickDoneBtn() {
		commonAction.click(loc_btnDone);
		logger.info("Clicked on 'Done' button");
		new GeneralSF(driver).waitTillLoaderDisappear();
		return this;
	}

	public ChangePasswordDialog clickCloseBtn() {
		commonAction.click(loc_btnClose);
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
		String error = commonAction.getText(loc_lblCurrentPasswordError);
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
		String error = commonAction.getText(loc_lblNewPasswordError);
		logger.info("Finished getting error message for 'New Password' field");
		return error;
	}
	
}
