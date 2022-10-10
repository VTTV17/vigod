package pages.dashboard.customers.segments;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

import java.time.Duration;

public class Segments {

	final static Logger logger = LogManager.getLogger(Segments.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public Segments(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".segment-list .btn-create")
	WebElement CREATE_SEGMENT_BTN;

	@FindBy(css = ".gs-search-box__wrapper .uik-input__input")
	WebElement SEARCH_BOX;

	@FindBy(css = ".confirm-modal .gs-button__green")
	WebElement DELETE_OK_BTN;

	@FindBy(css = ".confirm-modal .gs-button__gray--outline")
	WebElement DELETE_CANCEL_BTN;

	public Segments navigate() {
		new HomePage(driver).navigateToPage("Customers", "Segments");
		return this;
	}	
	
	public CreateSegment clickCreateSegmentBtn() {
		commonAction.clickElement(CREATE_SEGMENT_BTN);
		logger.info("Clicked on 'Create Segment' button.");
		return new CreateSegment(driver);
	}

	public Segments inputSearchTerm(String customerSegment) {
		commonAction.inputText(SEARCH_BOX, customerSegment);
		logger.info("Input '" + customerSegment + "' into Search box.");
		return this;
	}

	public Segments deleteSegment(String customerSegment) {
		String segmentXpath = "//div[contains(@class,'segment-list__widget-body')]//tbody/tr[1]/td[position()=2 and text()='%s']"
				.formatted(customerSegment);
		String deleteBtnXpath = segmentXpath.concat("/following-sibling::*//i[contains(@style,'icon-delete')]");
		commonAction.clickElement(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(deleteBtnXpath))));
		logger.info("Click on 'Delete' icon to delete customer segment '%s'.".formatted(customerSegment));
		return this;
	}

	public Segments clickOKBtn() {
		commonAction.clickElement(DELETE_OK_BTN);
		logger.info("Clicked on 'OK' button to confirm customer segment deletion.");
		return this;
	}

	public Segments clickCancelBtn() {
		commonAction.clickElement(DELETE_CANCEL_BTN);
		logger.info("Clicked on 'Cancel' button to abort customer segment deletion.");
		return this;
	}

}
