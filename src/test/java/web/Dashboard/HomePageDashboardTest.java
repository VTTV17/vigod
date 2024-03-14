package web.Dashboard;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import utilities.utils.PropertiesUtil;

import static utilities.account.AccountTest.ADMIN_SHOP_VI_PASSWORD;
import static utilities.account.AccountTest.ADMIN_SHOP_VI_USERNAME;

public class HomePageDashboardTest extends BaseTest {
    String languageDashboard;
    LoginPage loginDashboard;
    String userNameDb;
    String passwordDb;
    HomePage homePage;
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
        homePage.waitTillSpinnerDisappear().selectLanguage(languageDashboard)
                .waitTillLoadingDotsDisappear().verifyTextOfPage();
    }
}
