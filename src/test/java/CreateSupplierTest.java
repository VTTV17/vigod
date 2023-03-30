import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.dashboard.login.LoginPage;
import pages.dashboard.products.supplier.function.crud.CRUDSupplierPage;
import utilities.driver.InitWebdriver;

import java.io.File;

import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class CreateSupplierTest extends BaseTest {
    @BeforeSuite
    @Parameters({"browser", "headless", "account", "password"})
    void initPreCondition(@Optional("chrome") String browser,
                          @Optional("true") String headless,
                          @Optional(ADMIN_ACCOUNT_THANG) String account,
                          @Optional(ADMIN_PASSWORD_THANG) String password) {

        driver = new InitWebdriver().getDriver(browser, headless);
        new LoginPage(driver).loginDashboardByJsAndGetStoreInformation(account, password);
        tcsFileName = "check_product_detail_sf/Create supplier.xlsx".replace("/", File.separator);
    }

    @Test
    void CR_SUPPLIER_VIE_01_CreateVNSupplier() throws Exception {
        testCaseId = "CR_SUPPLIER_VIE_01";
        new CRUDSupplierPage(driver).setLanguage("VIE").createNewSupplier(true);
    }

    @Test
    void CR_SUPPLIER_VIE_02_CreateNonVNSupplier() throws Exception {
        testCaseId = "CR_SUPPLIER_VIE_02";
        new CRUDSupplierPage(driver).setLanguage("VIE").createNewSupplier(false);
    }

    @Test
    void CR_SUPPLIER_VIE_03_CheckErrorWhenLeaveRequiredFieldBlank() throws Exception {
        testCaseId = "CR_SUPPLIER_VIE_03";
        new CRUDSupplierPage(driver).setLanguage("VIE").checkErrorWhenLeaveRequiredFieldBlank();
    }

    @Test
    void CR_SUPPLIER_VIE_04_CheckErrorWhenInputDuplicateSupplierCode() throws Exception {
        testCaseId = "CR_SUPPLIER_VIE_04";
        new CRUDSupplierPage(driver).setLanguage("VIE").checkErrorWhenInputDuplicateSupplierCode();
    }

    @Test
    void CR_SUPPLIER_VIE_05_CheckErrorWhenInputInvalidFormatSupplierCode() throws Exception {
        testCaseId = "CR_SUPPLIER_VIE_05";
        new CRUDSupplierPage(driver).setLanguage("VIE").checkErrorWhenInputInvalidFormatSupplierCode();
    }

    @Test
    void CR_SUPPLIER_ENG_01_CreateVNSupplier() throws Exception {
        testCaseId = "CR_SUPPLIER_ENG_01";
        new CRUDSupplierPage(driver).setLanguage("ENG").createNewSupplier(true);
    }

    @Test
    void CR_SUPPLIER_ENG_02_CreateNonVNSupplier() throws Exception {
        testCaseId = "CR_SUPPLIER_ENG_02";
        new CRUDSupplierPage(driver).setLanguage("ENG").createNewSupplier(false);
    }

    @Test
    void CR_SUPPLIER_ENG_03_CheckErrorWhenLeaveRequiredFieldBlank() throws Exception {
        testCaseId = "CR_SUPPLIER_ENG_03";
        new CRUDSupplierPage(driver).setLanguage("ENG").checkErrorWhenLeaveRequiredFieldBlank();
    }

    @Test
    void CR_SUPPLIER_ENG_04_CheckErrorWhenInputDuplicateSupplierCode() throws Exception {
        testCaseId = "CR_SUPPLIER_ENG_04";
        new CRUDSupplierPage(driver).setLanguage("ENG").checkErrorWhenInputDuplicateSupplierCode();
    }

    @Test
    void CR_SUPPLIER_ENG_05_CheckErrorWhenInputInvalidFormatSupplierCode() throws Exception {
        testCaseId = "CR_SUPPLIER_ENG_05";
        new CRUDSupplierPage(driver).setLanguage("ENG").checkErrorWhenInputInvalidFormatSupplierCode();
    }
}
