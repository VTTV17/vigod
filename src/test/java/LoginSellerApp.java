import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import pages.sellerapp.HomePage;
import pages.sellerapp.LoginPage;

public class LoginSellerApp {

	AppiumDriver driver;
	LoginPage loginPage;

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

    @Test
    public void LoginDB_01_LoginWithEmptyCredentials() throws InterruptedException {
    	loginPage = new LoginPage(driver);
    	
    	//Empty username and password
    	loginPage.inputUsername("");
    	loginPage.inputPassword("");
    	loginPage.clickAgreeTerm();
    	Assert.assertFalse(loginPage.isLoginBtnEnabled());
    	
    	//Empty username
    	loginPage.inputUsername("");
    	loginPage.inputPassword("fortesting!1");
    	loginPage.clickAgreeTerm();
    	Assert.assertFalse(loginPage.isLoginBtnEnabled());
    	
    	//Empty password
    	loginPage.inputUsername("tienvan-staging-vn@mailnesia.com");
    	loginPage.inputPassword("");
    	loginPage.clickAgreeTerm();
    	Assert.assertFalse(loginPage.isLoginBtnEnabled());
    	
    }
    
    @Test
    public void LoginDB_02_LoginWithCorrectCredentials() throws InterruptedException {
    	loginPage = new LoginPage(driver);
    	
    	//Correct credentials
    	loginPage.inputUsername("tienvan-staging-vn@mailnesia.com");
    	loginPage.inputPassword("fortesting!1");
    	loginPage.clickAgreeTerm();
    	Assert.assertTrue(loginPage.isLoginBtnEnabled());
    	loginPage.clickLoginBtn();
    	Thread.sleep(3000);
    	Assert.assertTrue(new HomePage(driver).isAccountTabDisplayed());
    	
    }

    @AfterClass
    public void tearDown(){
        driver.quit();
    }

}
