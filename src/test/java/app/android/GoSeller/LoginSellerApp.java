package app.android.GoSeller;
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

import app.GoSeller.home.HomePage;
import app.GoSeller.login.LoginPage;
import io.appium.java_client.AppiumDriver;
import utilities.commons.UICommonMobile;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;
import utilities.driver.InitAppiumDriver;
import utilities.driver.InitWebdriver;
import utilities.screenshot.Screenshot;
import utilities.thirdparty.Mailnesia;
import utilities.utils.PropertiesUtil;
import utilities.utils.jsonFileUtility;


public class LoginSellerApp {

	AppiumDriver driver;
	WebDriver driverWeb;
	LoginPage loginPage;
	HomePage homePage;
	
	DataGenerator generate;

	JsonNode data = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("dashboard");
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
        capabilities.setCapability("udid", "R5CW81WLFPT"); //192.168.2.43:5555 10.10.2.100:5555 RF8N20PY57D 
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("appPackage", "com.mediastep.GoSellForSeller.STG");
        capabilities.setCapability("appActivity", "com.mediastep.gosellseller.modules.credentials.login.LoginActivity");
        capabilities.setCapability("noReset", "false");
        
		return new InitAppiumDriver().getAppiumDriver(capabilities, "http://127.0.0.1:4723/wd/hub");
	}	    
    
    @BeforeMethod
    public void generateData() throws Exception{
    	driver = launchApp();
    	generate = new DataGenerator();
    	
    	new UICommonMobile(driver).waitSplashScreenLoaded();
    }

	@AfterMethod(alwaysRun = true)
	public void tearDown() throws IOException {
		new Screenshot().takeScreenshot(driver);
		driver.quit();
		if (driverWeb != null) driverWeb.quit();
	}	  
    

    
    
    //Bug: After users input verification code and submit, they are logged out. The screen flashes a few times
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
