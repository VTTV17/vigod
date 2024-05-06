package web.Dashboard;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.login.LoginPage;
import web.Dashboard.products.all_products.management.ProductManagementPage;

import static utilities.account.AccountTest.ADMIN_ACCOUNT_THANG;
import static utilities.account.AccountTest.ADMIN_PASSWORD_THANG;

public class ProductManagementTest extends BaseTest {
    LoginInformation loginInformation;
    ProductManagementPage productManagementPage;
    @BeforeClass
    void setup() {
        driver = new InitWebdriver().getDriver(browser, headless);
        loginInformation = new LoginInformation("stgaboned@nbobd.com", ADMIN_PASSWORD_THANG);
        new LoginPage(driver).loginDashboardByJs(loginInformation);
        productManagementPage = new ProductManagementPage(driver).getLoginInformation(loginInformation);
    }

    @Test
    void MN_PRODUCT_01_CheckBulkActionClearStock() {
        productManagementPage.bulkClearStock();
    }

    @Test
    void MN_PRODUCT_02_CheckBulkActionDelete() {
        productManagementPage.bulkDeleteProduct();
    }

    @Test
    void MN_PRODUCT_03_CheckBulkActionDeactivate() {
        productManagementPage.bulkDeactivateProduct();
    }

    @Test
    void MN_PRODUCT_04_CheckBulkActionActivate() {
        productManagementPage.bulkActivateProduct();
    }

    @Test
    void MN_PRODUCT_05_CheckBulkActionUpdateStock() {
        productManagementPage.bulkUpdateStock();
    }

    @Test
    void MN_PRODUCT_06_CheckBulkActionUpdateTax() {
        productManagementPage.bulkUpdateTax();
    }
    @Test
    void MN_PRODUCT_07_CheckBulkActionDisplayOutOfStockProduct() {
        productManagementPage.bulkDisplayOutOfStockProduct();
    }

    @Test
    void MN_PRODUCT_08_CheckBulkActionUpdateSellingPlatform() {
        productManagementPage.bulkUpdateSellingPlatform();
    }

    @Test
    void MN_PRODUCT_09_CheckBulkActionUpdatePrice() {
        productManagementPage.bulkUpdatePrice();
    }
    @Test
    void MN_PRODUCT_10_CheckBulkActionSetStockAlert() {
        productManagementPage.bulkSetStockAlert();
    }

    @Test
    void MN_PRODUCT_11_CheckBulkActionManageStockByLotDate() {
        productManagementPage.bulkManageStockByLotDate();
    }

}
