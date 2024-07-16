package web.Dashboard.signup;

import static utilities.links.Links.DOMAIN;
import static utilities.links.Links.SIGNUP_PATH;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import api.kibana.KibanaAPI;
import utilities.commons.UICommonAction;
import utilities.model.dashboard.setupstore.SetupStoreDG;
import utilities.utils.PropertiesUtil;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;

public class SignupPage {

	final static Logger logger = LogManager.getLogger(SignupPage.class);
	
	/* Message headers of mails sent to seller's mailbox */
	public String WELCOME_MESSAGE_VI = "Chào mừng bạn đến với GoSell";
	public String WELCOME_MESSAGE_EN = "Welcome to GoSell";
	
	public String SUCCESSFUL_SIGNUP_MESSAGE_VI = "Đăng ký thành công tài khoản GoSell";
	public String SUCCESSFUL_SIGNUP_MESSAGE_EN = "Successful GoSell registration";
	
	public String VERIFICATION_CODE_MESSAGE_VI = "là mã xác minh tài khoản GoSell của bạn";
	public String VERIFICATION_CODE_MESSAGE_EN = "is your GoSell's verification code";
	/* ================================================== */
	
    WebDriver driver;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public SignupPage(WebDriver driver) {
        this.driver = driver;
        commonAction = new UICommonAction(driver);
    }

    By loc_ddlCountry = By.cssSelector(".input-field.phone-code .select-country__input-container input");
    By loc_ddlPhoneCode = By.cssSelector(".input-field.phone-code .option .code");
    
    By loc_txtUsername = By.id("username");
    By loc_txtPassword = By.id("password");
    By loc_txtReferralCode = By.id("refCode");
    By loc_btnSignup = By.cssSelector("button.uik-btn__iconRight");
    
    By loc_txtVerificationCode = By.id("verifyCode");
    By loc_lnkResendOTP = By.cssSelector(".resend-otp span.send-code");
    By loc_lblInvalidFeedback = By.cssSelector(".invalid-feedback");
    static public By loc_btnConfirm = By.cssSelector("button.button-v2.confirm-button"); //Work at verification screen and Setup Store screen
    
    By loc_lnkLogout = By.xpath("//div[contains(@class,'package-steps')]/following-sibling::*/a");
    
    By loc_lblSignupFailError = By.cssSelector(".alert__wrapper");
    By loc_lblSignupScreen = By.cssSelector(".step1-page__wrapper");
    By loc_lblVerificationCodeScreen = By.cssSelector(".modal-content");
    By loc_lblWizardScreen = By.cssSelector(".wizard-layout__title");
 
    
    public SignupPage navigate(String link) {
        driver.get(link);
        logger.info("Navigated to: {}", link);
    	return this;
    }    
    public SignupPage navigate() {
        return navigate(DOMAIN + SIGNUP_PATH);
    }

    public boolean isLanguageDropdownDisplayed() {
		boolean isDisplayed = !commonAction.getElements(LoginPage.loc_ddlLanguage).isEmpty();
		logger.info("Is Display Language Dropdown displayed: {}", isDisplayed);
		return isDisplayed;
    }    
	public SignupPage selectDisplayLanguage(String language) {
		//Temporarily use the same function from LoginPage. Will think of a better way to handle this
		new LoginPage(driver).selectDisplayLanguage(language);
		return this;
	}    
    public SignupPage selectCountry(String country) {
    	commonAction.waitVisibilityOfElementLocated(loc_ddlPhoneCode); //Implicitly means the dropdown has a default value and ready for further actions. Reason #1
    	commonAction.click(loc_ddlCountry);
    	commonAction.click(By.xpath(new SignupPageElement().loc_ddvCountry.formatted(country)));
    	logger.info("Selected country: " + country);    	
    	return this;
    }
    public SignupPage inputUsername(String user) {
    	commonAction.sendKeys(loc_txtUsername, user);
    	logger.info("Input Username: {}", user);
        return this;
    }
    public SignupPage inputPassword(String password) {
    	commonAction.sendKeys(loc_txtPassword, password);
    	logger.info("Input Password: {}", password);
        return this;
    }
    public SignupPage inputReferralCode(String code) {
    	commonAction.sendKeys(loc_txtReferralCode, code);
    	logger.info("Input Referral Code: {}", code);
    	return this;
    }
    public SignupPage clickSignupBtn() {
    	commonAction.click(loc_btnSignup);
    	logger.info("Clicked Signup button.");        
        return this;
    }
    public SignupPage fillOutSignupForm(String country, String user, String password, String referralCode) {
    	selectCountry(country);
    	inputUsername(user);
    	inputPassword(password);
    	inputReferralCode(referralCode);
    	clickSignupBtn();
    	return this;
    }
    public SignupPage fillOutSignupForm(SetupStoreDG store) {
        return fillOutSignupForm(store.getCountry(), store.getUsername(), store.getPassword(), store.getReferralCode());
    }
    
