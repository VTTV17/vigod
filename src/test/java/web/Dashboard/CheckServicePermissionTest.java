package web.Dashboard;

import api.Seller.login.Login;
import api.Seller.services.CreateServiceAPI;
import api.Seller.services.EditServiceAPI;
import api.Seller.services.ServiceCollectionAPI;
import api.Seller.services.ServiceInfoAPI;
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
import web.Dashboard.service.servicecollections.ServiceCollectionManagement;

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
        staffCredentials = new Login().setLoginInformation("+84",staffUserName,staffPass).getLoginInformation();
    }
    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        //clear data - delete all created group permission
        new PermissionAPI(ownerCredentials).deleteGroupPermission(groupPermissionId);
        new ServiceInfoAPI(ownerCredentials).deleteService(ownerCreatedServiceId);
        new ServiceInfoAPI(ownerCredentials).deleteService(staffCreatedServiceId);
        super.writeResult(result);
        driver.quit();
    }
    @DataProvider()
    public Object[][] servicePermissionModel() {
        return new Object[][]
                {
                        {"1","1"},
                        {"10","1"},
                        {"11","1"},
                        {"100","1"},
                        {"101","1"},
                        {"110","1"},
                        {"111","1"},
                        {"1000","1"},
                        {"1001","1"},
                        {"1010","1"},
                        {"1011","1"},
                        {"1100","1"},
                        {"1101","1"},
                        {"1110","1"},
                        {"1111","1"},
                        {"10000","1"},
                        {"10001","1"},
                        {"10010","1"},
                        {"10011","1"},
                        {"10100","1"},
                        {"10101","1"},
                        {"10110","1"},
                        {"10111","1"},
                        {"11000","1"},
                        {"11001","1"},
                        {"11010","1"},
                        {"11011","1"},
                        {"11100","1"},
                        {"11101","1"},
                        {"11110","1"},
                        {"11111","1"},
                        {"100000","1"},
                        {"100001","1"},
                        {"100010","1"},
                        {"100011","1"},
                        {"100100","1"},
                        {"100101","1"},
                        {"100110","1"},
                        {"100111","1"},
                        {"101000","1"},
                        {"101001","1"},
                        {"101010","1"},
                        {"101011","1"},
                        {"101100","1"},
                        {"101101","1"},
                        {"101110","1"},
                        {"101111","1"},
                        {"110000","1"},
                        {"110001","1"},
                        {"110010","1"},
                        {"110011","1"},
                        {"110100","1"},
                        {"110101","1"},
                        {"110110","1"},
                        {"110111","1"},
                        {"111000","1"},
                        {"111001","1"},
                        {"111010","1"},
                        {"111011","1"},
                        {"111100","1"},
                        {"111101","1"},
                        {"111110","1"},
                        {"111111","1"},
                        {"111111","0"},
                };
    }

    @Test(dataProvider = "servicePermissionModel")
    public void CheckPermissionServiceList(String serviceList, String collectionList){
        //Create a permisison
        staffLoginInfo = new Login().getInfo(staffCredentials);
        groupPermissionId = new PermissionAPI(ownerCredentials).createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);
        new CheckPermission(driver).waitUntilPermissionUpdated(staffLoginInfo.getStaffPermissionToken(),staffCredentials);
        // seller create service
        ownerCreatedServiceId = new CreateServiceAPI(ownerCredentials).createService(new ServiceInfo()).getServiceId();
        // staff create service
        staffCreatedServiceId = new CreateServiceAPI(staffCredentials).createService(new ServiceInfo()).getServiceId();
        //Get info of staff
        staffLoginInfo = new Login().getInfo(staffCredentials);
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setHome_none("11");
        model.setService_serviceManagement(serviceList);
        model.setService_serviceCollection(collectionList);
        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Tien's Permission", "Description Tien's Permission", model);
        //Get info of the staff after being granted the permission
        new CheckPermission(driver).waitUntilPermissionUpdated(staffLoginInfo.getStaffPermissionToken(),staffCredentials);
        staffLoginInfo = new Login().getInfo(staffCredentials);
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName,staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble().navigateToPage(Constant.SERVICES_MENU_ITEM_NAME);
        new ServiceManagementPage(driver,staffCredentials).navigateToServiceManagementUrl()
                .checkPermissionServiceManagement(allPermissions,staffCreatedServiceId,ownerCreatedServiceId)
                .completeVerifyStaffPermissionServiceManagement();
    }
    @DataProvider()
    public Object[][] serviceCollectionPermissionModel() {
        return new Object[][]
                {
                        {"1"},
                        {"10"},
                        {"11"},
                        {"100"},
                        {"101"},
                        {"110"},
                        {"111"},
                        {"1000"},
                        {"1001"},
                        {"1010"},
                        {"1011"},
                        {"1100"},
                        {"1101"},
                        {"1110"},
                        {"1111"},
                        {"10000"},
                        {"10001"},
                        {"10010"},
                        {"10011"},
                        {"10100"},
                        {"10101"},
                        {"10110"},
                        {"10111"},
                        {"11000"},
                        {"11001"},
                        {"11010"},
                        {"11011"},
                        {"11100"},
                        {"11101"},
                        {"11110"},
                        {"11111"}
                };
    }
    @Test(dataProvider="serviceCollectionPermissionModel")
    public void checkPermissionServiceCollection(String permissions){
        //Get info of staff
        staffLoginInfo = new Login().getInfo(staffCredentials);
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setHome_none("11");
        model.setService_serviceCollection(permissions);
        //create permisison
        groupPermissionId = new PermissionAPI(ownerCredentials).createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials,model);
        //Get info of the staff after being granted the permission
        new CheckPermission(driver).waitUntilPermissionUpdated(staffLoginInfo.getStaffPermissionToken(),staffCredentials);
        staffLoginInfo = new Login().getInfo(staffCredentials);
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        //Get collectionId
        int collectionId = new ServiceCollectionAPI(ownerCredentials).getNewestCollectionID();
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName,staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble().navigateToPage(Constant.SERVICES_MENU_ITEM_NAME);
        new ServiceCollectionManagement(driver,staffCredentials).navigateToServiceCollectUrl()
                .checkPermissionServiceCollection(allPermissions,collectionId)
                .completeVerifyStaffPermissionServiceCollection();
    }
}
