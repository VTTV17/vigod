package pages.dashboard.orders.returnorders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import utilities.UICommonAction;

public class SelectOrderToReturnDialog {

	final static Logger logger = LogManager.getLogger(SelectOrderToReturnDialog.class);

	WebDriver driver;
	UICommonAction commonAction;

	public SelectOrderToReturnDialog(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_dlgSelectOrderToReturn = By.cssSelector(".create-return-order-modal");
	By loc_btnCloseDialog = By.cssSelector("button.close");

	public boolean isDialogDisplayed() {
		commonAction.sleepInMiliSecond(1000);
		return commonAction.getElements(loc_dlgSelectOrderToReturn).size() >0;
	}
	
	public void closeDialog() {
		commonAction.click(loc_btnCloseDialog);
		logger.info("Closed Dialog.");
	}	
	
}
