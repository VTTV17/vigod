import org.testng.annotations.Test;
import pages.InternalTool;
import pages.Mailnesia;
import pages.dashboard.LoginPage;
import pages.dashboard.SignupPage;
import pages.dashboard.home.HomePage;
import pages.dashboard.settings.plans.PlansPage;

import java.io.IOException;
import java.sql.SQLException;

public class PlanPermissionTest extends BaseTest {
    LoginPage loginPage;
    HomePage homePage;
    PlansPage plansPage;
    InternalTool internalTool;
    String userName_goWeb = "automation0-shop285@mailnesia.com";
    String userName_goApp = "automation0-shop850@mailnesia.com";
    String userName_goPOS = "automation0-shop784@mailnesia.com";
    String userName_goSocial = "automation0-shop941@mailnesia.com";
    String userName_GoLead = "automation0-shop105@mailnesia.com";
    String password = "fortesting!1";
    String orderID;
    SignupPage signupPage;
    SignupDashboard signUpDashboardTest;
    public String SignUpForVNShopWithEmail() throws SQLException, InterruptedException {
        String country = "Vietnam";
        String currency = "Dong - VND(₫)";
        String language = "Tiếng Việt";
        String province = "Hồ Chí Minh";
        String district = "Quận 8";
        String ward = "Phường 2";
        String randomNumber = generate.generateNumber(5);
        String username = "automation0-shop" + randomNumber + "@mailnesia.com";
        String contact =  "9123456" + randomNumber;
        String pickupAddress = "12 Quang Trung";
        String secondPickupAddress = "16 Wall Street";
        String storeName = "Automation Shop " + randomNumber;
        signupPage = new SignupPage(driver);
        //Sign up
        signupPage.navigate()
                .fillOutSignupForm(country, username, password, "");
        Thread.sleep(8000);
        commonAction.openNewTab();
        commonAction.switchToWindow(1);
        String code = new Mailnesia(driver).navigate(username).getVerificationCode();
        commonAction.closeTab();
        commonAction.switchToWindow(0);
        signupPage.inputVerificationCode(code)
                .clickConfirmBtn();
        //Setup store
        signUpDashboardTest = new SignupDashboard(driver);
        signUpDashboardTest.setupShop(username, storeName, "", country, currency, language, contact, pickupAddress, secondPickupAddress, province, district, ward, "", "");
        return username;
    }

    /**
     *
     * @param plan Input value: GoWEB, GoAPP, GoPOS, GoSOCIAL, GoLEAD (need correct lowercase, uppercase)
     * @throws SQLException
     * @throws InterruptedException
     */
    public void SignUpSelectAndAprovePlan(String plan) throws SQLException,InterruptedException {
        switch (plan){
            case "GoWEB":
                userName_goWeb = SignUpForVNShopWithEmail();
                break;
            case "GoAPP":
                userName_goApp = SignUpForVNShopWithEmail();
                break;
            case "GoPOS":
                userName_goPOS = SignUpForVNShopWithEmail();
                break;
            case "GoSOCIAL":
                userName_goSocial = SignUpForVNShopWithEmail();
                break;
            case "GoLEAD":
                userName_GoLead = SignUpForVNShopWithEmail();
                break;
        }
        plansPage = new PlansPage(driver);
        plansPage.selectPlan(plan).selectPayment();
        orderID = plansPage.getOrderId();
        internalTool = new InternalTool(driver);
        internalTool.openNewTabAndNavigateToInternalTool()
                .login()
                .navigateToPage("GoSell","Packages","Orders list")
                .approveOrder(orderID);
    }

    public void checkPlanPermission(String packageType, String userName) throws IOException {
        loginPage = new LoginPage(driver);
        loginPage.navigate().performLogin(userName,password);
        homePage = new HomePage(driver);
        homePage.waitTillSpinnerDisappear()
                .checkPermissionAllPageByPackage(packageType)
                .completeVerifyPermissionByPackage();
    }
    @Test
    public void PP01_SelectAndAprovePlanGoWeb() throws SQLException, InterruptedException {
        SignUpSelectAndAprovePlan("GoWEB");
    }
    @Test
    public void PP02_CheckPermissionGoWeb() throws IOException {
        checkPlanPermission("GoWeb",userName_goWeb);
    }

    @Test
    public void PP03_SelectAndAprovePlanGoAPP() throws SQLException, InterruptedException {
        SignUpSelectAndAprovePlan("GoAPP");
    }
    @Test
    public void PP04_CheckPermissionGoApp() throws IOException {
        checkPlanPermission("GoApp",userName_goApp);
    }
    @Test
    public void PP05_SelectAndAprovePlanGoPOS() throws SQLException, InterruptedException {
        SignUpSelectAndAprovePlan("GoPOS");
    }
    @Test
    public void PP06_CheckPermissionGoPos() throws IOException {
        checkPlanPermission("GoPOS",userName_goPOS);
    }
    @Test
    public void PP07_SelectAndAprovePlanGoSocial() throws SQLException, InterruptedException {
        SignUpSelectAndAprovePlan("GoSOCIAL");
    }
    @Test
    public void PP08_CheckPermissionGoSocial() throws IOException {
        checkPlanPermission("GoSocial",userName_goSocial);
    }
    @Test
    public void PP09_SelectAndAprovePlanGoLead() throws SQLException, InterruptedException {
        SignUpSelectAndAprovePlan("GoLEAD");

    }
    @Test
    public void PP10_CheckPermissionLead() throws IOException {
        checkPlanPermission("GoLead",userName_GoLead);
    }
}
