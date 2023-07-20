package pages.sellerapp.login;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.sellerapp.home.HomePage;
import utilities.UICommonMobile;
import utilities.data.DataGenerator;
import utilities.model.sellerApp.login.LoginInformation;

public class LoginPage {

    final static Logger logger = LogManager.getLogger(LoginPage.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonMobile commonAction;
    private static final LoginInformation loginInfo = new LoginInformation();

    int defaultTimeout = 5;

    public LoginPage (WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonMobile(driver);
    }

    By ADMIN_TAB = By.xpath("//android.widget.LinearLayout[@content-desc='Quản trị viên' or @content-desc='Admin']/android.widget.TextView");
    By STAFF_TAB = By.xpath("//android.widget.LinearLayout[@content-desc='Nhân viên' or @content-desc='Staff']");

    By COUNTRYCODE = By.xpath("//*[ends-with(@resource-id,'edtCountry')]");
    By COUNTRY_SEARCHBOX = By.xpath("//*[ends-with(@resource-id,'edtCountriesSearch')]");
    By COUNTRY_SEARCHRESULT = By.xpath("//*[ends-with(@resource-id,'tvValue')]");

    By USERNAME = By.xpath("//*[ends-with(@resource-id,'edtUsername')]");
    By PASSWORD = By.xpath("//*[ends-with(@resource-id,'edtPassword')]");
    By TERM_CHECKBOX = By.xpath("//*[ends-with(@resource-id,'cbxTermAndPrivacy')]");
    By LOGIN_BTN = By.xpath("//*[ends-with(@resource-id,'btnLogin')]");

    By FORGOTPASS_LINK = By.xpath("//*[ends-with(@resource-id,'tvForgetPassword')]");
    By NEWPASSWORD = By.xpath("//*[ends-with(@resource-id,'edtNewPassword')]");
    By SEND_BTN = By.xpath("//*[ends-with(@resource-id,'rlSent')]");
    By VERIFICATIONCODE = By.xpath("//*[ends-with(@resource-id,'edtVerifyCode')]");
    By RESEND_BTN = By.xpath("//*[ends-with(@resource-id,'tvResend')]");

    By AVAILABLESHOP_BTN = By.xpath("//*[ends-with(@resource-id,'container')]");

    By USERNAME_ERROR = By.xpath("//*[ends-with(@resource-id,'tvErrorUsername')]");
    By PASSWORD_ERROR = By.xpath("//*[ends-with(@resource-id,'tvErrorPassword')]");


    public LoginPage clickAdminTab() {
        commonAction.getElement(ADMIN_TAB, defaultTimeout).click();
        logger.info("Clicked on Admin tab.");
        return this;
    }

    public LoginPage clickStaffTab() {
        commonAction.getElement(STAFF_TAB, defaultTimeout).click();
        logger.info("Clicked on Staff tab.");
        return this;
    }

    public LoginPage clickCountryCodeField() {
        commonAction.getElement(COUNTRYCODE, defaultTimeout).click();
        logger.info("Clicked on Country code field.");
        return this;
    }

    public LoginPage inputCountryCodeToSearchBox(String country) {
        commonAction.getElement(COUNTRY_SEARCHBOX, defaultTimeout).sendKeys(country);
        logger.info("Input Country code: " + country);
        return this;
    }

    public LoginPage selectCountryCodeFromSearchBox(String country) {
        clickCountryCodeField();
        inputCountryCodeToSearchBox(country);

        commonAction.clickElement(COUNTRY_SEARCHRESULT);
        logger.info("Selected country: " + country);
        return this;
    }

    public LoginPage clickUsername() {
        commonAction.getElement(USERNAME, defaultTimeout).click();
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

    public boolean isTermAgreementChecked() {
        boolean isChecked = commonAction.isElementChecked(TERM_CHECKBOX);
        logger.info("Is Term Agreement checkbox checked: " + isChecked);
        return isChecked;
    }

    public LoginPage clickAgreeTerm() {
        if (isTermAgreementChecked()) {
            return this;
        }
        commonAction.getElement(TERM_CHECKBOX, defaultTimeout).click();
        logger.info("Clicked on Term Agreement checkbox.");
        return this;
    }

    public LoginPage clickLoginBtn() {
        commonAction.getElement(LOGIN_BTN, defaultTimeout).click();
        logger.info("Clicked on Login button.");
        return this;
    }

    public boolean isLoginBtnEnabled() {
        boolean isEnabled = commonAction.getElement(LOGIN_BTN, defaultTimeout).isEnabled();
        logger.info("Is Login button enabled: " + isEnabled);
        return isEnabled;
    }

    public String getUsernameError() {
        String text = commonAction.getText(USERNAME_ERROR);
        logger.info("Retrieved error for username field: " + text);
        return text;
    }

    public String getPasswordError() {
        String text = commonAction.getText(PASSWORD_ERROR);
        logger.info("Retrieved error for password field: " + text);
        return text;
    }

    public LoginPage clickAvailableShop() {
        commonAction.getElement(AVAILABLESHOP_BTN, defaultTimeout).click();
        logger.info("Clicked on an available shop for staff.");
        return this;
    }

    public LoginPage clickForgotPassword() {
        commonAction.getElement(FORGOTPASS_LINK, defaultTimeout).click();
        logger.info("Clicked on Forgot Password link text.");
        return this;
    }

    public LoginPage inputNewPassword(String password) {
        commonAction.inputText(NEWPASSWORD, password);
        logger.info("Input '" + password + "' into New Password field.");
        return this;
    }

    public LoginPage clickSendBtn() {
        commonAction.getElement(SEND_BTN, defaultTimeout).click();
        logger.info("Clicked on Send button.");
        return this;
    }

    public LoginPage inputVerificationCode(String code) {
        commonAction.inputText(VERIFICATIONCODE, code);
        logger.info("Input '" + code + "' into Verification Code field.");
        return this;
    }
    public HomePage performLogin(String userName, String password){
        inputUsername(userName);
        inputPassword(password);
        clickAgreeTerm();
        clickLoginBtn();

        // set login information
        loginInfo.setEmail(userName);
        loginInfo.setPassword(password);
        return new HomePage(driver);
    }
    public HomePage performLogin(String countryCode, String userName, String password){
        selectCountryCodeFromSearchBox(countryCode);
        inputUsername(userName);
        inputPassword(password);
        clickAgreeTerm();
        clickLoginBtn();

        // set login information
        loginInfo.setPhoneCode(new DataGenerator().getPhoneCode(countryCode));
        loginInfo.setPhoneNumber(userName);
        loginInfo.setPassword(password);
        return new HomePage(driver);
    }

    // get phone code, phone number, email, password
    public LoginInformation getLoginInfo() {
        return loginInfo;
    }


}
