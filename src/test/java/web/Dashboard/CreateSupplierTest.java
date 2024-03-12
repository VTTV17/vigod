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

public class CreateSupplierTest extends BaseTest {
    @BeforeClass
    void setup() {
        driver = new InitWebdriver().getDriver(browser, headless);
        LoginInformation loginInformation = new LoginInformation();
        loginInformation.setEmail(ADMIN_ACCOUNT_THANG);
        loginInformation.setPassword(ADMIN_PASSWORD_THANG);
        new LoginPage(driver).loginDashboardByJsAndGetStoreInformation(loginInformation);
        tcsFileName = "check_product_detail_sf/Create supplier.xlsx".replace("/", File.separator);
    }

    @Test
    void CR_SUPPLIER_01_CreateVNSupplier() throws Exception {
        testCaseId = "CR_SUPPLIER_01";
        new CRUDSupplierPage(driver).setLanguage(language).createNewSupplier(true);
    }

    @Test
    void CR_SUPPLIER_02_CreateNonVNSupplier() throws Exception {
        testCaseId = "CR_SUPPLIER_02";
        new CRUDSupplierPage(driver).setLanguage(language).createNewSupplier(false);
    }

    @Test
    void CR_SUPPLIER_03_CheckErrorWhenLeaveRequiredFieldBlank() throws Exception {
        testCaseId = "CR_SUPPLIER_03";
        new CRUDSupplierPage(driver).setLanguage(language).checkErrorWhenLeaveRequiredFieldBlank();
    }

    @Test
    void CR_SUPPLIER_04_CheckErrorWhenInputDuplicateSupplierCode() throws Exception {
        testCaseId = "CR_SUPPLIER_04";
        new CRUDSupplierPage(driver).setLanguage(language).checkErrorWhenInputDuplicateSupplierCode();
    }

    @Test
    void CR_SUPPLIER_05_CheckErrorWhenInputInvalidFormatSupplierCode() throws Exception {
        testCaseId = "CR_SUPPLIER_05";
        new CRUDSupplierPage(driver).setLanguage(language).checkErrorWhenInputInvalidFormatSupplierCode();
    }
}
