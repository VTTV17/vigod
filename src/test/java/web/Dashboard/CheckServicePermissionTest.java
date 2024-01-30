package web.Dashboard;

import api.Seller.login.Login;
import api.Seller.services.CreateServiceAPI;
import api.Seller.services.EditServiceAPI;
import api.Seller.setting.PermissionAPI;
import api.Seller.setting.StaffManagement;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;
import org.testng.ITestResult;
import org.testng.annotations.*;
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

import javax.sound.midi.Soundbank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utilities.account.AccountTest.*;

public class CheckServicePermissionTest extends BaseTest {
    String sellerUserName;
    String sellerPassword;
    String languageDB;
    LoginInformation ownerCredentials;
    LoginInformation staffCredentials;
    int staffCreatedServiceId;
    int ownerCreatedServiceId;
    String staffUserName;
    String staffPass;
    LoginDashboardInfo staffLoginInfo;
    int groupPermissionId;
    @BeforeClass
    public void beforeClass() {
        sellerUserName = ADMIN_SHOP_VI_USERNAME;
        sellerPassword = ADMIN_SHOP_VI_PASSWORD;
        staffUserName = STAFF_SHOP_VI_USERNAME;
        staffPass = STAFF_SHOP_VI_PASSWORD;
        languageDB = language;
    }
    @BeforeMethod
    public void beforeMethod(){
        driver = new InitWebdriver().getDriver(browser, "false");
        ownerCredentials = new Login().setLoginInformation("+84",sellerUserName,sellerPassword).getLoginInformation();

        ownerCreatedServiceId = new CreateServiceAPI(ownerCredentials).createService(new ServiceInfo()).getServiceId();
        //Create a permisison
        staffCredentials = new Login().setLoginInformation("+84",staffUserName,staffPass).getLoginInformation();
        groupPermissionId = new PermissionAPI(ownerCredentials).createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);
        //
        staffLoginInfo = new Login().getInfo(staffCredentials);
        new CheckPermission(driver).waitUntilPermissionUpdated(staffLoginInfo.getStaffPermissionToken(),staffCredentials);
        staffCreatedServiceId = new CreateServiceAPI(staffCredentials).createService(new ServiceInfo()).getServiceId();
        //call api update service to Inactive
        EditServiceAPI editServiceAPI = new EditServiceAPI(ownerCredentials);
        editServiceAPI.setActiveStatus(false);
        try {
            editServiceAPI.updateService(staffCreatedServiceId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        //clear data - delete all created group permission
        new PermissionAPI(ownerCredentials).deleteGroupPermission(groupPermissionId);
        super.writeResult(result);
        driver.quit();
    }
    @DataProvider()
    public Object[][] servicePermissionModel() {
        return new Object[][]
                {
//                        {"0","1"},
//                        {"1","1"},
//                        {"10","1"},
//                        {"11","1"},
//                        {"100","1"},
//                        {"101","1"},
//                        {"110","1"},
                        {"111","1"},
                        {"1000","1"},
                        {"1001","1"},
                        {"1010","1"},
//                        {"1011","1"},
//                        {"1100","1"},
//                        {"1101","1"},
//                        {"1110","1"},
//                        {"1111","1"},
//                        {"10000","1"},
//                        {"10001","1"},
//                        {"10010","1"},
//                        {"10011","1"},
//                        {"10100","1"},
//                        {"10101","1"},
//                        {"10110","1"},
//                        {"10111","1"},
//                        {"11000","1"},
//                        {"11001","1"},
//                        {"11010","1"},
//                        {"11011","1"},
//                        {"11100","1"},
//                        {"11101","1"},
//                        {"11110","1"},
//                        {"11111","1"},
//                        {"100000","1"},
//                        {"100001","1"},
//                        {"100010","1"},
//                        {"100011","1"},
//                        {"100100","1"},
//                        {"100101","1"},
//                        {"100110","1"},
//                        {"100111","1"},
//                        {"101000","1"},
//                        {"101001","1"},
//                        {"101010","1"},
//                        {"101011","1"},
//                        {"101100","1"},
//                        {"101101","1"},
//                        {"101110","1"},
//                        {"101111","1"},
//                        {"110000","1"},
//                        {"110001","1"},
//                        {"110010","1"},
//                        {"110011","1"},
//                        {"110100","1"},
//                        {"110101","1"},
//                        {"110110","1"},
//                        {"110111","1"},
//                        {"111000","1"},
//                        {"111001","1"},
//                        {"111010","1"},
//                        {"111011","1"},
//                        {"111100","1"},
//                        {"111101","1"},
//                        {"111110","1"},
//                        {"111111","1"},
//                        {"1000000","1"}
                };
    }

    @Test(dataProvider = "servicePermissionModel")
    public void CheckPermissionServiceList(String serviceList, String collectionList){
        //Get info of staff
        staffLoginInfo = new Login().getInfo(staffCredentials);
        //Get staff id
        int staffId = new StaffManagement(ownerCredentials).getStaffId(staffLoginInfo.getUserId());
        //Remove all permission groups from the staff
        new PermissionAPI(ownerCredentials).removeAllGroupPermissionsFromStaff(staffId);
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setService_serviceManagement(serviceList);
        model.setService_serviceCollection(collectionList);
        //Create a permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Tien's Permission", "Description Tien's Permission", model);
        //Get info of the staff after being granted the permission
        staffLoginInfo = new Login().getInfo(staffCredentials);
        new CheckPermission(driver).waitUntilPermissionUpdated(staffLoginInfo.getStaffPermissionToken(),staffCredentials);
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName,staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble().navigateToPage(Constant.SERVICES_MENU_ITEM_NAME);
        new ServiceManagementPage(driver,staffCredentials).navigateToServiceManagementUrl()
                .checkPermissionServiceManagement(allPermissions,staffCreatedServiceId,ownerCreatedServiceId)
                .completeVerifyStaffPermissionServiceManagement();
    }
    @Test
    public void checkPermissionServiceCollection(){

    }
}