    public SignupPage inputVerificationCode(String verificationCode) {
    	commonAction.sendKeys(loc_txtVerificationCode, verificationCode);
    	logger.info("Input Verification Code: {}", verificationCode);
        return this;
    }
    public SignupPage clickResendOTP() {
    	commonAction.click(loc_lnkResendOTP);
    	logger.info("Clicked Resend linktext.");    
    	new HomePage(driver).getToastMessage(); //Wait a little before getting OTP later on
        return this;
    }
    public SetUpStorePage clickConfirmOTPBtn() {
    	commonAction.click(loc_btnConfirm);
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

    //Remove later
    public SignupPage clickLogout() {
    	commonAction.click(loc_lnkLogout);
    	logger.info("Clicked on Logout linktext.");        
    	return this;
    }
    //Remove later
    public SignupPage clickLogoutByJS() {
    	commonAction.clickJS(loc_lnkLogout);
    	logger.info("Clicked on Logout linktext.");        
    	return this;
    }

    //remove later
	public void setupShop(String username, String storeName, String url, String country, String currency,
			String storeLanguage, String contact, String pickupAddress, String secondPickupAddress, String province,
			String district, String ward, String city, String zipCode) {
//		inputStoreName(storeName);
//		if (!url.isEmpty()) {
//			inputStoreURL(url);
//		}
//		if (!country.isEmpty()) {
//			selectCountryToSetUpShop(country);
//		}
//		if (!currency.isEmpty()) {
//			selectCurrency(currency);
//		}
//		if (!storeLanguage.isEmpty()) {
//			selectLanguage(storeLanguage);
//		}
//		if (!contact.isEmpty()) {
//			if (username.matches("\\d+")) {
//				inputStoreMail(contact);
//			} else {
//				inputStorePhone(contact);
//			}			
//		}
//		inputPickupAddress(pickupAddress).selectProvince(province);
//		if (!country.contentEquals("Vietnam")) {
//			inputSecondPickupAddress(secondPickupAddress).inputCity(city).inputZipCode(zipCode);
//		} else {
//			selectDistrict(district).selectWard(ward);
//		}
//		clickCompleteBtn();
	}    
    
    public SignupPage verifyUsernameExistError(String signupLanguage) throws Exception {
    	String text = commonAction.getText(loc_lblSignupFailError, 1);
    	logger.info("Retrieve Username Exists error: {}", text);
    	String retrievedMsg = PropertiesUtil.getPropertiesValueByDBLang("signup.screen.error.userExists", signupLanguage);
    	soft.assertEquals(text,retrievedMsg, "[Signup][Username already exists] Message does not match.");
    	logger.info("verifyUsernameExistError completed");
    	return this;
    }
    
    public SignupPage verifyVerificationCodeError(String signupLanguage) throws Exception {
        String text = commonAction.getText(loc_lblInvalidFeedback);
        logger.info("Retrieve Verification Code error: {}", text);
    	String retrievedMsg = PropertiesUtil.getPropertiesValueByDBLang("signup.screen.error.wrongVerificationCode", signupLanguage);
    	Assert.assertEquals(text,retrievedMsg, "[Signup][Wrong Verification Code] Message does not match.");
        logger.info("verifyVerificationCodeError completed");
        return this;
    }

    public void completeVerify() {
        soft.assertAll();
    }

    public void verifyTextAtSignupScreen(String signupLanguage) throws Exception {
        String text = commonAction.getText(loc_lblSignupScreen);
        Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.screen.text", signupLanguage));
        logger.info("verifyTextAtSignupScreen completed");
    }    
    
    public void verifyTextAtVerificationCodeScreen(String username, String signupLanguage) throws Exception {
    	String text = commonAction.getText(loc_lblVerificationCodeScreen);
    	String subText = "";
    	if (!username.matches("\\d+")) subText = username + "\n";
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.verificationCode.text", signupLanguage).formatted(subText));
    	logger.info("verifyTextAtVerificationCodeScreen completed");
    }    
    
    public void verifyTextAtSetupShopScreen(String username, String country, String signupLanguage) throws Exception {
//    	String text = "";
//    	text = commonAction.getText(loc_lblWizardScreen);
//    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.screenTitle", signupLanguage));
//    	text = commonAction.getText(new ByChained(loc_txtStoreName, By.xpath("./parent::*/preceding-sibling::label")));
//    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.storeName", signupLanguage));
//    	text = commonAction.getAttribute(loc_txtStoreName, "placeholder");
//    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.storeNamePlaceHolder", signupLanguage));
//    	text = commonAction.getText(new ByChained(loc_txtStoreURL, By.xpath("./parent::*/parent::*/preceding-sibling::label")));
//    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.url", signupLanguage));
//    	text = commonAction.getText(new ByChained(loc_ddlCountryAtWizardScreen, By.xpath("./parent::*/preceding-sibling::label")));
//    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.country", signupLanguage));
//    	text = commonAction.getText(new ByChained(loc_dldCurrency, By.xpath("./parent::*/preceding-sibling::label")));
//    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.currency", signupLanguage));
//    	text = commonAction.getText(new ByChained(loc_dldStoreDefaultLanguage, By.xpath("./parent::*/preceding-sibling::label")));
//    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.storeLanguageTxt", signupLanguage));
//    	
//		if (!username.matches("\\d+")) {
//	    	text = commonAction.getText(new ByChained(loc_txtStorePhone, By.xpath("./parent::*/preceding-sibling::label")));
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.phone", signupLanguage));
//	    	text = commonAction.getAttribute(loc_txtStorePhone, "placeholder");
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.phonePlaceHolder", signupLanguage));
//		} else {
//	    	text = commonAction.getText(new ByChained(loc_txtStoreMail, By.xpath("./parent::*/preceding-sibling::label")));
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.mail", signupLanguage));
//	    	text = commonAction.getAttribute(loc_txtStoreMail, "placeholder");
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.mailPlaceHolder", signupLanguage));
//		}
//		
//    	
//		if (!country.contentEquals("Vietnam")) {
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.pickupAddress2PlaceHolder", signupLanguage));
//	    	text = commonAction.getText(new ByChained(loc_txtCity, By.xpath("./parent::*/preceding-sibling::label")));
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.city", signupLanguage));
//	    	text = commonAction.getText(new ByChained(loc_dldProvince, By.xpath("./parent::*/preceding-sibling::label")));
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.state", signupLanguage));
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.zipCode", signupLanguage));
//		} else {
//	    	text = commonAction.getText(new ByChained(loc_dldProvince, By.xpath("./parent::*/preceding-sibling::label")));
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.province", signupLanguage));
//	    	text = commonAction.getText(new ByChained(loc_dldDistrict, By.xpath("./parent::*/preceding-sibling::label")));
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.district", signupLanguage));
//	    	text = commonAction.getText(new ByChained(loc_dldWard, By.xpath("./parent::*/preceding-sibling::label")));
//	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.ward", signupLanguage));
//		}
//    	
//		text = commonAction.getText(loc_btnComplete);
//		Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.completeBtn", signupLanguage));
//    	text = commonAction.getText(loc_btnBack);
//    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.backBtn", signupLanguage));
//    	
//    	logger.info("verifyTextAtSetupShopScreen completed");
    }    
    
}
