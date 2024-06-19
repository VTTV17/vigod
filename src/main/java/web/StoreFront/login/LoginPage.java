package web.StoreFront.login;

import static utilities.links.Links.SF_DOMAIN;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;

import api.Seller.setting.StoreInformation;
import api.Buyer.login.LoginSF;
import web.StoreFront.GeneralSF;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.signup.SignupPage;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;
import utilities.model.sellerApp.login.LoginInformation;

public class LoginPage {
	
	final static Logger logger = LogManager.getLogger(LoginPage.class);
	
    WebDriver driver;
    UICommonAction commonAction;
    
    public LoginPage (WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_lblScreenText = By.cssSelector("#login-modal .modal-content");
    By loc_ddlCountry = By.id("login-country-code");
	By loc_lstCountry = By.id("login-country-code-menu");
	String loc_ddvCountryByName = ".//span[text()='%s']";
    By loc_txtUsername = By.id("login-username");
    By loc_txtPassword = By.id("login-password"); 
    By loc_btnLogin = By.cssSelector("#frm-login .btn-submit");
    By loc_lnkForgotPassword = By.id("open-forgot-pwd");
    By loc_btnFacebookLogin = By.cssSelector("#login-modal .facebook-login-button"); 
    By loc_lnkCreateAccount = By.cssSelector("#login-modal [data-target='#signup-modal']");
    By loc_lblUsernameError = By.id("login-username-error");
    By loc_lblLoginFailError = By.id("login-fail");
    By loc_lblPasswordError = By.id("login-password-error");
    
    public LoginPage navigate(String domain) {
        driver.get(domain);
        logger.debug("Page title is: "+driver.getTitle());
        return this;
    }

    public LoginPage selectCountry(String country) {
    	commonAction.click(loc_ddlCountry);
    	commonAction.click(new ByChained(loc_lstCountry, By.xpath(loc_ddvCountryByName.formatted(country))));
    	logger.info("Selected country: " + country);
    	return this;
    }        
    
    public LoginPage inputEmailOrPhoneNumber(String username) {
    	commonAction.sendKeys(loc_txtUsername, username);
    	logger.info("Input '" + username + "' into Username field.");
        return this;
    }

    public LoginPage inputPassword(String password) {
    	commonAction.sendKeys(loc_txtPassword, password);
    	logger.info("Input '" + password + "' into Password field.");
        return this;
    }    

    public LoginPage clickLoginBtn() {
    	commonAction.click(loc_btnLogin);
    	logger.info("Clicked on Login button.");
    	new GeneralSF(driver).waitTillLoaderDisappear();
        return this;
    }

    LoginPage login(String username, String password) {
    	inputEmailOrPhoneNumber(username).inputPassword(password).clickLoginBtn();
    	return this;
    }
    public LoginPage performLogin(String username, String password) {
        new GeneralSF(driver).waitTillLoaderDisappear();
    	new HeaderSF(driver).clickUserInfoIcon().clickLoginIcon();
    	login(username, password);
        return this;
    }
    public LoginPage performLogin(String country, String username, String password) {
    	new HeaderSF(driver).clickUserInfoIcon().clickLoginIcon();
    	selectCountry(country).login(username, password);
    	return this;
    }    
    
    public ForgotPasswordDialog clickForgotPassword() {
    	commonAction.click(loc_lnkForgotPassword);
    	logger.info("Clicked on Forgot Password linktext.");
    	return new ForgotPasswordDialog(driver);
    }   
    
    public LoginPage clickFacebookIcon() {
    	commonAction.click(loc_btnFacebookLogin);
    	logger.info("Clicked on Facebook login icon.");
    	commonAction.sleepInMiliSecond(1000);
    	return this;
    }  
    
    public SignupPage clickCreateNewAccount() {
    	commonAction.click(loc_lnkCreateAccount);
    	logger.info("Clicked on 'Create New Account' linktext.");
    	return new SignupPage(driver);
    }    

    public String getLoginFailError() {
    	String text = commonAction.getText(loc_lblLoginFailError);
    	logger.info("Error retrieved: " + text);
    	return text;
    }
    public String getUsernameError() {
    	String text = commonAction.getText(loc_lblUsernameError);
    	logger.info("Error retrieved: " + text);
    	return text;
    }    
    public String getPasswordError() {
    	String text = commonAction.getText(loc_lblPasswordError);
    	logger.info("Error retrieved: " + text);
    	return text;
    }    
    
    public void clickLoginBtnJS() {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", commonAction.getElement(loc_btnLogin));
        logger.info("Clicked on Login button.");
    }

    public void performLoginJS(String username, String password, String phoneCode, LoginInformation loginInformation) {
        // get buyer accessToken
        LoginSF login = new LoginSF(loginInformation);
        login.LoginToSF(username, password, phoneCode);
        String accessToken = login.getInfo().getAccessToken();

        // navigate to storefront
        navigate("https://%s%s/".formatted(new StoreInformation(loginInformation).getInfo().getStoreURL(), SF_DOMAIN));

        // update accessToken cookie
        ((JavascriptExecutor) driver).executeScript("document.cookie = 'Authorization=\"Bearer %s\"'".formatted(accessToken));

        // refresh page to get the newest configuration
        driver.navigate().refresh();
    }

    public void verifyTextAtLoginScreen(String signinLanguage) throws Exception {
        String text = commonAction.getText(loc_lblScreenText);
        Assert.assertEquals(text, PropertiesUtil.getPropertiesValueBySFLang("login.screen.text", signinLanguage));
        logger.info("verifyTextAtLoginScreen completed");
    }
    
}
