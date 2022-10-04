package pages.dashboard.customers.segments;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import utilities.UICommonAction;

import java.util.List;

public class SelectProductDialog {

	final static Logger logger = LogManager.getLogger(SelectProductDialog.class);

	WebDriver driver;
	UICommonAction commonAction;

	public SelectProductDialog(WebDriver driver) {
		this.driver = driver;
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".search-input.uik-input__input")
	WebElement SEARCH_BOX;

	@FindBy(css = ".product-name .name")
	WebElement PRODUCT_NAME;
	
	@FindBy(css = ".product-name .model")
	WebElement PRODUCT_MODEL;
	
	@FindBy(css = ".product-unit")
	WebElement PRODUCT_UNIT;

	@FindBy(css = "segment-add")
	WebElement ADD_SEGMENT_LNKTXT;

	@FindBy(css = ".custom-control-label")
	List<WebElement> CUSTOMER_SEGMENTS;
	
	@FindBy(css = ".modal-body .gs-button__green")
	WebElement OK_BTN;
	
	@FindBy(css = ".modal-body .gs-button__gray--outline")
	WebElement CANCEL_BTN;

	public SelectProductDialog inputSearchTerm(String customerSegment) {
		commonAction.inputText(SEARCH_BOX, customerSegment);
//		commonAction.sleepInMiliSecond(1000);
		logger.info("Input '" + customerSegment + "' into Search box.");
		return this;
	}

//	public SelectProductDialog selectProduct(String product, String model, String unit) {
//		String xpath;
//		
//		inputSearchTerm(product);
//		
//		
//		
//		if (model.length()>1) {
//			xpath = "./div[@class='name' and text()='%s']".formatted(product);
//		}
//		
//		if (PRODUCT_NAME.findElement(By.xpath("./div[@class='name' and text()='%s']".formatted(product))) != null) {
//			
//		}
//		
//
//		return this;
//	}

	public SelectProductDialog clickOKBtn() {
		commonAction.clickElement(OK_BTN);
		logger.info("Clicked on 'OK' button");
		return this;
	}
	
	public SelectProductDialog clickCancelBtn() {
		commonAction.clickElement(CANCEL_BTN);
		logger.info("Clicked on 'Cancel' button");
		return this;
	}

}
