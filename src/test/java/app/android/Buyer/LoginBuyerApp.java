package app.android.Buyer;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import api.Seller.login.Login;
import io.appium.java_client.AppiumDriver;
import app.Buyer.account.BuyerAccountPage;
import app.Buyer.account.BuyerMyProfile;
import app.Buyer.buyergeneral.BuyerGeneral;
import app.Buyer.login.LoginPage;
import app.Buyer.navigationbar.NavigationBar;
import app.Buyer.notificationpermission.NotificationPermission;
import app.Buyer.signup.SignupPage;
import web.Dashboard.customers.allcustomers.AllCustomers;
import web.Dashboard.customers.allcustomers.details.CustomerDetails;
import web.Dashboard.home.HomePage;
import utilities.thirdparty.Mailnesia;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonMobile;
import utilities.utils.jsonFileUtility;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;
import utilities.driver.InitAppiumDriver;
import utilities.driver.InitWebdriver;
import utilities.screenshot.Screenshot;

public class LoginBuyerApp {

	AppiumDriver driver;
	WebDriver driverWeb;
	BuyerAccountPage accountTab;
	NavigationBar navigationBar;
	LoginPage loginPage;
	SignupPage signupPage;
	BuyerGeneral buyerGeneral;
	UICommonMobile commonAction;
	DataGenerator generate;
	
	String language = "VIE";
	String expectedCodeMsg;
	String expectedChangePasswordMsg;
	List<String> oldPass;

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
	
	JsonNode sf = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("storefront");
	JsonNode gm = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("gomua");
	String BUYER_FORGOT_MAIL_USERNAME = sf.findValue("buyer").findValue("forgotMail").findValue("username").asText();
	String BUYER_FORGOT_MAIL_PASSWORD = sf.findValue("buyer").findValue("forgotMail").findValue("password").asText();
	String BUYER_FORGOT_MAIL_COUNTRY = sf.findValue("buyer").findValue("forgotMail").findValue("country").asText();
	String BUYER_FORGOT_PHONE_USERNAME = sf.findValue("buyer").findValue("forgotPhone").findValue("username").asText();
	String BUYER_FORGOT_PHONE_PASSWORD = sf.findValue("buyer").findValue("forgotPhone").findValue("password").asText();
	String BUYER_FORGOT_PHONE_COUNTRY = sf.findValue("buyer").findValue("forgotPhone").findValue("country").asText();
	String GOMUA_MAIL_USERNAME = gm.findValue("buyer").findValue("mail").findValue("username").asText();
	String GOMUA_MAIL_PASSWORD = gm.findValue("buyer").findValue("mail").findValue("password").asText();
	String GOMUA_MAIL_COUNTRY = gm.findValue("buyer").findValue("mail").findValue("country").asText();
	String GOMUA_PHONE_USERNAME = gm.findValue("buyer").findValue("phone").findValue("username").asText();
	String GOMUA_PHONE_PASSWORD = gm.findValue("buyer").findValue("phone").findValue("password").asText();
	String GOMUA_PHONE_COUNTRY = gm.findValue("buyer").findValue("phone").findValue("country").asText();
	
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

	@BeforeClass
	public void setUp() throws Exception {
		PropertiesUtil.setEnvironment("STAG");
		getCredentials();
		getExpectedMailMsg(language, new Login().getInfo(new Login().setLoginInformation(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD).getLoginInformation()).getStoreName());
	}

	@BeforeMethod
	public void generateData() throws Exception {
		instantiatePageObjects();
	}	

	@AfterMethod(alwaysRun = true)
	public void tearDown() throws IOException {
		new Screenshot().takeScreenshot(driver);
		driver.quit();
		if (driverWeb != null) driverWeb.quit();
	}	
	
	public void instantiatePageObjects() throws Exception {
		generate = new DataGenerator();
		driver = launchApp();
		navigationBar = new NavigationBar(driver);
		accountTab = new BuyerAccountPage(driver);
		loginPage = new LoginPage(driver);
		signupPage = new SignupPage(driver);
		buyerGeneral = new BuyerGeneral(driver);
		commonAction = new UICommonMobile(driver);

//		commonAction.waitSplashScreenLoaded();
		new NotificationPermission(driver).clickAllowBtn();
	}	
	
	public AppiumDriver launchApp() throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("udid", "RF8N20PY57D"); //192.168.2.43:5555 10.10.2.100:5555 RF8N20PY57D 
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appPackage", "com.mediastep.shop0017");
        capabilities.setCapability("appActivity", "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity");
        capabilities.setCapability("noReset", "false");
        
        String url = "http://127.0.0.1:4723/wd/hub";

