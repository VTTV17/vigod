import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import io.appium.java_client.AppiumDriver;
import pages.buyerapp.NavigationBar;
import pages.buyerapp.SignupPage;
import pages.buyerapp.account.BuyerAccountPage;
import pages.thirdparty.Mailnesia;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;
import utilities.jsonFileUtility;
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
	
	DataGenerator generate;
	
	String username;
	String mail;
	String phone;
	String password;
	String country;
	String countryCode;
	String displayName;
	String birthday;

	JsonNode data = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
	JsonNode data1 = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("storefront");
	String STORE_USERNAME = data.findValue("seller").findValue("mail").findValue("username").asText();
	String STORE_PASSWORD = data.findValue("seller").findValue("mail").findValue("password").asText();
	String STORE_COUNTRY = data.findValue("seller").findValue("mail").findValue("country").asText();
	String BUYER_MAIL_USERNAME = data1.findValue("buyer").findValue("mail").findValue("username").asText();
	String BUYER_MAIL_PASSWORD = data1.findValue("buyer").findValue("mail").findValue("password").asText();
	String BUYER_MAIL_COUNTRY = data1.findValue("buyer").findValue("mail").findValue("country").asText();
	String BUYER_PHONE_USERNAME = data1.findValue("buyer").findValue("phone").findValue("username").asText();
	String BUYER_PHONE_PASSWORD = data1.findValue("buyer").findValue("phone").findValue("password").asText();
	String BUYER_PHONE_COUNTRY = data1.findValue("buyer").findValue("phone").findValue("country").asText();		
	
	
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
			verificationCode = new InitConnection().getActivationKey(username);
		}
		return verificationCode;
	}	

	public String randomCountry() {
		List<String> countries = generate.getCountryList();
		return countries.get(new Random().nextInt(0, countries.size()));
	}	
	
	public void generateTestData() {
		phone = generate.randomNumberGeneratedFromEpochTime(10);
		mail = "auto0-buyer" + phone + "@mailnesia.com";
		password = "fortesting!1";
//		country = randomCountry();
		country = "Vietnam";
		countryCode = generate.getCountryCode(country);
		displayName = "Automation Buyer " + phone;
		birthday = "21-02-1990";
	}	

	public void instantiatePageObjects() {
		generate = new DataGenerator();
	}		
	
    @BeforeClass
    public void setUp() throws Exception {
        String udid = "RF8N20PY57D";
        String platformName = "Android";
        String appPackage = "com.mediastep.shop0017";
        String appActivity = "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity";
        String url = "http://127.0.0.1:4723/wd/hub";
        driver = new InitAppiumDriver().getAppiumDriver(udid, platformName, appPackage, appActivity, url);
        
        PropertiesUtil.setEnvironment("STAG");
        
        instantiatePageObjects();
        
        String sessionId = driver.getSessionId().toString();
        System.out.println("Current session: " + sessionId);
    }

    @BeforeMethod
    public void generateData(){
    	generateTestData();
    }
    
    @Test
    public void Signup_02_RequiredFieldsLeftEmpty() {
    	navigationBar = new NavigationBar(driver);
    	accountTab = new BuyerAccountPage(driver);
    	signupPage = new SignupPage(driver);
    	
    	String[] account = {mail, phone};
    	
    	for (String username : account) {
    		navigationBar.tapOnAccountIcon().clickSignupBtn();
    		
    		if (username.matches("[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}")) {
    			signupPage.clickMailTab();
    		} else {
    			signupPage.clickPhoneTab();
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
        	
        	driver.navigate().back();
    	}
    }
    
    @Test
    public void Signup_04_InvalidEmailFormat() {
    	navigationBar = new NavigationBar(driver);
    	accountTab = new BuyerAccountPage(driver);
    	signupPage = new SignupPage(driver);
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	signupPage.clickMailTab();
    	
    	signupPage.clickUsername(); //Workaround to simulate a tap on username field
    	new UICommonMobile(driver).hideKeyboard("android");
    	
    	signupPage.clickAgreeTermBtn();
    	
    	// Mail does not have symbol @
    	signupPage.inputUsername(generate.generateString(10));
    	signupPage.inputPassword(password);
    	signupPage.inputDisplayName(displayName);
    	Assert.assertFalse(signupPage.isContinueBtnEnabled());
    	Assert.assertEquals(signupPage.getUsernameError(), "Email không đúng");
    	
    	// Mail does not have suffix '.<>'. Eg. '.com'
    	signupPage.inputUsername(generate.generateString(10) + "@");
    	Assert.assertFalse(signupPage.isContinueBtnEnabled());
    	Assert.assertEquals(signupPage.getUsernameError(), "Email không đúng");
    	
    	signupPage.inputUsername(generate.generateString(10) + "@" + generate.generateString(5) + ".");
    	Assert.assertFalse(signupPage.isContinueBtnEnabled());
    	Assert.assertEquals(signupPage.getUsernameError(), "Email không đúng");
    	
    	driver.navigate().back();
    }
    
    @Test
    public void Signup_05_InvalidPhoneFormat() {
    	navigationBar = new NavigationBar(driver);
    	accountTab = new BuyerAccountPage(driver);
    	signupPage = new SignupPage(driver);
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	signupPage.clickPhoneTab();
    	
    	signupPage.clickUsername(); //Workaround to simulate a tap on username field
    	new UICommonMobile(driver).hideKeyboard("android");
    	
    	signupPage.clickAgreeTermBtn();
    	
    	// 7-digit phone number
    	signupPage.inputUsername(generate.generateNumber(7));
    	signupPage.inputPassword(password);
    	signupPage.inputDisplayName(displayName);
    	Assert.assertFalse(signupPage.isContinueBtnEnabled());
    	Assert.assertEquals(signupPage.getUsernameError(), "Điền từ 8 - 15 số");
    	
    	// 16-digit phone number
    	signupPage.inputUsername(generate.generateNumber(16));
    	Assert.assertFalse(signupPage.isContinueBtnEnabled());
    	Assert.assertEquals(signupPage.getUsernameError(), "Điền từ 8 - 15 số");
    	
    	driver.navigate().back();
    }
    
    @Test
    public void Signup_06_WrongVerificationCodeForEmailAccount() throws SQLException {
    	username = mail;
    	
    	navigationBar = new NavigationBar(driver);
    	accountTab = new BuyerAccountPage(driver);
    	signupPage = new SignupPage(driver);
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	signupPage.clickMailTab();
    	
    	signupPage.inputUsername(username);
    	signupPage.inputPassword(password);
    	signupPage.inputDisplayName(displayName);
    	signupPage.inputBirthday(birthday);
    	signupPage.clickAgreeTermBtn();
    	signupPage.clickContinueBtn();
    	
		String code = "";
		if (username.matches("[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}")) {
			driverWeb = new InitWebdriver().getDriver("chrome");
			code = getVerificationCode(username);
			driverWeb.quit();
		} else {
			code = getVerificationCode(countryCode+":"+username);
		}
    	
		signupPage.inputVerificationCode(String.valueOf(Integer.parseInt(code) + 1));
		signupPage.clickVerifyBtn();
		Assert.assertEquals(signupPage.getVerificationCodeError(), "Mã không hợp lệ");
		
    	driver.navigate().back();
    	driver.navigate().back();
    }
    
    @Test
    public void Signup_07_WrongVerificationCodeForPhoneAccount() throws SQLException {
    	username = phone;
    	
    	navigationBar = new NavigationBar(driver);
    	accountTab = new BuyerAccountPage(driver);
    	signupPage = new SignupPage(driver);
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	signupPage.clickPhoneTab();
    	
    	signupPage.inputUsername(username);
    	signupPage.inputPassword(password);
    	signupPage.inputDisplayName(displayName);
    	signupPage.inputBirthday(birthday);
    	signupPage.clickAgreeTermBtn();
    	signupPage.clickContinueBtn();
    	
    	String code = "";
    	if (username.matches("[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}")) {
    		driverWeb = new InitWebdriver().getDriver("chrome");
    		code = getVerificationCode(username);
    		driverWeb.quit();
    	} else {
    		code = getVerificationCode(countryCode+":"+username);
    	}
    	
    	signupPage.inputVerificationCode(String.valueOf(Integer.parseInt(code) + 1));
    	signupPage.clickVerifyBtn();
    	Assert.assertEquals(signupPage.getVerificationCodeError(), "Mã không hợp lệ");
    	
    	driver.navigate().back();
    	driver.navigate().back();
    }
    
//    @Test
    public void Signup_08_ResendVerificationCodeForMailAccount() throws SQLException, InterruptedException {
    	username = "auto0-buyer1371379583@mailnesia.com";
    	
    	navigationBar = new NavigationBar(driver);
    	accountTab = new BuyerAccountPage(driver);
    	signupPage = new SignupPage(driver);
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	signupPage.clickMailTab();
    	
    	signupPage.inputUsername(username);
    	signupPage.inputPassword(password);
    	signupPage.inputDisplayName(displayName);
    	signupPage.inputBirthday(birthday);
    	signupPage.clickAgreeTermBtn();
    	signupPage.clickContinueBtn();
    	
    	String code = "";
    	if (username.matches("[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}")) {
    		driverWeb = new InitWebdriver().getDriver("chrome");
    		code = getVerificationCode(username);
    		driverWeb.quit();
    	} else {
    		code = getVerificationCode(countryCode+":"+username);
    	}
    	
    	signupPage.clickResendBtn();
    	
    	Assert.assertEquals(signupPage.getToastMessage(), "Đã gửi lại mã. Vui lòng kiểm tra email");
    	
    	String newCode = "";
    	if (username.matches("[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}")) {
    		Thread.sleep(10000);
    		driverWeb = new InitWebdriver().getDriver("chrome");
    		newCode = getVerificationCode(username);
    		driverWeb.quit();
    	} else {
    		Thread.sleep(2000);
    		newCode = getVerificationCode(countryCode+":"+username);
    	}
    	
    	Assert.assertNotEquals(newCode, code, "Resent code");
    	
    	signupPage.inputVerificationCode(code);
    	signupPage.clickVerifyBtn();
    	Assert.assertEquals(signupPage.getVerificationCodeError(), "Mã không hợp lệ");
    	
    	driver.navigate().back();
    	driver.navigate().back();
    }
    
//    @Test
    public void Signup_09_ResendVerificationCodeForPhoneAccount() throws SQLException, InterruptedException {
    	username = "1367214474";
    	
    	navigationBar = new NavigationBar(driver);
    	accountTab = new BuyerAccountPage(driver);
    	signupPage = new SignupPage(driver);
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	signupPage.clickPhoneTab();
    	
    	signupPage.inputUsername(username);
    	signupPage.inputPassword(password);
    	signupPage.inputDisplayName(displayName);
    	signupPage.inputBirthday(birthday);
    	signupPage.clickAgreeTermBtn();
    	signupPage.clickContinueBtn();
    	
    	String code = "";
    	if (username.matches("[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}")) {
    		driverWeb = new InitWebdriver().getDriver("chrome");
    		code = getVerificationCode(username);
    		driverWeb.quit();
    	} else {
    		code = getVerificationCode(countryCode+":"+username);
    	}
    	
    	signupPage.clickResendBtn();
    	
    	Assert.assertEquals(signupPage.getToastMessage(), "Đã gửi lại mã. Vui lòng kiểm tra SMS");
    	
    	String newCode = "";
    	if (username.matches("[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}")) {
    		Thread.sleep(10000);
    		driverWeb = new InitWebdriver().getDriver("chrome");
    		newCode = getVerificationCode(username);
    		driverWeb.quit();
    	} else {
    		Thread.sleep(2000);
    		newCode = getVerificationCode(countryCode+":"+username);
    	}
    	
    	Assert.assertNotEquals(newCode, code, "Resent code");
    	
    	signupPage.inputVerificationCode(code);
    	signupPage.clickVerifyBtn();
    	Assert.assertEquals(signupPage.getVerificationCodeError(), "Mã không hợp lệ");
    	
    	driver.navigate().back();
    	driver.navigate().back();
    }

    @Test
    public void Signup_10_ExistingEmailAccount() throws SQLException, InterruptedException {
    	username = BUYER_MAIL_USERNAME;
    	
    	navigationBar = new NavigationBar(driver);
    	accountTab = new BuyerAccountPage(driver);
    	signupPage = new SignupPage(driver);
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	signupPage.clickMailTab();
    	
    	signupPage.inputUsername(username);
    	signupPage.inputPassword(password);
    	signupPage.inputDisplayName(displayName);
    	signupPage.inputBirthday(birthday);
    	signupPage.clickAgreeTermBtn();
    	signupPage.clickContinueBtn();
    	
    	Assert.assertEquals(signupPage.getUsernameError(), "Email đã tồn tại");
    	
    	driver.navigate().back();
    }  
    
    @Test
    public void Signup_11_ExistingPhoneAccount() throws SQLException, InterruptedException {
    	username = BUYER_PHONE_USERNAME;
    	
    	navigationBar = new NavigationBar(driver);
    	accountTab = new BuyerAccountPage(driver);
    	signupPage = new SignupPage(driver);
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	signupPage.clickPhoneTab();
    	
    	signupPage.selectCountryCodeFromSearchBox(BUYER_PHONE_COUNTRY);
    	new UICommonMobile(driver).hideKeyboard("android");
    	
    	signupPage.inputUsername(username);
    	signupPage.inputPassword(password);
    	signupPage.inputDisplayName(displayName);
    	signupPage.inputBirthday(birthday);
    	signupPage.clickAgreeTermBtn();
    	signupPage.clickContinueBtn();
    	
    	Assert.assertEquals(signupPage.getUsernameError(), "Số điện thoại đã tồn tại");
    	
    	driver.navigate().back();
    }    
    
    @Test
    public void Signup_12_LocalEmailAccount() throws SQLException {
    	country = "Vietnam";
    	countryCode = generate.getCountryCode(country);
    	username = mail;
    	
    	navigationBar = new NavigationBar(driver);
    	accountTab = new BuyerAccountPage(driver);
    	signupPage = new SignupPage(driver);
    	
    	navigationBar.tapOnAccountIcon().clickSignupBtn();
    	
    	signupPage.clickMailTab();
    	
    	signupPage.selectCountryCodeFromSearchBox(BUYER_PHONE_COUNTRY);
    	new UICommonMobile(driver).hideKeyboard("android");
    	
    	signupPage.inputUsername(username);
    	signupPage.inputPassword(password);
    	signupPage.inputDisplayName(displayName);
    	signupPage.inputBirthday(birthday);
    	signupPage.clickAgreeTermBtn();
    	signupPage.clickContinueBtn();
    	
    	Assert.assertEquals(signupPage.getUsernameError(), "Số điện thoại đã tồn tại");
    	
    	driver.navigate().back();
    }    
    
    @AfterClass
    public void tearDown(){
        driver.quit();
        if (driverWeb != null) driverWeb.quit();
    }

}
