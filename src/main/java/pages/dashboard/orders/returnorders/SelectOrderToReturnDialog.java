package pages.dashboard.orders.returnorders;

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

import utilities.UICommonAction;

public class SelectOrderToReturnDialog {

	final static Logger logger = LogManager.getLogger(SelectOrderToReturnDialog.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public SelectOrderToReturnDialog(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".create-return-order-modal")
	List<WebElement> SELECT_ORDER_TO_RETURN_DIALOG;
	
	@FindBy(css = "button.close")
	WebElement CLOSE_DIALOG;
	
	public boolean isDialogDisplayed() {
		commonAction.sleepInMiliSecond(1000);
		return !commonAction.isElementNotDisplay(SELECT_ORDER_TO_RETURN_DIALOG);
	}
	
	public void closeDialog() {
		commonAction.clickElement(CLOSE_DIALOG);
		logger.info("Closed Dialog.");
	}	
	
}
