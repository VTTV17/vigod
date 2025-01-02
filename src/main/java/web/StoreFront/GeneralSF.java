package web.StoreFront;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import utilities.commons.UICommonAction;
import utilities.links.Links;
import web.StoreFront.login.LoginPage;

public class GeneralSF {
	
	final static Logger logger = LogManager.getLogger(GeneralSF.class);
	
	WebDriver driver;
	UICommonAction commons;
	
	public GeneralSF(WebDriver driver) {
		this.driver = driver;
		commons = new UICommonAction(driver);
	}

	By loc_icnSpinner = By.cssSelector(".loader");
	By loc_lblCurrentLanguage = By.id("current-lang");
	By loc_icnFacebookBubble = By.id("fb-root");
	By loc_dlgLogin_btnLogin = By.cssSelector(".modal-login");
	By loc_dlgLogin_btnRegister = By.cssSelector(".modal-register");
	By loc_icnSearchLoading = By.cssSelector(".lds-ellipsis");

	public GeneralSF waitTillLoaderDisappear() {
		commons.waitInvisibilityOfElementLocated(loc_icnSpinner);
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
		String language = commons.getAttribute(loc_lblCurrentLanguage, "value");
		logger.info("Retrieved Storefront's display language: " + language);
		return language;
	}

    public GeneralSF hideFacebookBubble() {
    	commons.hideElement(commons.getElement(loc_icnFacebookBubble));
    	logger.info("Hid Facebook bubble."); 
        return this;
    }
	public LoginPage clickOnLoginButtonOnRequiredLoginModal(){
		UICommonAction.sleepInMiliSecond(1000);
		commons.waitVisibilityOfElementLocated(loc_dlgLogin_btnLogin);
		commons.click(loc_dlgLogin_btnLogin);
		logger.info("Click on Login button on required login modal.");
		return  new LoginPage(driver);
	}
	public LoginPage clickOnRegisterButtonOnRequiredLoginModal(){
		UICommonAction.sleepInMiliSecond(1000);
		commons.waitVisibilityOfElementLocated(loc_dlgLogin_btnRegister);
		commons.click(loc_dlgLogin_btnRegister);
		logger.info("Click on Register button on required login modal.");
		return  new LoginPage(driver);
	}
	public GeneralSF navigateToURL(String URL){
		commons.navigateToURL(URL);
		logger.info("Navigate to: "+URL);
		waitTillLoaderDisappear();
		return this;
	}
	public GeneralSF checkPageNotFound(String domain){
		Assert.assertEquals(commons.getCurrentURL(),domain+ Links.PAGE_404_PATH);
		logger.info("Check page not found.");
		return this;
	}
	public GeneralSF waitDotLoadingDisappear(){
//		commons.waitVisibilityOfElementLocated(loc_icnSearchLoading);
		commons.waitInvisibilityOfElementLocated(loc_icnSearchLoading);
		return this;
	}
}
