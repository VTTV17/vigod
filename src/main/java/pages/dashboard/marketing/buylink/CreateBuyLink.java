package pages.dashboard.marketing.buylink;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import utilities.UICommonAction;

public class CreateBuyLink {

	final static Logger logger = LogManager.getLogger(CreateBuyLink.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	SoftAssert soft = new SoftAssert();

	public CreateBuyLink(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

//	@FindBy(id = "domain-value-sub")
//	WebElement SUB_DOMAIN;
//
//
//	@FindBy(css = ".group-btn .btn-save")
//	WebElement SAVE_BTN;
//
//	@FindBy(xpath = "(//section[@class='landing-page-editor__pane--middle']/section//button[contains(@class,'gs-button__gray--outline')])[2]")
//	WebElement CANCEL_BTN;
//
//	@FindBy(css = "[data-sherpherd=\"tour-guide-alert-button-close\"]")
//	WebElement CLOSE_BTN;
	
	By PRODUCT_SELECTION_MODAL = By.cssSelector(".buy-link-created-modal");

    public boolean isProductSelectionDialogDisplayed() {
    	commonAction.sleepInMiliSecond(1000);
    	return !commonAction.isElementNotDisplay(driver.findElements(PRODUCT_SELECTION_MODAL));
    }   	
	
//	public CreateBuyLink inputSubDomain(String domain) {
//		commonAction.inputText(SUB_DOMAIN, domain);
//		logger.info("Input '" + domain + "' into Sub-domain field.");
//		return this;
//	}
//
//	public CreateBuyLink clickSaveBtn() {
//		commonAction.clickElement(SAVE_BTN);
//		logger.info("Clicked on 'Save' button");
//		return this;
//	}

//	public CreateBuyLink clickCancelBtn() {
//		commonAction.clickElement(CANCEL_BTN);
//		logger.info("Clicked on 'Cancel' button");
//		return this;
//	}
//
//	public CreateBuyLink clickCloseBtn() {
//		commonAction.clickElement(CLOSE_BTN);
//		logger.info("Clicked on 'Close' button");
//		return this;
//	}

}
