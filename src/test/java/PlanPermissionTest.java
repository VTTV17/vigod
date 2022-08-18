import org.testng.annotations.Test;
import pages.InternalTool;
import pages.dashboard.LoginPage;
import pages.dashboard.home.HomePage;
import pages.dashboard.settings.plans.PlansPage;

import java.io.IOException;

public class PlanPermissionTest extends BaseTest {
    LoginPage loginPage;
    HomePage homePage;
    PlansPage plansPage;
    InternalTool internalTool;
    String userName_goWeb = "automation0-shop285@mailnesia.com";
    String userName_goApp = "automation0-shop850@mailnesia.com";
    String userName_goPOS = "automation0-shop784@mailnesia.com";
    String userName_goSocial = "automation0-shop941@mailnesia.com";
    String password = "fortesting!1";
    String orderID;
    @Test
    public void PP01_SelectAndAprovePlanGoWeb() throws IOException {
        loginPage = new LoginPage(driver);
        loginPage.navigate().performLogin(userName_goWeb,password);
        homePage = new HomePage(driver);
        homePage.clickUpgradeNow();
        plansPage = new PlansPage(driver);
        plansPage.selectPlan("GoWEB").selectPayment();
        orderID = plansPage.getOrderId();
        plansPage.clickLogout();
        internalTool = new InternalTool(driver);
        internalTool.openNewTabAndNavigateToInternalTool()
                .login()
                .navigateToPage("GoSell","Packages","Orders list")
                .approveOrder(orderID);
    }
    @Test
    public void PP02_SelectAndAprovePlanGoAPP() throws IOException {
        loginPage = new LoginPage(driver);
        loginPage.navigate().performLogin(userName_goApp,password);
        homePage = new HomePage(driver);
        homePage.clickUpgradeNow();
        plansPage = new PlansPage(driver);
        plansPage.selectPlan("GoAPP").selectPayment();
        orderID = plansPage.getOrderId();
        plansPage.clickLogout();
        internalTool = new InternalTool(driver);
        internalTool.openNewTabAndNavigateToInternalTool()
                .login()
                .navigateToPage("GoSell","Packages","Orders list")
                .approveOrder(orderID);
    }
    @Test
    public void PP03_SelectAndAprovePlanGoPOS() throws IOException {
        loginPage = new LoginPage(driver);
        loginPage.navigate().performLogin(userName_goPOS,password);
        homePage = new HomePage(driver);
        homePage.clickUpgradeNow();
        plansPage = new PlansPage(driver);
        plansPage.selectPlan("GoPOS").selectPayment();
        orderID = plansPage.getOrderId();
        plansPage.clickLogout();
        internalTool = new InternalTool(driver);
        internalTool.openNewTabAndNavigateToInternalTool()
                .login()
                .navigateToPage("GoSell","Packages","Orders list")
                .approveOrder(orderID);
    }
    @Test
    public void PP03_SelectAndAprovePlanGoSocial() throws IOException {
        loginPage = new LoginPage(driver);
        loginPage.navigate().performLogin(userName_goSocial,password);
        homePage = new HomePage(driver);
        homePage.clickUpgradeNow();
        plansPage = new PlansPage(driver);
        plansPage.selectPlan("GoSOCIAL").selectPayment();
        orderID = plansPage.getOrderId();
        plansPage.clickLogout();
        internalTool = new InternalTool(driver);
        internalTool.openNewTabAndNavigateToInternalTool()
                .login()
                .navigateToPage("GoSell","Packages","Orders list")
                .approveOrder(orderID);
    }
    @Test
    public void checkPermissionGoWebPackage() throws IOException {
        loginPage = new LoginPage(driver);
        loginPage.navigate().performLogin(userName_goSocial,password);
        homePage = new HomePage(driver);
        homePage.waitTillSpinnerDisappear()
                .checkPermissionAllPageByPackage("GoSocial")
                .completeVerifyPermissionByPackage();
    }
}
