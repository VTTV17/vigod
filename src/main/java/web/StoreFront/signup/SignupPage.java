package web.StoreFront.signup;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import lombok.SneakyThrows;
import utilities.api.thirdparty.KibanaAPI;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.enums.DisplayLanguage;
import utilities.model.dashboard.storefront.BuyerSignupData;
import utilities.utils.PropertiesUtil;
import web.StoreFront.GeneralSF;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.login.LoginPage;

public class SignupPage {

    final static Logger logger = LogManager.getLogger(SignupPage.class);

    WebDriver driver;
    UICommonAction commonAction;
    SignupPageElement locator;

    public SignupPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
        locator = new SignupPageElement();
    }
    
    @SneakyThrows
    public static String localizedEmailAlreadyExistError(DisplayLanguage language) {
    	return PropertiesUtil.getPropertiesValueBySFLang("signup.screen.error.mailExists", language.name());
    }
    @SneakyThrows
    public static String localizedPhoneAlreadyExistError(DisplayLanguage language) {
    	return PropertiesUtil.getPropertiesValueBySFLang("signup.screen.error.phoneExists", language.name());
    }
    @SneakyThrows
    public static String localizedEmptyUsernameError(DisplayLanguage language) {
    	return PropertiesUtil.getPropertiesValueBySFLang("signup.screen.error.emptyUsername", language.name());
    }
    @SneakyThrows
    public static String localizedEmptyPasswordError(DisplayLanguage language) {
    	return PropertiesUtil.getPropertiesValueBySFLang("signup.screen.error.emptyPassword", language.name());
    }
    @SneakyThrows
    public static String localizedEmptyNameError(DisplayLanguage language) {
    	return PropertiesUtil.getPropertiesValueBySFLang("signup.screen.error.emptyDisplayName", language.name());
    }
    @SneakyThrows
    public static String localizedInvalidUsernameError(DisplayLanguage language) {
    	return PropertiesUtil.getPropertiesValueBySFLang("signup.screen.error.invalidUsernameFormat", language.name());
    }
    @SneakyThrows
    public static String localizedWrongVerificationCodeError(DisplayLanguage language) {
    	return PropertiesUtil.getPropertiesValueBySFLang("signup.screen.error.wrongVerificationCode", language.name());
    }

    /**
     * Retrieves the currently selected country
     */
    public String getSelectedCountry() {
    	String selectedCountry = commonAction.getText(locator.loc_ddlCountry).split("\\s*\\+\\d+")[0];
    	logger.info("Retrieved selected country: {}", selectedCountry);
    	return selectedCountry;
    }    
    public SignupPage selectCountry(String country) {
    	
    	if (getSelectedCountry().contentEquals(country)) return this;
    	
        commonAction.click(locator.loc_ddlCountry);
        commonAction.click(By.xpath(locator.loc_ddvCountryByName(country)));
        logger.info("Selected country: " + country);
        return this;
    }

    public SignupPage inputUsername(String user) {
        commonAction.sendKeys(locator.loc_txtUsername, user);
        logger.info("Input '" + user + "' into Username field.");
        return this;
    }

    public SignupPage inputPassword(String password) {
        commonAction.sendKeys(locator.loc_txtPassword, password);
        logger.info("Input '" + password + "' into Password field.");
        return this;
    }

    public SignupPage inputDisplayName(String name) {
        commonAction.sendKeys(locator.loc_txtDisplayUsername, name);
        logger.info("Input '" + name + "' into Display Name field.");
        return this;
    }

    public SignupPage inputBirthday(String date) {
        commonAction.sendKeys(locator.loc_txtBirthday, date);
        logger.info("Input '" + date + "' into Birthday field.");
        return this;
    }

    public SignupPage inputEmail(String mail) {
        commonAction.sendKeys(locator.loc_txtOptionalEmail, mail);
        logger.info("Input '" + mail + "' into Email field.");
        return this;
    }

    public SignupPage clickLater() {
        commonAction.click(locator.loc_lnkSkipEmail);
        logger.info("Clicked on 'Later' link text to skip inputing email.");
        new GeneralSF(driver).waitTillLoaderDisappear();
        return this;
    }    
    
    public SignupPage clickCompleteBtn() {
        commonAction.click(locator.loc_btnCompleteEmail);
        logger.info("Clicked on Complete button.");
        new GeneralSF(driver).waitTillLoaderDisappear();
        return this;
    }

    public SignupPage clickSignupBtn() {
        commonAction.click(locator.loc_btnSignup);
        logger.info("Clicked on Signup button.");
        new GeneralSF(driver).waitTillLoaderDisappear();
        return this;
    }
    
    public LoginPage clickLoginNow() {
    	commonAction.click(locator.loc_lnkLoginNow);
    	logger.info("Clicked 'Login Now' link text.");
    	return new LoginPage(driver);
    }

    public SignupPage fillOutSignupForm(String country, String user, String password, String displayName, String birthday) {
        new HeaderSF(driver).clickUserInfoIcon().clickSignupIcon();
        inputBirthday(birthday);
        selectCountry(country);
        inputUsername(user);
        inputPassword(password);
        inputDisplayName(displayName);
        clickSignupBtn();
        return this;
    }
    public SignupPage fillOutSignupForm(BuyerSignupData data) {
    	return fillOutSignupForm(data.getCountry(), data.getUsername(), data.getPassword(), data.getDisplayName(), data.getBirthday());
    }

    public SignupPage inputVerificationCode(String verificationCode) {
        commonAction.sendKeys(locator.loc_txtVerificationCode, verificationCode);
        logger.info("Input Verification Code: {}", verificationCode);
        return this;
    }

    public SignupPage clickResendOTP() {
        commonAction.click(locator.loc_lnkResendOTP);
        logger.info("Clicked Resend linktext.");
        return this;
    }

    public void clickConfirmBtn() {
        commonAction.click(locator.loc_btnConfirmOTP);
        logger.info("Clicked on Confirm button.");
        new GeneralSF(driver).waitTillLoaderDisappear();
    }

	public String getUsernameExistError() {
		String text = commonAction.getText(locator.loc_lblSignupFailError);
		logger.info("Retrieve Username Exists error: {}", text);
		return text;
	}
    public String getUsernameError() {
    	String text = commonAction.getText(locator.loc_lblUsernameError);
    	logger.info("Retrieve Username error: {}", text);
    	return text;
    }

    public String getPasswordError() {
    	String text = commonAction.getText(locator.loc_lblPasswordError);
    	logger.info("Retrieve Password error: {}", text);
    	return text;
    }
 
    public String getDisplayNameError() {
    	String text = commonAction.getText(locator.loc_lblDisplayNameError);
    	logger.info("Retrieve Display Name error: {}", text);
    	return text;
    }

    public String getVerificationCodeError() {
    	String text = commonAction.getText(locator.loc_lblWrongCodeError);
    	logger.info("Retrieve Verification Code error: {}", text);
    	return text;
    }
    
    public SignupPage navigateToSignUp(String domain) {
        commonAction.navigateToURL(domain);
        UICommonAction.sleepInMiliSecond(1000);
        new HeaderSF(driver).clickUserInfoIcon()
                .clickSignupIcon();
        return this;
    }
    public void signUpWithEmail(String country, String userName, String passWord, String displayName, String birthday) throws SQLException {
        onlyFillOutSignupForm(country, userName, passWord, displayName, birthday);
        String verifyCode =  new KibanaAPI().getKeyFromKibana(userName,"activationKey");
        inputVerificationCode(verifyCode);
        clickConfirmBtn();
        UICommonAction.sleepInMiliSecond(1000);
    }

    public void signUpWithPhoneNumber(String country, String userName, String passWord, String displayName, String birthday) throws SQLException {
        onlyFillOutSignupForm(country, userName, passWord, displayName, birthday);
        String phoneCode = DataGenerator.getPhoneCode(country);
        String verificationCode = new KibanaAPI().getKeyFromKibana(phoneCode+":"+userName,"activationKey");
//                new InitConnection().getActivationKey(new DataGenerator().getPhoneCode(country) + ":" + userName);
        inputVerificationCode(verificationCode);
        clickConfirmBtn();
        inputEmail(displayName+"@mailnesia.com").clickCompleteBtn();
    }
    public SignupPage onlyFillOutSignupForm(String country, String user, String password, String displayName, String birthday) {
        if(!birthday.isEmpty()) inputBirthday(birthday);
        selectCountry(country);
        inputUsername(user);
        inputPassword(password);
        inputDisplayName(displayName);
        clickSignupBtn();
        return this;
    }
    
    public void verifyTextAtSignupScreen() throws Exception {
        String text = commonAction.getText(locator.loc_lblSignupScreen);
        Assert.assertEquals(text, PropertiesUtil.getPropertiesValueBySFLang("signup.screen.text"));
        logger.info("verifyTextAtSignupScreen completed");
    }    

    public void verifyTextAtVerificationCodeScreen(String username) throws Exception {
    	String text = commonAction.getText(locator.loc_lblVerificationCodeScreen);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueBySFLang("signup.verificationCode.text").formatted(username));
    	logger.info("verifyTextAtVerificationCodeScreen completed");
    }       
    
}
