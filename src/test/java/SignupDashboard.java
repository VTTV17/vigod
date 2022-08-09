import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import pages.dashboard.LoginPage;
import pages.dashboard.SignupPage;
import pages.dashboard.home.HomePage;
import utilities.database.InitConnection;
import pages.Mailnesia;

import java.sql.SQLException;

public class SignupDashboard extends BaseTest{

	SignupPage signupPage;

    public String getVerificationCode(String username) {
    	// Get verification code from Mailnesia
    	commonAction.openNewTab();
    	commonAction.switchToWindow(1);
    	String verificationCode = new Mailnesia(driver).navigate(username).getVerificationCode();
    	commonAction.closeTab();
    	commonAction.switchToWindow(0);    	
    	return verificationCode;
    }			
	
    @BeforeMethod
    public void setup() throws InterruptedException {
    	super.setup();
    	signupPage = new SignupPage(driver);
    }		
	
    @Test
    public void SignUpForForeignShopWithPhone() throws SQLException, InterruptedException {

    	String randomNumber = generate.generateNumber(3);
    	String username = "automation0-shop" + randomNumber + "@mailnesia.com";
    	String password = "fortesting!1";
    	String country = "United Kingdom";
    	String countryCode = "+44";
    	String currency = "Pound Sterling - GBP(£)";
    	String language = "Tiếng Anh";
    	String storeName = "Automation Shop " + randomNumber;
    	String storePhone = "9123456" + randomNumber;
    	String pickupAddress = "12 HighWay Revenue";
    	String secondPickupAddress = "16 Wall Street";
    	String city = "Cockney";
    	String state = "England";
    	String zipCode = "987654";    	
    	
    	// Sign up
    	signupPage.navigate()
        .fillOutSignupForm(country, storePhone, password)
        .inputVerificationCode(new InitConnection().getActivationKey(countryCode + ":" + storePhone))
    	.clickConfirmBtn();
    	
    	//Setup store
    	signupPage.inputStoreName(storeName)
    	.selectCountryToSetUpShop(country)
    	.selectCurrency(currency)
    	.selectLanguage(language)
    	.inputStoreMail(username)
    	.inputPickupAddress(pickupAddress)
    	.inputSecondPickupAddress(secondPickupAddress)
    	.inputCity(city)
    	.selectCityCode(state)
    	.inputZipCode(zipCode)
    	.clickCompleteBtn();
    	
    	signupPage.clickLogout();
    	
    	// Re-login to the shop
        new LoginPage(driver).navigate().performLogin(country, storePhone, password);
    	new HomePage(driver).clickUpgradeNow();
    	Thread.sleep(1000);
    	new HomePage(driver).waitTillSpinnerDisappear().clickLogout();    	
    }
    
    @Test
    public void SignUpForVNShopWithPhone() throws SQLException, InterruptedException {
    	
    	String randomNumber = generate.generateNumber(3);
    	String username = "automation0-shop" + randomNumber + "@mailnesia.com";
    	String password = "fortesting!1";
    	String country = "Vietnam";
    	String countryCode = "+84";
    	String currency = "Dong - VND(₫)";
    	String language = "Tiếng Việt";
    	String storeName = "Automation Shop " + randomNumber;
    	String storePhone = "9123456" + randomNumber;
    	String pickupAddress = "12 Quang Trung";
    	String city = "Hồ Chí Minh";
    	String district = "Quận 8";
    	String ward = "Phường 2";   	
    	
    	//Sign up
    	signupPage.navigate()
    	.fillOutSignupForm(country, storePhone, password)
    	.inputVerificationCode(new InitConnection().getActivationKey(countryCode + ":" + storePhone))
    	.clickConfirmBtn();
    	
    	//Setup store
    	signupPage.inputStoreName(storeName)
    	.selectCountryToSetUpShop(country)
    	.selectCurrency(currency)
    	.selectLanguage(language)
    	.inputStoreMail(username)
    	.inputPickupAddress(pickupAddress)
    	.selectCityCode(city)
    	.selectDistrict(district)
    	.selectWard(ward)
    	.clickCompleteBtn()
    	.clickLogout();
    	
    	// Re-login to the shop
    	new LoginPage(driver).navigate().performLogin(country, storePhone, password);
    	new HomePage(driver).clickUpgradeNow();
    	Thread.sleep(1000);
    	new HomePage(driver).waitTillSpinnerDisappear().clickLogout();    	
    }
    
