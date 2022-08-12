package pages.dashboard;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utilities.UICommonAction;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

import static utilities.links.Links.*;

public class SignupPage {

	final static Logger logger = LogManager.getLogger(SignupPage.class);
	
    WebDriver driver;
    WebDriverWait wait;
    UICommonAction commonAction;

    public SignupPage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        commonAction = new UICommonAction(driver);
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "#username")
    WebElement USERNAME;

    @FindBy (css = "#password")
    WebElement PASSWORD;

    @FindBy (css = "button.uik-btn__iconRight")
    WebElement SIGNUP_BTN;
    
    @FindBy (css = ".btn-next")
    WebElement COMPLETE_BTN;
    
    @FindBy (css = ".wizard-layout__content a")
    WebElement LOGOUT;

    @FindBy (css = "div.uik-select__valueWrapper>div>div:nth-child(2)")
    WebElement COUNTRY_CODE;

    @FindBy (css = "div.uik-select__valueRenderedWrapper")
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
    WebElement CITYCODE_DROPDOWN;
    
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

    @FindBy (css = "button.uik-select__option>span>div>div>div>div:nth-child(1)")
    List<WebElement> COUNTRY_LIST;

    @FindBy (css = "#verifyCode")
    WebElement OTP;

    @FindBy (css = ".btn-confirm")
    WebElement CONFIRM_OTP;

    @FindBy (css = ".resend-otp")
    WebElement RESEND_OTP;    
    
    @FindBy (id = "fb-root")
    WebElement FACEBOOK_BUBBLE;    
    
    public SignupPage navigate() {
        driver.get(DOMAIN + SIGNUP_PATH);
        wait.until(ExpectedConditions.titleIs(SIGNUP_PAGE_TITLE));
        return this;
    }
    public SignupPage selectCountry(String country) {
    	commonAction.clickElement(COUNTRY_DROPDOWN);
    	driver.findElement(By.xpath("//*[@class='uik-select__optionList']//div[@class='phone-option']/div[text()='%s']".formatted(country))).click();
    	logger.info("Selected country: " + country);
    	return this;
    }

    public SignupPage selectCountryToSetUpShop(String country) {
    	String selectedOption = commonAction.selectByVisibleText(COUNTRY_DROPDOWN_SETUP_SHOP, country);
    	logger.info("Selected country: " + selectedOption);
    	return this;
    }
    
    public SignupPage selectCurrency(String currency) {
    	String selectedOption = commonAction.selectByVisibleText(CURRENCY, currency);
    	logger.info("Selected currency: " + selectedOption);
    	return this;
    }
    
    public SignupPage selectLanguage(String language) {
    	String selectedOption = commonAction.selectByVisibleText(STORE_LANGUAGE, language);
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
    
    public SignupPage selectCityCode(String cityCode) {
    	String selectedOption = commonAction.selectByVisibleText(CITYCODE_DROPDOWN, cityCode);
    	logger.info("Selected city code: " + selectedOption);
    	return this;
    }
    
    public SignupPage selectDistrict(String district) {
    	String selectedOption = commonAction.selectByVisibleText(DISTRICT_DROPDOWN, district);
    	logger.info("Selected district: " + selectedOption);
    	return this;
    }

    public SignupPage selectWard(String ward) {
    	String selectedOption = commonAction.selectByVisibleText(WARD, ward);
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

    public SignupPage clickSignupBtn() {
    	commonAction.clickElement(SIGNUP_BTN);
    	logger.info("Clicked on Signup button.");        
        return this;
    }

    public SignupPage fillOutSignupForm(String country, String user, String password) {
    	selectCountry(country);
    	inputMailOrPhoneNumber(user);
    	inputPassword(password);
    	clickSignupBtn();
        return this;
    }    
    
    public SignupPage inputVerificationCode(String verificationCode) throws SQLException {
    	commonAction.inputText(OTP, verificationCode);
    	logger.info("Input '" + verificationCode + "' into Verification Code field.");
        return this;
    }

    public void clickConfirmBtn() {
    	commonAction.clickElement(CONFIRM_OTP);
    	logger.info("Clicked on Confirm button.");     
    }
    
}
