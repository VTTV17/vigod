package web.Dashboard;

import org.testng.annotations.*;
import utilities.model.sellerApp.login.LoginInformation;
import web.BaseTest;
import web.Dashboard.login.LoginPage;
import web.Dashboard.supplier.supplier.crud.CRUDSupplierPage;
import utilities.driver.InitWebdriver;

import java.io.File;

import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class UpdateSupplierTest extends BaseTest {
    @BeforeClass
    void setup() {
        driver = new InitWebdriver().getDriver(browser, headless);
        LoginInformation loginInformation = new LoginInformation();
        loginInformation.setEmail(ADMIN_ACCOUNT_THANG);
        loginInformation.setPassword(ADMIN_PASSWORD_THANG);
        new LoginPage(driver).loginDashboardByJsAndGetStoreInformation(loginInformation);
        tcsFileName = "check_product_detail_sf/Update supplier.xlsx".replace("/", File.separator);
    }

    /* Language : VIE */
    @Test
    void UP_SUPPLIER_01_ViewSupplierDetail() {
        testCaseId = "UP_SUPPLIER_01";
        new CRUDSupplierPage(driver).setLanguage(language).checkSupplierInformation();
    }

    @Test
    void UP_SUPPLIER_02_UpdateVNSupplier() throws Exception {
        testCaseId = "UP_SUPPLIER_02";
        new CRUDSupplierPage(driver).setLanguage(language).updateSupplier(true);
    }

    @Test
    void UP_SUPPLIER_03_UpdateNonVNSupplier() throws Exception {
        testCaseId = "UP_SUPPLIER_03";
        new CRUDSupplierPage(driver).setLanguage(language).updateSupplier(false);
    }

    @Test
    void UP_SUPPLIER_04_CheckErrorWhenLeaveRequiredFieldBlank() throws Exception {
        testCaseId = "UP_SUPPLIER_04";
        new CRUDSupplierPage(driver).setLanguage(language).checkErrorWhenLeaveRequiredFieldBlank();
    }

    @Test
    void UP_SUPPLIER_05_CheckErrorWhenInputDuplicateSupplierCode() throws Exception {
        testCaseId = "UP_SUPPLIER_05";
        new CRUDSupplierPage(driver).setLanguage(language).checkErrorWhenInputDuplicateSupplierCode();
    }

    @Test
    void UP_SUPPLIER_06_CheckErrorWhenInputInvalidFormatSupplierCode() throws Exception {
        testCaseId = "UP_SUPPLIER_06";
        new CRUDSupplierPage(driver).setLanguage(language).checkErrorWhenInputInvalidFormatSupplierCode();
    }

    @Test
    void UP_SUPPLIER_07_CheckOrderHistory() throws Exception {
        testCaseId = "UP_SUPPLIER_07";
        new CRUDSupplierPage(driver).setLanguage(language).checkOrderHistory();
    }

    @Test
    void UP_SUPPLIER_08_DeleteSupplier() throws Exception {
        testCaseId = "UP_SUPPLIER_08";
        new CRUDSupplierPage(driver).setLanguage(language).deleteSupplier();
    }
}
