import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;

public class AppiumTest {

	AppiumDriver driver;

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
    public void login() throws InterruptedException {
    	driver.findElement(By.id("com.mediastep.GoSellForSeller.STG:id/edtUsername")).sendKeys("tienvan-staging-vn@mailnesia.com");
    	driver.findElement(By.id("com.mediastep.GoSellForSeller.STG:id/edtPassword")).sendKeys("fortesting!1");
    	driver.findElement(By.id("com.mediastep.GoSellForSeller.STG:id/cbxTermAndPrivacy")).click();
    	driver.findElement(By.id("com.mediastep.GoSellForSeller.STG:id/btnLogin")).click();
    }

    @AfterClass
    public void tearDown(){
        driver.quit();
    }

}
