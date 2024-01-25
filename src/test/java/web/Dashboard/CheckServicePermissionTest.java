package web.Dashboard;

import api.Seller.login.Login;
import api.Seller.services.CreateServiceAPI;
import api.Seller.setting.PermissionAPI;
import api.Seller.setting.StaffManagement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utilities.commons.UICommonAction;
import utilities.constant.Constant;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.services.ServiceInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import utilities.permission.CheckPermission;
import web.BaseTest;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.service.ServiceManagementPage;

import static utilities.account.AccountTest.*;

public class CheckServicePermissionTest extends BaseTest {
    String sellerUserName;
    String sellerPassword;
    String languageDB;
    LoginInformation ownerCredentials;
    LoginInformation staffCredentials;
    int createdServiceId;
    int noCreatedServiceId;
    String staffUserName;
    String staffPass;
    LoginDashboardInfo staffLoginInfo;
    @BeforeClass
    public void beforeClass() {
        driver = new InitWebdriver().getDriver(browser, "false");
        sellerUserName = ADMIN_SHOP_VI_USERNAME;
        sellerPassword = ADMIN_SHOP_VI_PASSWORD;
        languageDB = language;
        ownerCredentials = new Login().setLoginInformation("+84",sellerUserName,sellerPassword).getLoginInformation();
        noCreatedServiceId = new CreateServiceAPI(ownerCredentials).createService(new ServiceInfo()).getServiceId();
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setService_serviceManagement("11111111");
        model.setService_serviceCollection("1");

        //Create a permisison
        int groupPermissionId = new PermissionAPI(ownerCredentials).createGroupPermissionAndGetID("Create group permission", "Create Description Tien's Permission", model);

        staffUserName = STAFF_SHOP_VI_USERNAME;
        staffPass = STAFF_SHOP_VI_PASSWORD;
        staffCredentials = new Login().setLoginInformation("+84",staffUserName,staffPass).getLoginInformation();
        staffLoginInfo = new Login().getInfo(staffCredentials);
        new CheckPermission(driver).waitUntilUpdatPermission(staffLoginInfo.getStaffPermissionToken(),staffCredentials);
        createdServiceId = new CreateServiceAPI(staffCredentials).createService(new ServiceInfo()).getServiceId();
    }
    @Test
    public void Check_Permission_Service_List(){
        //Get info of staff
        staffLoginInfo = new Login().getInfo(staffCredentials);

        //Get staff id
        int staffId = new StaffManagement(ownerCredentials).getStaffId(staffLoginInfo.getSellerID());

        //Remove all permission groups from the staff
        new PermissionAPI(ownerCredentials).removeAllGroupPermissionsFromStaff(staffId);

        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setService_serviceManagement("11111111");
        model.setService_serviceCollection("1");

        //Create a permisison
        int groupPermissionId = new PermissionAPI(ownerCredentials).createGroupPermissionAndGetID("Create group permission", "Create Description Tien's Permission", model);

        //Grant the permission to the staff
        new PermissionAPI(ownerCredentials).grantGroupPermissionToStaff(staffId, groupPermissionId);
        staffLoginInfo = new Login().getInfo(staffCredentials);
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        LoginPage login = new LoginPage(driver);
        login.navigate().performLogin(sellerUserName, sellerPassword);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble().navigateToPage(Constant.SERVICES_MENU_ITEM_NAME);
        new ServiceManagementPage(driver).goToCreateServicePage();
        new ServiceManagementPage(driver,ownerCredentials).checkPermissionServiceManagement(allPermissions,createdServiceId,noCreatedServiceId);
    }
}
