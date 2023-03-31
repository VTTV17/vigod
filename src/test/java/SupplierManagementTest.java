import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.dashboard.login.LoginPage;
import utilities.driver.InitWebdriver;

import java.io.File;

import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class SupplierManagementTest extends BaseTest{
    @BeforeSuite
    @Parameters({"browser", "headless"})
    void initPreCondition(@Optional("chrome") String browser,
                          @Optional("true") String headless) {

        driver = new InitWebdriver().getDriver(browser, headless);
        new LoginPage(driver).loginDashboardByJsAndGetStoreInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);
        tcsFileName = "check_product_detail_sf/Update supplier.xlsx".replace("/", File.separator);
    }

    @Test
    void checkLanguage() {

    }
}
