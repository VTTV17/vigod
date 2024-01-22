package utilities.thirdparty;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.commons.UICommonAction;

public class Lazada {

	final static Logger logger = LogManager.getLogger(Lazada.class);

	WebDriver driver;
	WebDriverWait wait;

	UICommonAction commonAction;

	public Lazada(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		commonAction = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

    @FindBy (id = "country")
    WebElement COUNTRY_DROPDOWN;	
	
	@FindBy(id = "fm-login-id")
	WebElement USERNAME;

	@FindBy(id = "fm-login-password")
	WebElement PASSWORD;

	@FindBy(id = "fm-login-submit")
	WebElement LOGIN_BTN;
	
	@FindBy(css = ".timestone-confirm_authorization")
	WebElement CONFIRM_AUTHORIZATION_BTN;
	
	@FindBy(css = ".active-branch-modal .gs-button__green")
	WebElement OK_BTN;
	
	@FindBy(css = ".shopee-spin-content.orange")
	WebElement LOADING_SPINNER;

    public Lazada selectCountry(String country) {
    	commonAction.clickElement(COUNTRY_DROPDOWN);
    	commonAction.sleepInMiliSecond(500);
		driver.findElement(By.xpath("//li[contains(@class,'next-menu-item') and text()='%s']".formatted(country))).click();
    	logger.info("Selected country with country code '%s'.".formatted(country));
    	return this;
    }	
	
	public Lazada inputUsername(String username) {
		commonAction.inputText(USERNAME, username);
		logger.info("Input '" + username + "' into Username field.");
		return this;
	}

	public Lazada inputPassword(String password) {
		commonAction.inputText(PASSWORD, password);
		logger.info("Input '" + password + "' into Password field.");
		return this;
	}

	public Lazada clickLoginBtn() {
		commonAction.clickElement(LOGIN_BTN);
		logger.info("Clicked on Login button.");
		return this;
	}
	
	public Lazada clickConfirmAuthorization() {
		commonAction.clickElement(CONFIRM_AUTHORIZATION_BTN);
		logger.info("Clicked on 'Confirm Authorization' button.");
		return this;
	}
	
	public Lazada clickOK() {
		commonAction.clickElement(OK_BTN);
		logger.info("Clicked on 'OK' button to select branch for inventory synchronization.");
		return this;
	}

    public Lazada waitTillSpinnerDisappear() {
    	commonAction.waitForElementInvisible(LOADING_SPINNER, 30);
    	logger.info("Spinner has finished loading");
    	return this;
    }		
	
	public Lazada performLogin(String countryCode, String username, String password) {
		selectCountry(countryCode);
		inputUsername(username);
		inputPassword(password);
		clickLoginBtn();
		return this;
	}


	
}
