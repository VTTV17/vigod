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
import lombok.SneakyThrows;
import api.Buyer.login.LoginSF;
import web.StoreFront.GeneralSF;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.signup.SignupPage;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;
import utilities.enums.DisplayLanguage;
import utilities.model.sellerApp.login.LoginInformation;

public class LoginPage {
	
	final static Logger logger = LogManager.getLogger(LoginPage.class);
	
    WebDriver driver;
    UICommonAction commonAction;
    LoginPageElement locator;
    
    public LoginPage (WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
        locator = new LoginPageElement();
    }

    @SneakyThrows
    public static String localizedEmptyUsernameError(DisplayLanguage language) {
    	return PropertiesUtil.getPropertiesValueBySFLang("login.error.emptyUsername", language.name());
    }    
    @SneakyThrows
    public static String localizedEmptyPasswordError(DisplayLanguage language) {
    	return PropertiesUtil.getPropertiesValueBySFLang("login.error.emptyPassword", language.name());
    }
    @SneakyThrows
    public static String localizedInvalidUsernameError(DisplayLanguage language) {
    	return PropertiesUtil.getPropertiesValueBySFLang("login.error.invalidUsername", language.name());
    }
    @SneakyThrows
    public static String localizedWrongCredentialsError(DisplayLanguage language) {
    	return PropertiesUtil.getPropertiesValueBySFLang("login.error.wrongCredentials", language.name());
    }
    
    //TODO delete this function
    public LoginPage navigate(String domain) {
        driver.get(domain);
        logger.debug("Page title is: "+driver.getTitle());
        return this;
    }

    public LoginPage selectCountry(String country) {
    	commonAction.click(locator.loc_ddlCountry);
    	commonAction.click(new ByChained(locator.loc_lstCountry, By.xpath(locator.loc_ddvCountryByName.formatted(country))));
    	logger.info("Selected country: " + country);
    	return this;
    }        
    
    public LoginPage inputEmailOrPhoneNumber(String username) {
    	commonAction.sendKeys(locator.loc_txtUsername, username);
    	logger.info("Input username: {}", username);
        return this;
    }

    public LoginPage inputPassword(String password) {
    	commonAction.sendKeys(locator.loc_txtPassword, password);
    	logger.info("Input password: {}", password);
        return this;
    }    

    public LoginPage clickLoginBtn() {
    	commonAction.click(locator.loc_btnLogin);
    	logger.info("Clicked Login button.");
    	new GeneralSF(driver).waitTillLoaderDisappear();
        return this;
    }

    LoginPage login(String username, String password) {
    	return inputEmailOrPhoneNumber(username).inputPassword(password).clickLoginBtn();
    }
    public LoginPage performLogin(String username, String password) {
        new GeneralSF(driver).waitTillLoaderDisappear();
    	new HeaderSF(driver).clickUserInfoIcon().clickLoginIcon();
    	login(username, password);
        return this;
    }
    public LoginPage performLogin(String country, String username, String password) {
    	new GeneralSF(driver).waitTillLoaderDisappear();
    	new HeaderSF(driver).clickUserInfoIcon().clickLoginIcon();
    	selectCountry(country).login(username, password);
    	return this;
    }    
    
    public ForgotPasswordDialog clickForgotPassword() {
    	commonAction.click(locator.loc_lnkForgotPassword);
    	logger.info("Clicked Forgot Password linktext.");
    	return new ForgotPasswordDialog(driver);
    }   
    
    public LoginPage clickFacebookIcon() {
    	commonAction.click(locator.loc_btnFacebookLogin);
    	logger.info("Clicked Facebook login icon.");
    	UICommonAction.sleepInMiliSecond(1000);
    	return this;
    }  
    
    public SignupPage clickCreateNewAccount() {
    	commonAction.click(locator.loc_lnkCreateAccount);
    	logger.info("Clicked 'Create New Account' linktext.");
    	return new SignupPage(driver);
    }    

    public String getLoginFailError() {
    	String text = commonAction.getText(locator.loc_lblLoginFailError);
    	logger.info("Error retrieved: " + text);
    	return text;
    }
    public String getUsernameError() {
    	String text = commonAction.getText(locator.loc_lblUsernameError);
    	logger.info("Error retrieved: " + text);
    	return text;
    }    
    public String getPasswordError() {
    	String text = commonAction.getText(locator.loc_lblPasswordError);
    	logger.info("Error retrieved: " + text);
    	return text;
    }    
    
    public void clickLoginBtnJS() {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click()", commonAction.getElement(locator.loc_btnLogin));
        logger.info("Clicked Login button.");
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
        String text = commonAction.getText(locator.loc_lblScreenText);
        Assert.assertEquals(text, PropertiesUtil.getPropertiesValueBySFLang("login.screen.text", signinLanguage));
        logger.info("verifyTextAtLoginScreen completed");
    }
    
}
