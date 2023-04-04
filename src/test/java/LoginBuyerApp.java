import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import pages.buyerapp.LoginPage;
import pages.buyerapp.NavigationBar;
import pages.buyerapp.account.BuyerAccountPage;
import utilities.jsonFileUtility;
import utilities.data.DataGenerator;

public class LoginBuyerApp {

	AppiumDriver driver;
	BuyerAccountPage accountTab;
	NavigationBar navigationBar;
	LoginPage loginPage;
	
	DataGenerator generate;

	JsonNode sf = jsonFileUtility.readJsonFile("LoginInfo.json").findValue("storefront");
	String BUYER_MAIL_USERNAME = sf.findValue("buyer").findValue("mail").findValue("username").asText();
	String BUYER_MAIL_PASSWORD = sf.findValue("buyer").findValue("mail").findValue("password").asText();
	String BUYER_MAIL_COUNTRY = sf.findValue("buyer").findValue("mail").findValue("country").asText();
	
    @BeforeClass
    public void setUp() throws Exception {
		DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("deviceName", "Android");
        caps.setCapability("platformName", "Android");
        caps.setCapability("platformVersion", "12.0");
        caps.setCapability("udid", "RF8N20PY57D");
        caps.setCapability("appPackage", "com.mediastep.shop0017");
        caps.setCapability("appActivity", "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity");
        caps.setCapability("noReset", "false");
        
        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
        
    }

    @BeforeMethod
    public void generateData(){
       generate = new DataGenerator();
    }
    
    @Test
    public void LoginSF_04_LoginWithCorrectCredentials() throws InterruptedException {
    	loginPage = new LoginPage(driver);
    	navigationBar = new NavigationBar(driver);
    	accountTab = new BuyerAccountPage(driver);
    	
    	navigationBar.tapOnAccountIcon().clickLoginBtn();
    	loginPage.inputUsername(BUYER_MAIL_USERNAME).inputPassword(BUYER_MAIL_PASSWORD).clickLoginBtn();
    	
    	accountTab.clickLogoutBtn();
    }

    @AfterClass
    public void tearDown(){
        driver.quit();
    }

}
