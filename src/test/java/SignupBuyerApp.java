import static utilities.links.Links.SF_DOMAIN;

import java.sql.SQLException;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.dashboard.login.Login;
import api.dashboard.setting.StoreInformation;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import pages.buyerapp.NavigationBar;
import pages.buyerapp.NotificationPermission;
import pages.buyerapp.SignupPage;
import pages.buyerapp.account.BuyerAccountPage;
import pages.buyerapp.account.BuyerMyProfile;
import pages.dashboard.customers.allcustomers.AllCustomers;
import pages.dashboard.customers.allcustomers.CustomerDetails;
import pages.dashboard.home.HomePage;
import pages.thirdparty.Mailnesia;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;
import utilities.driver.InitAppiumDriver;
import utilities.driver.InitWebdriver;


public class SignupBuyerApp {

	AppiumDriver driver;
	WebDriver driverWeb;
	BuyerAccountPage accountTab;
	NavigationBar navigationBar;
	SignupPage signupPage;
	UICommonMobile commonAction;
	DataGenerator generate;
	
	String username;
	String mail;
	String phone;
	String password;
	String country;
	String phoneCode;
	String countryCode;
	String displayName;
	String birthday;
	
	String STORE_USERNAME;
	String STORE_PASSWORD;
	String STORE_COUNTRY;
	String ANOTHER_STORE_USERNAME;
	String ANOTHER_STORE_PASSWORD;
	String ANOTHER_STORE_COUNTRY;
	String BUYER_MAIL_USERNAME;
	String BUYER_MAIL_PASSWORD;
	String BUYER_MAIL_COUNTRY;
	String BUYER_PHONE_USERNAME;
	String BUYER_PHONE_PASSWORD;
	String BUYER_PHONE_COUNTRY;
	
	public void getCredentials() {
		STORE_USERNAME = AccountTest.ADMIN_USERNAME_TIEN;
		STORE_PASSWORD = AccountTest.ADMIN_PASSWORD_TIEN;
		STORE_COUNTRY = AccountTest.ADMIN_COUNTRY_TIEN;
		ANOTHER_STORE_USERNAME = AccountTest.ADMIN_ACCOUNT_THANG;
		ANOTHER_STORE_PASSWORD = AccountTest.ADMIN_PASSWORD_THANG;
		ANOTHER_STORE_COUNTRY = AccountTest.ADMIN_COUNTRY_TIEN;
		BUYER_MAIL_USERNAME = AccountTest.SF_USERNAME_VI_1;
		BUYER_MAIL_PASSWORD = AccountTest.SF_SHOP_VI_PASSWORD;
		BUYER_MAIL_COUNTRY = AccountTest.ADMIN_COUNTRY_TIEN;
		BUYER_PHONE_USERNAME = AccountTest.SF_USERNAME_PHONE_VI_1;
		BUYER_PHONE_PASSWORD = AccountTest.SF_SHOP_VI_PASSWORD;
		BUYER_PHONE_COUNTRY = AccountTest.ADMIN_COUNTRY_TIEN;
	}		
	
	/**
	 * This method retrieves a verification code for a given username. 
	 * If the username is a valid email address, the method retrieves the verification code from Mailnesia.
	 * Otherwise, it retrieves the code from a database.
	 * @param username either an email address (tienvan@mailnesia.com) or a phone number (+84:0123456789)
	 * @return the retrieved verification code as a String
	 * @throws SQLException if there is an error retrieving the reset key from the database
	 */
	public String getVerificationCode(String phoneCode, String username) throws SQLException {
		String code;
		if (username.matches("\\d+")) {
			code = new InitConnection().getActivationKey(phoneCode + ":" + username);
		} else {
			/*
			// Launch a new browser
			driverWeb = new InitWebdriver().getDriver("chrome", "noHeadless");
			// Get verification code from Mailnesia
			code = new Mailnesia(driverWeb).navigateToMailAndGetVerifyCode(username);
			driverWeb.quit();
			*/
			code = new InitConnection().getActivationKey(username);
		}
		return code;
	}	
	
