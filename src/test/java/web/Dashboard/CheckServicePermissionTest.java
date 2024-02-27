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
import java.io.IOException;


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

    @Test(dataProvider = "servicePermissionModel", dataProviderClass = PermissionDataProvider.class)
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
    @Test(dataProvider="serviceCollectionPermissionModel",dataProviderClass = PermissionDataProvider.class)
    public void checkPermissionServiceCollection(String serviceCollectionBinary){
        //Get info of staff
        staffLoginInfo = new Login().getInfo(staffCredentials);
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setHome_none("11");
        model.setService_serviceCollection(serviceCollectionBinary);
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
