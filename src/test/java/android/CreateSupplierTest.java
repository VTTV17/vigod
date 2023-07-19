package android;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import pages.sellerapp.home.HomePage;
import pages.sellerapp.login.LoginPage;
import utilities.driver.InitAppiumDriver;

import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class CreateSupplierTest extends BaseTest{
    String udid = "10HC8G04UP0003U";
    String appPackage = "com.mediastep.GoSellForSeller.STG";
    String appActivity = "com.mediastep.gosellseller.modules.credentials.login.LoginActivity";
    String URL = "http://127.0.0.1:4723/wd/hub";

    @BeforeClass
    void setup() throws Exception {
//        tcsFileName = "android/Check promotion at product detail screen.xlsx".replace("/", File.separator);
        driver = new InitAppiumDriver().getAppiumDriver(udid, "ANDROID", appPackage, appActivity, URL);
        new LoginPage(driver).performLogin(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);
        new HomePage(driver).navigateToPage("Supplier");
    }

    @Test
    void T() {

    }

}
