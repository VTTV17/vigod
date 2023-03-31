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

public class UpdateSupplierTest extends BaseTest {
    @BeforeSuite
    @Parameters({"browser", "headless"})
    void initPreCondition(@Optional("chrome") String browser,
                          @Optional("true") String headless) {

        driver = new InitWebdriver().getDriver(browser, headless);
        new LoginPage(driver).loginDashboardByJsAndGetStoreInformation(ADMIN_ACCOUNT_THANG, ADMIN_PASSWORD_THANG);
        tcsFileName = "check_product_detail_sf/Update supplier.xlsx".replace("/", File.separator);
    }

    /* Language : VIE */
    @Test
    void UP_SUPPLIER_VIE_01_ViewSupplierDetail() throws Exception {
        testCaseId = "UP_SUPPLIER_VIE_01";
        new CRUDSupplierPage(driver).setLanguage("VIE").checkSupplierInformation();
    }

    @Test
    void UP_SUPPLIER_VIE_02_UpdateVNSupplier() throws Exception {
        testCaseId = "UP_SUPPLIER_VIE_02";
        new CRUDSupplierPage(driver).setLanguage("VIE").updateSupplier(true);
    }

    @Test
    void UP_SUPPLIER_VIE_03_UpdateNonVNSupplier() throws Exception {
        testCaseId = "UP_SUPPLIER_VIE_03";
        new CRUDSupplierPage(driver).setLanguage("VIE").updateSupplier(false);
    }

    @Test
    void UP_SUPPLIER_VIE_04_CheckErrorWhenLeaveRequiredFieldBlank() throws Exception {
        testCaseId = "UP_SUPPLIER_VIE_04";
        new CRUDSupplierPage(driver).setLanguage("VIE").checkErrorWhenLeaveRequiredFieldBlank();
    }

    @Test
    void UP_SUPPLIER_VIE_05_CheckErrorWhenInputDuplicateSupplierCode() throws Exception {
        testCaseId = "UP_SUPPLIER_VIE_05";
        new CRUDSupplierPage(driver).setLanguage("VIE").checkErrorWhenInputDuplicateSupplierCode();
    }

    @Test
    void UP_SUPPLIER_VIE_06_CheckErrorWhenInputInvalidFormatSupplierCode() throws Exception {
        testCaseId = "UP_SUPPLIER_VIE_06";
        new CRUDSupplierPage(driver).setLanguage("VIE").checkErrorWhenInputInvalidFormatSupplierCode();
    }

    @Test
    void UP_SUPPLIER_VIE_07_CheckOrderHistory() throws Exception {
        testCaseId = "UP_SUPPLIER_VIE_07";
        new CRUDSupplierPage(driver).setLanguage("VIE").checkOrderHistory();
    }

    @Test
    void UP_SUPPLIER_VIE_08_DeleteSupplier() throws Exception {
        testCaseId = "UP_SUPPLIER_VIE_08";
        new CRUDSupplierPage(driver).setLanguage("VIE").deleteSupplier();
    }

    /* Language : ENG */
    @Test
    void UP_SUPPLIER_ENG_01_ViewSupplierDetail() throws Exception {
        testCaseId = "UP_SUPPLIER_ENG_01";
        new CRUDSupplierPage(driver).setLanguage("ENG").checkSupplierInformation();
    }
    @Test
    void UP_SUPPLIER_ENG_02_UpdateVNSupplier() throws Exception {
        testCaseId = "UP_SUPPLIER_ENG_02";
        new CRUDSupplierPage(driver).setLanguage("ENG").updateSupplier(true);
    }

    @Test
    void UP_SUPPLIER_ENG_03_UpdateNonVNSupplier() throws Exception {
        testCaseId = "UP_SUPPLIER_ENG_03";
        new CRUDSupplierPage(driver).setLanguage("ENG").updateSupplier(false);
    }

    @Test
    void UP_SUPPLIER_ENG_04_CheckErrorWhenLeaveRequiredFieldBlank() throws Exception {
        testCaseId = "UP_SUPPLIER_ENG_04";
        new CRUDSupplierPage(driver).setLanguage("ENG").checkErrorWhenLeaveRequiredFieldBlank();
    }

    @Test
    void UP_SUPPLIER_ENG_05_CheckErrorWhenInputDuplicateSupplierCode() throws Exception {
        testCaseId = "UP_SUPPLIER_ENG_05";
        new CRUDSupplierPage(driver).setLanguage("ENG").checkErrorWhenInputDuplicateSupplierCode();
    }

    @Test
    void UP_SUPPLIER_ENG_06_CheckErrorWhenInputDuplicateSupplierCode() throws Exception {
        testCaseId = "UP_SUPPLIER_ENG_06";
        new CRUDSupplierPage(driver).setLanguage("ENG").checkErrorWhenInputInvalidFormatSupplierCode();
    }

    @Test
    void UP_SUPPLIER_ENG_07_CheckOrderHistory() throws Exception {
        testCaseId = "UP_SUPPLIER_ENG_07";
        new CRUDSupplierPage(driver).setLanguage("ENG").checkOrderHistory();
    }

    @Test
    void UP_SUPPLIER_ENG_08_DeleteSupplier() throws Exception {
        testCaseId = "UP_SUPPLIER_ENG_08";
        new CRUDSupplierPage(driver).setLanguage("ENG").deleteSupplier();
    }
}
