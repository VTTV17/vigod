package web.Dashboard;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.login.LoginPage;
import web.Dashboard.supplier.supplier.management.SupplierManagementPage;

import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class SupplierManagementTest extends BaseTest {
    LoginInformation loginInformation;

    @BeforeClass
    void setup() {
        driver = new InitWebdriver().getDriver(browser, headless);
        loginInformation = new LoginInformation();
        loginInformation.setEmail(ADMIN_ACCOUNT_THANG);
        loginInformation.setPassword(ADMIN_PASSWORD_THANG);
        new LoginPage(driver).loginDashboardByJs(loginInformation);
        tcsFileName = "Supplier management.xlsx";
    }

    @Test
    void MN_SUPPLIER_01_CheckUI() throws Exception {
        testCaseId = "MN_SUPPLIER_01";
        new SupplierManagementPage(driver).getLoginInformation(loginInformation).setLanguage(language).checkUISupplierManagementPage();
    }

    @Test
    void MN_SUPPLIER_02_CheckNavigateToAddSupplierPage() {
        testCaseId = "MN_SUPPLIER_02";
        new SupplierManagementPage(driver).getLoginInformation(loginInformation).setLanguage(language).checkNavigateToAddSupplierPage();
    }

    @Test
    void MN_SUPPLIER_03_SearchSupplierByInvalidSupplierCode() {
        testCaseId = "MN_SUPPLIER_03";
        new SupplierManagementPage(driver).getLoginInformation(loginInformation).setLanguage(language).checkSearchWithInvalidSupplierCode();
    }

    @Test
    void MN_SUPPLIER_04_SearchSupplierByValidSupplierCode() {
        testCaseId = "MN_SUPPLIER_04";
        new SupplierManagementPage(driver).getLoginInformation(loginInformation).setLanguage(language).checkSearchWithValidSupplierCode();
    }

    @Test
    void MN_SUPPLIER_05_SearchSupplierByInvalidSupplierName() {
        testCaseId = "MN_SUPPLIER_05";
        new SupplierManagementPage(driver).getLoginInformation(loginInformation).setLanguage(language).checkSearchWithInvalidSupplierName();
    }

    @Test
    void MN_SUPPLIER_06_SearchSupplierByValidSupplierName() {
        testCaseId = "MN_SUPPLIER_06";
        new SupplierManagementPage(driver).getLoginInformation(loginInformation).setLanguage(language).checkSearchWithValidSupplierName();
    }
}
