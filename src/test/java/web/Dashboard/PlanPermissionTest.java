package web.Dashboard;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import utilities.thirdparty.Mailnesia;

import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.settings.plans.PlansPage;
import web.Dashboard.signup.SignupPage;
import web.InternalTool.InternalTool;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;

import java.io.IOException;
import java.sql.SQLException;
import static utilities.account.AccountTest.*;
import static utilities.file.FileNameAndPath.FILE_PERMISSION_PLAN_TCS;

@Deprecated
public class PlanPermissionTest extends BaseTest {
    LoginPage loginPage;
    HomePage homePage;
    PlansPage plansPage;
    InternalTool internalTool;
    String userName_goWeb;
    String userName_goApp;
    String userName_goPOS;
    String userName_goSocial;
    String userName_GoLead;
    String password;
    String orderID;
    SignupPage signupPage;
    @BeforeClass
    public void getData(){
        userName_goWeb = ADMIN_USERNAME_GOWEB;
        userName_goApp = ADMIN_USERNAME_GOAPP;
        userName_goPOS = ADMIN_USERNAME_GOPOS;
        userName_goSocial = ADMIN_USERNAME_GOSOCIAL;
        userName_GoLead = ADMIN_USERNAME_GOLEAD;
        tcsFileName = FILE_PERMISSION_PLAN_TCS;
        password = ADMIN_CREATE_NEW_SHOP_PASSWORD;
        generate = new DataGenerator();
    }
    @BeforeMethod
    public void setUp(){
        driver = new InitWebdriver().getDriver(browser, headless);
    }
    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
        if (driver != null) driver.quit();
    }
    public void setupShop(String username, String storeName, String url, String contact, String pickupAddress, String province, String district, String ward) {
//        signupPage.inputStoreName(storeName);
        if (url != "") {
//            signupPage.inputStoreURL(url);
        }
        if (!username.matches("\\d+")) {
//            signupPage.inputStorePhone(contact);
        } else {
//            signupPage.inputStoreMail(contact);
        }
//        signupPage.inputPickupAddress(pickupAddress)
//                .selectProvince(province)
//                .selectDistrict(district)
//                .selectWard(ward)
//                .clickCompleteBtn();
    }

    public String SignUpForVNShopWithEmail() throws SQLException, InterruptedException {
        String country = "Vietnam";
        String province = "Hồ Chí Minh";
        String district = "Quận 8";
        String ward = "Phường 2";
        String randomNumber = generate.generateNumber(5);
        String username = "automation0-shop" + randomNumber + "@mailnesia.com";
        String contact =  "9123456" + randomNumber;
        String pickupAddress = "12 Quang Trung";
        String storeName = "Automation Shop " + randomNumber;
        signupPage = new SignupPage(driver);
        commonAction = new UICommonAction(driver);
        //Sign up
        signupPage.navigate()
                .fillOutSignupForm(country, username, password);
        Thread.sleep(8000);
        commonAction.openNewTab();
        commonAction.switchToWindow(1);
        String code = new Mailnesia(driver).navigate(username).getVerificationCode();
        commonAction.closeTab();
        commonAction.switchToWindow(0);
        signupPage.inputVerificationCode(code)
                .clickConfirmOTPBtn();
        //Setup store
        setupShop(username, storeName, "",  contact, pickupAddress, province, district, ward);
        return username;
    }

    /**
     *
     * @param plan Input value: GoWEB, GoAPP, GoPOS, GoSOCIAL, GoLEAD (need correct lowercase, uppercase)
     * @throws SQLException
     * @throws InterruptedException
     */
    public void SignUpSelectAndApprovePlan(String plan) throws SQLException,InterruptedException {
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
//        plansPage = new PlansPage(driver);
//        plansPage.selectPlan(plan);
//        new PackagePayment(driver).selectPaymentMethod(PaymentMethod.BANKTRANSFER);
//        orderID = new PackagePayment(driver).getOrderId();
//        plansPage = new PlansPage(driver);
//        new PackagePayment(driver).clickOnLogOut();
//        internalTool = new InternalTool(driver);
//        internalTool.openNewTabAndNavigateToInternalTool()
//                .login()
//                .navigateToPage("GoSell","Packages","Orders list")
//                .approveOrder(orderID).closeTab();
    }

    public void checkPlanPermission(String packageType, String userName) throws IOException {
        loginPage = new LoginPage(driver);
        loginPage.navigate().performLogin(userName,password);
        homePage = new HomePage(driver);
        homePage.waitTillSpinnerDisappear()
                .checkPermissionAllPageByPackage("ALL",packageType)
                .completeVerifyPermissionByPackage();
    }
    @Test
    public void PP01_CheckPermissionGoWebWithExistentAccount() throws  IOException {
        testCaseId = "PP01";
        checkPlanPermission("GoWeb",userName_goWeb);
    }
    @Test
    public void PP02_CheckPermissionGoWebWithNewAccount() throws IOException, SQLException, InterruptedException {
        testCaseId = "PP02";
        SignUpSelectAndApprovePlan("GoWEB");
        checkPlanPermission("GoWeb",userName_goWeb);
    }

    @Test
    public void PP03_CheckPermissionGoAppWithExistentAccount() throws IOException {
        testCaseId = "PP03";
        checkPlanPermission("GoApp",userName_goApp);
    }
    @Test
    public void PP04_CheckPermissionGoAppWithNewAccount() throws IOException, SQLException, InterruptedException {
        testCaseId = "PP04";
        SignUpSelectAndApprovePlan("GoAPP");
        checkPlanPermission("GoApp",userName_goApp);
    }
    @Test
    public void PP05_CheckPermissionGoPOSWithExistentAccount() throws IOException {
        testCaseId = "PP05";
        checkPlanPermission("GoPOS",userName_goPOS);
    }
    @Test
    public void PP06_CheckPermissionGoPosWithNewAccount() throws IOException, SQLException, InterruptedException {
        testCaseId = "PP06";
        SignUpSelectAndApprovePlan("GoPOS");
        checkPlanPermission("GoPOS",userName_goPOS);
    }
    @Test
    public void PP07_CheckPermissionGoSocialWithExistentAccount() throws IOException {
        testCaseId = "PP07";
        checkPlanPermission("GoSocial",userName_goSocial);
    }
    @Test
    public void PP08_CheckPermissionGoSocialWithNewAccount() throws IOException, SQLException, InterruptedException {
        testCaseId = "PP08";
        SignUpSelectAndApprovePlan("GoSOCIAL");
        checkPlanPermission("GoSocial",userName_goSocial);
    }
    @Test
    public void PP09_CheckPermissionGoLeadWithExistentAccount() throws IOException {
        testCaseId = "PP09";
        checkPlanPermission("GoLead",userName_GoLead);
    }
    @Test
    public void PP10_CheckPermissionGoLeadWithNewAccount() throws IOException, SQLException, InterruptedException {
        testCaseId = "PP10";
        SignUpSelectAndApprovePlan("GoLEAD");
        checkPlanPermission("GoLead",userName_GoLead);
    }
}
