package pages.dashboard.products.purchaseorders;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class PurchaseOrders {
	WebDriver driver;
	UICommonAction commons;
	WebDriverWait wait;

	final static Logger logger = LogManager.getLogger(PurchaseOrders.class);

	public PurchaseOrders(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commons = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".purchase-order-list-page .d-desktop-flex .uik-input__input")
	WebElement SEARCH_BOX;

	public PurchaseOrders inputSearchTerm(String searchTerm) {
		commons.inputText(SEARCH_BOX, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}

    /*Verify permission for certain feature*/
    public void verifyPermissionToManagePurchaseOrders(String permission, String url) {
		if (permission.contentEquals("A")) {
			Assert.assertTrue(commons.getCurrentURL().contains(url));
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commons.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }

    /*-------------------------------------*/   	
	
}
