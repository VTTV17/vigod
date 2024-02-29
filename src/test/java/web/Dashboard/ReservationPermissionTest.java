package web.Dashboard;

import api.Seller.customers.Customers;
import api.Seller.login.Login;
import api.Seller.reservations.CreateBookingPOS;
import api.Seller.reservations.ReservationAPI;
import api.Seller.services.ServiceInfoAPI;
import api.Seller.setting.PermissionAPI;
import api.Seller.setting.StaffManagement;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.constant.Constant;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import utilities.permission.CheckPermission;
import web.BaseTest;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.reservation.POSReservation.POSCreateReservation;
import web.Dashboard.reservation.ReservationManagement;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static utilities.account.AccountTest.*;
import static utilities.account.AccountTest.STAFF_SHOP_VI_PASSWORD;

public class ReservationPermissionTest extends BaseTest {
    String sellerUserName;
    String sellerPassword;
    String staffUserName;
    String staffPass;
    String languageDB;
    LoginInformation ownerCredentials;
    LoginInformation staffCredentials;
    LoginDashboardInfo staffLoginInfo;
    int groupPermissionId;
    int serviceId;
    Customers customerAPI;
    @BeforeClass
    public void beforeClass() {
        sellerUserName = ADMIN_SHOP_VI_USERNAME;
        sellerPassword = ADMIN_SHOP_VI_PASSWORD;
        staffUserName = STAFF_SHOP_VI_USERNAME;
        staffPass = STAFF_SHOP_VI_PASSWORD;
        languageDB = language;
    }

