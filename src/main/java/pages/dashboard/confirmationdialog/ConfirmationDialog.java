package pages.dashboard.confirmationdialog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class ConfirmationDialog {

	final static Logger logger = LogManager.getLogger(ConfirmationDialog.class);

	WebDriver driver;
	UICommonAction commonAction;

	public ConfirmationDialog(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_dlgDialog = By.cssSelector(".modal-dialog.confirm-modal");
	By loc_btnCancel = By.xpath("//*[contains(@class,'modal-footer')]/button[contains(@class,'gs-button__gray') or contains(@class,'gs-button__white')]");
	By loc_btnOK = By.cssSelector(".modal-footer button.gs-button__green");
	
	public void clickGrayBtn() {
		commonAction.click(loc_btnCancel);
	}
	
	public void clickGreenBtn() {
		commonAction.click(loc_btnOK);
	}

	public String getGrayBtnText() {
		return commonAction.getText(loc_btnCancel);
	}	
	
	public String getGreenBtnText() {
		return commonAction.getText(loc_btnOK);
	}	
	
	public ConfirmationDialog clickCancelBtn() {
		clickGrayBtn();
		logger.info("Clicked on 'Cancel' button on Comfirmation Dialog.");
		return this;
	}

	public ConfirmationDialog clickOKBtn() {
		clickGreenBtn();
		logger.info("Clicked on 'OK' button on Comfirmation Dialog");
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}

	public boolean isConfirmationDialogDisplayed() {
		commonAction.sleepInMiliSecond(1000);
		return commonAction.getElements(loc_dlgDialog).size() > 0;
	}

}
