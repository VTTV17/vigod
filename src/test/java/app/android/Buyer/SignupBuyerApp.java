package app.android.Buyer;
import static utilities.links.Links.SF_DOMAIN;

import java.io.IOException;
import java.sql.SQLException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import api.Seller.setting.StoreInformation;
import io.appium.java_client.AppiumDriver;
import app.Buyer.account.BuyerAccountPage;
import app.Buyer.account.BuyerMyProfile;
import app.Buyer.buyergeneral.BuyerGeneral;
import app.Buyer.navigationbar.NavigationBar;
import app.Buyer.notificationpermission.NotificationPermission;
import app.Buyer.signup.SignupPage;
import web.Dashboard.customers.allcustomers.AllCustomers;
import web.Dashboard.customers.allcustomers.details.CustomerDetails;
import web.Dashboard.home.HomePage;
import utilities.thirdparty.Mailnesia;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonMobile;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;
import utilities.driver.InitAppiumDriver;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.screenshot.Screenshot;
import web.Dashboard.login.LoginPage;
import web.StoreFront.GeneralSF;


public class SignupBuyerApp {

	AppiumDriver driver;
	WebDriver driverWeb;
	BuyerAccountPage accountTab;
	NavigationBar navigationBar;
	SignupPage signupPage;
	BuyerGeneral buyerGeneral;
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
	String language = "VIE";
	String expectedCodeMsg;
	String expectedSignupMsg;
	
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
	LoginInformation loginInformation;
	
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

	public void getExpectedMailMsg(String language, String storeName) throws Exception {
		expectedCodeMsg = PropertiesUtil.getPropertiesValueBySFLang("mail.signup.verificationCode", language).formatted(storeName);
		expectedSignupMsg = PropertiesUtil.getPropertiesValueBySFLang("mail.signup.successfulRegistration", language).formatted(storeName);
	}		
	
	public String getVerificationCode(String phoneCode, String username) throws SQLException {
		if (username.matches("\\d+")) {
			return new InitConnection().getActivationKey(phoneCode + ":" + username);
		}
		return new InitConnection().getActivationKey(username);
	}	
	
	public String [][] getEmailContent(String username) {
		driverWeb = new InitWebdriver().getDriver("chrome", "noHeadless");
		String [][] mailContent = new Mailnesia(driverWeb).navigate(username).getListOfEmailHeaders();
		driverWeb.quit();
		return mailContent;
	}
	
	public void verifySignupMail(String username) throws Exception {
		String [][] mailContent = getEmailContent(username);
		Assert.assertEquals(mailContent[0][3], expectedSignupMsg);
		Assert.assertTrue(mailContent[1][3].contains(expectedCodeMsg));
	}	

	public void createAccountOnSF(String country, String phoneCode, String username, String password, String displayName, String birthday) throws SQLException {
		loginInformation = new Login().setLoginInformation(ANOTHER_STORE_COUNTRY, ANOTHER_STORE_USERNAME, ANOTHER_STORE_PASSWORD).getLoginInformation();
		String URL = "https://%s%s/".formatted(new StoreInformation(loginInformation).getInfo().getStoreURL(), SF_DOMAIN);
		driverWeb = new InitWebdriver().getDriver("chrome", "noHeadless");
		new GeneralSF(driver).navigateToURL(URL);
		web.StoreFront.signup.SignupPage signupPage = new web.StoreFront.signup.SignupPage(driverWeb);
		signupPage.fillOutSignupForm(country, username, password, displayName, birthday)
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
    	accountTab.logOutOfApp();
	}
	
	public void logintoApp(String country, String username, String password) {
		navigationBar.tapOnAccountIcon().clickLoginBtn().performLogin(country, username, password);
	}
	
