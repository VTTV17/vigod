package pages.dashboard;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class ConfirmationDialog {

	final static Logger logger = LogManager.getLogger(ConfirmationDialog.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public ConfirmationDialog(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".modal-dialog.confirm-modal")
	List<WebElement> CONFIRMATION_DIALOG;

	@FindBy(css = ".modal-footer button.gs-button__gray--outline")
	WebElement CANCEL_BTN;

	@FindBy(css = ".modal-footer button.gs-button__green")
	WebElement OK_BTN;

	public ConfirmationDialog clickCancelBtn() {
		commonAction.clickElement(CANCEL_BTN);
		logger.info("Clicked on 'Cancel' button on Comfirmation Dialog.");
		return this;
	}

	public ConfirmationDialog clickOKBtn() {
		commonAction.clickElement(OK_BTN);
		logger.info("Clicked on 'OK' button on Comfirmation Dialog");
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}

	public boolean isConfirmationDialogDisplayed() {
		commonAction.sleepInMiliSecond(500);
		return !commonAction.isElementNotDisplay(CONFIRMATION_DIALOG);
	}

}
