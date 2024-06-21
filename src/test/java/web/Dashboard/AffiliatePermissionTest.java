package web.Dashboard;

import api.Seller.affiliate.commission.APICommissionManagement;
import api.Seller.affiliate.commission.APICreateEditCommission;
import api.Seller.affiliate.order.APIPartnerOrders;
import api.Seller.affiliate.partner.APICreateEditPartner;
import api.Seller.affiliate.partner.APIPartnerManagement;
import api.Seller.customers.APIAllCustomers;
import api.Seller.customers.APIEditCustomer;
import api.Seller.login.Login;
import api.Seller.orders.pos.APICreateOrderPOS;
import api.Seller.products.all_products.APIEditProduct;
import api.Seller.products.all_products.CreateProduct;
import api.Seller.setting.PermissionAPI;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.driver.InitWebdriver;
import utilities.enums.ApproveStatus;
import utilities.enums.TransferStatus;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.marketing.affiliate.CommissionInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.marketing.affiliate.Customers.ResellerCustomers;
import web.Dashboard.marketing.affiliate.commission.CommissionPage;
import web.Dashboard.marketing.affiliate.information.Information;
import web.Dashboard.marketing.affiliate.order.PartnerOrdersPage;
import web.Dashboard.marketing.affiliate.partner.PartnerPage;
import web.Dashboard.marketing.affiliate.partnerinventory.TransferPage;
import web.Dashboard.marketing.affiliate.payout.payouthistory.PayoutHistoryElement;
import web.Dashboard.marketing.affiliate.payout.payouthistory.PayoutHistoryPage;
import web.Dashboard.marketing.affiliate.payout.payoutinformation.PayoutInformationPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utilities.account.AccountTest.*;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;

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
        MAX_PRICE = 999999L;
        CreateProduct productInfo = new CreateProduct(ownerCredentials).createWithoutVariationProduct(false,100);
        productCreatedByShopOwner = productInfo.getProductName();
        productIds.add(productInfo.getProductID());

        //Create full permission for staff
        groupPermissionId = new PermissionAPI(ownerCredentials).createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);

        //Staff create product
        productInfo = new CreateProduct(ownerCredentials).createWithoutVariationProduct(false,100);
        productCreatedByStaff = productInfo.getProductName();
        productIds.add(productInfo.getProductID());
    }
    @BeforeMethod
    public void beforeMethod() {
        driver = new InitWebdriver().getDriver(browser, "false");
    }

    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        //clear data - delete all created group permission
