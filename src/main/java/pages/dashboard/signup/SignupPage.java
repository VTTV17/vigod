package pages.dashboard.signup;

import static utilities.links.Links.DOMAIN;
import static utilities.links.Links.SIGNUP_PAGE_TITLE;
import static utilities.links.Links.SIGNUP_PATH;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import utilities.PropertiesUtil;
import utilities.UICommonAction;

public class SignupPage {

	final static Logger logger = LogManager.getLogger(SignupPage.class);
	
	
	public String country;
	public String countryCode;
	public String currency;
	
	/* Message headers of mails sent to seller's mailbox */
	public String WELCOME_MESSAGE_VI = "Chào mừng bạn đến với GoSell";
	public String WELCOME_MESSAGE_EN = "Welcome to GoSell";
	
	public String SUCCESSFUL_SIGNUP_MESSAGE_VI = "Đăng ký thành công tài khoản GoSell";
	public String SUCCESSFUL_SIGNUP_MESSAGE_EN = "Successful GoSell registration";
	
	public String VERIFICATION_CODE_MESSAGE_VI = "là mã xác minh tài khoản GoSell của bạn";
	public String VERIFICATION_CODE_MESSAGE_EN = "is your GoSell's verification code";
	/* ================================================== */
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    SoftAssert soft = new SoftAssert();    
    