    @Test
    public void SignUpForForeignShopWithEmail() throws SQLException, InterruptedException {
    	
    	String randomNumber = generate.generateNumber(3);
    	String username = "automation0-shop" + randomNumber + "@mailnesia.com";
    	String password = "fortesting!1";
    	String country = "United Kingdom";
    	String countryCode = "+44";
    	String currency = "Pound Sterling - GBP(£)";
    	String language = "Tiếng Anh";
    	String storeName = "Automation Shop " + randomNumber;
    	String storePhone = "9123456" + randomNumber;
    	String pickupAddress = "12 HighWay Revenue";
    	String secondPickupAddress = "16 Wall Street";
    	String city = "Cockney";
    	String state = "England";
    	String zipCode = "987654";
    	
    	// Sign up
    	signupPage.navigate().fillOutSignupForm(country, username, password);
    	Thread.sleep(7000);
    	signupPage.inputVerificationCode(getVerificationCode(username))
    	.clickConfirmBtn();
    	
    	//Setup store
    	signupPage.inputStoreName(storeName)
    	.selectCountryToSetUpShop(country)
    	.selectCurrency(currency)
    	.selectLanguage(language)
    	.inputStorePhone(storePhone)
    	.inputPickupAddress(pickupAddress)
    	.inputSecondPickupAddress(secondPickupAddress)
    	.inputCity(city)
    	.selectCityCode(state)
    	.inputZipCode(zipCode)
    	.clickCompleteBtn();
    	
    	signupPage.clickLogout();
    	
    	// Re-login to the shop
        new LoginPage(driver).navigate().performLogin(username, password);
    	new HomePage(driver).clickUpgradeNow();
    	Thread.sleep(1000);
    	new HomePage(driver).waitTillSpinnerDisappear().clickLogout();
    }
    
    @Test
    public void SignUpForVNShopWithEmail() throws SQLException, InterruptedException {
    	
    	String randomNumber = generate.generateNumber(3);
    	String username = "automation0-shop" + randomNumber + "@mailnesia.com";
    	String password = "fortesting!1";
    	String country = "Vietnam";
    	String countryCode = "+84";
    	String currency = "Dong - VND(₫)";
    	String language = "Tiếng Việt";
    	String storeName = "Automation Shop " + randomNumber;
    	String storePhone = "9123456" + randomNumber;
    	String pickupAddress = "12 Quang Trung";
    	String city = "Hồ Chí Minh";
    	String district = "Quận 8";
    	String ward = "Phường 2";
    	
    	// Sign up
    	signupPage.navigate().fillOutSignupForm(country, username, password);
    	Thread.sleep(7000);
    	signupPage.inputVerificationCode(getVerificationCode(username))
    	.clickConfirmBtn();
    	
    	//Setup store
    	signupPage.inputStoreName(storeName)
    	.selectCountryToSetUpShop(country)
    	.selectCurrency(currency)
    	.selectLanguage(language)
    	.inputStorePhone(storePhone)
    	.inputPickupAddress(pickupAddress)
    	.selectCityCode(city)
    	.selectDistrict(district)
    	.selectWard(ward)
    	.clickCompleteBtn();
    	
    	signupPage.clickLogout();
    	
    	// Re-login to the shop
    	new LoginPage(driver).navigate().performLogin(username, password);
    	new HomePage(driver).clickUpgradeNow();
    	Thread.sleep(1000);
    	new HomePage(driver).waitTillSpinnerDisappear().clickLogout();
    }
    
}