//        super.writeResult(result);
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
//                {"0"},
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
                {"11111"},
                {"100000"},
                {"100001"},
                {"100010"},
                {"100011"},
                {"100100"},
                {"100101"},
                {"100110"},
                {"100111"},
                {"101000"},
                {"101001"},
                {"101010"},
                {"101011"},
                {"101100"},
                {"101101"},
                {"101110"},
                {"101111"},
                {"110000"},
                {"110001"},
                {"110010"},
                {"110011"},
                {"110100"},
                {"110101"},
                {"110110"},
                {"110111"},
                {"111000"},
                {"111001"},
                {"111010"},
                {"111011"},
                {"111100"},
                {"111101"},
                {"111110"},
                {"111111"}
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
                {"1"},  //Bug: khong hien restricted khi khong co quyen add và edit
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

    //create order if order list has less than 2 PENDING order.
    public void callAPICreateDropshipOrderIfAny(boolean isProductCommission){
        int orderProductListSize;
        if(isProductCommission){
                orderProductListSize = new APIPartnerOrders(ownerCredentials).getOrderProductCommissionByApproveStatus(ApproveStatus.PENDING).size();
        }else orderProductListSize = new APIPartnerOrders(ownerCredentials).getOrderRevenueCommissionByApproveStatus(ApproveStatus.PENDING).size();
        for(int i=0; i< 2 - orderProductListSize; i++){
            //Get customer
            int customer = new APIAllCustomers(ownerCredentials).getAllAccountCustomerId().get(0);
            if (i == 0) {
                //Get Partner has commission by product
                int partner = isProductCommission ? new APIPartnerManagement(ownerCredentials).getPartnerHasCommissionByProduct().get(0) : new APIPartnerManagement(ownerCredentials).getPartnerHasCommissionByRevenue().get(0);
                //Assign partner to customer
                new APIEditCustomer(ownerCredentials).assignPartnerToCustomer(customer, partner);
                //Create order
                new APICreateOrderPOS(ownerCredentials).CreatePOSOrder(customer,productIds.get(0));
            } else {
                //Create order
                new APICreateOrderPOS(ownerCredentials).CreatePOSOrder(customer,productIds.get(0));
            }
        }
    }
    @DataProvider
    public Object[] DropshipOrderData(){
        return new Object[][]{
                {"1"},
                {"10"}, //Bug khong co quyen view list, dang hien restricted pag
                {"11"},
                {"100"}, //bug tuong tu
                {"101"},
                {"110"},//bug tuong tu
                {"111"}
        };
    }
    @Test(dataProvider = "DropshipOrderData")
    public void checkDropshipOrderPermission(String dataBinary){
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setHome_none("1");
        model.setAffiliate_dropshipOrders(dataBinary);

        // Create Order for Commission by Product tab
        callAPICreateDropshipOrderIfAny(true);

        //Create Order for Commission by Revenue tab
        callAPICreateDropshipOrderIfAny(false);

        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Description Vi's Permission", model);

        //Get info of the staff after being granted the permission
        staffLoginInfo = new Login().getInfo(staffCredentials);

        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());

        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new PartnerOrdersPage(driver).checkDropshipOrderPermission(allPermissions);
    }
    public void callAPIResellerCreatePOSOrder(){
        LoginInformation resellerCredentials = new Login().setLoginInformation("+84", USERNAME_RESELLER_SHOPVI, PASSWORD_RESELLER_SHOPVI).getLoginInformation();
        int orderProductListSize = new APIPartnerOrders(ownerCredentials).getResellerOrderByApproveStatus(ApproveStatus.PENDING).size();
        for(int i=0; i< 2 - orderProductListSize; i++){
            new APICreateOrderPOS(resellerCredentials).CreatePOSOrder(0);
        }
    }
    @DataProvider
    public Object[] ResellerOrderData(){
        return new Object[][]{
                {"1"},
                {"10"}, //Bug Khong co quyen View list order, 4.5 đang hiện Restricted page >>Step co ticket improve fix 4.6
                {"11"},
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
                {"10111"},
//                {"11000"},
//                {"11001"},
//                {"11010"},
//                {"11011"},
//                {"11100"},
                {"11101"},
//                {"11110"},
                {"11111"},
        };
    }
    @Test(dataProvider = "ResellerOrderData")
    public void checkResellerOrderPermission(String dataBinary){
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setHome_none("1");
        model.setAffiliate_resellerOrders(dataBinary);

        // Create Order for Reseller
        callAPIResellerCreatePOSOrder();

        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Description Vi's Permission", model);

        //Get info of the staff after being granted the permission
        staffLoginInfo = new Login().getInfo(staffCredentials);

        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());

        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new PartnerOrdersPage(driver).checkResellerOrderPermission(allPermissions);
    }
    @DataProvider
    public Object[] PayoutData(){
        return new Object[][]{
                {"1"},
                {"10"},
//                {"11"},
//                {"100"},
//                {"101"},
                {"110"},
                {"111"},
        };
    }
    @Test(dataProvider = "PayoutData")
    public void verifyDropshipPayoutPermission(String dataBinary){
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setHome_none("1");
        model.setAffiliate_dropshipPayout(dataBinary);

        // Create Order for Reseller
        callAPIResellerCreatePOSOrder();

        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Description Vi's Permission", model);

        //Get info of the staff after being granted the permission
        staffLoginInfo = new Login().getInfo(staffCredentials);

        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());

        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new PayoutInformationPage(driver).getLoginInfo(staffCredentials).verifyPayoutPermission(allPermissions,true);
    }
    @Test(dataProvider = "PayoutData")
    public void verifyResellerPayoutPermission(String dataBinary){
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setHome_none("1");
        model.setAffiliate_resellerPayout(dataBinary);

        // Create Order for Reseller
        callAPIResellerCreatePOSOrder();

        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Description Vi's Permission", model);

        //Get info of the staff after being granted the permission
        staffLoginInfo = new Login().getInfo(staffCredentials);

        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());

        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new PayoutInformationPage(driver).getLoginInfo(staffCredentials).verifyPayoutPermission(allPermissions,false);
    }
    @DataProvider
    public Object[] ResellerInventoryData(){
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
//                {"111111"},
//                {"1000000"},
//                {"1000001"},
//                {"1000010"},
//                {"1000011"},
//                {"1000100"},
//                {"1000101"},
//                {"1000110"},
//                {"1000111"},
//                {"1001000"},
//                {"1001001"},
//                {"1001010"},
//                {"1001011"},
//                {"1001100"},
//                {"1001101"},
//                {"1001110"},
//                {"1001111"},
//                {"1010000"},
//                {"1010001"},
//                {"1010010"},
//                {"1010011"},
//                {"1010100"},
//                {"1010101"},
//                {"1010110"},
//                {"1010111"},
//                {"1011000"},
//                {"1011001"},
//                {"1011010"},
//                {"1011011"},
//                {"1011100"},
//                {"1011101"},
//                {"1011110"},
                {"1011111"},
                {"1100000"},
                {"1100001"},
                {"1100010"},
                {"1100011"},
                {"1100100"},
                {"1100101"},
                {"1100110"},
                {"1100111"},
                {"1101000"},
                {"1101001"},
                {"1101010"},
                {"1101011"},
                {"1101100"},
                {"1101101"},
                {"1101110"},
                {"1101111"},
                {"1110000"},
                {"1110001"},
                {"1110010"},
                {"1110011"},
                {"1110100"},
                {"1110101"},
                {"1110110"},
                {"1110111"},
                {"1111000"},
                {"1111001"},
                {"1111010"},
                {"1111011"},
                {"1111100"},
                {"1111101"},
                {"1111110"},
                {"1111111"}
        };
    }
    @Test(dataProvider = "ResellerInventoryData")
    public void verifyResellerInventoryPermission(String dataBinary){
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setHome_none("1");
        model.setAffiliate_resellerInventory(dataBinary);
        model.setProduct_productManagement("1");
        model.setAffiliate_resellerPartner("1");

        // Resellerl login
        LoginInformation resellerCredentials = new Login().setLoginInformation("+84", USERNAME_RESELLER_SHOPVI, PASSWORD_RESELLER_SHOPVI).getLoginInformation();

        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Description Vi's Permission", model);

        //Get info of the staff after being granted the permission
        staffLoginInfo = new Login().getInfo(staffCredentials);

        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new TransferPage(driver).getLoginInfo(ownerCredentials,resellerCredentials,staffCredentials).checkPartnerTransferPermision(allPermissions,productCreatedByShopOwner,productCreatedByStaff);
    }
    @DataProvider
    public Object[] ResellerCustomerData(){
        return new Object[][]{
                {"0"},
                {"1"},
                {"10"},
                {"11"},
        };
    }
    @Test(dataProvider = "ResellerCustomerData")
    public void verifyResellerCustomerPermission(String dataBinary){
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setHome_none("1");
        model.setAffiliate_resellerCustomer(dataBinary);

        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Description Vi's Permission", model);

        //Get info of the staff after being granted the permission
        staffLoginInfo = new Login().getInfo(staffCredentials);

        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new ResellerCustomers(driver,staffCredentials).checkResellerCustomerPermission(allPermissions);
    }
    @DataProvider
    public Object[] PayoutHistoryData(){
        return new Object[][]{
                {"0"},
                {"1"},
        };
    }
    @Test(dataProvider = "PayoutHistoryData")
    public void verifyPayoutHistoryPermission(String dataBinary){
        //Set permission model
        CreatePermission model = new CreatePermission();
        model.setHome_none("1");
        model.setAffiliate_payoutHistory(dataBinary);

        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Description Vi's Permission", model);

        //Get info of the staff after being granted the permission
        staffLoginInfo = new Login().getInfo(staffCredentials);

        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new PayoutHistoryPage(driver,staffCredentials).checkViewPayoutHistoryPermission(allPermissions);
    }
}