	public String getNewVerificationCode(String phoneCode, String username) throws SQLException {
		/*
		if (username.matches("\\d+")) {
			commonAction.sleepInMiliSecond(2000);
		} else {
			commonAction.sleepInMiliSecond(10000);
		}
		*/
		return getVerificationCode(phoneCode, username);
	}	

	// This function checks if an email is sent to the user saying the user has signed up for an account successfully
	public void verifyEmailUponSuccessfulSignup(String username) {
		String language = "vi";
		String title = new Login().setDashboardLoginInfo(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD).getInfo().getStoreName();
		String expectedVerificationCodeMessage;
		String expectedSuccessfulSignupMessage;
		driverWeb = new InitWebdriver().getDriver("chrome", "noHeadless");
		pages.storefront.signup.SignupPage sfSignupPage = new pages.storefront.signup.SignupPage(driverWeb);
		if (language.contentEquals("vi")) {
			expectedSuccessfulSignupMessage = sfSignupPage.SUCCESSFUL_SIGNUP_MESSAGE_VI.formatted(title);
			expectedVerificationCodeMessage = sfSignupPage.VERIFICATION_CODE_MESSAGE_VI.formatted(title);
		} else {
			expectedSuccessfulSignupMessage = sfSignupPage.SUCCESSFUL_SIGNUP_MESSAGE_EN.formatted(title);
			expectedVerificationCodeMessage = sfSignupPage.VERIFICATION_CODE_MESSAGE_VI.formatted(title);
		}
		String [][] mailContent = new Mailnesia(driverWeb).navigate(username).getListOfEmailHeaders();
		driverWeb.quit();
		Assert.assertEquals(mailContent[0][3], expectedSuccessfulSignupMessage);
		Assert.assertTrue(mailContent[1][3].contains(expectedVerificationCodeMessage));
	}	

	public void createAccountOnSF(String country, String phoneCode, String username, String password, String displayName, String birthday) throws SQLException {
		new Login().setDashboardLoginInfo(ANOTHER_STORE_COUNTRY, ANOTHER_STORE_USERNAME, ANOTHER_STORE_PASSWORD);
		String URL = "https://%s%s/".formatted(new StoreInformation().getInfo().getStoreURL(), SF_DOMAIN);
		driverWeb = new InitWebdriver().getDriver("chrome", "noHeadless");
		pages.storefront.signup.SignupPage signupPage = new pages.storefront.signup.SignupPage(driverWeb);
		signupPage.navigate(URL).fillOutSignupForm(country, username, password, displayName, birthday)
		.inputVerificationCode(new InitConnection().getActivationKey(username)).clickConfirmBtn();
		if (username.matches("\\d+")) {
			signupPage.inputEmail(mail).clickCompleteBtn();
		}
		driverWeb.quit();
	}  	
	
	public void fillOutRegistrationForm(String country, String username, String password, String displayName) {
    	if (username.matches("\\d+")) {
    		signupPage.clickPhoneTab();
    		signupPage.selectCountryCodeFromSearchBox(country);
    	}
    	commonAction.hideKeyboard("android");
    	signupPage.inputUsername(username);
    	signupPage.inputPassword(password);
    	signupPage.inputDisplayName(displayName);
    	signupPage.clickBirthday();
    	signupPage.clickBirthdayOKBtn();
    	birthday = signupPage.getBirthday();
    	signupPage.clickAgreeTermBtn();
    	signupPage.clickContinueBtn();
	}
	
	public void createAccount(String country, String phoneCode, String username, String password, String displayName) throws SQLException {
		navigationBar.tapOnAccountIcon().clickSignupBtn();
		
		fillOutRegistrationForm(country, username, password, displayName);
		
		signupPage.inputVerificationCode(getVerificationCode(phoneCode, username)).clickVerifyBtn();
		commonAction.sleepInMiliSecond(3000);
	}
	
