package web.Dashboard;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import web.BaseTest;
import web.Dashboard.login.LoginPage;
import web.Dashboard.supplier.supplier.crud.CRUDSupplierPage;

import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class CreateSupplierTest extends BaseTest {
    LoginInformation loginInformation;

    @BeforeClass
    void setup() {
        driver = new InitWebdriver().getDriver(browser, headless);
        loginInformation = new LoginInformation();
        loginInformation.setEmail(ADMIN_ACCOUNT_THANG);
        loginInformation.setPassword(ADMIN_PASSWORD_THANG);
        new LoginPage(driver).loginDashboardByJsAndGetStoreInformation(loginInformation);
        tcsFileName = "Create supplier.xlsx";
    }

    @Test
    void CR_SUPPLIER_01_CreateVNSupplier() throws Exception {
        testCaseId = "CR_SUPPLIER_01";
        new CRUDSupplierPage(driver).getLoginInformation(loginInformation).setLanguage(language).createNewSupplier(true);
    }

    @Test
    void CR_SUPPLIER_02_CreateNonVNSupplier() throws Exception {
        testCaseId = "CR_SUPPLIER_02";
        new CRUDSupplierPage(driver).getLoginInformation(loginInformation).setLanguage(language).createNewSupplier(false);
    }

    @Test
    void CR_SUPPLIER_03_CheckErrorWhenLeaveRequiredFieldBlank() throws Exception {
        testCaseId = "CR_SUPPLIER_03";
        new CRUDSupplierPage(driver).getLoginInformation(loginInformation).setLanguage(language).checkErrorWhenLeaveRequiredFieldBlank();
    }

    @Test
    void CR_SUPPLIER_04_CheckErrorWhenInputDuplicateSupplierCode() throws Exception {
        testCaseId = "CR_SUPPLIER_04";
        new CRUDSupplierPage(driver).getLoginInformation(loginInformation).setLanguage(language).checkErrorWhenInputDuplicateSupplierCode();
    }

    @Test
    void CR_SUPPLIER_05_CheckErrorWhenInputInvalidFormatSupplierCode() throws Exception {
        testCaseId = "CR_SUPPLIER_05";
        new CRUDSupplierPage(driver).getLoginInformation(loginInformation).setLanguage(language).checkErrorWhenInputInvalidFormatSupplierCode();
    }
}
