package web.StoreFront.login;

import static utilities.links.Links.SF_DOMAIN;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import api.Buyer.login.LoginSF;
import api.Seller.setting.StoreInformation;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.setting.languages.translation.StorefrontCSR;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.utils.PropertiesUtil;
import utilities.utils.localization.TranslateText;
import web.StoreFront.GeneralSF;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.signup.SignupPage;

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

    public static String localizedEmptyUsernameError(List<StorefrontCSR> translation) {
    	return TranslateText.localizedText(translation, "gosell.welcome.required.username");
    }     
    public static String localizedEmptyPasswordError(List<StorefrontCSR> translation) {
    	return TranslateText.localizedText(translation, "gosell.welcome.required.pwd");
    }   
    public static String localizedInvalidUsernameError(List<StorefrontCSR> translation) {
    	return TranslateText.localizedText(translation, "gosell.welcome.invalid.username");
    }   
    public static String localizedWrongCredentials(List<StorefrontCSR> translation) {
    	return TranslateText.localizedText(translation, "gosell.welcome.loginFail");
    }   
    
    //TODO delete this function
    public LoginPage navigate(String domain) {
        driver.get(domain);
        logger.debug("Page title is: "+driver.getTitle());
        return this;
    }

    /**
     * Retrieves the currently selected country
     */
    public String getSelectedCountry() {
    	String selectedCountry = commonAction.getText(locator.loc_ddlCountry).split("\\s*\\+\\d+")[0];
    	logger.info("Retrieved selected country: {}", selectedCountry);
    	return selectedCountry;
    }
    public LoginPage selectCountry(String country) {
    	
    	if (getSelectedCountry().contentEquals(country)) return this;
    	
    	commonAction.click(locator.loc_ddlCountry);
    	commonAction.click(locator.loc_ddvCountryByName(country));
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
