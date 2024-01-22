package pages.gomua.headergomua;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.UICommonAction;

public class ChangePasswordDialog {

	final static Logger logger = LogManager.getLogger(ChangePasswordDialog.class);

	WebDriver driver;
	UICommonAction commonAction;

	public ChangePasswordDialog(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_txtCurrentPassword = By.id("pwd");
	By loc_btnNewPassword = By.id("new-pwd");
	By loc_btnDone = By.cssSelector("[beetranslate='beecow.getemail.button.done']");
	By loc_btnClose = By.cssSelector("#change-pass .fa-times");

	public ChangePasswordDialog inputCurrentPassword(String currentPassword) {
		commonAction.inputText(loc_txtCurrentPassword, currentPassword);
		logger.info("Input '" + currentPassword + "' into Current Password field.");
		return this;
	}

	public ChangePasswordDialog inputNewPassword(String newPassword) {
		commonAction.inputText(loc_btnNewPassword, newPassword);
		logger.info("Input '" + newPassword + "' into New Password field.");
		return this;
	}

	public ChangePasswordDialog clickDoneBtn() {
		commonAction.click(loc_btnDone);
		logger.info("Clicked on 'Done' button");
//		new LoginPage(driver).waitTillLoaderDisappear();
		return this;
	}

	public ChangePasswordDialog clickCloseBtn() {
		commonAction.click(loc_btnClose);
		logger.info("Clicked on 'Close' button");
		return this;
	}
}
