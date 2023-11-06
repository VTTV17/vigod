package pages.storefront;

import com.github.dockerjava.api.model.Link;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import pages.dashboard.home.HomePage;
import pages.storefront.login.LoginPage;
import utilities.UICommonAction;
import utilities.links.Links;

import java.time.Duration;

public class GeneralSF {
	WebDriver driver;
	WebDriverWait wait;
	UICommonAction commons;
	final static Logger logger = LogManager.getLogger(GeneralSF.class);

	public GeneralSF(WebDriver driver) {
		this.driver = driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		commons = new UICommonAction(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(css = ".loader")
	WebElement SPINNER;

    @FindBy(id = "current-lang")
    WebElement LANGUAGE;	
    
	@FindBy (id = "fb-root")
	WebElement FACEBOOK_BUBBLE;
	@FindBy(css = ".modal-login")
	WebElement LOGIN_BUTTON_ON_MODAL;
	@FindBy(css = ".modal-register")
	WebElement REGISTER_BUTTON_ON_MODAL;
	@FindBy(css = ".lds-ellipsis")
	WebElement SEARCH_LOADING;
	public GeneralSF waitTillLoaderDisappear() {
		commons.waitForElementInvisible(SPINNER, 20);
		logger.info("Loader has finished loading");
		return this;
	}
	
	/**
	* <p>
	* Get the language Storefront is displayed in
	* <p>
	* @return either 'en' or 'vi' which donate English or Vietnamese accordingly
	*/
	public String getDisplayLanguage() {
		String language = commons.getElementAttribute(LANGUAGE, "value");
		logger.info("Retrieved Storefront's display language: " + language);
		return language;
	}

    public GeneralSF hideFacebookBubble() {
    	commons.hideElement(FACEBOOK_BUBBLE);
    	logger.info("Hid Facebook bubble."); 
        return this;
    }
	public LoginPage clickOnLoginButtonOnRequiredLoginModal(){
		commons.sleepInMiliSecond(1000);
		commons.waitForElementVisible(LOGIN_BUTTON_ON_MODAL,3000);
		commons.clickElement(LOGIN_BUTTON_ON_MODAL);
		logger.info("Click on Login button on required login modal.");
		return  new LoginPage(driver);
	}
	public LoginPage clickOnRegisterButtonOnRequiredLoginModal(){
		commons.sleepInMiliSecond(1000);
		commons.waitForElementVisible(REGISTER_BUTTON_ON_MODAL,3000);
		commons.clickElement(REGISTER_BUTTON_ON_MODAL);
		logger.info("Click on Register button on required login modal.");
		return  new LoginPage(driver);
	}
	public GeneralSF navigateToURL(String URL){
		commons.navigateToURL(URL);
		logger.info("Navigate to: "+URL);
		return this;
	}
	public GeneralSF checkPageNotFound(String domain){
		Assert.assertEquals(commons.getCurrentURL(),domain+ Links.PAGE_404_PATH);
		logger.info("Check page not found.");
		return this;
	}
	public GeneralSF waitDotLoadingDisappear(){
		commons.waitForElementVisible(SEARCH_LOADING);
		commons.waitForElementInvisible(SEARCH_LOADING);
		return this;
	}
	public GeneralSF hideLoadingIcon() {
		commons.hideElement(SPINNER);
		logger.info("Hid Loading icon.");
		return this;
	}
}