		return new InitAppiumDriver().getAppiumDriver(capabilities, url);
	}	

	public void getExpectedMailMsg(String language, String storeName) throws Exception {
		expectedCodeMsg = PropertiesUtil.getPropertiesValueBySFLang("mail.signup.changePassword.verificationCode", language).formatted(storeName);
		expectedChangePasswordMsg = PropertiesUtil.getPropertiesValueBySFLang("mail.signup.changePassword.success", language).formatted(storeName);
	}		
	
	public String getResetCode(String phoneCode, String username) throws SQLException {
		if (username.matches("\\d+")) {
			return new InitConnection().getResetKey(phoneCode + ":" + username);
		}
		return new InitConnection().getResetKey(username);
	}	

	public String [][] getEmailContent(String username) {
		commonAction.sleepInMiliSecond(5000);
		driverWeb = new InitWebdriver().getDriver("chrome", "noHeadless");
		String [][] mailContent = new Mailnesia(driverWeb).navigate(username).getListOfEmailHeaders();
		driverWeb.quit();
		return mailContent;
	}	

	public void verifyBuyerDataOnDashboard(String country, String phoneCode, String username, String displayName, String birthday) {
    	driverWeb = new InitWebdriver().getDriver("chrome", "noHeadless");
		new web.Dashboard.login.LoginPage(driverWeb).navigate().performLogin(STORE_COUNTRY, STORE_USERNAME, STORE_PASSWORD);
		new HomePage(driverWeb).selectLanguage("ENG");
		CustomerDetails customerDetail = new AllCustomers(driverWeb).navigate().selectBranch("None Branch").searchAndGoToCustomerDetailByName(displayName);
		if (!username.matches("\\d+")) {
			Assert.assertEquals(customerDetail.getEmail(), username);
		} else {
			Assert.assertEquals(customerDetail.getPhoneNumber(), phoneCode+":"+username);
		}
		Assert.assertEquals(customerDetail.getCountry(), country);
		driverWeb.quit();
	}		
	
	public void changePassword(String currentPassword, String newPassword) {
		new BuyerMyProfile(driver).clickChangePassword()
		.inputCurrentPassword(currentPassword)
		.inputNewPassword(newPassword)
		.clickChangePasswordDoneBtn();
	}		
	
	public List<String> resetToOriginalPassword(String currentPassword, String originalPassword) {
		List<String> oldPasswords = new ArrayList<String>(); 
		for (int i=0; i<5; i++) {
			String newPassword = (i==4) ? originalPassword : originalPassword + generate.generateNumber(3)+ "!";
			changePassword(currentPassword, newPassword);
			currentPassword = newPassword;
			
    		if (i==0||i==4) continue; // First and last changed passwords will not be added to the list
    		oldPasswords.add(newPassword);
		}
		return oldPasswords;
	}		
	
	@Test
	public void Login_11_ForgotPasswordForMailAccount() throws SQLException {
		String country = BUYER_FORGOT_MAIL_COUNTRY;
		String username = BUYER_FORGOT_MAIL_USERNAME;
		String password = BUYER_FORGOT_MAIL_PASSWORD;
		
		String newPassword = password + "@" + generate.generateNumber(3);
		
		navigationBar.tapOnAccountIcon().clickLoginBtn();
		
		loginPage.clickForgotPasswordLink()
		.inputUsername(username)
		.inputNewPassword(newPassword);
		signupPage.clickContinueBtn();
		
		String code = getResetCode(country, username);
		
		signupPage.inputVerificationCode(code).clickVerifyBtn();
		
		accountTab.clickProfile();
		
		commonAction.navigateBack();
		
		accountTab.logOutOfApp();
		
		accountTab.clickLoginBtn().performLogin(country, username, password);
		Assert.assertEquals(buyerGeneral.getToastMessage(), "Email hoặc mật khẩu không đúng");
		
		loginPage.inputPassword(newPassword).clickLoginBtn();
		
		accountTab.clickProfile();
		
		commonAction.swipeByCoordinatesInPercent(0.5, 0.8, 0.5, 0.2);
		oldPass = resetToOriginalPassword(newPassword, password);
		
		String [][] mailContent = getEmailContent(username);
		Assert.assertEquals(mailContent[0][3], expectedChangePasswordMsg);
		Assert.assertEquals(mailContent[1][3], code + " " + expectedCodeMsg);
	}
	
	@Test
	public void Login_12_ForgotPasswordForPhoneAccount() throws SQLException {
		String country = BUYER_FORGOT_PHONE_COUNTRY;
		String username = BUYER_FORGOT_PHONE_USERNAME;
		String password = BUYER_FORGOT_PHONE_PASSWORD;
		
		String newPassword = password + "@" + generate.generateNumber(3);
		
		navigationBar.tapOnAccountIcon().clickLoginBtn();
		
		loginPage.clickPhoneTab().clickForgotPasswordLink()
			.selectCountry(country)
			.inputUsername(username)
			.inputNewPassword(newPassword);
		
		signupPage.clickContinueBtn();
		
		String code = getResetCode(generate.getPhoneCode(country), username);
		
		signupPage.inputVerificationCode(code).clickVerifyBtn();
		
		accountTab.clickProfile();
		
		commonAction.navigateBack();
		
		accountTab.logOutOfApp();
		
		accountTab.clickLoginBtn().performLogin(country, username, password);
		Assert.assertEquals(buyerGeneral.getToastMessage(), "Sai số điện thoại hoặc mật khẩu");
		
		loginPage.inputPassword(newPassword).clickLoginBtn();
		
		accountTab.clickProfile();
		
		commonAction.swipeByCoordinatesInPercent(0.5, 0.8, 0.5, 0.2);
		resetToOriginalPassword(newPassword, password);
		
		commonAction.sleepInMiliSecond(2000);
	}
}
