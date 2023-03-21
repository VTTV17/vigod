package pages.dashboard;

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

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class Pagination {

	final static Logger logger = LogManager.getLogger(Pagination.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public Pagination(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".pagination [aria-label='Next']")
	List<WebElement> NEXT_BTN;

	@FindBy(css = ".pagination [aria-label='Previous']")
	WebElement PREVIOUS_BTN;

	public Pagination clickNextBtn() {
		commonAction.clickElement(NEXT_BTN.get(0));
		logger.info("Clicked on 'Next' button at pagination.");
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}

	public Pagination clickPreviousBtn() {
		commonAction.clickElement(PREVIOUS_BTN);
		logger.info("Clicked on 'Previous' button at pagination");
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}

	public boolean isNextBtnDisplayed() {
		commonAction.sleepInMiliSecond(500);
		return !commonAction.isElementNotDisplay(NEXT_BTN);
	}

}