    @BeforeMethod
    public void beforeMethod() {
        driver = new InitWebdriver().getDriver(browser, "false");
        ownerCredentials = new Login().setLoginInformation("+84", sellerUserName, sellerPassword).getLoginInformation();
        staffCredentials = new Login().setLoginInformation("+84", staffUserName, staffPass).getLoginInformation();
        customerAPI = new Customers(ownerCredentials);

    }

    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        //clear data - delete all created group permission
        new PermissionAPI(ownerCredentials).deleteGroupPermission(groupPermissionId);
        new ServiceInfoAPI(ownerCredentials).deleteService(serviceId);
        super.writeResult(result);
//        driver.quit();
    }

    @DataProvider
    public Object[] reservationPermissionModel() {
        return new Object[][]{
//                {"1"},
//                {"10"},
//                {"11"},
//                {"100"},
//                {"101"},
//                {"110"},
//                {"111"},
//                {"1000"},
//                {"1001"},
//                {"1010"},
//                {"1011"},
//                {"1100"},
//                {"1101"},
//                {"1110"},
//                {"1111"},
//                {"10000"},
//                {"10001"},
//                {"10010"}, //BH-31529
//                {"10011"},
//                {"10100"},
//                {"10101"},
//                {"10110"},
//                {"10111"},
//                {"11000"},
//                {"11001"},
//                {"11010"},
//                {"11011"},
//                {"11100"},
//                {"11101"},
//                {"11110"},
//                {"11111"},
//                {"100000"},
//                {"100001"},
//                {"100010"},
//                {"100011"},
//                {"100100"},
//                {"100101"},
//                {"100110"},
//                {"100111"},
//                {"101000"},
//                {"101001"},
//                {"101010"},
//                {"101011"},
//                {"101100"},
//                {"101101"},
//                {"101110"},
//                {"101111"},
//                {"110000"},
//                {"110001"},
//                {"110010"},
//                {"110011"},
//                {"110100"},
//                {"110101"},
//                {"110110"},
//                {"110111"},
//                {"111000"},
//                {"111001"},
//                {"111010"},
//                {"111011"},
//                {"111100"},
//                {"111101"},
//                {"111110"},
//                {"111111"}
        };
    }

    @Test(dataProvider = "reservationPermissionModel")
    public void CheckPermissionReservation(String reservationPer) {
        serviceId = new ServiceInfoAPI(ownerCredentials).getActiveServiceId();
        int toConfirmReservationId = new CreateBookingPOS(ownerCredentials).CreateBookingPOS(List.of(serviceId));
        int confirmedReservationId = new CreateBookingPOS(ownerCredentials).CreateBookingPOS(List.of(serviceId));
        new ReservationAPI(ownerCredentials).confirmReservationAPI(confirmedReservationId);
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setHome_none("11");
        model.setReservation_reservationManagement(reservationPer);
        //get staff login info
        staffLoginInfo = new Login().getInfo(staffCredentials);
        //create permisison
        groupPermissionId = new PermissionAPI(ownerCredentials).createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials, model);
        //Get info of the staff after being granted the permission
        new CheckPermission(driver).waitUntilPermissionUpdated(staffLoginInfo.getStaffPermissionToken(), staffCredentials);
        staffLoginInfo = new Login().getInfo(staffCredentials);
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble().navigateToPage(Constant.RESERVATIONS_MENU_ITEM_NAME);
        new ReservationManagement(driver)
                .checkPermissionReservationManagement(allPermissions, toConfirmReservationId, confirmedReservationId)
                .completeVerifyStaffPermissionReservationManagement();
    }

    CreatePermission setPermissionPOSModel(String POSPermissionBinary) {
        CreatePermission model = new CreatePermission();
        model.setHome_none("11");
        model.setService_serviceManagement("1");
        model.setCustomer_customerManagement("100000");
//        Random rd = new Random();
//        if (rd.nextBoolean()) {
//            model.setService_serviceManagement("1");
//        } else model.setService_serviceManagement("0");
//        if (rd.nextBoolean()) {
//            model.setCustomer_customerManagement("11");
//        } else if (rd.nextBoolean()) {
//            model.setCustomer_customerManagement("01");
//        } else model.setCustomer_customerManagement("00");
//        if (rd.nextBoolean()) {
//            model.setCustomer_customerManagement("100000");
//        }
        System.out.println("serviceManagement:"+model.getService_serviceManagement());
        System.out.println("customerManagement:"+model.getCustomer_customerManagement());
        model.setReservation_posService(POSPermissionBinary);
        return model;
    }
    @DataProvider
    public Object[] POSPermissionModel() {
        return new Object[][]{
//                {"1"},
//                {"10"},
//                {"11"},
//                {"100"},
//                {"101"},
//                {"110"},
                {"111"}
        };
    }
    @Test(dataProvider = "POSPermissionModel")
    public void checkPermissionPOS(String POSPermissionBinary) {
        List<String> customerNameNotAssigneds= customerAPI.getNamesOfCustomersAssignedToStaff(-1);
        String customerNameNotAssignedStaff = customerNameNotAssigneds.get(0);
        int staffUserId = new StaffManagement(ownerCredentials).getStaffId(new Login().getInfo(staffCredentials).getUserId());
        List<String> customerAssignedToStaffs = customerAPI.getNamesOfCustomersAssignedToStaff(staffUserId);
        List<Integer> customerIdNotAssigneds= customerAPI.getIdsOfCustomersAssignedToStaff(-1);
        if(customerAssignedToStaffs.size()==0)
            customerAPI.assignStaffToCustomer(staffUserId,customerIdNotAssigneds.get(0));
        String customerNameAssignedStaff = customerAPI.getNamesOfCustomersAssignedToStaff(staffUserId).get(0);
        //Create group permission
        groupPermissionId = new PermissionAPI(ownerCredentials).createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials, setPermissionPOSModel(POSPermissionBinary));
        //get staff login info
        staffLoginInfo = new Login().getInfo(staffCredentials);
        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new POSCreateReservation(driver,staffCredentials)
                .checkPermissionPOSService(allPermissions,customerNameAssignedStaff,customerNameNotAssignedStaff)
                .completeVerifyStaffPermissionPOSService();
    }
}
