package pages.dashboard.orders.createquotation;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class CreateQuotation {

	final static Logger logger = LogManager.getLogger(CreateQuotation.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public CreateQuotation(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = "#dropdownSuggestionProduct input.uik-input__input")
	WebElement SEARCH_BOX;

	public CreateQuotation inputSearchTerm(String searchTerm) {
		commonAction.inputText(SEARCH_BOX, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search box.");
		return this;
	}

    /*Verify permission for certain feature*/
    public void verifyPermissionToCreateQuotation(String permission, String url) {
		if (permission.contentEquals("A")) {
			inputSearchTerm("Test Permission");
		} else if (permission.contentEquals("D")) {
			Assert.assertFalse(commonAction.getCurrentURL().contains(url));
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }

    /*-------------------------------------*/   	
	
}
