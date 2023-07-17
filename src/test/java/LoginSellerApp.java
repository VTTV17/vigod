import java.io.IOException;
import java.sql.SQLException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import io.appium.java_client.AppiumDriver;
import pages.sellerapp.HomePage;
import pages.sellerapp.LoginPage;
import pages.thirdparty.Mailnesia;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;
import utilities.jsonFileUtility;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;
import utilities.driver.InitAppiumDriver;
import utilities.driver.InitWebdriver;
import utilities.screenshot.Screenshot;


public class LoginSellerApp {

	AppiumDriver driver;
	WebDriver driverWeb;
	LoginPage loginPage;
	HomePage homePage;
	
	DataGenerator generate;

	JsonNode data = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
	String MAIL_COUNTRY = data.findValue("seller").findValue("mail").findValue("country").asText();
	String MAIL = data.findValue("seller").findValue("mail").findValue("username").asText();
	String PASSWORD = data.findValue("seller").findValue("mail").findValue("password").asText();
	String PHONE = data.findValue("seller").findValue("phone").findValue("username").asText();
	String PHONE_PASSWORD = data.findValue("seller").findValue("phone").findValue("password").asText();
	String PHONE_COUNTRY = data.findValue("seller").findValue("phone").findValue("country").asText();
	String PHONE_COUNTRYCODE = data.findValue("seller").findValue("phone").findValue("countryCode").asText();
	String STAFF = data.findValue("staff").findValue("mail").findValue("username").asText();
	String STAFF_PASSWORD = data.findValue("staff").findValue("mail").findValue("password").asText();
	String SELLER_FORGOT_MAIL_USERNAME = data.findValue("seller").findValue("forgotMail").findValue("username").asText();
	String SELLER_FORGOT_MAIL_PASSWORD = data.findValue("seller").findValue("forgotMail").findValue("password").asText();
	String SELLER_FORGOT_MAIL_COUNTRY = data.findValue("seller").findValue("forgotMail").findValue("country").asText();
	String SELLER_FORGOT_PHONE_USERNAME = data.findValue("seller").findValue("forgotPhone").findValue("username").asText();
	String SELLER_FORGOT_PHONE_PASSWORD = data.findValue("seller").findValue("forgotPhone").findValue("password").asText();
	String SELLER_FORGOT_PHONE_COUNTRY = data.findValue("seller").findValue("forgotPhone").findValue("country").asText();
	
	public String getResetCode(String phoneCode, String username) throws SQLException {
		if (username.matches("\\d+")) {
			return new InitConnection().getResetKey(phoneCode + ":" + username);
		}
		return new InitConnection().getResetKey(username);
	}	

	public String [][] getEmailContent(String username) {
		driverWeb = new InitWebdriver().getDriver("chrome", "noHeadless");
		String [][] mailContent = new Mailnesia(driverWeb).navigate(username).getListOfEmailHeaders();
		driverWeb.quit();
		return mailContent;
	}		
	
    @BeforeClass
    public void setUp() throws Exception {
        PropertiesUtil.setEnvironment("STAG");
    }

	public AppiumDriver launchApp() throws Exception {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("udid", "10.10.2.100:5555"); //192.168.2.43:5555 10.10.2.100:5555 RF8N20PY57D 
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appPackage", "com.mediastep.GoSellForSeller.STG");
        capabilities.setCapability("appActivity", "com.mediastep.gosellseller.modules.credentials.login.LoginActivity");
        capabilities.setCapability("noReset", "false");
        
        String url = "http://127.0.0.1:4723/wd/hub";

		return new InitAppiumDriver().getAppiumDriver(capabilities, url);
	}	    
    
    @BeforeMethod
    public void generateData() throws Exception{
    	driver = launchApp();
    	generate = new DataGenerator();
    	
    	new UICommonMobile(driver).waitSplashScreenLoaded();
//		new NotificationPermission(driver).clickAllowBtn();
    }

	@AfterMethod(alwaysRun = true)
	public void tearDown() throws IOException {
		new Screenshot().takeScreenshot(driver);
		driver.quit();
		if (driverWeb != null) driverWeb.quit();
	}	  
    
    @Test
    public void LoginDB_01_LoginWithEmptyCredentials() throws InterruptedException {
    	loginPage = new LoginPage(driver);
    	
    	//Empty username and password
    	loginPage.inputUsername("").inputPassword("").clickAgreeTerm();
    	Assert.assertFalse(loginPage.isLoginBtnEnabled());
    	
    	//Empty username
    	loginPage.inputUsername("").inputPassword(PASSWORD).clickAgreeTerm();
    	Assert.assertFalse(loginPage.isLoginBtnEnabled());
    	
    	//Empty password
    	loginPage.inputUsername(MAIL).inputPassword("").clickAgreeTerm();
    	Assert.assertFalse(loginPage.isLoginBtnEnabled());
    }
    
    @Test
    public void LoginDB_02_LoginWithInvalidPhoneFormat() throws InterruptedException {
    	loginPage = new LoginPage(driver);
    	
    	loginPage.clickUsername(); //Workaround to simulate a tap on username field
    	
    	new UICommonMobile(driver).hideKeyboard("android");
    	
    	//7-digit phone number
    	loginPage.inputUsername(generate.generateNumber(7)).inputPassword(PASSWORD).clickAgreeTerm();
    	
    	Assert.assertFalse(loginPage.isLoginBtnEnabled());
    	Assert.assertEquals(loginPage.getUsernameError(), "Điền từ 8 – 15 số");
    	
    	//16-digit phone number
    	loginPage.inputUsername(generate.generateNumber(16)).inputPassword(PASSWORD).clickAgreeTerm();
    	
    	Assert.assertEquals(loginPage.getUsernameError(), "Điền từ 8 – 15 số");
    }
    