	public void logOutOfApp() {
    	commonAction.swipeByCoordinatesInPercent(0.5, 0.8, 0.5, 0.2);
    	accountTab.clickLogoutBtn().clickConfirmLogoutBtn();
	}
	
	public void logintoApp(String country, String username, String password) {
		navigationBar.tapOnAccountIcon().clickLoginBtn().performLogin(country, username, password);
	}
	
	public void checkBuyerDataOnApp(String country, String phoneCode, String username, String displayName, String mail, String birthday) {
    	BuyerMyProfile myProfile = accountTab.clickProfile();
    	Assert.assertEquals(myProfile.getDisplayName(), displayName, "Display name");
    	if (!username.matches("\\d+")) {
    		Assert.assertEquals(myProfile.getEmail(), mail, "Email");
    	} else {
    		Assert.assertEquals(myProfile.getPhoneNumber(), phoneCode+":"+username, "Phone");
    	}
    	commonAction.swipeByCoordinatesInPercent(0.5, 0.8, 0.5, 0.2);
    	Assert.assertEquals(myProfile.getBirthday(), birthday, "Birthday");
    	//Handle inconsistency in country between different languages
    	String tempCountry = myProfile.clickAddress().getCountry();
    	String formatedCountry = tempCountry.contentEquals("Việt Nam") ? "Vietnam":tempCountry;
    	Assert.assertEquals(formatedCountry, country, "Country");
	}
	
	public void checkBuyerDataOnDashboard(String country, String phoneCode, String username, String displayName, String mail, String birthday) {
    	driverWeb = new InitWebdriver().getDriver("chrome", "noHeadless");
		pages.dashboard.login.LoginPage dashboard = new pages.dashboard.login.LoginPage(driverWeb);
		dashboard.navigate().performLogin(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD);
		new HomePage(driverWeb).selectLanguage("ENG");
		new AllCustomers(driverWeb).navigate().selectBranch("None Branch").clickUser(displayName);
		CustomerDetails customerDetail = new CustomerDetails(driverWeb);
		if (!username.matches("\\d+")) {
			Assert.assertEquals(customerDetail.getEmail(), mail);
		} else {
			Assert.assertEquals(customerDetail.getPhoneNumber(), phoneCode+":"+username);
		}
		Assert.assertEquals(customerDetail.getCountry(), country);
		Assert.assertEquals(customerDetail.getBirthday().replace("/", "-"), birthday, "Birthday");
		driverWeb.quit();
	}
	
	public void checkBuyerLocationInDatabase(String country, String phoneCode, String username) throws SQLException {
		if (username.matches("\\d+")) {
			Assert.assertEquals(new InitConnection().getUserLocationCode(phoneCode+":"+username), generate.getCountryCode(country));
			return;
		} 
		Assert.assertEquals(new InitConnection().getUserLocationCode(username), generate.getCountryCode(country));
	}	
	
	public void generateTestData() {
		country = generate.randomCountry();
		phoneCode = generate.getPhoneCode(country);
		phone = generate.randomPhoneByCountry(country); 
		mail = "auto0-buyer" + phone + "@mailnesia.com";
		password = "fortesting!1";
		displayName = "Auto Buyer " + phone;
		birthday = "21-02-1990";
	}	

	public AppiumDriver launchApp() throws Exception {
		String udid = "10.10.2.100:5555"; //RF8N20PY57D 192.168.2.43:5555
		String platformName = "Android";
		String appPackage = "com.mediastep.shop0017";
		String appActivity = "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity";
		String url = "http://127.0.0.1:4723/wd/hub";
		return new InitAppiumDriver().getAppiumDriver(udid, platformName, appPackage, appActivity, url);
	}
	
