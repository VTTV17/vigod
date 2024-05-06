package web.Dashboard.settings.requiredpassworddialog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import web.Dashboard.confirmationdialog.ConfirmationDialog;

public class RequiredPasswordDialog extends ConfirmationDialog {

	final static Logger logger = LogManager.getLogger(RequiredPasswordDialog.class);

	WebDriver driver;
	RequiredPasswordDialogElement elements;

	public RequiredPasswordDialog(WebDriver driver) {
		super(driver);
		elements = new RequiredPasswordDialogElement();
	}

	public RequiredPasswordDialog inputPassword(String password) {
		commonAction.inputText(elements.loc_txtPassword, password);
		logger.info("Input Password: " + password);
		return this;
	}
    
}