    @Test
    public void LoginDB_03_LoginWithInvalidMailFormat() throws InterruptedException {
    	loginPage = new LoginPage(driver);
    	
    	loginPage.clickUsername(); //Workaround to simulate a tap on username field
    	
    	new UICommonMobile(driver).hideKeyboard("android");
    	
    	// Mail does not have symbol @
    	loginPage.inputUsername(generate.generateString(10)).inputPassword(PASSWORD).clickAgreeTerm();
    	
    	Assert.assertFalse(loginPage.isLoginBtnEnabled());
    	Assert.assertEquals(loginPage.getUsernameError(), "Email không đúng");
    	
    	// Mail does not have suffix '.<>'. Eg. '.com'
    	loginPage.inputUsername(generate.generateString(10) + "@").inputPassword(PASSWORD).clickAgreeTerm();
    	Assert.assertEquals(loginPage.getUsernameError(), "Email không đúng");
    	
    	loginPage.inputUsername(generate.generateString(10) + "@" + generate.generateString(5) + ".").inputPassword(PASSWORD).clickAgreeTerm();
    	Assert.assertEquals(loginPage.getUsernameError(), "Email không đúng");
    }

    @Test
    public void LoginDB_04_LoginWithIncorrectCredentials() throws InterruptedException {
    	loginPage = new LoginPage(driver);
    	
    	//On first attempt, the screen always gets refreshed
    	loginPage.performLogin(SELLER_FORGOT_MAIL_COUNTRY, SELLER_FORGOT_MAIL_USERNAME, generate.generateString(10));
    	Thread.sleep(2000);
    	
    	// Incorrect mail account
    	loginPage.performLogin(SELLER_FORGOT_MAIL_COUNTRY, SELLER_FORGOT_MAIL_USERNAME, generate.generateString(10));
    	Assert.assertEquals(loginPage.getPasswordError(), "Email/Số điện thoại hoặc mật khẩu không chính xác");
    	
    	// Incorrect phone account
    	loginPage.performLogin(SELLER_FORGOT_MAIL_COUNTRY, SELLER_FORGOT_PHONE_USERNAME, generate.generateString(10));
    	Assert.assertEquals(loginPage.getPasswordError(), "Email/Số điện thoại hoặc mật khẩu không chính xác");
    }    
    
    @Test
    public void LoginDB_05_LoginWithCorrectCredentials() throws InterruptedException {
    	loginPage = new LoginPage(driver);
    	homePage = new HomePage(driver);
    	
    	loginPage.performLogin(MAIL_COUNTRY, MAIL, PASSWORD);
    	Assert.assertTrue(homePage.isAccountTabDisplayed());
    	homePage.clickAccountTab().clickLogoutBtn().clickLogoutOKBtn();
    	
    	loginPage.performLogin(PHONE_COUNTRY, PHONE, PHONE_PASSWORD);
    	Assert.assertTrue(homePage.isAccountTabDisplayed());
    }
    
    @Test
    public void LoginDB_06_StaffLogin() throws InterruptedException {
    	loginPage = new LoginPage(driver);
    	homePage = new HomePage(driver);
    	
    	//On first attempt, the screen always gets refreshed
    	loginPage.clickStaffTab().inputUsername(STAFF).inputPassword(PASSWORD + "1").clickAgreeTerm().clickLoginBtn();
    	Thread.sleep(2000);
    	
    	// Incorrect credentials
    	loginPage.clickStaffTab().inputUsername(STAFF).inputPassword(PASSWORD + "1").clickAgreeTerm().clickLoginBtn();
    	Assert.assertEquals(loginPage.getPasswordError(), "Email/Số điện thoại hoặc mật khẩu không chính xác");
    	
    	// Correct credentials
    	loginPage.inputUsername(STAFF).inputPassword(PASSWORD).clickAgreeTerm().clickLoginBtn().clickAvailableShop();
    	Assert.assertTrue(homePage.isAccountTabDisplayed());
    }
    
    @Test
    public void LoginDB_07_SellerForgotPassword() throws SQLException {
    	
		String[][] testData = { 
				{ SELLER_FORGOT_MAIL_COUNTRY, SELLER_FORGOT_MAIL_USERNAME, SELLER_FORGOT_MAIL_PASSWORD },
				{ SELLER_FORGOT_PHONE_COUNTRY, SELLER_FORGOT_PHONE_USERNAME, SELLER_FORGOT_PHONE_PASSWORD }
		};
    	
    	loginPage = new LoginPage(driver);
    	homePage = new HomePage(driver);
    	
		for (String[] row : testData) {
			String country = row[0];
			String username = row[1];
			String password = row[2];

			String newPassword =  password + generate.generateNumber(6);
			
	    	loginPage.clickForgotPassword().inputUsername(username).inputNewPassword(newPassword).clickSendBtn();
	    	
			String code = getResetCode(generate.getPhoneCode(country), username);
	    	
	    	loginPage.inputVerificationCode(code).clickSendBtn();
	    	
	    	homePage.clickAccountTab().clickLogoutBtn().clickLogoutOKBtn();
	    	
	    	// Log into store with new password
	    	loginPage.performLogin(country, username, newPassword);
	    	Assert.assertTrue(homePage.isAccountTabDisplayed());
	    	
	    	homePage.clickAccountTab().clickLogoutBtn().clickLogoutOKBtn();	
	    	
	    	if (!username.matches("\\d+")) {
				String [][] mailContent = getEmailContent(username);
				Assert.assertTrue(mailContent[1][3].contains(code));
	    	}
		}    	
    }
}
