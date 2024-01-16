package pages.dashboard.reservation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class ReservationManagement {

	final static Logger logger = LogManager.getLogger(ReservationManagement.class);

	WebDriver driver;
	UICommonAction commonAction;

	public ReservationManagement(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_txtSearch = By.cssSelector(".reservation-list__filter-container input.uik-input__input");

	public ReservationManagement inputSearchTerm(String searchTerm) {
		commonAction.sendKeys(loc_txtSearch, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		return this;
	}

    /*Verify permission for certain feature*/
    public void verifyPermissionToManageReservation(String permission) {
		if (permission.contentEquals("A")) {
			inputSearchTerm("Test Permission");
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/    	
	
}
