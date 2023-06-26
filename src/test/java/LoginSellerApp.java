import java.sql.SQLException;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
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


public class LoginSellerApp {

	AppiumDriver driver;
	WebDriver driverWeb;
	LoginPage loginPage;
	HomePage homePage;
	
	DataGenerator generate;

	JsonNode data = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
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
	
	/**
	 * This method retrieves a verification code for a given username. 
	 * If the username is a valid email address, the method retrieves the verification code from Mailnesia.
	 * Otherwise, it retrieves the code from a database.
	 * @param username either an email address (tienvan@mailnesia.com) or a phone number (+84:0123456789)
	 * @return the retrieved verification code as a String
	 * @throws SQLException if there is an error retrieving the reset key from the database
	 */
	public String getVerificationCode(String username) throws SQLException {
		String verificationCode;
		if (username.matches("[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}")) {
			// Get verification code from Mailnesia
			verificationCode = new Mailnesia(driverWeb).navigateToMailAndGetVerifyCode(username);
		} else {
			verificationCode = new InitConnection().getResetKey(username);
		}
		return verificationCode;
	}	
	
    @BeforeClass
    public void setUp() throws Exception {
        String udid = "RF8N20PY57D";
        String platformName = "Android";
        String appPackage = "com.mediastep.GoSellForSeller.STG";
        String appActivity = "com.mediastep.gosellseller.modules.credentials.login.LoginActivity";
        String url = "http://127.0.0.1:4723/wd/hub";
        driver = new InitAppiumDriver().getAppiumDriver(udid, platformName, appPackage, appActivity, url);
        
        PropertiesUtil.setEnvironment("STAG");
    }

    @BeforeMethod
    public void generateData(){
       generate = new DataGenerator();
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
    	
    	loginPage.inputUsername(generate.generateString(10) + "@nbobd.com").inputPassword(generate.generateString(10))
    	.clickAgreeTerm().clickLoginBtn();
    	
    	Thread.sleep(2000);
    	
    	// Incorrect mail account
    	loginPage.inputUsername(generate.generateString(10) + "@nbobd.com").inputPassword(generate.generateString(10))
    	.clickAgreeTerm().clickLoginBtn();
    	
    	Assert.assertEquals(loginPage.getPasswordError(), "Email/Số điện thoại hoặc mật khẩu không chính xác");
    	
    	// Incorrect phone account
    	loginPage.inputUsername(generate.generateNumber(13)).inputPassword(generate.generateString(10))
    	.clickAgreeTerm().clickLoginBtn();
    	
    	Assert.assertEquals(loginPage.getPasswordError(), "Email/Số điện thoại hoặc mật khẩu không chính xác");
    	
    }    
    
    @Test
    public void LoginDB_05_LoginWithCorrectCredentials() throws InterruptedException {
    	loginPage = new LoginPage(driver);
    	homePage = new HomePage(driver);
    	
    	loginPage.inputUsername(MAIL).inputPassword(PASSWORD).clickAgreeTerm().clickLoginBtn();
    	Assert.assertTrue(homePage.isAccountTabDisplayed());
    	
    	homePage.clickAccountTab().clickLogoutBtn().clickLogoutOKBtn();
    	
    	loginPage.selectCountryCodeFromSearchBox(PHONE_COUNTRY).inputUsername(PHONE).inputPassword(PASSWORD).clickAgreeTerm().clickLoginBtn();
    	Assert.assertTrue(homePage.isAccountTabDisplayed());
    	
    	homePage.clickAccountTab().clickLogoutBtn().clickLogoutOKBtn();
    }
    
    @Test
    public void LoginDB_06_StaffLogin() throws InterruptedException {
    	loginPage = new LoginPage(driver);
    	homePage = new HomePage(driver);
    	
//    	loginPage.clickStaffTab();
//    	loginPage.inputUsername(STAFF).inputPassword(PASSWORD + "1").clickAgreeTerm().clickLoginBtn();
//    	Thread.sleep(3000);
    	
    	// Incorrect credentials
    	loginPage.clickStaffTab();
    	loginPage.inputUsername(STAFF).inputPassword(PASSWORD + "1").clickAgreeTerm().clickLoginBtn();
    	Assert.assertEquals(loginPage.getPasswordError(), "Email/Số điện thoại hoặc mật khẩu không chính xác");
    	
    	// Correct credentials
    	loginPage.inputUsername(STAFF).inputPassword(PASSWORD).clickAgreeTerm().clickLoginBtn().clickAvailableShop();
    	Assert.assertTrue(homePage.isAccountTabDisplayed());
    	
    	homePage.clickAccountTab().clickLogoutBtn().clickLogoutOKBtn();
    	
    	loginPage.clickAdminTab();
    }
    
    @Test
    public void LoginDB_07_SellerForgotPassword() throws InterruptedException, SQLException {
    	
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
	    	
			String code = "";
			if (username.matches("[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}")) {
				driverWeb = new InitWebdriver().getDriver("chrome");
				code = getVerificationCode(username);
				driverWeb.quit();
			} else {
				code = getVerificationCode(generate.getPhoneCode(country)+":"+username);
			}
	    	
	    	loginPage.inputVerificationCode(code);
	    	loginPage.clickSendBtn();
	    	
//	    	Thread.sleep(5000);
	    	homePage.clickAccountTab().clickLogoutBtn().clickLogoutOKBtn();
	    	
	    	// Log into store with new password
	    	loginPage.selectCountryCodeFromSearchBox(country).inputUsername(username).inputPassword(newPassword).clickAgreeTerm().clickLoginBtn();
	    	Assert.assertTrue(homePage.isAccountTabDisplayed());
	    	
	    	homePage.clickAccountTab().clickLogoutBtn().clickLogoutOKBtn();	
		}    	
    }

    @AfterClass
    public void tearDown(){
        driver.quit();
        if (driverWeb != null) driverWeb.quit();
    }

}
