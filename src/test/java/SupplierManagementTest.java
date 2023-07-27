import api.dashboard.login.Login;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.dashboard.login.LoginPage;
import pages.dashboard.products.supplier.function.management.SupplierManagementPage;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;

import java.io.File;

import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class SupplierManagementTest extends BaseTest{
    LoginInformation loginInformation;

    @BeforeClass
    void setup() {
        driver = new InitWebdriver().getDriver(browser, headless);
        loginInformation = new Login().setLoginInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG).getLoginInformation();
        new LoginPage(driver).loginDashboardByJsAndGetStoreInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);
        tcsFileName = "check_product_detail_sf/Supplier management.xlsx".replace("/", File.separator);
    }

    @Test
    void MN_SUPPLIER_01_CheckUI() throws Exception {
        testCaseId = "MN_SUPPLIER_01";
        new SupplierManagementPage(driver, loginInformation).setLanguage(language).checkUISupplierManagementPage();
    }

    @Test
    void MN_SUPPLIER_02_CheckNavigateToAddSupplierPage() {
        testCaseId = "MN_SUPPLIER_02";
        new SupplierManagementPage(driver, loginInformation).setLanguage(language).checkNavigateToAddSupplierPage();
    }

    @Test
    void MN_SUPPLIER_03_SearchSupplierByInvalidSupplierCode() {
        testCaseId = "MN_SUPPLIER_03";
        new SupplierManagementPage(driver, loginInformation).setLanguage(language).checkSearchWithInvalidSupplierCode();
    }

    @Test
    void MN_SUPPLIER_04_SearchSupplierByValidSupplierCode() {
        testCaseId = "MN_SUPPLIER_04";
        new SupplierManagementPage(driver, loginInformation).setLanguage(language).checkSearchWithValidSupplierCode();
    }

    @Test
    void MN_SUPPLIER_05_SearchSupplierByInvalidSupplierName() {
        testCaseId = "MN_SUPPLIER_05";
        new SupplierManagementPage(driver, loginInformation).setLanguage(language).checkSearchWithInvalidSupplierName();
    }

    @Test
    void MN_SUPPLIER_06_SearchSupplierByValidSupplierName() {
        testCaseId = "MN_SUPPLIER_06";
        new SupplierManagementPage(driver, loginInformation).setLanguage(language).checkSearchWithValidSupplierName();
    }
}
