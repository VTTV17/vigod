package mobile.buyer.login;

import mobile.buyer.buyergeneral.BuyerGeneral;
import mobile.buyer.login.forgot_password.ForgotPasswordScreen;
import mobile.buyer.login.signup.SignupScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.commons.UICommonMobile;

import java.time.Duration;

public class LoginScreen extends LoginElement {

    final static Logger logger = LogManager.getLogger(LoginScreen.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile commonAction;

    int defaultTimeout = 5;

    public LoginScreen(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonMobile(driver);
    }


    public LoginScreen clickUsername() {
        commonAction.clickElement(loc_txtUsername, defaultTimeout);
        logger.info("Clicked on Username field.");
        return this;
    }

    public LoginScreen inputUsername(String username) {
        commonAction.inputText(loc_txtUsername, username);
        logger.info("Input '{}' into Username field.", username);
        return this;
    }

    public LoginScreen inputPassword(String password) {
        commonAction.inputText(loc_txtPassword, password);
        logger.info("Input '{}' into Password field.", password);
        return this;
    }

    public boolean isLoginBtnEnabled() {
        boolean isEnabled = commonAction.isElementEnabled(loc_btnLogin);
        logger.info("Is 'Login' button enabled: {}", isEnabled);
        return isEnabled;
    }

    public LoginScreen clickLoginBtn() {
        commonAction.clickElement(loc_btnLogin, defaultTimeout);
        logger.info("Clicked on Login button.");
        return this;
    }

    public LoginScreen clickSignupLinkText() {
        commonAction.clickElement(loc_lnkSignup, defaultTimeout);
        logger.info("Clicked on 'Signup' link text.");
        return this;
    }

    public LoginScreen performLogin(String userName, String pass) {
        if (!userName.matches("[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}")) {
            clickPhoneTab();
        }
        inputUsername(userName);
        inputPassword(pass);
        commonAction.sleepInMiliSecond(1000);
        clickLoginBtn();
        return this;
    }

    public LoginScreen performLogin(String country, String username, String password) {
        if (username.matches("\\d+")) {
            clickPhoneTab();
            new SignupScreen(driver).selectCountryCodeFromSearchBox(country);
        }
        inputUsername(username);
        inputPassword(password);
        clickLoginBtn();
        return this;
    }

    public LoginScreen clickPhoneTab() {
        commonAction.clickElement(loc_tabPhone);
        logger.info("Clicked on Phone tab.");
        commonAction.sleepInMiliSecond(1000); //Click on Phone tab => Username field is not properly located
        return this;
    }

    public LoginScreen clickForgotPasswordLink() {
        commonAction.clickElement(loc_lnkForgotPassword, defaultTimeout);

        //Sometimes the element is still present. The code below helps handle this intermittent issue
        boolean isElementPresent = true;
        for (int i = 0; i < 3; i++) {
            if (commonAction.getElements(loc_lnkForgotPassword).isEmpty()) {
                isElementPresent = false;
                break;
            }
            commonAction.sleepInMiliSecond(500);
        }
        if (isElementPresent) {
            commonAction.clickElement(loc_lnkForgotPassword);
        }

        logger.info("Clicked on 'Forgot Password' link text.");
        return this;
    }

    public LoginScreen inputUsernameForgotPassword(String username) {
        new ForgotPasswordScreen(driver).inputUsernameForgotPassword(username);
        return this;
    }

    public LoginScreen inputNewPassword(String password) {
        new ForgotPasswordScreen(driver).inputNewPassword(password);
        return this;
    }

    public LoginScreen verifyToastMessage(String expected) {
        new BuyerGeneral(driver).verifyToastMessage(expected);
        return this;
    }
}
