package web.Dashboard.supplier.purchaseorders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class PurchaseOrders {
	WebDriver driver;
	UICommonAction commons;

	final static Logger logger = LogManager.getLogger(PurchaseOrders.class);

	public PurchaseOrders(WebDriver driver) {
		this.driver = driver;
		commons = new UICommonAction(driver);
	}

	By loc_txtSearch = By.cssSelector(".purchase-order-list-page .d-desktop-flex .uik-input__input");

	public PurchaseOrders inputSearchTerm(String searchTerm) {
		commons.sendKeys(loc_txtSearch, searchTerm);
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