	public void instantiatePageObjects() throws Exception {
		generate = new DataGenerator();
		driver = launchApp();
		navigationBar = new NavigationBar(driver);
		accountTab = new BuyerAccountPage(driver);
		signupPage = new SignupPage(driver);
		commonAction = new UICommonMobile(driver);
	}		
	
    @BeforeClass
    public void setUp() {
        PropertiesUtil.setEnvironment("STAG");
        getCredentials();
    }

    @BeforeMethod
    public void generateData() throws Exception{
        instantiatePageObjects();
        new NotificationPermission(driver).clickAllowBtn();
    	generateTestData();
    }

    @AfterMethod
    public void tearDown(){
        driver.quit();
        if (driverWeb != null) driverWeb.quit();
    }    
    
    @Test
    public void Signup_02_RequiredFieldsLeftEmpty() {

    	String[] account = {mail, phone};
    	
    	for (String username : account) {
    		navigationBar.tapOnAccountIcon().clickSignupBtn();
    		
    		if (username.matches("\\d+")) {
    			signupPage.clickPhoneTab();
    		} else {
    			signupPage.clickMailTab();
    		}
    		
        	// All fields are left empty
        	signupPage.clickAgreeTermBtn();
        	Assert.assertFalse(signupPage.isContinueBtnEnabled());
        	
        	// Only username is left empty
        	signupPage.inputUsername("");
        	signupPage.inputPassword(password);
        	signupPage.inputDisplayName(displayName);
        	signupPage.inputBirthday(birthday);
        	Assert.assertFalse(signupPage.isContinueBtnEnabled());
        	
        	// Only password is left empty
        	signupPage.inputUsername(username);
        	signupPage.inputPassword("");
        	Assert.assertFalse(signupPage.isContinueBtnEnabled());
        	
        	// Only name is left empty
        	signupPage.inputPassword(password);
        	signupPage.inputDisplayName("");
        	Assert.assertFalse(signupPage.isContinueBtnEnabled());
        	
        	// Only birthday is left empty
        	signupPage.inputDisplayName(displayName);
        	signupPage.inputBirthday("");
        	Assert.assertTrue(signupPage.isContinueBtnEnabled());
        	commonAction.navigateBack();
    	}
    }
    
    @Test
    public void Signup_04_InvalidEmailFormat() {
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn().clickMailTab();
    	
    	signupPage.clickUsername(); //Workaround to simulate a tap on username field
    	commonAction.hideKeyboard("android");
    	
    	signupPage.clickAgreeTermBtn();
    	
    	// Mail does not have symbol @
    	signupPage.inputUsername(generate.generateString(10)).inputPassword(password).inputDisplayName(displayName);
    	Assert.assertFalse(signupPage.isContinueBtnEnabled());
    	Assert.assertEquals(signupPage.getUsernameError(), "Email không đúng");
    	
    	// Mail does not have suffix '.<>'. Eg. '.com'
    	signupPage.inputUsername(generate.generateString(10) + "@");
    	Assert.assertFalse(signupPage.isContinueBtnEnabled());
    	Assert.assertEquals(signupPage.getUsernameError(), "Email không đúng");
    	
    	signupPage.inputUsername(generate.generateString(10) + "@" + generate.generateString(5) + ".");
    	Assert.assertFalse(signupPage.isContinueBtnEnabled());
    	Assert.assertEquals(signupPage.getUsernameError(), "Email không đúng");
    }
    
    @Test
    public void Signup_05_InvalidPhoneFormat() {
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn().clickPhoneTab();
    	
    	signupPage.clickUsername(); //Workaround to simulate a tap on username field
    	commonAction.hideKeyboard("android");
    	
    	signupPage.clickAgreeTermBtn();
    	
    	// 7-digit phone number
    	signupPage.inputUsername(generate.generateNumber(7)).inputPassword(password).inputDisplayName(displayName);
    	Assert.assertFalse(signupPage.isContinueBtnEnabled());
    	Assert.assertEquals(signupPage.getUsernameError(), "Điền từ 8 - 15 số");
    	
    	// 16-digit phone number
    	signupPage.inputUsername(generate.generateNumber(16));
    	Assert.assertFalse(signupPage.isContinueBtnEnabled());
    	Assert.assertEquals(signupPage.getUsernameError(), "Điền từ 8 - 15 số");
    }
    
