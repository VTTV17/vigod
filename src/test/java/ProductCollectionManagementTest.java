import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import pages.dashboard.products.productcollection.productcollectionmanagement.ProductCollectionManagement;
import utilities.PropertiesUtil;

import static utilities.account.AccountTest.ADMIN_SHOP_VI_PASSWORD;
import static utilities.account.AccountTest.ADMIN_SHOP_VI_USERNAME;

public class ProductCollectionManagementTest extends BaseTest{
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
