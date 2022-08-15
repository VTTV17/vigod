import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import pages.dashboard.LoginPage;
import pages.dashboard.SignupPage;
import pages.dashboard.home.HomePage;
import utilities.jsonFileUtility;
import utilities.database.InitConnection;
import pages.Mailnesia;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupDashboard extends BaseTest{

	SignupPage signupPage;
	
	String randomNumber;
	String mail;
	String username;
	String password;
	String country;
	String countryCode;
	String currency;
	String language;
	String storeName;
	String storeURL;
	String storePhone;
	String pickupAddress;
	String secondPickupAddress;
	String province;
	String district;
	String ward;
	String city;
	String zipCode;
	
    String INVALID_CODE_ERROR = "Mã xác thực không đúng";	
    String USERNAME_EXIST_ERROR = "Email / số điện thoại đã tồn tại";	

	public String getVerificationCode(String username) throws InterruptedException, SQLException {
		String verificationCode;
		if (!username.matches("\\d+")) {
			// Get verification code from Mailnesia
			Thread.sleep(8000);
			commonAction.openNewTab();
			commonAction.switchToWindow(1);
			verificationCode = new Mailnesia(driver).navigate(username).getVerificationCode();
			commonAction.closeTab();
			commonAction.switchToWindow(0);  
		} else {
			verificationCode = new InitConnection().getActivationKey(signupPage.countryCode + ":" + username);
		}
		return verificationCode;
	}	
	
    public void setupShop(String username, String storeName, String url, String country, String currency, String language, String contact, String pickupAddress, String secondPickupAddress, String province, String district, String ward, String city, String zipCode) {
    	signupPage.inputStoreName(storeName);
    	if (url != "") {
    		signupPage.inputStoreURL(url);
    	}
    	signupPage.selectCountryToSetUpShop(country)
    	.selectCurrency(currency)
    	.selectLanguage(language);
    	if (!username.matches("\\d+")) {
    		signupPage.inputStorePhone(contact);
    	} else {
    		signupPage.inputStoreMail(contact);
    	}
    	
    	signupPage.inputPickupAddress(pickupAddress)
    	.selectProvince(province);
		
		if (!country.contentEquals("Vietnam")) {
			signupPage.inputSecondPickupAddress(secondPickupAddress)
			.inputCity(city)
			.inputZipCode(zipCode);	
		} else {
			signupPage.selectDistrict(district)
			.selectWard(ward);
		}
		signupPage.clickCompleteBtn();
    }    
    
    public void reLogintoShop(String country, String user, String password) throws InterruptedException {
        new LoginPage(driver).navigate().performLogin(country, user, password);
    	new HomePage(driver).clickUpgradeNow();
    	Thread.sleep(1000);
    	new HomePage(driver).waitTillSpinnerDisappear().clickLogout();    
    }			

    @BeforeMethod
    public void setup() throws InterruptedException {
    	super.setup();
    	signupPage = new SignupPage(driver);
    	
		randomNumber = generate.generateNumber(3);
		mail = "automation0-shop" + randomNumber + "@mailnesia.com";
		storePhone = "9123456" + randomNumber;
		password = "fortesting!1";
		country = "rd";
		countryCode = "+84";
		currency = "rd";
		language = "rd";
		storeName = "Automation Shop " + randomNumber;
		storeURL = "";
		pickupAddress = "12 Quang Trung";
    	secondPickupAddress = "16 Wall Street";
    	province = "rd";
    	district = "rd";
    	ward = "rd";
    	city = "Cockney";
    	zipCode = generate.generateNumber(6);
    }		

	@Test
	public void SignUpForShopWithRandomData() throws SQLException, InterruptedException {

		String username = storePhone;
		String contact = mail;
		
    	//Sign up
    	signupPage.navigate()
    	.fillOutSignupForm(country, username, password)
    	.inputVerificationCode(getVerificationCode(username))
    	.clickConfirmBtn();
    	
    	country = signupPage.country;
    	
		//Setup store
    	setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress, secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();
		
		// Re-login to the shop
		reLogintoShop(country, username, password);
	}       
    
    @Test
    public void SignUpForForeignShopWithPhone() throws SQLException, InterruptedException {

    	String country = "United Kingdom";
    	String currency = "Pound Sterling - GBP(£)";
    	String language = "Tiếng Anh";
    	String pickupAddress = "12 HighWay Revenue";
    	String secondPickupAddress = "16 Wall Street";
    	String city = "East London";
    	String province = "England";
    	
		String username = storePhone;
		String contact = mail;
		
    	//Sign up
    	signupPage.navigate()
    	.fillOutSignupForm(country, username, password)
    	.inputVerificationCode(getVerificationCode(username))
    	.clickConfirmBtn();
    	
    	country = signupPage.country;
    	
		//Setup store
    	setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress, secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();
		
		// Re-login to the shop
		reLogintoShop(country, username, password);  	
    }

    @Test
    public void SignUpForForeignShopWithEmail() throws SQLException, InterruptedException {
    	
    	String country = "United Kingdom";
    	String currency = "Pound Sterling - GBP(£)";
    	String language = "Tiếng Anh";
    	String pickupAddress = "12 HighWay Revenue";
    	String secondPickupAddress = "16 Wall Street";
    	String city = "Cockney";
    	String province = "England";
    	
		String username = mail;
		String contact = storePhone;
		
    	//Sign up
    	signupPage.navigate()
    	.fillOutSignupForm(country, username, password)
    	.inputVerificationCode(getVerificationCode(username))
    	.clickConfirmBtn();
    	
    	country = signupPage.country;
    	
		//Setup store
    	setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress, secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();
		
		// Re-login to the shop
		reLogintoShop(country, username, password);
    }    
    
    @Test
    public void SignUpForVNShopWithPhone() throws SQLException, InterruptedException {
    	
    	String country = "Vietnam";
    	String currency = "Dong - VND(₫)";
    	String language = "Tiếng Việt";
    	String pickupAddress = "12 Quang Trung";
    	
		String username = storePhone;
		String contact = mail;
		
    	//Sign up
    	signupPage.navigate()
    	.fillOutSignupForm(country, username, password)
    	.inputVerificationCode(getVerificationCode(username))
    	.clickConfirmBtn();
    	
    	country = signupPage.country;
    	
		//Setup store
    	setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress, secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();
		
		// Re-login to the shop
		reLogintoShop(country, username, password);
    }
    
    @Test
    public void SignUpForVNShopWithEmail() throws SQLException, InterruptedException {
    	
    	String country = "Vietnam";
    	String currency = "Dong - VND(₫)";
    	String language = "Tiếng Việt";
    	String province = "Hồ Chí Minh";
    	String district = "Quận 8";
    	String ward = "Phường 2";
    	
		String username = mail;
		String contact = storePhone;
		
    	//Sign up
    	signupPage.navigate()
    	.fillOutSignupForm(country, username, password)
    	.inputVerificationCode(getVerificationCode(username))
    	.clickConfirmBtn();
    	
    	country = signupPage.country;
    	
		//Setup store
    	setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress, secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();
		
		// Re-login to the shop
		reLogintoShop(country, username, password);
    }

    @Test
    public void BH_4036_SignUpForShopWithExistingPhoneAccount() throws SQLException, InterruptedException {
    	
		JsonNode data = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
		storePhone = data.findValue("seller").findValue("phone").findValue("username").asText();
		password = data.findValue("seller").findValue("phone").findValue("password").asText();
		country = data.findValue("seller").findValue("phone").findValue("country").asText();
    	
    	String username = storePhone;
    	
    	// Sign up
    	signupPage.navigate()
    	.fillOutSignupForm(country, username, password);
    	signupPage.verifyUsernameExistError(USERNAME_EXIST_ERROR)
    	.completeVerify();
    }    
    
    @Test
    public void BH_4038_ResendVerificationCodeToPhone() throws SQLException, InterruptedException {
   
    	String username = storePhone;
    	String contact = mail;
    	
    	// Sign up
    	signupPage.navigate()
    	.fillOutSignupForm(country, username, password);
    	String firstCode = getVerificationCode(username);
    	signupPage.inputVerificationCode(firstCode);
    	signupPage.clickResendOTP();
    	signupPage.clickConfirmBtn();
    	signupPage.verifyVerificationCodeError(INVALID_CODE_ERROR).completeVerify();
    	String resentCode = getVerificationCode(username);
    	signupPage.inputVerificationCode(resentCode);
    	Assert.assertNotEquals(firstCode, resentCode, "New verification code has not been sent to user");
    	signupPage.clickConfirmBtn();
    	
    	country = signupPage.country;
    	
    	//Setup store
    	setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress, secondPickupAddress, province, district, ward, city, zipCode);
    	signupPage.clickLogout();
    	
    	// Re-login to the shop
    	reLogintoShop(country, username, password);
    }
    
    @Test
    public void BH_4039_ResendVerificationCodeToEmail() throws SQLException, InterruptedException {
    	
    	String username = mail;
    	String contact = storePhone;
    	
    	// Sign up
    	signupPage.navigate()
    	.fillOutSignupForm(country, username, password);
    	String firstCode = getVerificationCode(username);
    	signupPage.inputVerificationCode(firstCode);
    	signupPage.clickResendOTP();
    	signupPage.clickConfirmBtn();
    	signupPage.verifyVerificationCodeError(INVALID_CODE_ERROR).completeVerify();
    	String resentCode = getVerificationCode(username);
    	signupPage.inputVerificationCode(resentCode);
    	Assert.assertNotEquals(firstCode, resentCode, "New verification code has not been sent to user");
    	signupPage.clickConfirmBtn();
    	
    	country = signupPage.country;
    	
    	//Setup store
    	setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress, secondPickupAddress, province, district, ward, city, zipCode);
    	signupPage.clickLogout();
    	
    	// Re-login to the shop
    	reLogintoShop(country, username, password);
    }

  @Test
	public void BH_5195_SignUpForShopWithURLInUpperCase() throws SQLException, InterruptedException {
  	
		String username = mail;
		String contact = storePhone;

		Pattern p = Pattern.compile("[A-Za-z0-9]+");
		Matcher m = p.matcher(storeName + generate.generateString(10));
		while (m.find()) {
			storeURL += m.group();
		}
	
		// Sign up
		signupPage.navigate()
		.fillOutSignupForm(country, username, password)
		.inputVerificationCode(getVerificationCode(username))
		.clickConfirmBtn();
	
		country = signupPage.country;
  	
		//Setup store
		setupShop(username, storeName, storeURL, country, currency, language, contact, pickupAddress, secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();
  	
		// Re-login to the shop
		reLogintoShop(country, username, password);
  	
		Assert.assertEquals(storeURL.toLowerCase(), new InitConnection().getStoreURL(storeName));
  }    
    
}