    @Test
    public void Signup_06_WrongVerificationCodeForEmailAccount() throws SQLException {
    	username = mail;
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	fillOutRegistrationForm(country, username, password, displayName);
    	
		String code = getVerificationCode(phoneCode, username);
    	
		signupPage.inputVerificationCode(String.valueOf(Integer.parseInt(code) + 1)).clickVerifyBtn();
		Assert.assertEquals(signupPage.getVerificationCodeError(), "Mã không hợp lệ");
    }
    
    @Test
    public void Signup_07_WrongVerificationCodeForPhoneAccount() throws SQLException {
    	username = phone;
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	fillOutRegistrationForm(country, username, password, displayName);
    	
    	String code = getVerificationCode(phoneCode, username);
    	
    	signupPage.inputVerificationCode(String.valueOf(Integer.parseInt(code) + 1)).clickVerifyBtn();
    	Assert.assertEquals(signupPage.getVerificationCodeError(), "Mã không hợp lệ");
    }
    
    @Test
    public void Signup_08_ResendVerificationCodeForMailAccount() throws SQLException {
    	username = mail;
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	fillOutRegistrationForm(country, username, password, displayName);
    	
    	String code = getVerificationCode(phoneCode, username);
    	
    	signupPage.clickResendBtn();
    	
    	Assert.assertEquals(signupPage.getToastMessage(), "Đã gửi lại mã. Vui lòng kiểm tra email");
    	
    	String newCode = getNewVerificationCode(phoneCode, username);
    	
    	Assert.assertNotEquals(newCode, code, "Resent code");
    	
    	signupPage.inputVerificationCode(code).clickVerifyBtn();
    	Assert.assertEquals(signupPage.getVerificationCodeError(), "Mã không hợp lệ");
    }
    
    @Test
    public void Signup_09_ResendVerificationCodeForPhoneAccount() throws SQLException {
    	username = phone;
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	fillOutRegistrationForm(country, username, password, displayName);
    	
    	String code = getVerificationCode(phoneCode, username);
    	
    	signupPage.clickResendBtn();
    	
    	Assert.assertEquals(signupPage.getToastMessage(), "Đã gửi lại mã. Vui lòng kiểm tra SMS");
    	
    	String newCode = getNewVerificationCode(phoneCode, username);
    	
    	Assert.assertNotEquals(newCode, code, "Resent code");
    	
    	signupPage.inputVerificationCode(code).clickVerifyBtn();
    	
    	Assert.assertEquals(signupPage.getVerificationCodeError(), "Mã không hợp lệ");
    }

    @Test
    public void Signup_10_ExistingEmailAccount() throws SQLException {
    	username = BUYER_MAIL_USERNAME;
    	country = BUYER_MAIL_COUNTRY;
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	fillOutRegistrationForm(country, username, password, displayName);
    	
    	Assert.assertEquals(signupPage.getUsernameError(), "Email đã tồn tại");
    }  
    
