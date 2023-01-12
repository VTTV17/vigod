package pages.dashboard.products.productreviews;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.dashboard.ConfirmationDialog;
import pages.dashboard.customers.allcustomers.AllCustomers;
import pages.dashboard.home.HomePage;
import pages.dashboard.products.productcollection.productcollectionmanagement.ProductCollectionManagement;
import utilities.UICommonAction;

public class ProductReviews {
	WebDriver driver;
	UICommonAction commons;
	WebDriverWait wait;

	final static Logger logger = LogManager.getLogger(ProductReviews.class);

	public ProductReviews(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commons = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".d-desktop-flex .gs-search-box__wrapper input")
	WebElement SEARCH_BOX;

	public ProductReviews inputSearchTerm(String searchTerm) {
		commons.inputText(SEARCH_BOX, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}
	
    /*Verify permission for certain feature*/
    public void verifyPermissionToManageReviews(String permission) {
		if (permission.contentEquals("A")) {
			new ProductReviews(driver).inputSearchTerm("Test Permission");
		} else if (permission.contentEquals("D")) {
			// Not reproducible
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/    	
    
}
