package web.Dashboard.signup;

import static utilities.links.Links.DOMAIN;
import static utilities.links.Links.SIGNUP_PATH;

import java.sql.SQLException;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;

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

    By loc_lblSelectedLanguage = By.cssSelector(".sign-up-widget__changeLanguage-selected");
    By loc_lnkEnglish = By.cssSelector(".sign-up-widget__changeLanguage-english");
    By loc_lnkVietnamese = By.cssSelector(".sign-up-widget__changeLanguage:nth-of-type(2)");
    By loc_txtUsername = By.cssSelector("#username");
    By loc_txtPassword = By.cssSelector("#password");
    By loc_txtReferralCode = By.cssSelector("#refCode");
    By loc_btnSignup = By.cssSelector("button.uik-btn__iconRight");
    By loc_btnBack = By.cssSelector(".btn-back");
    By loc_btnComplete = By.cssSelector(".btn-next");
    By loc_lnkLogout = By.xpath("//div[contains(@class,'package-steps')]/following-sibling::*/a");
    By loc_ddlCountry = By.cssSelector(".phone-code div.uik-select__valueRenderedWrapper");
    By loc_lstCountry = By.cssSelector(".uik-select__optionContent .phone-option");
    By loc_txtStoreName = By.id("nameStore");
    By loc_txtStoreURL = By.id("url");
    By loc_txtStorePhone = By.id("contactNumber");
    By loc_txtStoreMail = By.id("email");
    By loc_txtPickupAddress = By.id("pickupAddress");
    By loc_txtSecondaryPickupAddress = By.id("pickupAddress2");
    By loc_ddlCountryAtWizardScreen = By.id("countryCode");
    By loc_txtCity = By.id("cityName");
    By loc_dldProvince = By.id("cityCode");
    By loc_dldDistrict = By.id("districtCode");
    By loc_dldWard = By.id("wardCode");
    By loc_txtZipCode = By.id("zipCode");
    By loc_dldCurrency = By.id("currencyCode");
    By loc_dldStoreDefaultLanguage = By.id("country");
    By loc_txtVerificationCode = By.cssSelector("#verifyCode");
    By loc_btnConfirmOTP = By.cssSelector(".btn-confirm");
    By loc_lnkResendOTP = By.cssSelector(".resend-otp a");
    By loc_dlgFacebook = By.id("fb-root");
    By loc_lblSignupFailError = By.cssSelector(".alert__wrapper");
    By loc_lblSignupScreen = By.cssSelector(".step1-page__wrapper");
    By loc_lblVerificationCodeScreen = By.cssSelector(".modal-content");
    By loc_lblWizardScreen = By.cssSelector(".wizard-layout__title");
    
    public SignupPage navigate() {
    	String url = DOMAIN + SIGNUP_PATH;
        driver.get(url);
        logger.info("Navigated to '%s'.".formatted(url));
        return this;
    }
    
    public SignupPage navigate(String link) {
    	String url = DOMAIN + link;
        driver.get(url);
        logger.info("Navigated to '%s'.".formatted(url));
    	return this;
    }

	/**
	 * <p>
	 * Get current display language of Dashboard at Sign-up screen
	 * <p>
	 * @return Dashboard's current display language
	 * It can be one of the following: Tiếng anh/English/Tiếng việt/Tiếng Việt
	 */
	public String getDisplayLanguage() {
		String displayLanguage = commonAction.getText(loc_lblSelectedLanguage);
		logger.info("Retrieved current display language '%s'.".formatted(displayLanguage));
		return displayLanguage;
	}      
    
	/**
	 * <p> Change language of Dashboard at Sign-up screen <p>
	 * Example: selectDisplayLanguage("ENG")
	 * @param language the desired language - either VIE or ENG
	 */	
	public SignupPage selectDisplayLanguage(String language) throws Exception {
		for (int i=0; i<3; i++) {
			switch (language.toUpperCase()) {
			case "ENG":
				commonAction.click(loc_lnkEnglish);
				commonAction.sleepInMiliSecond(1000);
				//Make sure the language is selected
				if (!commonAction.getAttribute(loc_lnkEnglish, "class").contains("-selected")) continue;
				break;
			case "VIE":
				commonAction.click(loc_lnkVietnamese);
				commonAction.sleepInMiliSecond(1000);
				//Make sure the language is selected
				if (!commonAction.getAttribute(loc_lnkVietnamese, "class").contains("-selected")) continue;
				break;
			default:
				throw new Exception("Input value does not match any of the accepted values: VIE/ENG");
			}			
		}
	logger.info("Selected display language '%s'.".formatted(language));
	return this;
	}    
    
    public SignupPage selectCountry(String country) {
    	commonAction.click(loc_ddlCountry);
    	if (country.contentEquals("rd")) {
    		commonAction.sleepInMiliSecond(500);
    		int randomNumber = new Random().nextInt(0, commonAction.getElements(loc_lstCountry).size());
    		commonAction.getElements(loc_lstCountry).get(randomNumber).click();
    	} else {
    		commonAction.sleepInMiliSecond(500);
    		driver.findElement(By.xpath("//*[@class='uik-select__optionList']//div[@class='phone-option']/div[text()=\"%s\"]".formatted(country))).click();
    	} 
    	logger.info("Selected country: " + country);
    	return this;
    }

    public SignupPage selectCountryToSetUpShop(String country) {
    	String selectedOption;
    	if (country.contentEquals("rd")) {
    		int optionCount = commonAction.waitTillSelectDropdownHasData(commonAction.getElement(loc_ddlCountryAtWizardScreen));
    		int randomNumber = new Random().nextInt(1, optionCount);
			selectedOption = commonAction.selectByIndex(commonAction.getElement(loc_ddlCountryAtWizardScreen), randomNumber);
    	} else {
    		selectedOption = commonAction.selectByVisibleText(commonAction.getElement(loc_ddlCountryAtWizardScreen), country);
    	}        	
    	logger.info("Selected country: " + selectedOption);
    	return this;
    }
    
    public String selectCurrency(String currency) {
    	String selectedOption;
    	if (currency.contentEquals("rd")) {
    		int optionCount = commonAction.waitTillSelectDropdownHasData(commonAction.getElement(loc_dldCurrency));
    		int randomNumber = new Random().nextInt(1, optionCount);
        	selectedOption = commonAction.selectByIndex(commonAction.getElement(loc_dldCurrency), randomNumber);
    	} else {
    		selectedOption = commonAction.selectByVisibleText(commonAction.getElement(loc_dldCurrency), currency);
    	}        	
    	logger.info("Selected currency: " + selectedOption);
    	return selectedOption;
    }
    
    public SignupPage selectLanguage(String language) {
    	String selectedOption;
    	if (language.contentEquals("rd")) {
    		int optionCount = commonAction.waitTillSelectDropdownHasData(commonAction.getElement(loc_dldStoreDefaultLanguage));
    		int randomNumber = new Random().nextInt(1, optionCount);
        	selectedOption = commonAction.selectByIndex(commonAction.getElement(loc_dldStoreDefaultLanguage), randomNumber);
    	} else {
    		selectedOption = commonAction.selectByVisibleText(commonAction.getElement(loc_dldStoreDefaultLanguage), language);
    	}     	
    	logger.info("Selected language: " + selectedOption);
    	return this;
    }
    
    public SignupPage inputStoreName(String storeName) {
    	commonAction.sendKeys(loc_txtStoreName, storeName);
    	logger.info("Input '" + storeName + "' into Store Name field.");
    	return this;
    }
    
    public SignupPage inputStoreURL(String storeURL) {
    	commonAction.sendKeys(loc_txtStoreURL, storeURL);
    	logger.info("Input '" + storeURL + "' into Store URL field.");
    	return this;
    }
    
    public SignupPage inputStorePhone(String phone) {
    	commonAction.sendKeys(loc_txtStorePhone, phone);
    	logger.info("Input '" + phone + "' into Phone field.");
    	return this;
    }
    
    public SignupPage inputStoreMail(String mail) {
    	commonAction.sendKeys(loc_txtStoreMail, mail);
    	logger.info("Input '" + mail + "' into Mail field.");
    	return this;
    }
    
    public SignupPage inputPickupAddress(String address) {
    	commonAction.sendKeys(loc_txtPickupAddress, address);
    	logger.info("Input '" + address + "' into Pickup Address field.");
    	return this;
    }
    
    public SignupPage inputSecondPickupAddress(String address) {
    	commonAction.sendKeys(loc_txtSecondaryPickupAddress, address);
    	logger.info("Input '" + address + "' into Second Pickup Address field.");
    	return this;
    }
    
    public SignupPage inputCity(String city) {
    	commonAction.sendKeys(loc_txtCity, city);
    	logger.info("Input '" + city + "' into City field.");
        return this;
    }     
    
    public SignupPage selectProvince(String province) {
    	String selectedOption;
    	if (province.contentEquals("rd")) {
    		int optionCount = commonAction.waitTillSelectDropdownHasData(commonAction.getElement(loc_dldProvince));
    		int randomNumber = new Random().nextInt(1, optionCount);
        	selectedOption = commonAction.selectByIndex(commonAction.getElement(loc_dldProvince), randomNumber);
    	} else {
    		selectedOption = commonAction.selectByVisibleText(commonAction.getElement(loc_dldProvince), province);
    	}      	
    	logger.info("Selected state/province: " + selectedOption);
    	return this;
    }
    
    public SignupPage selectDistrict(String district) {
    	String selectedOption;
    	if (district.contentEquals("rd")) {
    		int optionCount = commonAction.waitTillSelectDropdownHasData(commonAction.getElement(loc_dldDistrict));
    		int randomNumber = new Random().nextInt(1, optionCount);
        	selectedOption = commonAction.selectByIndex(commonAction.getElement(loc_dldDistrict), randomNumber);
    	} else {
    		selectedOption = commonAction.selectByVisibleText(commonAction.getElement(loc_dldDistrict), district);
    	}    	
    	logger.info("Selected district: " + selectedOption);
    	return this;
    }

    public SignupPage selectWard(String ward) {
    	String selectedOption;
    	if (ward.contentEquals("rd")) {
    		int optionCount = commonAction.waitTillSelectDropdownHasData(commonAction.getElement(loc_dldWard));
    		int randomNumber = new Random().nextInt(1, optionCount);
        	selectedOption = commonAction.selectByIndex(commonAction.getElement(loc_dldWard), randomNumber);
    	} else {
    		selectedOption = commonAction.selectByVisibleText(commonAction.getElement(loc_dldWard), ward);
    	}
    	logger.info("Selected ward: " + selectedOption);
    	return this;
    }        
    
    public SignupPage inputZipCode(String zipCode) {
    	commonAction.sendKeys(loc_txtZipCode, zipCode);
    	logger.info("Input '" + zipCode + "' into Zipcode field.");
        return this;
    }   
    
    public SignupPage clickCompleteBtn() {
    	commonAction.removeFbBubble();
    	logger.info("Hid Facebook bubble."); 
    	commonAction.click(loc_btnComplete);
    	logger.info("Clicked on Complete button.");
    	return this;
    }
    
    public SignupPage clickLogout() {
    	commonAction.click(loc_lnkLogout);
    	logger.info("Clicked on Logout linktext.");        
    	return this;
    }
    
    public SignupPage clickLogoutByJS() {
    	commonAction.clickJS(loc_lnkLogout);
    	logger.info("Clicked on Logout linktext.");        
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
    
    public SignupPage inputReferralCode(String code) {
    	commonAction.sendKeys(loc_txtReferralCode, code);
    	logger.info("Input '" + code + "' into Referral Code field.");
    	return this;
    }

    public SignupPage clickSignupBtn() {
    	commonAction.click(loc_btnSignup);
    	logger.info("Clicked on Signup button.");        
        return this;
    }

    public SignupPage fillOutSignupForm(String country, String user, String password, String referralCode) {
    	selectCountry(country);
    	inputMailOrPhoneNumber(user);
    	inputPassword(password);
    	inputReferralCode(referralCode);
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
    }

	public void setupShop(String username, String storeName, String url, String country, String currency,
			String storeLanguage, String contact, String pickupAddress, String secondPickupAddress, String province,
			String district, String ward, String city, String zipCode) {
		inputStoreName(storeName);
		if (!url.isEmpty()) {
			inputStoreURL(url);
		}
		if (!country.isEmpty()) {
			selectCountryToSetUpShop(country);
		}
		if (!currency.isEmpty()) {
			selectCurrency(currency);
		}
		if (!storeLanguage.isEmpty()) {
			selectLanguage(storeLanguage);
		}
		if (!contact.isEmpty()) {
			if (username.matches("\\d+")) {
				inputStoreMail(contact);
			} else {
				inputStorePhone(contact);
			}			
		}
		inputPickupAddress(pickupAddress).selectProvince(province);
		if (!country.contentEquals("Vietnam")) {
			inputSecondPickupAddress(secondPickupAddress).inputCity(city).inputZipCode(zipCode);
		} else {
			selectDistrict(district).selectWard(ward);
		}
		clickCompleteBtn();
	}    
    
    public SignupPage verifyUsernameExistError(String signupLanguage) throws Exception {
    	String text = commonAction.getText(loc_lblSignupFailError, 1);
    	String retrievedMsg = PropertiesUtil.getPropertiesValueByDBLang("signup.screen.error.userExists", signupLanguage);
    	soft.assertEquals(text,retrievedMsg, "[Signup][Username already exists] Message does not match.");
    	logger.info("verifyUsernameExistError completed");
    	return this;
    }
    
    public SignupPage verifyVerificationCodeError(String signupLanguage) throws Exception {
        String text = commonAction.getText(loc_lblSignupFailError);
    	String retrievedMsg = PropertiesUtil.getPropertiesValueByDBLang("signup.screen.error.wrongVerificationCode", signupLanguage);
    	soft.assertEquals(text,retrievedMsg, "[Signup][Wrong Verification Code] Message does not match.");
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
    	String text = "";
    	text = commonAction.getText(loc_lblWizardScreen);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.screenTitle", signupLanguage));
    	text = commonAction.getText(new ByChained(loc_txtStoreName, By.xpath("./parent::*/preceding-sibling::label")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.storeName", signupLanguage));
    	text = commonAction.getAttribute(loc_txtStoreName, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.storeNamePlaceHolder", signupLanguage));
    	text = commonAction.getText(new ByChained(loc_txtStoreURL, By.xpath("./parent::*/parent::*/preceding-sibling::label")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.url", signupLanguage));
    	text = commonAction.getText(new ByChained(loc_ddlCountryAtWizardScreen, By.xpath("./parent::*/preceding-sibling::label")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.country", signupLanguage));
    	text = commonAction.getText(new ByChained(loc_dldCurrency, By.xpath("./parent::*/preceding-sibling::label")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.currency", signupLanguage));
    	text = commonAction.getText(new ByChained(loc_dldStoreDefaultLanguage, By.xpath("./parent::*/preceding-sibling::label")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.storeLanguageTxt", signupLanguage));
    	
		if (!username.matches("\\d+")) {
	    	text = commonAction.getText(new ByChained(loc_txtStorePhone, By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.phone", signupLanguage));
	    	text = commonAction.getAttribute(loc_txtStorePhone, "placeholder");
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.phonePlaceHolder", signupLanguage));
		} else {
	    	text = commonAction.getText(new ByChained(loc_txtStoreMail, By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.mail", signupLanguage));
	    	text = commonAction.getAttribute(loc_txtStoreMail, "placeholder");
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.mailPlaceHolder", signupLanguage));
		}
		
    	text = commonAction.getText(new ByChained(loc_txtPickupAddress, By.xpath("./parent::*/preceding-sibling::label")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.pickupAddress1", signupLanguage));
    	text = commonAction.getAttribute(loc_txtPickupAddress, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.pickupAddress1PlaceHolder", signupLanguage));
    	
		if (!country.contentEquals("Vietnam")) {
	    	text = commonAction.getText(new ByChained(loc_txtSecondaryPickupAddress, By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.pickupAddress2", signupLanguage));
	    	text = commonAction.getAttribute(loc_txtSecondaryPickupAddress, "placeholder");
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.pickupAddress2PlaceHolder", signupLanguage));
	    	text = commonAction.getText(new ByChained(loc_txtCity, By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.city", signupLanguage));
	    	text = commonAction.getText(new ByChained(loc_dldProvince, By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.state", signupLanguage));
	    	text = commonAction.getText(new ByChained(loc_txtZipCode, By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.zipCode", signupLanguage));
		} else {
	    	text = commonAction.getText(new ByChained(loc_dldProvince, By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.province", signupLanguage));
	    	text = commonAction.getText(new ByChained(loc_dldDistrict, By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.district", signupLanguage));
	    	text = commonAction.getText(new ByChained(loc_dldWard, By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.ward", signupLanguage));
		}
    	
		text = commonAction.getText(loc_btnComplete);
		Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.completeBtn", signupLanguage));
    	text = commonAction.getText(loc_btnBack);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.backBtn", signupLanguage));
    	
    	logger.info("verifyTextAtSetupShopScreen completed");
    }    
    
}