    public SignupPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".sign-up-widget__changeLanguage-selected")
    WebElement CURRENT_DISPLAY_LANGUAGE;    

    @FindBy(css = ".sign-up-widget__changeLanguage-english")
    WebElement ENGLISH_LANGUAGE;    

    @FindBy(css = ".sign-up-widget__changeLanguage:nth-of-type(2)")
    WebElement VIETNAMESE_LANGUAGE;
    
    @FindBy(css = "#username")
    WebElement USERNAME;

    @FindBy (css = "#password")
    WebElement PASSWORD;
    
    @FindBy (css = "#refCode")
    WebElement REFERRAL_CODE;

    @FindBy (css = "button.uik-btn__iconRight")
    WebElement SIGNUP_BTN;
    
    @FindBy (css = ".btn-back")
    WebElement BACK_BTN;
    
    @FindBy (css = ".btn-next")
    WebElement COMPLETE_BTN;
    
    @FindBy (xpath = "//div[contains(@class,'package-steps')]/following-sibling::*/a")
    WebElement LOGOUT;
    
    @FindBy (css = "[btntext='Log out'] button.gs-button__green")
    WebElement LOGOUT_BTN;

    @FindBy (css = "div.uik-select__valueWrapper>div>div:nth-child(2)")
    WebElement COUNTRY_CODE;

    @FindBy (css = ".phone-code div.uik-select__valueRenderedWrapper")
    WebElement COUNTRY_DROPDOWN;
    
    @FindBy (id = "nameStore")
    WebElement STORE_NAME;
    
    @FindBy (id = "url")
    WebElement STORE_URL;
    
    @FindBy (id = "contactNumber")
    WebElement STORE_PHONE;
    
    @FindBy (id = "email")
    WebElement STORE_MAIL;
    
    @FindBy (id = "pickupAddress")
    WebElement PICKUP_ADDRESS;
    
    @FindBy (id = "pickupAddress2")
    WebElement SECOND_PICKUP_ADDRESS;
    
    @FindBy (id = "countryCode")
    WebElement COUNTRY_DROPDOWN_SETUP_SHOP;
    
    @FindBy (id = "cityName")
    WebElement CITY;
    
    @FindBy (id = "cityCode")
    WebElement PROVINCE_DROPDOWN;
    
    @FindBy (id = "districtCode")
    WebElement DISTRICT_DROPDOWN;
    
    @FindBy (id = "wardCode")
    WebElement WARD;
    
    @FindBy (id = "zipCode")
    WebElement ZIPCODE;
    
    @FindBy (id = "currencyCode")
    WebElement CURRENCY;
    
    @FindBy (id = "country")
    WebElement STORE_LANGUAGE;

    @FindBy (css = ".uik-select__optionContent .phone-option")
    List<WebElement> COUNTRY_LIST;

    @FindBy (css = "#verifyCode")
    WebElement OTP;

    @FindBy (css = ".btn-confirm")
    WebElement CONFIRM_OTP;

    @FindBy (css = ".resend-otp a")
    WebElement RESEND_OTP;
    
    @FindBy (id = "fb-root")
    WebElement FACEBOOK_BUBBLE;
    
    @FindBy (css = ".alert__wrapper")
    WebElement USEREXIST_ERROR;
    
    @FindBy (css = ".alert__wrapper")
    WebElement WRONG_CODE_ERROR;
    
    @FindBy (css = ".step1-page__wrapper")
    WebElement SIGNUP_SCREEN_TXT;
    
    @FindBy (css = ".modal-content")
    WebElement VERIFICATION_CODE_SCREEN_TXT;
    
    @FindBy (css = ".wizard-layout__title")
    WebElement WIZARD_SCREEN_TITLE;
    
    public SignupPage navigate() {
    	String url = DOMAIN + SIGNUP_PATH;
        driver.get(url);
        wait.until(ExpectedConditions.titleIs(SIGNUP_PAGE_TITLE));
        logger.info("Navigated to '%s'.".formatted(url));
        return this;
    }
    
    public SignupPage navigate(String link) {
    	String url = DOMAIN + link;
        driver.get(url);
//        wait.until(ExpectedConditions.titleIs(SIGNUP_PAGE_TITLE));
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
		String displayLanguage = commonAction.getText(CURRENT_DISPLAY_LANGUAGE);
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
				commonAction.clickElement(ENGLISH_LANGUAGE);
				commonAction.sleepInMiliSecond(1000);
				//Make sure the language is selected
				if (!commonAction.getElementAttribute(ENGLISH_LANGUAGE, "class").contains("-selected")) continue;
				break;
			case "VIE":
				commonAction.clickElement(VIETNAMESE_LANGUAGE);
				commonAction.sleepInMiliSecond(1000);
				//Make sure the language is selected
				if (!commonAction.getElementAttribute(VIETNAMESE_LANGUAGE, "class").contains("-selected")) continue;
				break;
			default:
				throw new Exception("Input value does not match any of the accepted values: VIE/ENG");
			}			
		}
	logger.info("Selected display language '%s'.".formatted(language));
	return this;
	}    
    
    public SignupPage selectCountry(String country) {
    	commonAction.clickElement(COUNTRY_DROPDOWN);
    	if (country.contentEquals("rd")) {
    		commonAction.sleepInMiliSecond(500);
    		int randomNumber = new Random().nextInt(0, COUNTRY_LIST.size());
    		COUNTRY_LIST.get(randomNumber).click();
    	} else {
    		commonAction.sleepInMiliSecond(500);
    		driver.findElement(By.xpath("//*[@class='uik-select__optionList']//div[@class='phone-option']/div[text()=\"%s\"]".formatted(country))).click();
    	} 
    	String[] selectedOption = COUNTRY_DROPDOWN.getText().split("\n");
    	logger.info("Selected country '%s'. Its according code is '%s'.".formatted(selectedOption[0],selectedOption[1]));
    	this.country = selectedOption[0];
    	this.countryCode = selectedOption[1];
    	return this;
    }

    public SignupPage selectCountryToSetUpShop(String country) {
    	String selectedOption;
    	if (country.contentEquals("rd")) {
    		int optionCount = commonAction.waitTillSelectDropdownHasData(COUNTRY_DROPDOWN_SETUP_SHOP);
    		int randomNumber = new Random().nextInt(1, optionCount);
			selectedOption = commonAction.selectByIndex(COUNTRY_DROPDOWN_SETUP_SHOP, randomNumber);
    	} else {
    		selectedOption = commonAction.selectByVisibleText(COUNTRY_DROPDOWN_SETUP_SHOP, country);
    	}        	
    	logger.info("Selected country: " + selectedOption);
    	return this;
    }
    
    public String selectCurrency(String currency) {
    	String selectedOption;
    	if (currency.contentEquals("rd")) {
    		int optionCount = commonAction.waitTillSelectDropdownHasData(CURRENCY);
    		int randomNumber = new Random().nextInt(1, optionCount);
        	selectedOption = commonAction.selectByIndex(CURRENCY, randomNumber);
    	} else {
    		selectedOption = commonAction.selectByVisibleText(CURRENCY, currency);
    	}        	
    	logger.info("Selected currency: " + selectedOption);
    	this.currency = selectedOption;
    	return selectedOption;
    }
    
    public SignupPage selectLanguage(String language) {
    	String selectedOption;
    	if (language.contentEquals("rd")) {
    		int optionCount = commonAction.waitTillSelectDropdownHasData(STORE_LANGUAGE);
    		int randomNumber = new Random().nextInt(1, optionCount);
        	selectedOption = commonAction.selectByIndex(STORE_LANGUAGE, randomNumber);
    	} else {
    		selectedOption = commonAction.selectByVisibleText(STORE_LANGUAGE, language);
    	}     	
    	logger.info("Selected language: " + selectedOption);
    	return this;
    }
    
    public SignupPage inputStoreName(String storeName) {
    	commonAction.inputText(STORE_NAME, storeName);
    	logger.info("Input '" + storeName + "' into Store Name field.");
    	return this;
    }
    
    public SignupPage inputStoreURL(String storeURL) {
    	commonAction.inputText(STORE_URL, storeURL);
    	logger.info("Input '" + storeURL + "' into Store URL field.");
    	return this;
    }
    
    public SignupPage inputStorePhone(String phone) {
    	commonAction.inputText(STORE_PHONE, phone);
    	logger.info("Input '" + phone + "' into Phone field.");
    	return this;
    }
    
    public SignupPage inputStoreMail(String mail) {
    	commonAction.inputText(STORE_MAIL, mail);
    	logger.info("Input '" + mail + "' into Mail field.");
    	return this;
    }
    
    public SignupPage inputPickupAddress(String address) {
    	commonAction.inputText(PICKUP_ADDRESS, address);
    	logger.info("Input '" + address + "' into Pickup Address field.");
    	return this;
    }
    
    public SignupPage inputSecondPickupAddress(String address) {
    	commonAction.inputText(SECOND_PICKUP_ADDRESS, address);
    	logger.info("Input '" + address + "' into Second Pickup Address field.");
    	return this;
    }
    
    public SignupPage inputCity(String city) {
    	commonAction.inputText(CITY, city);
    	logger.info("Input '" + city + "' into City field.");
        return this;
    }     
    
    public SignupPage selectProvince(String province) {
    	String selectedOption;
    	if (province.contentEquals("rd")) {
    		int optionCount = commonAction.waitTillSelectDropdownHasData(PROVINCE_DROPDOWN);
    		int randomNumber = new Random().nextInt(1, optionCount);
        	selectedOption = commonAction.selectByIndex(PROVINCE_DROPDOWN, randomNumber);
    	} else {
    		selectedOption = commonAction.selectByVisibleText(PROVINCE_DROPDOWN, province);
    	}      	
    	logger.info("Selected state/province: " + selectedOption);
    	return this;
    }
    
    public SignupPage selectDistrict(String district) {
    	String selectedOption;
    	if (district.contentEquals("rd")) {
    		int optionCount = commonAction.waitTillSelectDropdownHasData(DISTRICT_DROPDOWN);
    		int randomNumber = new Random().nextInt(1, optionCount);
        	selectedOption = commonAction.selectByIndex(DISTRICT_DROPDOWN, randomNumber);
    	} else {
    		selectedOption = commonAction.selectByVisibleText(DISTRICT_DROPDOWN, district);
    	}    	
    	logger.info("Selected district: " + selectedOption);
    	return this;
    }

    public SignupPage selectWard(String ward) {
    	String selectedOption;
    	if (ward.contentEquals("rd")) {
    		int optionCount = commonAction.waitTillSelectDropdownHasData(WARD);
    		int randomNumber = new Random().nextInt(1, optionCount);
        	selectedOption = commonAction.selectByIndex(WARD, randomNumber);
    	} else {
    		selectedOption = commonAction.selectByVisibleText(WARD, ward);
    	}
    	logger.info("Selected ward: " + selectedOption);
    	return this;
    }        
    
    public SignupPage inputZipCode(String zipCode) {
    	commonAction.inputText(ZIPCODE, zipCode);
    	logger.info("Input '" + zipCode + "' into Zipcode field.");
        return this;
    }   
    
    public SignupPage clickCompleteBtn() {
    	commonAction.hideElement(FACEBOOK_BUBBLE);
    	logger.info("Hid Facebook bubble."); 
    	commonAction.clickElement(COMPLETE_BTN);
    	logger.info("Clicked on Complete button.");
    	return this;
    }
    
    public SignupPage clickLogout() {
    	commonAction.clickElement(LOGOUT);
    	logger.info("Clicked on Logout linktext.");        
    	return this;
    }
    
    public SignupPage clickLogoutByJS() {
    	commonAction.clickElementByJS(LOGOUT);
    	logger.info("Clicked on Logout linktext.");        
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
    
    public SignupPage inputReferralCode(String code) {
    	commonAction.inputText(REFERRAL_CODE, code);
    	logger.info("Input '" + code + "' into Referral Code field.");
    	return this;
    }

    public SignupPage clickSignupBtn() {
    	commonAction.clickElement(SIGNUP_BTN);
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
    	String text = commonAction.getText(USEREXIST_ERROR);
    	String retrievedMsg = PropertiesUtil.getPropertiesValueByDBLang("signup.screen.error.userExists", signupLanguage);
    	soft.assertEquals(text,retrievedMsg, "[Signup][Username already exists] Message does not match.");
    	logger.info("verifyUsernameExistError completed");
    	return this;
    }
    
    public SignupPage verifyVerificationCodeError(String signupLanguage) throws Exception {
        String text = commonAction.getText(WRONG_CODE_ERROR);
    	String retrievedMsg = PropertiesUtil.getPropertiesValueByDBLang("signup.screen.error.wrongVerificationCode", signupLanguage);
    	soft.assertEquals(text,retrievedMsg, "[Signup][Wrong Verification Code] Message does not match.");
        logger.info("verifyVerificationCodeError completed");
        return this;
    }

    public void completeVerify() {
        soft.assertAll();
    }

    public void verifyTextAtSignupScreen(String signupLanguage) throws Exception {
        String text = commonAction.getText(SIGNUP_SCREEN_TXT);
        Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.screen.text", signupLanguage));
        logger.info("verifyTextAtSignupScreen completed");
    }    
    
    public void verifyTextAtVerificationCodeScreen(String username, String signupLanguage) throws Exception {
    	String text = commonAction.getText(VERIFICATION_CODE_SCREEN_TXT);
    	String subText = "";
    	if (!username.matches("\\d+")) subText = username + "\n";
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.verificationCode.text", signupLanguage).formatted(subText));
    	logger.info("verifyTextAtVerificationCodeScreen completed");
    }    
    
    public void verifyTextAtSetupShopScreen(String username, String country, String signupLanguage) throws Exception {
    	String text = "";
    	text = commonAction.getText(WIZARD_SCREEN_TITLE);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.screenTitle", signupLanguage));
    	text = commonAction.getText(STORE_NAME.findElement(By.xpath("./parent::*/preceding-sibling::label")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.storeName", signupLanguage));
    	text = commonAction.getElementAttribute(STORE_NAME, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.storeNamePlaceHolder", signupLanguage));
    	text = commonAction.getText(STORE_URL.findElement(By.xpath("./parent::*/parent::*/preceding-sibling::label")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.url", signupLanguage));
    	text = commonAction.getText(COUNTRY_DROPDOWN_SETUP_SHOP.findElement(By.xpath("./parent::*/preceding-sibling::label")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.country", signupLanguage));
    	text = commonAction.getText(CURRENCY.findElement(By.xpath("./parent::*/preceding-sibling::label")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.currency", signupLanguage));
    	text = commonAction.getText(STORE_LANGUAGE.findElement(By.xpath("./parent::*/preceding-sibling::label")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.storeLanguageTxt", signupLanguage));
    	
		if (!username.matches("\\d+")) {
	    	text = commonAction.getText(STORE_PHONE.findElement(By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.phone", signupLanguage));
	    	text = commonAction.getElementAttribute(STORE_PHONE, "placeholder");
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.phonePlaceHolder", signupLanguage));
		} else {
	    	text = commonAction.getText(STORE_MAIL.findElement(By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.mail", signupLanguage));
	    	text = commonAction.getElementAttribute(STORE_MAIL, "placeholder");
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.mailPlaceHolder", signupLanguage));
		}
		
    	text = commonAction.getText(PICKUP_ADDRESS.findElement(By.xpath("./parent::*/preceding-sibling::label")));
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.pickupAddress1", signupLanguage));
    	text = commonAction.getElementAttribute(PICKUP_ADDRESS, "placeholder");
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.pickupAddress1PlaceHolder", signupLanguage));
    	
		if (!country.contentEquals("Vietnam")) {
	    	text = commonAction.getText(SECOND_PICKUP_ADDRESS.findElement(By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.pickupAddress2", signupLanguage));
	    	text = commonAction.getElementAttribute(SECOND_PICKUP_ADDRESS, "placeholder");
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.pickupAddress2PlaceHolder", signupLanguage));
	    	text = commonAction.getText(CITY.findElement(By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.city", signupLanguage));
	    	text = commonAction.getText(PROVINCE_DROPDOWN.findElement(By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.state", signupLanguage));
	    	text = commonAction.getText(ZIPCODE.findElement(By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.zipCode", signupLanguage));
		} else {
	    	text = commonAction.getText(PROVINCE_DROPDOWN.findElement(By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.province", signupLanguage));
	    	text = commonAction.getText(DISTRICT_DROPDOWN.findElement(By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.district", signupLanguage));
	    	text = commonAction.getText(WARD.findElement(By.xpath("./parent::*/preceding-sibling::label")));
	    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.ward", signupLanguage));
		}
    	
		text = commonAction.getText(COMPLETE_BTN);
		Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.completeBtn", signupLanguage));
    	text = commonAction.getText(BACK_BTN);
    	Assert.assertEquals(text, PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.backBtn", signupLanguage));
    	
    	logger.info("verifyTextAtSetupShopScreen completed");
    }    
    
}
