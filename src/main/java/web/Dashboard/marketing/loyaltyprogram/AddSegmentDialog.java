package web.Dashboard.marketing.loyaltyprogram;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import web.Dashboard.home.HomePage;
import utilities.commons.UICommonAction;

public class AddSegmentDialog {

	final static Logger logger = LogManager.getLogger(AddSegmentDialog.class);

	WebDriver driver;
	UICommonAction commonAction;

	public AddSegmentDialog(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
	}

	By loc_txtSearchSegment = By.cssSelector(".search-input.uik-input__input");
	By loc_lblSegments = By.cssSelector(".custom-control-label");
	By loc_btnOK = By.cssSelector(".modal-body .gs-button__green");
	By loc_btnCancel = By.cssSelector(".modal-body .gs-button__gray--outline");

	public AddSegmentDialog inputSearchTerm(String customerSegment) {
		commonAction.sendKeys(loc_txtSearchSegment, customerSegment);
		logger.info("Input '" + customerSegment + "' into Search box.");
		new HomePage(driver).waitTillSpinnerDisappear1();
		return this;
	}

	public AddSegmentDialog selectCustomerSegment(String customerSegment) {
		String retrievedText;
		boolean clicked = false;
		for (WebElement el : commonAction.getListElement(loc_lblSegments)) {
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

	public AddSegmentDialog clickOKBtn() {
		commonAction.click(loc_btnOK);
		logger.info("Clicked on 'OK' button");
		return this;
	}
	
	public AddSegmentDialog clickCancelBtn() {
		commonAction.click(loc_btnCancel);
		logger.info("Clicked on 'Cancel' button");
		return this;
	}

}
