package web.Dashboard.signup;

import static utilities.links.Links.DOMAIN;
import static utilities.links.Links.DOMAIN_BIZ;
import static utilities.links.Links.SIGNUP_PATH;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import utilities.api.thirdparty.KibanaAPI;
import utilities.commons.UICommonAction;
import utilities.enums.DisplayLanguage;
import utilities.enums.Domain;
import utilities.model.dashboard.setupstore.SetupStoreDG;
import utilities.utils.PropertiesUtil;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;

public class SignupPage {

	final static Logger logger = LogManager.getLogger(SignupPage.class);
	
    WebDriver driver;
    UICommonAction commonAction;
    SignupPageElement locator;

    /**
     * Domain defaults to VN. Use the object's constructor to override it when necessary
     */
    Domain domain = Domain.VN;    
    
    public SignupPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
        locator = new SignupPageElement();
    }
    public SignupPage(WebDriver driver, Domain domain) {
        this(driver);
        this.domain = domain;
    }
 
    /**
     * Navigates to Sign-up screen by URL
     */
    public SignupPage navigate() {
    	
    	var url = switch (domain) {
	        case VN -> DOMAIN + SIGNUP_PATH;
	        case BIZ -> DOMAIN_BIZ + SIGNUP_PATH;
	        default -> throw new IllegalArgumentException("Unexpected value: " + domain);
    	};
    	
    	driver.get(url);
    	logger.info("Navigated to: {}", url);
        return this;
    }    

    /**
     * Changes display language. Works on domain .vn only
     * @param lang
     * @return
     */
    public SignupPage changeDisplayLanguage(DisplayLanguage lang) {
    	
    	if (!List.of(DisplayLanguage.values()).contains(lang)) throw new IllegalArgumentException("Unexpected value: " + lang);
    	
    	if (domain.equals(Domain.VN)) selectDisplayLanguage(lang);
    	
    	return this;
    }   	
	
    public boolean isLanguageDropdownDisplayed() {
		boolean isDisplayed = !commonAction.getElements(LoginPage.loc_ddlLanguage).isEmpty();
		logger.info("Is Display Language Dropdown displayed: {}", isDisplayed);
		return isDisplayed;
    }    

	public SignupPage selectDisplayLanguage(DisplayLanguage lang) {
		//Temporarily use the same function from LoginPage. Will think of a better way to handle this
		new LoginPage(driver, domain).selectDisplayLanguage(lang);
		return this;
	}    
    public SignupPage selectCountry(String country) {
    	commonAction.waitVisibilityOfElementLocated(locator.loc_ddlPhoneCode); //Implicitly means the dropdown has a default value and ready for further actions. Reason #1
    	commonAction.click(locator.loc_ddlCountry);
    	commonAction.click(By.xpath(locator.loc_ddvCountry.formatted(country)));
    	logger.info("Selected country: " + country);    	
    	return this;
    }
    public SignupPage inputUsername(String user) {
    	commonAction.sendKeys(locator.loc_txtUsername, user);
    	logger.info("Input Username: {}", user);
        return this;
    }
    public SignupPage inputPassword(String password) {
    	commonAction.sendKeys(locator.loc_txtPassword, password);
    	logger.info("Input Password: {}", password);
        return this;
    }
    public SignupPage clickSignupBtn() {
    	commonAction.click(locator.loc_btnSignup);
    	logger.info("Clicked Signup button.");        
        return this;
    }
    public SignupPage fillOutSignupForm(String country, String user, String password) {
    	selectCountry(country);
    	inputUsername(user);
    	inputPassword(password);
    	clickSignupBtn();
    	return this;
    }
    public SignupPage fillOutSignupForm(SetupStoreDG store) {
        return fillOutSignupForm(store.getCountry(), store.getUsername(), store.getPassword());
    }
    
    public SignupPage inputVerificationCode(String verificationCode) {
    	commonAction.sendKeys(locator.loc_txtVerificationCode, verificationCode);
    	logger.info("Input Verification Code: {}", verificationCode);
        return this;
    }
    public SignupPage clickResendOTP() {
    	commonAction.click(locator.loc_lnkResendOTP);
    	logger.info("Clicked Resend linktext.");    
    	new HomePage(driver).getToastMessage(); //Wait a little before getting OTP later on
        return this;
    }
    public SetUpStorePage clickConfirmOTPBtn() {
    	commonAction.click(locator.loc_btnConfirm);
    	logger.info("Clicked Confirm OTP button.");
    	return new SetUpStorePage(driver);
    }
    /**
     * Retrieve verification code for a username then input it to Verification Code field
     * @param username Eg. "tv@mailnesia.com" or "+84:0831234567"
     */
    public SetUpStorePage provideVerificationCode(String username) {
    	inputVerificationCode(new KibanaAPI().getKeyFromKibana(username, "activationKey"));
    	return clickConfirmOTPBtn();
    }    
    public SetUpStorePage provideVerificationCode(SetupStoreDG store) {
    	String formattedUsername = store.getUsername().matches("\\d+") ? "%s:%s".formatted(store.getPhoneCode(), store.getUsername()) : store.getUsername();
    	return provideVerificationCode(formattedUsername);
    }    
    
	public String getUsernameExistError() {
		String text = commonAction.getText(locator.loc_lblSignupFailError);
		logger.info("Retrieve Username Exists error: {}", text);
		return text;
	}
    public SignupPage verifyUsernameExistError(String signupLanguage) throws Exception {
    	String retrievedMsg = PropertiesUtil.getPropertiesValueByDBLang("signup.screen.error.userExists", signupLanguage);
    	Assert.assertEquals(getUsernameExistError(),retrievedMsg, "Username exists error");
    	logger.info("verifyUsernameExistError completed");
    	return this;
    }
    
    public String getVerificationCodeError() {
    	String text = commonAction.getText(locator.loc_lblInvalidFeedback);
    	logger.info("Retrieve Verification Code error: {}", text);
    	return text;
    }
    public SignupPage verifyVerificationCodeError(DisplayLanguage language) throws Exception {
    	String retrievedMsg = PropertiesUtil.getPropertiesValueByDBLang("signup.screen.error.wrongVerificationCode", language.name());
    	Assert.assertEquals(getVerificationCodeError(), retrievedMsg, "Verification Code error");
    	logger.info("verifyVerificationCodeError completed");
    	return this;
    }
    //Will be eradicated
    public SignupPage verifyVerificationCodeError(String signupLanguage) throws Exception {
    	String retrievedMsg = PropertiesUtil.getPropertiesValueByDBLang("signup.screen.error.wrongVerificationCode", signupLanguage);
    	Assert.assertEquals(getVerificationCodeError(), retrievedMsg, "Verification Code error");
        logger.info("verifyVerificationCodeError completed");
        return this;
    }

    public void verifyTextAtSignupScreen(String signupLanguage) throws Exception {
        String text = commonAction.getText(locator.loc_lblSignupScreen);
        Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.screen.text", signupLanguage));
        logger.info("verifyTextAtSignupScreen completed");
    }    
    
    public void verifyTextAtVerificationCodeScreen(String username, String signupLanguage) throws Exception {
    	String text = commonAction.getText(locator.loc_lblVerificationCodeScreen);
    	String subText = "";
    	if (!username.matches("\\d+")) subText = username + "\n";
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.verificationCode.text", signupLanguage).formatted(subText));
    	logger.info("verifyTextAtVerificationCodeScreen completed");
    }    
    
    public void verifyTextAtSetupShopScreen(String username, String country, String signupLanguage) throws Exception {
//    	String text = "";
//    	text = commonAction.getText(locator.loc_lblWizardScreen);
//    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.screenTitle", signupLanguage));
//    	text = commonAction.getText(new ByChained(locator.loc_txtStoreName, By.xpath("./parent::*/preceding-sibling::label")));
//    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.storeName", signupLanguage));
//    	text = commonAction.getAttribute(locator.loc_txtStoreName, "placeholder");
//    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.storeNamePlaceHolder", signupLanguage));
//    	text = commonAction.getText(new ByChained(locator.loc_txtStoreURL, By.xpath("./parent::*/parent::*/preceding-sibling::label")));
//    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.url", signupLanguage));
//    	text = commonAction.getText(new ByChained(locator.loc_ddlCountryAtWizardScreen, By.xpath("./parent::*/preceding-sibling::label")));
//    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.country", signupLanguage));
//    	text = commonAction.getText(new ByChained(locator.loc_dldCurrency, By.xpath("./parent::*/preceding-sibling::label")));
//    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.currency", signupLanguage));
//    	text = commonAction.getText(new ByChained(locator.loc_dldStoreDefaultLanguage, By.xpath("./parent::*/preceding-sibling::label")));
//    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.storeLanguageTxt", signupLanguage));
//    	
//		if (!username.matches("\\d+")) {
//	    	text = commonAction.getText(new ByChained(locator.loc_txtStorePhone, By.xpath("./parent::*/preceding-sibling::label")));
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.phone", signupLanguage));
//	    	text = commonAction.getAttribute(locator.loc_txtStorePhone, "placeholder");
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.phonePlaceHolder", signupLanguage));
//		} else {
//	    	text = commonAction.getText(new ByChained(locator.loc_txtStoreMail, By.xpath("./parent::*/preceding-sibling::label")));
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.mail", signupLanguage));
//	    	text = commonAction.getAttribute(locator.loc_txtStoreMail, "placeholder");
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.mailPlaceHolder", signupLanguage));
//		}
//		
//    	
//		if (!country.contentEquals("Vietnam")) {
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.pickupAddress2PlaceHolder", signupLanguage));
//	    	text = commonAction.getText(new ByChained(locator.loc_txtCity, By.xpath("./parent::*/preceding-sibling::label")));
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.city", signupLanguage));
//	    	text = commonAction.getText(new ByChained(locator.loc_dldProvince, By.xpath("./parent::*/preceding-sibling::label")));
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.state", signupLanguage));
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.zipCode", signupLanguage));
//		} else {
//	    	text = commonAction.getText(new ByChained(locator.loc_dldProvince, By.xpath("./parent::*/preceding-sibling::label")));
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.province", signupLanguage));
//	    	text = commonAction.getText(new ByChained(locator.loc_dldDistrict, By.xpath("./parent::*/preceding-sibling::label")));
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.district", signupLanguage));
//	    	text = commonAction.getText(new ByChained(locator.loc_dldWard, By.xpath("./parent::*/preceding-sibling::label")));
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.ward", signupLanguage));
//		}
//    	
//		text = commonAction.getText(locator.loc_btnComplete);
//		Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.completeBtn", signupLanguage));
//    	text = commonAction.getText(locator.loc_btnBack);
//    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.backBtn", signupLanguage));
//    	
//    	logger.info("verifyTextAtSetupShopScreen completed");
    }    
    
}