    @Test
    public void Signup_11_ExistingPhoneAccount() throws SQLException {
    	username = BUYER_PHONE_USERNAME;
    	country = BUYER_PHONE_COUNTRY;
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	fillOutRegistrationForm(country, username, password, displayName);
    	
    	Assert.assertEquals(signupPage.getUsernameError(), "Số điện thoại đã tồn tại");
    }    
    
//    @Test
    public void Signup_12_LocalEmailAccount() throws Exception {
    	
		country = "Vietnam";
		phoneCode = generate.getPhoneCode(country);
		phone = generate.randomPhoneByCountry(country); 
		mail = "auto0-buyer" + phone + "@mailnesia.com";
		displayName = "Auto Buyer " + phone;
    	username = mail;
    	
    	/* Signup */
    	createAccount(country, phoneCode, username, password, displayName);
    	
    	/* Logout */
    	logOutOfApp();
    	
    	/* Re-login */
    	logintoApp(country, username, password);
    	
    	/* Validate buyer info on app */
    	checkBuyerDataOnApp(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
    	
    	/* Validate buyer info on Dashboard */
    	checkBuyerDataOnDashboard(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
		
		/* Validate buyer's location code in database */
    	checkBuyerLocationInDatabase(STORE_COUNTRY, phoneCode, username);
		
		// Verify mails sent to the user saying the sign-up is successful
		if (!username.matches("\\d+")) {
			verifyEmailUponSuccessfulSignup(username);			
		}
    }  
    
//    @Test
    public void Signup_13_LocalPhoneAccount() throws Exception {
    	
		country = "Vietnam";
		phoneCode = generate.getPhoneCode(country);
		phone = generate.randomPhoneByCountry(country); 
		mail = "auto0-buyer" + phone + "@mailnesia.com";
		displayName = "Auto Buyer " + phone;
		username = phone;
    	
    	/* Signup */
    	createAccount(country, phoneCode, username, password, displayName);
    	
    	/* Logout */
    	logOutOfApp();
    	
    	/* Re-login */
    	logintoApp(country, username, password);
    	
    	/* Validate buyer info on app */
    	checkBuyerDataOnApp(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
    	
    	/* Validate buyer info on Dashboard */
    	checkBuyerDataOnDashboard(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
		
		/* Validate buyer's location code in database */
    	checkBuyerLocationInDatabase(STORE_COUNTRY, phoneCode, username);
		
		// Verify mails sent to the user saying the sign-up is successful
		if (!username.matches("\\d+")) {
			verifyEmailUponSuccessfulSignup(username);			
		}
    }      
    
//    @Test
    public void Signup_14_ForeignPhoneAccount() throws Exception {
    	
		username = phone;
    	
    	/* Signup */
    	createAccount(country, phoneCode, username, password, displayName);
    	
    	/* Logout */
    	logOutOfApp();
    	
    	/* Re-login */
    	logintoApp(country, username, password);
    	
    	/* Validate buyer info on app */
    	checkBuyerDataOnApp(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
    	
    	/* Validate buyer info on Dashboard */
    	checkBuyerDataOnDashboard(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
    	
    	/* Validate buyer's location code in database */
    	checkBuyerLocationInDatabase(STORE_COUNTRY, phoneCode, username);
    	
    	// Verify mails sent to the user saying the sign-up is successful
    	if (!username.matches("\\d+")) {
    		verifyEmailUponSuccessfulSignup(username);			
    	}
    }    
    
//    @Test
    public void Signup_15_FirstLogin() throws Exception {
    	
    	username = mail;
    	
    	createAccountOnSF(country, phoneCode, username, password, displayName, birthday);
    	
    	logintoApp(country, username, password);
    	
    	checkBuyerDataOnApp(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
    	
    	checkBuyerDataOnDashboard(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
    	
    	checkBuyerLocationInDatabase(country, phoneCode, username);
    }      
    
//    @Test
    public void Signup_16_Resignup() throws Exception {

    	username = phone;
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	fillOutRegistrationForm(country, username, password, displayName);
    	
    	commonAction.sleepInMiliSecond(2000);
    	commonAction.hideKeyboard("android");
    	commonAction.navigateBack();
    	commonAction.hideKeyboard("android");
    	commonAction.navigateBack();
    	
    	createAccount(country, phoneCode, username, password, displayName);
    	
    	checkBuyerDataOnApp(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
    	
    	checkBuyerDataOnDashboard(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
    	
    	checkBuyerLocationInDatabase(STORE_COUNTRY, phoneCode, username);
    }      
    
}
