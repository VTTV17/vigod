package pages.storefront.signup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import pages.Mailnesia;
import pages.storefront.header.HeaderSF;
import pages.storefront.login.LoginPage;
import utilities.UICommonAction;
import utilities.database.InitConnection;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Random;

import static utilities.links.Links.*;

public class SignupPage {

    final static Logger logger = LogManager.getLogger(SignupPage.class);

    public String country;
    public String countryCode;

    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;
    Mailnesia mailnesia;

    SoftAssert soft = new SoftAssert();

    public SignupPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "#signup-username")
    WebElement USERNAME;

    @FindBy(css = "#signup-displayName")
    WebElement DISPLAY_USERNAME;

    @FindBy(css = "#signup-dob")
    WebElement BIRTHDAY;

    @FindBy(css = "#get-email")
    WebElement EMAIL_TEXTBOX;

    @FindBy(css = "#signup-password")
    WebElement PASSWORD;

    @FindBy(css = "#frm-get-email .btn-submit")
    WebElement COMPLETE_BTN;

    @FindBy(css = "#frm-signup .btn-submit")
    WebElement SIGNUP_BTN;

    @FindBy(css = "div.uik-select__valueWrapper>div>div:nth-child(2)")
    WebElement COUNTRY_CODE;

    @FindBy(css = "#signup-country-code")
    WebElement COUNTRY_DROPDOWN;

    @FindBy(css = "#signup-country-code-menu .dropdown-item")
    List<WebElement> COUNTRY_LIST;

    @FindBy(css = "#activate-code")
    WebElement OTP;

    @FindBy(css = "#frm-activate .btn-submit")
    WebElement CONFIRM_OTP;

    @FindBy(id = "activate-resend-code")
    WebElement RESEND_OTP;

    @FindBy(id = "signup-fail")
    WebElement USEREXIST_ERROR;

    @FindBy(id = "signup-username-error")
    WebElement USER_ERROR;

    @FindBy(id = "signup-password-error")
    WebElement PASSWORD_ERROR;

    @FindBy(id = "activate-fail")
    WebElement WRONG_CODE_ERROR;

    public SignupPage navigate() {
        driver.get(DOMAIN1);
        return this;
    }

    public SignupPage selectCountry(String country) {
        commonAction.clickElement(COUNTRY_DROPDOWN);
        if (country.contentEquals("rd")) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int randomNumber = new Random().nextInt(0, COUNTRY_LIST.size());
            COUNTRY_LIST.get(randomNumber).click();
        } else {
            driver.findElement(By.xpath("//ul[@id='signup-country-code-menu']//span[text()='%s']".formatted(country))).click();
        }
        String[] selectedOption = COUNTRY_DROPDOWN.getText().split("\n");
        logger.info("Selected country '%s'. Its according code is '%s'.".formatted(selectedOption[0], selectedOption[1]));
        this.country = selectedOption[0];
        this.countryCode = selectedOption[1];
        return this;
    }

    public SignupPage inputMailOrPhoneNumber(String user) {
        commonAction.inputText(USERNAME, user);
        logger.info("Input '" + user + "' into Username field.");
        return this;
    }

    public SignupPage inputPassword(String password) {
        commonAction.inputText(PASSWORD, password);
        logger.info("Input '" + password + "' into Password field.");
        return this;
    }

    public SignupPage inputDisplayName(String name) {
        commonAction.inputText(DISPLAY_USERNAME, name);
        logger.info("Input '" + name + "' into Display Name field.");
        return this;
    }

    public SignupPage inputBirthday(String date) {
        commonAction.inputText(BIRTHDAY, date);
        logger.info("Input '" + date + "' into Birthday field.");
        return this;
    }

    public SignupPage inputEmail(String mail) {
        commonAction.inputText(EMAIL_TEXTBOX, mail);
        logger.info("Input '" + mail + "' into Email field.");
        commonAction.sleepInMiliSecond(5000); //Without this delay, the email can not be sent to back end.
        return this;
    }

    public SignupPage clickCompleteBtn() {
        commonAction.clickElement(COMPLETE_BTN);
        logger.info("Clicked on Complete button.");
        commonAction.sleepInMiliSecond(2000); //Without this delay, the email can not be sent to back end.
        new LoginPage(driver).waitTillLoaderDisappear();
        return this;
    }

    public SignupPage clickSignupBtn() {
        commonAction.clickElement(SIGNUP_BTN);
        logger.info("Clicked on Signup button.");
        new LoginPage(driver).waitTillLoaderDisappear();
        return this;
    }

    public SignupPage fillOutSignupForm(String country, String user, String password, String displayName, String birthday) {
        new HeaderSF(driver).clickUserInfoIcon()
                .clickSignupIcon();
        inputBirthday(birthday);
        selectCountry(country);
        inputMailOrPhoneNumber(user);
        inputPassword(password);
        inputDisplayName(displayName);
        clickSignupBtn();
        return this;
    }

    public SignupPage inputVerificationCode(String verificationCode) throws SQLException {
        commonAction.inputText(OTP, verificationCode);
        logger.info("Input '" + verificationCode + "' into Verification Code field.");
        return this;
    }

    public SignupPage clickResendOTP() {
        commonAction.clickElement(RESEND_OTP);
        logger.info("Clicked on Resend linktext.");
        return this;
    }

    public void clickConfirmBtn() {
        commonAction.clickElement(CONFIRM_OTP);
        logger.info("Clicked on Confirm button.");
        new LoginPage(driver).waitTillLoaderDisappear();
    }

    public SignupPage verifyUsernameExistError(String errMessage) {
        String text = commonAction.getText(USEREXIST_ERROR);
        soft.assertEquals(text, errMessage, "[Signup][Username already exists] Message does not match.");
        logger.info("verifyUsernameExistError completed");
        return this;
    }

    public SignupPage verifyEmailOrPhoneNumberError(String errMessage) {
        String text = commonAction.getText(USER_ERROR);
        soft.assertEquals(text, errMessage, "[Signup][Email or Phone Number] Message does not match.");
        logger.info("verifyEmailOrPhoneNumberError completed");
        return this;
    }

    public SignupPage verifyPasswordError(String errMessage) {
        String text = commonAction.getText(PASSWORD_ERROR);
        soft.assertEquals(text, errMessage, "[Signup][Password] Message does not match.");
        logger.info("verifyPasswordError completed");
        return this;
    }

    public SignupPage verifyVerificationCodeError(String errMessage) {
        String text = commonAction.getText(WRONG_CODE_ERROR);
        soft.assertEquals(text, errMessage, "[Signup][Wrong Verification Code] Message does not match.");
        logger.info("verifyVerificationCodeError completed");
        return this;
    }

    public void completeVerify() {
        soft.assertAll();
    }

    public SignupPage navigate(String domain) {
        commonAction.navigateToURL(domain);
        return this;
    }

    public void signUpWithEmail(String country, String userName, String passWord, String displayName, String birthday) throws SQLException {
        fillOutSignupForm(country, userName, passWord, displayName, birthday);
        mailnesia = new Mailnesia(driver);
        String verifyCode = mailnesia.navigateToMailAndGetVerifyCode(userName);
        inputVerificationCode(verifyCode);
        clickConfirmBtn();
        commonAction.sleepInMiliSecond(1000);
    }

    public void signUpWithPhoneNumber(String country, String userName, String passWord, String displayName, String birthday) throws SQLException {
        fillOutSignupForm(country, userName, passWord, displayName, birthday);
        String verificationCode = new InitConnection().getActivationKey(countryCode + ":" + userName);
        inputVerificationCode(verificationCode);
        clickConfirmBtn();
        inputEmail(displayName+"@mailnesia.com").clickCompleteBtn();
    }
}
