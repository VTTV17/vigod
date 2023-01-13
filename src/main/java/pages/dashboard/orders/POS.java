package pages.dashboard.orders;

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

public class POS {

	final static Logger logger = LogManager.getLogger(POS.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public POS(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = "#dropdownSuggestionProduct input.uik-input__input")
	WebElement SEARCH_BOX;

	public POS inputProductSearchTerm(String searchTerm) {
		commonAction.inputText(SEARCH_BOX, searchTerm);
		logger.info("Input '" + searchTerm + "' into Search Product box.");
//		new HomePage(driver).waitTillSpinnerDisappear();
		return this;
	}

    /*Verify permission for certain feature*/
    public void verifyPermissionToUsePOS(String permission) {
		if (permission.contentEquals("A")) {
			commonAction.switchToWindow(1);
			new POS(driver).inputProductSearchTerm("Test Permission");
			commonAction.closeTab();
			commonAction.switchToWindow(0);
		} else if (permission.contentEquals("D")) {
			Assert.assertTrue(commonAction.getAllWindowHandles().size() == 1);
		} else {
			Assert.assertEquals(new HomePage(driver).verifySalePitchPopupDisplay(), 0);
		}
    }
    /*-------------------------------------*/    	
	
}
