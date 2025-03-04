package app.Buyer.login;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import app.Buyer.buyergeneral.BuyerGeneral;
import app.Buyer.signup.SignupPage;
import utilities.commons.UICommonMobile;
import utilities.model.dashboard.setting.languages.translation.MobileAndroid;
import utilities.utils.localization.TranslateText;

public class LoginPage {

	final static Logger logger = LogManager.getLogger(LoginPage.class);

    WebDriver driver;
    UICommonMobile commonAction;

    int defaultTimeout = 5;
    
    public LoginPage (WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonMobile(driver);
    }

    public static String localizedInvalidEmailError(List<MobileAndroid> translation) {
    	return TranslateText.localizedText(translation, "error_email_notice_label");
    }       
    public static String localizedInvalidPhoneError(List<MobileAndroid> translation) {
    	return TranslateText.localizedText(translation, "social_phone_invalid");
    }     
    public static String localizedWrongEmailOrPasswordError(List<MobileAndroid> translation) {
    	return TranslateText.localizedText(translation, "incorrect_email_pass");
    }       
    public static String localizedWrongPhoneOrPasswordError(List<MobileAndroid> translation) {
    	return TranslateText.localizedText(translation, "incorrect_phone_pass");
    }       
    
    By loc_txtUsername = By.xpath("//*[ends-with(@resource-id,'field') and not (contains(@resource-id,'password'))]//*[ends-with(@resource-id,'edittext')]");
    By loc_txtPassword = By.xpath("//*[ends-with(@resource-id,'field') and contains(@resource-id,'password')]//*[ends-with(@resource-id,'edittext')]");
    By loc_btnLogin = By.xpath("//*[ends-with(@resource-id,'submit') or ends-with(@resource-id,'check_email')]");
    By loc_lnkSignup = By.xpath("//*[ends-with(@resource-id,'txt_sign_up')]");
    By loc_tabPhone = By.xpath("(//*[ends-with(@resource-id,'account_v2_tabs')]/android.widget.LinearLayout/android.widget.LinearLayout)[2]");
    By loc_lnkForgotPassword = By.xpath("//*[ends-with(@resource-id,'forgot_pass')]");
    
    public LoginPage clickPhoneTab() {
        commonAction.clickElement(loc_tabPhone);
        logger.info("Clicked Phone tab.");
        return this;
    }    
    
    public LoginPage clickUsername() {
    	commonAction.clickElement(loc_txtUsername, defaultTimeout);
    	logger.info("Clicked on Username field.");
    	return this;
    }    
    
    public LoginPage inputUsername(String username) {
    	commonAction.inputText(loc_txtUsername, username);
    	logger.info("Input '" + username + "' into Username field.");
        return this;
    }

    public LoginPage inputPassword(String password) {
    	commonAction.inputText(loc_txtPassword, password);
    	logger.info("Input '" + password + "' into Password field.");
        return this;
    }

    public boolean isLoginBtnEnabled() {
    	boolean isEnabled = commonAction.isElementEnabled(loc_btnLogin);
    	logger.info("Is 'Login' button enabled: " + isEnabled);
    	return isEnabled;
    }    
    
    public LoginPage clickLoginBtn() {
    	commonAction.clickElement(loc_btnLogin, defaultTimeout);
    	logger.info("Clicked on Login button.");
        return this;
    }

    public LoginPage clickSignupLinkText() {
    	commonAction.clickElement(loc_lnkSignup, defaultTimeout);
    	logger.info("Clicked on 'Signup' link text.");
        return this;
    }

    public LoginPage selectCountry(String country) {
    	new SignupPage(driver).selectCountry(country);
    	return this;
    }    
    
    public LoginPage performLogin(String userName, String pass){
        // Logger
        LogManager.getLogger().info("===== STEP =====> [LoginGoBUYER] START... ");
        if (!userName.matches("[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}")) {
            clickPhoneTab();
        }
        inputUsername(userName);
        inputPassword(pass);
        UICommonMobile.sleepInMiliSecond(1000);
        clickLoginBtn();

        // Logger
        LogManager.getLogger().info("===== STEP =====> [LoginGoBUYER] DONE!!! ");

        return this;
    }

    public LoginPage performLogin(String country, String username, String password){
    	if (username.matches("\\d+")) {
    		clickPhoneTab();
    		new SignupPage(driver).selectCountry(country);
    	}
    	inputUsername(username).inputPassword(password).clickLoginBtn();
    	return this;
    }

    public ForgotPasswordPage clickForgotPasswordLink() {
    	commonAction.clickElement(loc_lnkForgotPassword, defaultTimeout);
    	
    	//Sometimes the element is still present. The code below helps handle this intermittent issue
    	boolean isElementPresent = true;
    	for (int i=0; i<3; i++) {
    		if (commonAction.getElements(loc_lnkForgotPassword).size() == 0) {
    			isElementPresent = false;
    			break;
    		}
    		UICommonMobile.sleepInMiliSecond(500);
    	}
    	if (isElementPresent) {
    		commonAction.clickElement(loc_lnkForgotPassword);
    	}
    	
    	logger.info("Clicked on 'Forgot Password' link text.");
        return new ForgotPasswordPage(driver);
    }    
    
    public LoginPage verifyToastMessage(String expected){
        new BuyerGeneral(driver).verifyToastMessage(expected);
        return this;
    }
}
