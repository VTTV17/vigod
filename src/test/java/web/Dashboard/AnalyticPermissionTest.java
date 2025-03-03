package web.Dashboard;

import api.Seller.login.Login;
import api.Seller.setting.PermissionAPI;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.commons.UICommonAction;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import web.Dashboard.analytics.OrderAnalytics;
import web.Dashboard.analytics.ReservationAnalytics;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.onlineshop.preferences.Configuration;

import java.io.IOException;

import static utilities.account.AccountTest.*;
import static utilities.account.AccountTest.STAFF_SHOP_VI_PASSWORD;

public class AnalyticPermissionTest extends BaseTest{
    String sellerUserName;
    String sellerPassword;
    String staffUserName;
    String staffPass;
    String languageDB;
    LoginInformation ownerCredentials;
    LoginInformation staffCredentials;
    int groupPermissionId;
    LoginDashboardInfo staffLoginInfo;

    @BeforeClass
    public void beforeClass() {
        sellerUserName = ADMIN_SHOP_VI_USERNAME;
        sellerPassword = ADMIN_SHOP_VI_PASSWORD;
        staffUserName = STAFF_SHOP_VI_USERNAME;
        staffPass = STAFF_SHOP_VI_PASSWORD;
        languageDB = language;
        ownerCredentials = new Login().setLoginInformation("+84", sellerUserName, sellerPassword).getLoginInformation();
        staffCredentials = new Login().setLoginInformation("+84", staffUserName, staffPass).getLoginInformation();

        //Create full permission for staff
        groupPermissionId = new PermissionAPI(ownerCredentials).createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);
    }
    @BeforeMethod
    public void beforeMethod() {
        driver = new InitWebdriver().getDriver(browser, "false");
    }

    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        driver.quit();
    }
    @DataProvider
    public Object[] OrderAnalyticData(){
        return new Object[][]{
                {"0"},
                {"10"},
                {"11"}
        };
    }
    @Test(dataProvider = "OrderAnalyticData")
    public void checkOrderAnalyticPermission(String binaryData){
        CreatePermission model = new CreatePermission();
        model.setHome_none("10");
        model.setAnalytics_orders(binaryData);
        model.setProduct_productManagement("11111");

        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Description Vi's Permission", model);
        //Get info of the staff after being granted the permission
        staffLoginInfo = new Login().getInfo(staffCredentials);
        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());

        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
//        new UICommonAction(driver).sleepInMiliSecond(2000);
        new HomePage(driver).waitTillSpinnerDisappear1();
        new OrderAnalytics(driver).getLoginInfo(ownerCredentials,staffCredentials).checkOrderAnalyticPermission(allPermissions);
    }
    @DataProvider
    public Object[] ReservationAnalyticData(){
       return new Object[][]{
               {"0"},
               {"1"}
       };
    }
    @Test(dataProvider = "ReservationAnalyticData")
    public void checkReservationAnalyticPermission(String binaryData){
        CreatePermission model = new CreatePermission();
        model.setHome_none("10");
        model.setAnalytics_reservation(binaryData);

        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Description Vi's Permission", model);
        //Get info of the staff after being granted the permission
        staffLoginInfo = new Login().getInfo(staffCredentials);
        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());

        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new ReservationAnalytics(driver).checkReservationAnalyticsPermission(allPermissions);
    }
}
