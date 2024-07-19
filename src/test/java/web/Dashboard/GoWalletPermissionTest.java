package web.Dashboard;

import api.Seller.login.Login;
import api.Seller.products.all_products.CreateProduct;
import api.Seller.setting.PermissionAPI;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import web.Dashboard.gowallet.transactionhistory.TransactionHistoryPage;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.marketing.affiliate.information.Information;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utilities.account.AccountTest.*;
import static utilities.account.AccountTest.STAFF_SHOP_VI_PASSWORD;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;

public class GoWalletPermissionTest extends BaseTest{
    String sellerUserName;
    String sellerPassword;
    String staffUserName;
    String staffPass;
    String languageDB;
    LoginInformation ownerCredentials;
    LoginInformation staffCredentials;
    int groupPermissionId;
    LoginDashboardInfo staffLoginInfo;
    List<Integer> productIds = new ArrayList<>();

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
    public void writeResult(ITestResult result) throws IOException {
        driver.quit();
    }
    @DataProvider
    public Object[] GoWalletData(){
        return new Object[][]{
//                {"0"},
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
                {"1100"},
                {"1101"},
                {"1110"},
                {"1111"}
        };
    }
    @Test(dataProvider = "GoWalletData")
    public void checkGoWalletPermission(String binaryData){
        CreatePermission model = new CreatePermission();
        model.setHome_none("1");
        model.setGoWallet_none(binaryData);
        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Description Vi's Permission", model);
        //Get info of the staff after being granted the permission
        staffLoginInfo = new Login().getInfo(staffCredentials);
        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new TransactionHistoryPage(driver).verifyGoWalletPermission(allPermissions);
    }
}
