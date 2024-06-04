package web.StoreFront.signup;

import static utilities.links.Links.SF_URL_TIEN;

import java.sql.SQLException;
import java.util.Random;

import api.kibana.KibanaAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import web.StoreFront.GeneralSF;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.login.LoginPage;
import utilities.thirdparty.Mailnesia;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;

public class SignupPage extends GeneralSF {

    final static Logger logger = LogManager.getLogger(SignupPage.class);

	/* Message headers of mails sent to user's mailbox, '%s' should be replaced with shop's name */
	public String SUCCESSFUL_SIGNUP_MESSAGE_VI = "Đăng kí thành công tài khoản trên %s";
	public String SUCCESSFUL_SIGNUP_MESSAGE_EN = "Successfully register acount on %s";
	
	public String VERIFICATION_CODE_MESSAGE_VI = "là mã xác minh tài khoản trên %s của bạn";
	public String VERIFICATION_CODE_MESSAGE_EN = "is code to verify your e-mail address on %s";
	/* ================================================== */
    
    WebDriver driver;
    UICommonAction commonAction;
    Mailnesia mailnesia;

    SoftAssert soft = new SoftAssert();

    public SignupPage(WebDriver driver) {
        super(driver);
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_lblSignupScreen = By.cssSelector("#signup-modal .modal-content");
    By loc_lblVerificationCodeScreen = By.cssSelector("#activate-modal .modal-content");
    By loc_txtUsername = By.cssSelector("#signup-username");
    By loc_txtDisplayUsername = By.cssSelector("#signup-displayName");
    By loc_txtBirthday = By.cssSelector("#signup-dob");
    By loc_txtOptionalEmail = By.cssSelector("#get-email");
    By loc_txtPassword = By.cssSelector("#signup-password");
    By loc_lnkLoginNow = By.cssSelector("#signup-modal [data-target='#login-modal']");
    By loc_lnkSkipEmail = By.id("get-email-skip");
    By loc_btnCompleteEmail = By.cssSelector("#frm-get-email .btn-submit");
    By loc_btnSignup = By.cssSelector("#frm-signup .btn-submit");
    By loc_ddlCountry = By.cssSelector("#signup-country-code");
    By loc_lstCountry = By.cssSelector("#signup-country-code-menu .dropdown-item");
    By loc_txtVerificationCode = By.cssSelector("#activate-code");
    By loc_btnConfirmOTP = By.cssSelector("#frm-activate .btn-submit");
    By loc_lnkResendOTP = By.id("activate-resend-code");
    By loc_lblSignupFailError = By.id("signup-fail");
    By loc_lblUsernameError = By.id("signup-username-error");
    By loc_lblPasswordError = By.id("signup-password-error");
    By loc_lblDisplayNameError = By.id("signup-displayName-error");
    By loc_lblWrongCodeError = By.id("activate-fail");	

    public SignupPage navigate() {
        driver.get(SF_URL_TIEN); //Temporary
        return this;
    }

    public SignupPage selectCountry(String country) {
        commonAction.click(loc_ddlCountry);
        if (country.contentEquals("rd")) {
        	commonAction.sleepInMiliSecond(500);
            int randomNumber = new Random().nextInt(0, commonAction.getElements(loc_lstCountry).size());
            commonAction.getElements(loc_lstCountry).get(randomNumber).click();
        } else {
            driver.findElement(By.xpath("//ul[@id='signup-country-code-menu']//span[text()='%s']".formatted(country))).click();
        }
        logger.info("Selected country: " + country);
        return this;
    }

    public SignupPage inputMailOrPhoneNumber(String user) {
        commonAction.sendKeys(loc_txtUsername, user);
        logger.info("Input '" + user + "' into Username field.");
        return this;
    }

    public SignupPage inputPassword(String password) {
        commonAction.sendKeys(loc_txtPassword, password);
        logger.info("Input '" + password + "' into Password field.");
        return this;
    }

    public SignupPage inputDisplayName(String name) {
        commonAction.sendKeys(loc_txtDisplayUsername, name);
        logger.info("Input '" + name + "' into Display Name field.");
        return this;
    }

    public SignupPage inputBirthday(String date) {
        commonAction.sendKeys(loc_txtBirthday, date);
        logger.info("Input '" + date + "' into Birthday field.");
        return this;
    }

    public SignupPage inputEmail(String mail) {
        commonAction.sendKeys(loc_txtOptionalEmail, mail);
        logger.info("Input '" + mail + "' into Email field.");
        return this;
    }

    public SignupPage clickLater() {
        commonAction.click(loc_lnkSkipEmail);
        logger.info("Clicked on 'Later' link text to skip inputing email.");
        new GeneralSF(driver).waitTillLoaderDisappear();
        return this;
    }    
    
    public SignupPage clickCompleteBtn() {
        commonAction.click(loc_btnCompleteEmail);
        logger.info("Clicked on Complete button.");
        commonAction.sleepInMiliSecond(2000); //Without this delay, the email can not be sent to back end.
        new GeneralSF(driver).waitTillLoaderDisappear();
        return this;
    }

    public SignupPage clickSignupBtn() {
        commonAction.click(loc_btnSignup);
        logger.info("Clicked on Signup button.");
        new GeneralSF(driver).waitTillLoaderDisappear();
        return this;
    }
    
    public LoginPage clickLoginNow() {
    	commonAction.click(loc_lnkLoginNow);
    	logger.info("Clicked on 'Login Now' link text.");
    	return new LoginPage(driver);
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
        commonAction.sendKeys(loc_txtVerificationCode, verificationCode);
        logger.info("Input '" + verificationCode + "' into Verification Code field.");
        return this;
    }

    public SignupPage clickResendOTP() {
        commonAction.click(loc_lnkResendOTP);
        logger.info("Clicked on Resend linktext.");
        return this;
    }

    public void clickConfirmBtn() {
        commonAction.click(loc_btnConfirmOTP);
        logger.info("Clicked on Confirm button.");
        new GeneralSF(driver).waitTillLoaderDisappear();
    }

    public SignupPage verifyUsernameExistError(String errMessage) {
        String text = commonAction.getText(loc_lblSignupFailError);
        soft.assertEquals(text, errMessage, "[Signup][Username already exists] Message does not match.");
        logger.info("verifyUsernameExistError completed");
        return this;
    }

    public SignupPage verifyEmailOrPhoneNumberError(String errMessage) {
        String text = commonAction.getText(loc_lblUsernameError);
        soft.assertEquals(text, errMessage, "[Signup][Email or Phone Number] Message does not match.");
        logger.info("verifyEmailOrPhoneNumberError completed");
        return this;
    }

    public SignupPage verifyPasswordError(String errMessage) {
        String text = commonAction.getText(loc_lblPasswordError);
        soft.assertEquals(text, errMessage, "[Signup][Password] Message does not match.");
        logger.info("verifyPasswordError completed");
        return this;
    }
    
    public SignupPage verifyDisplayNameError(String errMessage) {
    	String text = commonAction.getText(loc_lblDisplayNameError);
    	soft.assertEquals(text, errMessage, "[Signup][Display Name] Message does not match.");
    	logger.info("verifyDisplayNameError completed");
    	return this;
    }

    public SignupPage verifyVerificationCodeError(String errMessage) {
        String text = commonAction.getText(loc_lblWrongCodeError);
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
    public SignupPage navigateToSignUp(String domain) {
        commonAction.navigateToURL(domain);
        commonAction.sleepInMiliSecond(1000);
        new HeaderSF(driver).clickUserInfoIcon()
                .clickSignupIcon();
        return this;
    }
    public void signUpWithEmail(String country, String userName, String passWord, String displayName, String birthday) throws SQLException {
        onlyFillOutSignupForm(country, userName, passWord, displayName, birthday);
        String verifyCode =  new InitConnection().getActivationKey(userName);
        inputVerificationCode(verifyCode);
        clickConfirmBtn();
        commonAction.sleepInMiliSecond(1000);
    }

    public void signUpWithPhoneNumber(String country, String userName, String passWord, String displayName, String birthday) throws SQLException {
        onlyFillOutSignupForm(country, userName, passWord, displayName, birthday);
        String phoneCode = new DataGenerator().getPhoneCode(country);
        String verificationCode = new KibanaAPI().getKeyFromKibana(phoneCode+":"+userName,"activationKey");
//                new InitConnection().getActivationKey(new DataGenerator().getPhoneCode(country) + ":" + userName);
        inputVerificationCode(verificationCode);
        clickConfirmBtn();
        inputEmail(displayName+"@mailnesia.com").clickCompleteBtn();
    }
    public SignupPage onlyFillOutSignupForm(String country, String user, String password, String displayName, String birthday) {
        if(!birthday.isEmpty()) inputBirthday(birthday);
        selectCountry(country);
        inputMailOrPhoneNumber(user);
        inputPassword(password);
        inputDisplayName(displayName);
        clickSignupBtn();
        return this;
    }
    
    public void verifyTextAtSignupScreen() throws Exception {
        String text = commonAction.getText(loc_lblSignupScreen);
        Assert.assertEquals(text, PropertiesUtil.getPropertiesValueBySFLang("signup.screen.text"));
        logger.info("verifyTextAtSignupScreen completed");
    }    

    public void verifyTextAtVerificationCodeScreen(String username) throws Exception {
    	String text = commonAction.getText(loc_lblVerificationCodeScreen);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueBySFLang("signup.verificationCode.text").formatted(username));
    	logger.info("verifyTextAtVerificationCodeScreen completed");
    }       
    
}
