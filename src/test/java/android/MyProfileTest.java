package android;

import api.dashboard.login.Login;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import pages.buyerapp.NavigationBar;
import pages.buyerapp.account.BuyerAccountPage;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitAppiumDriver;
import utilities.model.dashboard.services.ServiceInfo;

public class MyProfileTest {
    WebDriver driver;
    String buyer;
    String passBuyer;
    NavigationBar navigationBar;
    BuyerAccountPage accountPage;
    @BeforeClass
    public void setUp() throws Exception {
        String udid = "R5CR92R4K7V";
        String platformName = "Android";
        String appPackage = "com.mediastep.shop0037";
        String appActivity = "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity";
        String url = "http://127.0.0.1:4723/wd/hub";
        PropertiesUtil.setEnvironment("STAG");
        PropertiesUtil.setSFLanguage("VIE");
        driver = new InitAppiumDriver().getAppiumDriver(udid, platformName, appPackage, appActivity, url);
        buyer = AccountTest.SF_USERNAME_VI_1;
        passBuyer = AccountTest.SF_SHOP_VI_PASSWORD;
    }
    @AfterClass
    public void tearDown(){
        driver.quit();
    }
    @AfterMethod
    public void restartApp(){
        ((AndroidDriver) driver).resetApp();
        new UICommonMobile(driver).waitSplashScreenLoaded();
    }
    @BeforeMethod
    public void login(){
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnAccountIcon()
                .clickLoginBtn()
                .performLogin(buyer,passBuyer);
    }
    @Test
    public void MUP01_CheckTextOfMyProfilePage() throws Exception {
        accountPage = new BuyerAccountPage(driver);
        accountPage.clickProfile().verifyTextMyProfile();
    }
}
