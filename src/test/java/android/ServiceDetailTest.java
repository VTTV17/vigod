package android;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.buyerapp.BuyerGeneral;
import pages.buyerapp.NavigationBar;
import pages.buyerapp.search.BuyerSearchDetailPage;
import pages.buyerapp.search.BuyerSearchPage;

import java.net.URL;

public class ServiceDetailTest {
    NavigationBar navigationBar;
    BuyerSearchPage searchPage;
    BuyerSearchDetailPage searchDetailPage;
    WebDriver driver;

    @BeforeClass
    public void setUp() throws Exception {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("deviceName", "Android");
        caps.setCapability("platformName", "Android");
        caps.setCapability("platformVersion", "11.0");
        caps.setCapability("udid", "R5CR92R4K7V");
//        caps.setCapability("appPackage", "com.mediastep.GoSellForSeller.STG");
//        caps.setCapability("appActivity", "com.mediastep.gosellseller.modules.credentials.login.LoginActivity");
        caps.setCapability("appPackage", "com.mediastep.shop0003");
        caps.setCapability("appActivity", "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity");

        caps.setCapability("noReset", "false");

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), caps);
    }
    @Test
    public void demo(){

        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnSearchIcon()
                .tapOnSearchBar()
                .inputKeywordToSearch("Automation Service SVnSmnnuyefP");
    }
}
