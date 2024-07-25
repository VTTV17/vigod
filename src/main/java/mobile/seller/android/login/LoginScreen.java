package mobile.seller.android.login;

import lombok.Getter;
import mobile.seller.android.home.HomeScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.commons.UICommonAndroid;
import utilities.data.DataGenerator;
import utilities.model.sellerApp.login.LoginInformation;

import java.time.Duration;

import static utilities.environment.goSELLEREnvironment.goSELLERHomeActivity;

public class LoginScreen {

    final static Logger logger = LogManager.getLogger(LoginScreen.class);

    WebDriver driver;
    WebDriverWait wait;
    UICommonAndroid commonMobile;
    @Getter
    private static LoginInformation loginInformation = new LoginInformation();

    public LoginScreen(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonMobile = new UICommonAndroid(driver);
    }

    By loc_tabAdmin = By.xpath("//android.widget.LinearLayout[@content-desc='Quản trị viên' or @content-desc='Admin']/android.widget.TextView");
    By loc_tabStaff = By.xpath("//android.widget.LinearLayout[@content-desc='Nhân viên' or @content-desc='Staff']");

    By COUNTRY_CODE = By.xpath("//*[ends-with(@resource-id,'edtCountry')]");
    By COUNTRY_SEARCH_BOX = By.xpath("//*[ends-with(@resource-id,'edtCountriesSearch')]");
    By COUNTRY_SEARCH_RESULT = By.xpath("//*[ends-with(@resource-id,'tvValue')]");

    By USERNAME = By.xpath("//*[ends-with(@resource-id,'edtUsername')]");
    By PASSWORD = By.xpath("//*[ends-with(@resource-id,'edtPassword')]");
    By TERM_CHECKBOX = By.xpath("//*[ends-with(@resource-id,'cbxTermAndPrivacy')]");
    By LOGIN_BTN = By.xpath("//*[ends-with(@resource-id,'tvLogin')]");
    By FORGOT_PASS_LINK = By.xpath("//*[ends-with(@resource-id,'tvForgetPassword')]");
    By NEW_PASSWORD = By.xpath("//*[ends-with(@resource-id,'edtNewPassword')]");
    By SEND_BTN = By.xpath("//*[ends-with(@resource-id,'rlSent')]");
    By VERIFICATION_CODE = By.xpath("//*[ends-with(@resource-id,'edtVerifyCode')]");
    By RESEND_BTN = By.xpath("//*[ends-with(@resource-id,'tvResend')]");

    By AVAILABLE_SHOP_BTN = By.xpath("//*[ends-with(@resource-id,'container')]");

    By USERNAME_ERROR = By.xpath("//*[ends-with(@resource-id,'tvErrorUsername')]");
    By PASSWORD_ERROR = By.xpath("//*[ends-with(@resource-id,'tvErrorPassword')]");


    public LoginScreen clickAdminTab() {
        commonMobile.getElement(loc_tabAdmin).click();
        logger.info("Clicked on Admin tab.");
        return this;
    }

    public LoginScreen clickStaffTab() {
        commonMobile.getElement(loc_tabStaff).click();
        logger.info("Clicked on Staff tab.");
        return this;
    }

    public void clickCountryCodeField() {
        commonMobile.getElement(COUNTRY_CODE).click();
        logger.info("Clicked on Country code field.");
    }

    public void inputCountryCodeToSearchBox(String country) {
        commonMobile.getElement(COUNTRY_SEARCH_BOX).sendKeys(country);
        logger.info("Input Country code: {}", country);
    }

    public void selectCountryCodeFromSearchBox(String country) {
        clickCountryCodeField();
        inputCountryCodeToSearchBox(country);

        commonMobile.click(COUNTRY_SEARCH_RESULT);
        logger.info("Selected country: {}", country);
    }

    public LoginScreen clickUsername() {
        commonMobile.getElement(USERNAME).click();
        logger.info("Clicked on Username field.");
        return this;
    }

    public void inputUsername(String username) {
        commonMobile.sendKeys(USERNAME, username);
        logger.info("Input '{}' into Username field.", username);
    }

    public void inputPassword(String password) {
        commonMobile.sendKeys(PASSWORD, password);
        logger.info("Input '{}' into Password field.", password);
    }

    public boolean isTermAgreementChecked() {
        boolean isChecked = commonMobile.isChecked(TERM_CHECKBOX);
        logger.info("Is Term Agreement checkbox checked: {}", isChecked);
        return isChecked;
    }

    public void clickAgreeTerm() {
        if (isTermAgreementChecked()) {
            return;
        }
        commonMobile.getElement(TERM_CHECKBOX).click();
        logger.info("Clicked on Term Agreement checkbox.");
    }

    public void clickLoginBtn() {
        commonMobile.getElement(LOGIN_BTN).click();
        logger.info("Clicked on Login button.");
    }

    public boolean isLoginBtnEnabled() {
        boolean isEnabled = commonMobile.getElement(LOGIN_BTN).isEnabled();
        logger.info("Is Login button enabled: {}", isEnabled);
        return isEnabled;
    }

    public String getUsernameError() {
        String text = commonMobile.getText(USERNAME_ERROR);
        logger.info("Retrieved error for username field: {}", text);
        return text;
    }

    public String getPasswordError() {
        String text = commonMobile.getText(PASSWORD_ERROR);
        logger.info("Retrieved error for password field: {}", text);
        return text;
    }

    public LoginScreen clickAvailableShop() {
        commonMobile.getElement(AVAILABLE_SHOP_BTN).click();
        logger.info("Clicked on an available shop for staff.");
        return this;
    }

    public LoginScreen clickForgotPassword() {
        commonMobile.getElement(FORGOT_PASS_LINK).click();
        logger.info("Clicked on Forgot Password link text.");
        return this;
    }

    public LoginScreen inputNewPassword(String password) {
        commonMobile.sendKeys(NEW_PASSWORD, password);
        logger.info("Input '{}' into New Password field.", password);
        return this;
    }

    public LoginScreen clickSendBtn() {
        commonMobile.getElement(SEND_BTN).click();
        logger.info("Clicked on Send button.");
        return this;
    }

    public LoginScreen inputVerificationCode(String code) {
        commonMobile.sendKeys(VERIFICATION_CODE, code);
        logger.info("Input '{}' into Verification Code field.", code);
        return this;
    }

    public void performLogin(LoginInformation loginInformation) {
        // Get login information
        LoginScreen.loginInformation = loginInformation;

        // Perform login
        inputUsername(loginInformation.getEmail());
        inputPassword(loginInformation.getPassword());
        clickAgreeTerm();
        clickLoginBtn();

        // Wait login success
        commonMobile.waitUntilScreenLoaded(goSELLERHomeActivity);
    }

    public HomeScreen performLogin(String countryCode, String userName, String password) {
        selectCountryCodeFromSearchBox(countryCode);
        inputUsername(userName);
        inputPassword(password);
        clickAgreeTerm();
        clickLoginBtn();

        // set login information
        loginInformation.setPhoneCode(new DataGenerator().getPhoneCode(countryCode));
        loginInformation.setPhoneNumber(userName);
        loginInformation.setPassword(password);
        return new HomeScreen(driver);
    }
}
