import org.testng.annotations.Test;
import pages.InternalTool;
import pages.dashboard.LoginPage;
import pages.dashboard.home.HomePage;
import pages.dashboard.settings.plans.PlansPage;

public class PlanPermissionTest extends BaseTest {
    LoginPage loginPage;
    HomePage homePage;
    PlansPage plansPage;
    InternalTool internalTool;
    String userName_goWeb = "automation0-shop285@mailnesia.com";
    String password = "fortesting!1";
    String orderID;
    @Test
    public void SelectAndAprovePlan(){
        loginPage = new LoginPage(driver);
        loginPage.navigate().performLogin(userName_goWeb,password);
        homePage = new HomePage(driver);
        homePage.clickUpgradeNow();
        plansPage = new PlansPage(driver);
        plansPage.selectPlan("GoWEB")
                .selectPayment();
        orderID = plansPage.getOrderId();
        plansPage.clickLogout();
        internalTool = new InternalTool(driver);
        internalTool.openNewTabAndNavigateToInternalTool()
                .login()
                .navigateToPage("GoSell","Packages","Orders list")
                .approveOrder(orderID);
    }
}
