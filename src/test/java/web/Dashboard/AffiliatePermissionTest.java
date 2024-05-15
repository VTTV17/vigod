package web.Dashboard;

import api.Seller.affiliate.commission.APICommissionManagement;
import api.Seller.affiliate.commission.APICreateEditCommission;
import api.Seller.affiliate.partner.APICreateEditPartner;
import api.Seller.affiliate.partner.APIPartnerManagement;
import api.Seller.login.Login;
import api.Seller.products.all_products.APIEditProduct;
import api.Seller.products.all_products.CreateProduct;
import api.Seller.setting.PermissionAPI;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.marketing.affiliate.CommissionInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.marketing.affiliate.commission.CommissionPage;
import web.Dashboard.marketing.affiliate.information.Information;
import web.Dashboard.marketing.affiliate.partner.PartnerPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utilities.account.AccountTest.*;
import static utilities.account.AccountTest.STAFF_SHOP_VI_PASSWORD;

public class AffiliatePermissionTest extends BaseTest{
    String sellerUserName;
    String sellerPassword;
    String staffUserName;
    String staffPass;
    String languageDB;
    LoginInformation ownerCredentials;
    LoginInformation staffCredentials;
    int groupPermissionId;
    LoginDashboardInfo staffLoginInfo;
    String productCreatedByShopOwner = "Gel Rửa Mặt La Roche-Posay Dành Cho Da Dầu, Nhạy Cảm 200ml Effaclar Purifying Foaming Gel For Oily Sensitive Skin";
    String productCreatedByStaff = "Ao thun staff tao";
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
        // Shop owner create product
        CreateProduct productInfo = new CreateProduct(ownerCredentials).createWithoutVariationProduct(false,100);
        productCreatedByShopOwner = productInfo.getProductName();
        productIds.add(productInfo.getProductID());

        //Create full permission for staff
        groupPermissionId = new PermissionAPI(ownerCredentials).createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);

//        //Staff create product
        productInfo = new CreateProduct(ownerCredentials).createWithoutVariationProduct(false,100);
        productCreatedByStaff = productInfo.getProductName();
        productIds.add(productInfo.getProductID()); }
    @BeforeMethod
    public void beforeMethod() {
        driver = new InitWebdriver().getDriver(browser, "false");
    }

    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        //clear data - delete all created group permission
        super.writeResult(result);
//        driver.quit();
    }
    @AfterClass
    public void afterClass(){
        //delete product
        for (int productId:productIds) {
            new APIEditProduct(ownerCredentials).deleteProduct(productId);
        }
        //delete  commission
        List<Integer> commissionIds = new APICommissionManagement(ownerCredentials).getCommissionIdList();
        for(int i=0; i<commissionIds.size()-4;i++){
            new APICommissionManagement(ownerCredentials).deleteCommission(commissionIds.get(i));
        }
    }
    @DataProvider
    public Object[] ViewInfomationData(){
        return new Object[][]{
                {"0"},
                {"1"}
        };
    }
    @Test(dataProvider = "ViewInfomationData")
    public void checkViewDropshipInfo(String dataBinary){
        CreatePermission model = new CreatePermission();
        model.setHome_none("1");
        model.setAffiliate_dropshipInformation(dataBinary);
        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Description Vi's Permission", model);
        //Get info of the staff after being granted the permission
        staffLoginInfo = new Login().getInfo(staffCredentials);
        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new Information(driver).verifyViewDropshipInfo(allPermissions);
    }
    @Test(dataProvider = "ViewInfomationData")
    public void checkViewResellerInfo(String dataBinary){
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setHome_none("1");
        model.setAffiliate_resellerInformation(dataBinary);
        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Description Vi's Permission", model);
        //Get info of the staff after being granted the permission
        staffLoginInfo = new Login().getInfo(staffCredentials);
        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new Information(driver).verifyViewResellerInfo(allPermissions);
    }
    public int getPartnerId(boolean isDropship){
        List<Integer> ids = isDropship?new APIPartnerManagement(ownerCredentials).getDropshipList():new APIPartnerManagement(ownerCredentials).getResellerList();
        if(ids.isEmpty()){
            if (isDropship) {
                new APICreateEditPartner(ownerCredentials).createPartner("Dropship");
            } else {
                new APICreateEditPartner(ownerCredentials).createPartner("Reseller");
            }
            ids = isDropship?new APIPartnerManagement(ownerCredentials).getDropshipList():new APIPartnerManagement(ownerCredentials).getResellerList();
        }
        return ids.get(0);
    }
    @DataProvider
    public Object[] DropshipPartnerData(){
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
//                {"10010"},
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
    @Test(dataProvider = "DropshipPartnerData")
    public void checkDropshipPartnerPers(String dataBinary){
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setHome_none("1");
        model.setAffiliate_dropshipPartner(dataBinary);
        //Get dropshipId
        int dropshipId = getPartnerId(true);
        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Description Vi's Permission", model);
        //Get info of the staff after being granted the permission
        staffLoginInfo = new Login().getInfo(staffCredentials);
        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new PartnerPage(driver).getLoginInfo(ownerCredentials).verifyPartnerPermission(allPermissions,dropshipId,true);
    }
    @DataProvider
    public Object[] ResellerPartnerData(){
        return new Object[][]{
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
                {"1111"}
        };
    }
    @Test(dataProvider = "ResellerPartnerData")
    public void checkResellerPartnerPers(String dataBinary){
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setHome_none("1");
        model.setAffiliate_resellerPartner(dataBinary);
        //Get resellerId
        int resellerId = getPartnerId(false);
        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Description Vi's Permission", model);
        //Get info of the staff after being granted the permission
        staffLoginInfo = new Login().getInfo(staffCredentials);
        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new PartnerPage(driver).getLoginInfo(ownerCredentials).verifyPartnerPermission(allPermissions,resellerId,false);
    }
    public int callAPIGetCommissionId(){
        List<Integer> ids = new APICommissionManagement(ownerCredentials).getCommissionIdList();
        int id;
        if(ids.isEmpty()){
            id = new APICreateEditCommission(ownerCredentials).createProductCommisionForAll(new CommissionInfo()).getId();
        }else id = ids.get(0);
        return id;
    }
    @DataProvider
    public Object[] CommissionData(){
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
        };
    }
    @Test(dataProvider = "CommissionData")
    public void checkCommissionPermisison(String dataBinary){
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setHome_none("1");
        model.setProduct_productManagement("1");
        model.setProduct_collection("1");
        model.setAffiliate_commission(dataBinary);
        //Get resellerId
        int commissionId = callAPIGetCommissionId();
        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Description Vi's Permission", model);
        //Get info of the staff after being granted the permission
        staffLoginInfo = new Login().getInfo(staffCredentials);
        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new CommissionPage(driver).verifyCommissionPagePermission(allPermissions,commissionId,productCreatedByShopOwner,productCreatedByStaff);
    }
}
