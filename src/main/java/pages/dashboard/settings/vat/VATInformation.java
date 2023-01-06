package pages.dashboard.settings.vat;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import pages.dashboard.home.HomePage;
import utilities.UICommonAction;

public class VATInformation {

	final static Logger logger = LogManager.getLogger(VATInformation.class);

	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commonAction;

	public VATInformation(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = "li:nth-child(7) > a.nav-link")
	WebElement VAT_TAB;
	
	@FindBy(css = ".VAT .gs-button__green")
	WebElement ADD_TAX_INFORMATION_BTN;

	@FindBy(id = "name")
	WebElement TAX_NAME;
	
	@FindBy(css = ".VATmodal .gs-button__white")
	WebElement CANCEL_BTN;
	

	public VATInformation navigate() {
		commonAction.clickElement(VAT_TAB);
		logger.info("Clicked on VAT tab.");
		return this;
	}

	public VATInformation clickAddTaxInformation() {
		if (commonAction.isElementVisiblyDisabled(ADD_TAX_INFORMATION_BTN.findElement(By.xpath("./parent::*/parent::*")))) {
			Assert.assertFalse(new HomePage(driver).isMenuClicked(ADD_TAX_INFORMATION_BTN));
			return this;
		}
		commonAction.clickElement(ADD_TAX_INFORMATION_BTN);
		logger.info("Clicked on 'Add Tax Information' button.");
		return this;
	}

	public VATInformation inputTaxName(String taxName) {
		commonAction.inputText(TAX_NAME, taxName);
		logger.info("Input '" + taxName + "' into Tax Name field.");
		return this;
	}
	
	public VATInformation clickCancelBtn() {
		commonAction.clickElement(CANCEL_BTN);
		logger.info("Clicked on 'Cancel' button in 'Add tax information' dialog.");
		return this;
	}


}
