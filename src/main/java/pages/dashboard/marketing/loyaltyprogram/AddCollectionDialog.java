package pages.dashboard.marketing.loyaltyprogram;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import utilities.UICommonAction;

import java.util.List;

public class AddCollectionDialog {

	final static Logger logger = LogManager.getLogger(AddCollectionDialog.class);

	WebDriver driver;
	UICommonAction commonAction;

	public AddCollectionDialog(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".search-input.uik-input__input")
	WebElement SEARCH_BOX;

	@FindBy(id = "name")
	WebElement TIER_NAME;

	@FindBy(css = "segment-add")
	WebElement ADD_SEGMENT_LNKTXT;

	@FindBy(css = ".custom-control-label")
	List<WebElement> CUSTOMER_SEGMENTS;
	
	@FindBy(css = ".modal-body .gs-button__green")
	WebElement OK_BTN;
	
	@FindBy(css = ".modal-body .gs-button__gray--outline")
	WebElement CANCEL_BTN;

	public AddCollectionDialog inputSearchTerm(String customerSegment) {
		commonAction.inputText(SEARCH_BOX, customerSegment);
		logger.info("Input '" + customerSegment + "' into Search box.");
		return this;
	}

	public AddCollectionDialog selectCustomerSegment(String customerSegment) {
		String retrievedText;
		boolean clicked = false;
		for (WebElement el : CUSTOMER_SEGMENTS) {
			retrievedText = commonAction.getText(el).trim();
			if (retrievedText.contentEquals(customerSegment)) {
				clicked = true;
				commonAction.clickElement(el);
				logger.info("Selected Customer Segment: " + customerSegment);
				break;
			}
		}
		Assert.assertTrue(clicked, "Customer Segment '%s' ".formatted(customerSegment) + "is not found");
		return this;
	}

	public AddCollectionDialog clickOKBtn() {
		commonAction.clickElement(OK_BTN);
		logger.info("Clicked on 'OK' button");
		return this;
	}
	
	public AddCollectionDialog clickCancelBtn() {
		commonAction.clickElement(CANCEL_BTN);
		logger.info("Clicked on 'Cancel' button");
		return this;
	}

}
