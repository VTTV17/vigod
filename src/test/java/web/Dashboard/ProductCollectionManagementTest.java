package web.Dashboard;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.products.productcollection.productcollectionmanagement.ProductCollectionManagement;
import utilities.utils.PropertiesUtil;
import utilities.driver.InitWebdriver;

import java.io.IOException;

import static utilities.account.AccountTest.ADMIN_SHOP_VI_PASSWORD;
import static utilities.account.AccountTest.ADMIN_SHOP_VI_USERNAME;

public class ProductCollectionManagementTest extends BaseTest {
    String languageDashboard;
    LoginPage loginDashboard;
    String userNameDb;
    String passwordDb;
    HomePage homePage;
    ProductCollectionManagement productCollectionManagement;
    @BeforeClass
    public void getData() throws Exception {
        languageDashboard = PropertiesUtil.getLanguageFromConfig("Dashboard");
        userNameDb = ADMIN_SHOP_VI_USERNAME;
        passwordDb = ADMIN_SHOP_VI_PASSWORD;
    }
    @BeforeMethod
    public void setUp(){
        driver = new InitWebdriver().getDriver(browser, "false");
    }
    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
        if (driver != null) driver.quit();
    }
    @Test
    public void verifyText() throws Exception {
        loginDashboard = new LoginPage(driver);
        loginDashboard.navigate().performLogin(userNameDb, passwordDb);
        homePage = new HomePage(driver);
        homePage.waitTillSpinnerDisappear().hideFacebookBubble().selectLanguage(languageDashboard);
        productCollectionManagement = new ProductCollectionManagement(driver);
        productCollectionManagement.navigateToProductCollectionManagement().verifyTextOfPage();
    }
}
