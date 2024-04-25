package web.Dashboard.confirmationdialog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

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
	By loc_btnClose = By.xpath("//*[contains(@class,'modal-footer')]/button[contains(@class,'gs-button__yellow')]");
	public By loc_btnOK = By.cssSelector(".modal-footer button.gs-button__green");
	By loc_cntMessage = By.cssSelector(".modal-body");
	By loc_ctnTitle = By.cssSelector(".modal-title");
	By loc_btnRed = By.cssSelector(".gs-button__red");
	
	//Temporary locator
	By loc_btnCancel_V2 = By.cssSelector(".modal-footer button:nth-child(1)");
	By loc_btnOK_V2 = By.cssSelector(".modal-footer button:nth-child(2)");
	
	public void clickGrayBtn() {
		commonAction.click(loc_btnCancel);
	}
	public void clickYellowBtn() {
		commonAction.click(loc_btnClose);
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
	
	public ConfirmationDialog clickCancelBtn_V2() {
		commonAction.click(loc_btnCancel_V2);
		logger.info("Clicked on 'Cancel' button on Comfirmation Dialog.");
		return this;
	}

	public ConfirmationDialog clickOKBtn() {
		clickGreenBtn();
		logger.info("Clicked on 'OK' button on Comfirmation Dialog");
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}
	
	//Temporary function
	public ConfirmationDialog clickOKBtn_V2() {
		commonAction.click(loc_btnOK_V2);
		logger.info("Clicked on 'OK' button on Comfirmation Dialog");
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}

	public boolean isConfirmationDialogDisplayed() {
		commonAction.sleepInMiliSecond(1000);
		return commonAction.getElements(loc_dlgDialog).size() > 0;
	}
	public String getPopUpContent(){
		String message = commonAction.getText(loc_cntMessage);
		logger.info("PopUp content: "+message);
		return message;
	}
	public String getPopUpTitle(){
		String message = commonAction.getText(loc_ctnTitle);
		logger.info("PopUp title: "+message);
		return message;
	}
	public ConfirmationDialog clickOnRedBtn(){
		commonAction.click(loc_btnRed);
		logger.info("Click on red button.");
		return this;
	}
}