	public void verifyBuyerDataOnApp(String country, String phoneCode, String username, String displayName, String mail, String birthday) {
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
	
	public void verifyBuyerDataOnDashboard(String country, String phoneCode, String username, String displayName, String mail, String birthday) {
    	driverWeb = new InitWebdriver().getDriver("chrome", "noHeadless");
		LoginPage dashboard = new LoginPage(driverWeb);
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
	
	public void verifyBuyerLocationInDatabase(String country, String phoneCode, String username) throws SQLException {
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
        DesiredCapabilities capabilities = new DesiredCapabilities();
//        capabilities.setCapability("udid", "192.168.2.43:5555");
        capabilities.setCapability("udid", "RF8N20PY57D"); //10.10.2.100:5555 RF8N20PY57D
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appPackage", "com.mediastep.shop0017");
        capabilities.setCapability("appActivity", "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity");
        capabilities.setCapability("noReset", "false");
        
        String url = "http://127.0.0.1:4723/wd/hub";

		return new InitAppiumDriver().getAppiumDriver(capabilities, url);
	}
	
	public void instantiatePageObjects() throws Exception {
		generate = new DataGenerator();
		driver = launchApp();
		navigationBar = new NavigationBar(driver);
		accountTab = new BuyerAccountPage(driver);
		signupPage = new SignupPage(driver);
		buyerGeneral = new BuyerGeneral(driver);
		commonAction = new UICommonMobile(driver);
		
//		commonAction.waitSplashScreenLoaded();
		new NotificationPermission(driver).clickAllowBtn();
	}		
	
    @BeforeClass
    public void setUp() throws Exception {
        PropertiesUtil.setEnvironment("STAG");
        getCredentials();
        getExpectedMailMsg(language, new Login().getInfo(new Login().setLoginInformation(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD).getLoginInformation()).getStoreName());
    }

    @BeforeMethod
    public void generateData() throws Exception{
        instantiatePageObjects();
    	generateTestData();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() throws IOException{
    	new Screenshot().takeScreenshot(driver);
        driver.quit();
        if (driverWeb != null) driverWeb.quit();
    }    
    
    @Test
    public void Signup_02_RequiredFieldsLeftEmpty() {

    	String[] account = {mail, phone};
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	for (String username : account) {
    		
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
    	Assert.assertEquals(signupPage.getUsernameError(), "Điền từ 8 – 15 số");
    	
    	// 16-digit phone number
    	signupPage.inputUsername(generate.generateNumber(16));
    	Assert.assertFalse(signupPage.isContinueBtnEnabled());
    	Assert.assertEquals(signupPage.getUsernameError(), "Điền từ 8 – 15 số");
    }
    
    @Test
    public void Signup_06_WrongVerificationCodeForEmailAccount() throws SQLException {
    	username = mail;
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	fillOutRegistrationForm(country, username, password, displayName);
    	
		String code = getVerificationCode(phoneCode, username);
    	
		signupPage.inputVerificationCode(String.valueOf(Integer.parseInt(code) + 1)).clickVerifyBtn();
		Assert.assertEquals(signupPage.getVerificationCodeError(), "Mã không hợp lệ");
    	
    	/* Verify new activation code is sent to buyers */
    	commonAction.sleepInMiliSecond(10000);
    	String[][] mailContent = getEmailContent(username);
    	Assert.assertEquals(mailContent[0][3], code + " " + expectedCodeMsg);
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
    	
    	Assert.assertEquals(buyerGeneral.getToastMessage(), "Đã gửi lại mã. Vui lòng kiểm tra email");
    	
    	String newCode = getVerificationCode(phoneCode, username);
    	
    	Assert.assertNotEquals(newCode, code, "Resent code");
    	
    	signupPage.inputVerificationCode(code).clickVerifyBtn();
    	Assert.assertEquals(signupPage.getVerificationCodeError(), "Mã không hợp lệ");
    	
    	/* Verify new activation code is sent to buyers */
    	commonAction.sleepInMiliSecond(10000);
    	String[][] mailContent = getEmailContent(username);
    	Assert.assertEquals(mailContent[0][3], newCode + " " + expectedCodeMsg);
    	Assert.assertEquals(mailContent[1][3], code + " " + expectedCodeMsg);
    }
    
    @Test
    public void Signup_09_ResendVerificationCodeForPhoneAccount() throws SQLException {
    	username = phone;
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	fillOutRegistrationForm(country, username, password, displayName);
    	
    	String code = getVerificationCode(phoneCode, username);
    	
    	signupPage.clickResendBtn();
    	
    	Assert.assertEquals(buyerGeneral.getToastMessage(), "Đã gửi lại mã. Vui lòng kiểm tra SMS");
    	
    	String newCode = getVerificationCode(phoneCode, username);
    	
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
    
    @Test
    public void Signup_12_LocalEmailAccount() throws Exception {
    	
		country = "Vietnam";
		phoneCode = generate.getPhoneCode(country);
		phone = generate.randomPhoneByCountry(country); 
		mail = "auto0-buyer" + phone + "@mailnesia.com";
		displayName = "Auto Buyer " + phone;
    	username = mail;
    	
    	createAccount(country, phoneCode, username, password, displayName);
    	
    	logOutOfApp();
    	
    	logintoApp(country, username, password);
    	
    	verifyBuyerDataOnApp(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
    	
    	verifyBuyerDataOnDashboard(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
		
    	verifyBuyerLocationInDatabase(STORE_COUNTRY, phoneCode, username);
		
		// Verify mails sent to the user saying the sign-up is successful
		if (!username.matches("\\d+")) {
			verifySignupMail(username);			
		}
    }  
    
    @Test
    public void Signup_13_LocalPhoneAccount() throws Exception {
    	
		country = "Vietnam";
		phoneCode = generate.getPhoneCode(country);
		phone = generate.randomPhoneByCountry(country); 
		mail = "auto0-buyer" + phone + "@mailnesia.com";
		displayName = "Auto Buyer " + phone;
		username = phone;
    	
    	createAccount(country, phoneCode, username, password, displayName);
    	
    	logOutOfApp();
    	
    	logintoApp(country, username, password);
    	
    	verifyBuyerDataOnApp(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
    	
    	verifyBuyerDataOnDashboard(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
		
    	verifyBuyerLocationInDatabase(STORE_COUNTRY, phoneCode, username);
		
		// Verify mails sent to the user saying the sign-up is successful
		if (!username.matches("\\d+")) {
			verifySignupMail(username);			
		}
    }      
    
    @Test
    public void Signup_14_ForeignPhoneAccount() throws Exception {
    	
		username = phone;
    	
    	createAccount(country, phoneCode, username, password, displayName);
    	
    	logOutOfApp();
    	
    	logintoApp(country, username, password);
    	
    	verifyBuyerDataOnApp(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
    	
    	verifyBuyerDataOnDashboard(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
    	
    	verifyBuyerLocationInDatabase(STORE_COUNTRY, phoneCode, username);
    	
    	// Verify mails sent to the user saying the sign-up is successful
    	if (!username.matches("\\d+")) {
    		verifySignupMail(username);			
    	}
    }    
    
    @Test
    public void Signup_15_FirstLogin() throws Exception {
    	
    	username = mail;
    	
    	createAccountOnSF(country, phoneCode, username, password, displayName, birthday);
    	
    	logintoApp(country, username, password);
    	
    	verifyBuyerDataOnApp(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
    	
    	verifyBuyerDataOnDashboard(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
    	
    	verifyBuyerLocationInDatabase(country, phoneCode, username);
    }      
    
    @Test
    public void Signup_16_Resignup() throws Exception {

    	username = phone;
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	fillOutRegistrationForm(country, username, password, displayName);
    	
    	// Abort registration at verification screen
    	commonAction.sleepInMiliSecond(2000);
    	commonAction.hideKeyboard("android");
    	commonAction.navigateBack();
    	commonAction.hideKeyboard("android");
    	commonAction.navigateBack();
    	
    	// Register again
    	createAccount(country, phoneCode, username, password, displayName);
    	
    	verifyBuyerDataOnApp(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
    	
    	verifyBuyerDataOnDashboard(STORE_COUNTRY, phoneCode, username, displayName, mail, birthday);
    	
    	verifyBuyerLocationInDatabase(STORE_COUNTRY, phoneCode, username);
    }      
    
}
