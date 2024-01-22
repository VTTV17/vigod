package web.Dashboard.products.inventory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class Inventory {
	WebDriver driver;
	UICommonAction commons;

	final static Logger logger = LogManager.getLogger(Inventory.class);

	public Inventory(WebDriver driver) {
		this.driver = driver;
		commons = new UICommonAction(driver);
	}

	By loc_btnInventoryHistory = By.cssSelector(".inventory-list-page .gs-page-title button");

    public Inventory navigate() {
    	new HomePage(driver).navigateToPage("Products", "Inventory");
        return this;
    }	
	
	public InventoryHistory clickInventoryHistory() {
		commons.click(loc_btnInventoryHistory);
		logger.info("Clicked on 'Inventory History' button.");
		new HomePage(driver).waitTillSpinnerDisappear1();
		return new InventoryHistory(driver);
	}

    /*Verify permission for certain feature*/
    public void verifyPermissionToSeeInventoryHistory(String permission, String url) {
    	clickInventoryHistory();
		if (permission.contentEquals("A")) {
			Assert.assertTrue(commons.getCurrentURL().contains(url));
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }


    /*-------------------------------------*/   	
	
}
