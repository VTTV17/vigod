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
    
    By USERNAME = By.xpath("//*[ends-with(@resource-id,'field') and not (contains(@resource-id,'password'))]//*[ends-with(@resource-id,'edittext')]");
    By PASSWORD = By.xpath("//*[ends-with(@resource-id,'field') and contains(@resource-id,'password')]//*[ends-with(@resource-id,'edittext')]");
    By LOGIN_BTN = By.xpath("//*[ends-with(@resource-id,'submit') or ends-with(@resource-id,'check_email')]");
    By SIGNUP_LINKTEXT = By.xpath("//*[ends-with(@resource-id,'txt_sign_up')]");
    By PHONE_TAB = By.xpath("(//*[ends-with(@resource-id,'account_v2_tabs')]/android.widget.LinearLayout/android.widget.LinearLayout)[2]");
    By FORGOTPASSWORD = By.xpath("//*[ends-with(@resource-id,'forgot_pass')]");
    By USERNAME_FORGOTPASSWORD = By.xpath("(//*[ends-with(@resource-id,'social_layout_limit_edittext')])[1]");
    By PASSWORD_FORGOTPASSWORD = By.xpath("(//*[ends-with(@resource-id,'social_layout_limit_edittext')])[2]");

    public LoginPage clickUsername() {
    	commonAction.clickElement(USERNAME, defaultTimeout);
    	logger.info("Clicked on Username field.");
    	return this;
    }    
    
    public LoginPage inputUsername(String username) {
    	commonAction.inputText(USERNAME, username);
    	logger.info("Input '" + username + "' into Username field.");
        return this;
    }

    public LoginPage inputPassword(String password) {
    	commonAction.inputText(PASSWORD, password);
    	logger.info("Input '" + password + "' into Password field.");
        return this;
    }

    public boolean isLoginBtnEnabled() {
    	boolean isEnabled = commonAction.isElementEnabled(LOGIN_BTN);
    	logger.info("Is 'Login' button enabled: " + isEnabled);
    	return isEnabled;
    }    
    
    public LoginPage clickLoginBtn() {
    	commonAction.clickElement(LOGIN_BTN, defaultTimeout);
    	logger.info("Clicked on Login button.");
        return this;
    }

    public LoginPage clickSignupLinkText() {
    	commonAction.clickElement(SIGNUP_LINKTEXT, defaultTimeout);
    	logger.info("Clicked on 'Signup' link text.");
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
    		new SignupPage(driver).selectCountryCodeFromSearchBox(country);
    	}
    	inputUsername(username);
    	inputPassword(password);
    	clickLoginBtn();
    	return this;
    }
    public LoginPage clickPhoneTab() {
        commonAction.clickElement(PHONE_TAB);
        logger.info("Clicked Phone tab.");
        UICommonMobile.sleepInMiliSecond(1000, "Wait after tapping Phone tab"); //Click on Phone tab => Username field is not properly located
        return this;
    }
    public LoginPage clickForgotPasswordLink() {
    	commonAction.clickElement(FORGOTPASSWORD, defaultTimeout);
    	
    	//Sometimes the element is still present. The code below helps handle this intermittent issue
    	boolean isElementPresent = true;
    	for (int i=0; i<3; i++) {
    		if (commonAction.getElements(FORGOTPASSWORD).size() == 0) {
    			isElementPresent = false;
    			break;
    		}
    		UICommonMobile.sleepInMiliSecond(500);
    	}
    	if (isElementPresent) {
    		commonAction.clickElement(FORGOTPASSWORD);
    	}
    	
    	logger.info("Clicked on 'Forgot Password' link text.");
        return this;
    }    
    
    public LoginPage inputUsernameForgotPassword(String username) {
    	commonAction.inputText(USERNAME_FORGOTPASSWORD, username);
    	logger.info("Input '" + username + "' into Username field.");
        return this;
    }

    public LoginPage inputNewPassword(String password) {
        commonAction.inputText(PASSWORD_FORGOTPASSWORD, password);
        logger.info("Input '" + password + "' into New Password field.");
        return this;
    }
    public LoginPage verifyToastMessage(String expected){
        new BuyerGeneral(driver).verifyToastMessage(expected);
        return this;
    }
}
