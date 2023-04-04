import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import pages.sellerapp.HomePage;
import pages.sellerapp.LoginPage;
import utilities.jsonFileUtility;
import utilities.data.DataGenerator;

public class LoginSellerApp {

	AppiumDriver driver;
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
	
    @BeforeClass
    public void setUp() throws Exception {
		DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("deviceName", "Android");
        caps.setCapability("platformName", "Android");
        caps.setCapability("platformVersion", "12.0");
        caps.setCapability("udid", "RF8N20PY57D");
        caps.setCapability("appPackage", "com.mediastep.GoSellForSeller.STG");
        caps.setCapability("appActivity", "com.mediastep.gosellseller.modules.credentials.login.LoginActivity");
        caps.setCapability("noReset", "false");
        
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
        
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
    	
    	driver.navigate().back(); //Workaround to hide keyboard
    	
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
    	
    	driver.navigate().back(); //Workaround to hide keyboard
    	
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
    public void LoginDB_04_LoginWithCorrectCredentials() throws InterruptedException {
    	loginPage = new LoginPage(driver);
    	homePage = new HomePage(driver);
    	
    	loginPage.inputUsername(MAIL).inputPassword(PASSWORD).clickAgreeTerm().clickLoginBtn();
    	Assert.assertTrue(homePage.isAccountTabDisplayed());
    	
    	homePage.clickAccountTab().clickLogoutBtn().clickLogoutOKBtn();
    	
    	loginPage.inputUsername(PHONE).inputPassword(PASSWORD).clickAgreeTerm().clickLoginBtn();
    	Assert.assertTrue(homePage.isAccountTabDisplayed());
    	
    	homePage.clickAccountTab().clickLogoutBtn().clickLogoutOKBtn();
    }

    @AfterClass
    public void tearDown(){
        driver.quit();
    }

}
