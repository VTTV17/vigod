package web.Dashboard;
import api.Seller.login.Login;
import api.Seller.marketing.*;
import api.Seller.marketing.membership.LoyaltyProgram;
import api.Seller.products.all_products.APIEditProduct;
import api.Seller.products.all_products.APICreateProduct;
import api.Seller.promotion.CreatePromotion;
import api.Seller.promotion.PromotionList;
import api.Seller.setting.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.constant.Constant;
import utilities.driver.InitWebdriver;
import utilities.enums.DiscountStatus;
import utilities.enums.DiscountType;
import utilities.enums.PushNotiEvent;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import utilities.permission.CheckPermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.marketing.buylink.BuyLinkManagement;
import web.Dashboard.marketing.emailcampaign.EmailCampaignManagement;
import web.Dashboard.marketing.landingpage.LandingPage;
import web.Dashboard.marketing.loyaltypoint.LoyaltyPoint;
import web.Dashboard.marketing.pushnotification.PushNotificationManagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utilities.account.AccountTest.*;
import static utilities.account.AccountTest.STAFF_SHOP_VI_PASSWORD;

public class MarketingPermissionTest extends BaseTest {
    String sellerUserName;
    String sellerPassword;
    String staffUserName;
    String staffPass;
    String languageDB;
    LoginInformation ownerCredentials;
    LoginInformation staffCredentials;
    LoginDashboardInfo staffLoginInfo;
    int groupPermissionId;
    String productCreatedByShopOwner = "Gel Rửa Mặt La Roche-Posay Dành Cho Da Dầu, Nhạy Cảm 200ml Effaclar Purifying Foaming Gel For Oily Sensitive Skin";
    String productCreatedByStaff = "Ao thun staff tao";
    List<Integer> productIds = new ArrayList<>();
    List<Integer> pushNotiCampaignIds = new ArrayList<>();
    final static Logger logger = LogManager.getLogger(MarketingPermissionTest.class);
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
        APICreateProduct productInfo = new APICreateProduct(ownerCredentials).createWithoutVariationProduct(false,100);
        productCreatedByShopOwner = productInfo.getProductName();
        productIds.add(productInfo.getProductID());

        //Create full permission for staff
        groupPermissionId = new PermissionAPI(ownerCredentials).createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);

        //Staff create product
        productInfo = new APICreateProduct(ownerCredentials).createWithoutVariationProduct(false,100);
        productCreatedByStaff = productInfo.getProductName();
        productIds.add(productInfo.getProductID());
    }
    @AfterClass
    public void afterClass(){
        //delete product
        for (int productId:productIds) {
            new APIEditProduct(ownerCredentials).deleteProduct(productId);
        }
        for (int notiId:pushNotiCampaignIds) {
            new APIPushNotification(ownerCredentials).deletePushNotiCampaign(notiId);
        }
    }
    @BeforeMethod
    public void beforeMethod() {
        driver = new InitWebdriver().getDriver(browser, "false");
    }

    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        //clear data
        List<Integer> membershipProgramList = new LoyaltyProgram(ownerCredentials).getMembershipList();
        for (int i=2;i<membershipProgramList.size();i++){
            new LoyaltyProgram(ownerCredentials).deleteMembership(membershipProgramList.get(i));
        }
        super.writeResult(result);
        driver.quit();
    }
    public int callAPIGetPublishedLandingPageId(){
        int publishedLandingId = new APILandingPage(ownerCredentials).getAPublishLandingPageId();
        if (publishedLandingId==0){
            publishedLandingId = callAPIGetDraftLandingPageId();
            new APILandingPage(ownerCredentials).publishLandingPage(publishedLandingId);
        }
        return publishedLandingId;
    }
    public int callAPIGetDraftLandingPageId(){
        int draftId = new APILandingPage(ownerCredentials).getADraftLandingPageId();
        if(draftId==0){
            draftId = new APILandingPage(ownerCredentials).createLandingPage().getId();
        }
        return draftId;
    }
    @DataProvider
    public Object[][] LandingPagePermissionModel() {
        Object[][] result = new Object[][]{
//                {"1","1"},
//                {"10","1"}, //bug: no permission View list landing page >> actual: show restricted page. > fixed
//                {"11","1"},  //bug: has permission Create landing page >> actual show restricted popup >fixed
//                {"100","1"},    //Bug: has permission edit, don't have permission View detail >> actual: detail page still show (wrong Edit and View detail permission) >>fixed
//                {"101","1"},
//                {"110","1"},
//                {"111","1"},
//                {"1000","1"},
//                {"1001","1"},
//                {"1010","1"},
//                {"1011","1"},
//                {"1100","1"},
//                {"1101","1"},
//                {"1110","1"},
//                {"1111","1"},
//                {"10000","1"},
//                {"10001","1"},
//                {"10010","1"},
//                {"10011","1"},
//                {"10100","1"},
//                {"10101","1"},
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
                {"1000000","1"},
                {"1000001","1"},
                {"1000010","1"},
                {"1000011","1"},
                {"1000100","1"},
                {"1000101","1"},
                {"1000110","1"},
                {"1000111","1"},
                {"1001000","1"},
                {"1001001","1"},
                {"1001010","1"},
                {"1001011","1"},
                {"1001100","1"},
                {"1001101","1"},
                {"1001110","1"},
                {"1001111","1"},
                {"1010000","1"},
                {"1010001","1"},
                {"1010010","1"},
                {"1010011","1"},
                {"1010100","1"},
                {"1010101","1"},
                {"1010110","1"},
                {"1010111","1"},
                {"1011000","1"},
                {"1011001","1"},
                {"1011010","1"},
                {"1011011","1"},
                {"1011100","1"},
                {"1011101","1"},
                {"1011110","1"},
                {"1011111","1"},
                {"1100000","1"},
                {"1100001","1"},
                {"1100010","1"},
                {"1100011","1"},
                {"1100100","1"},
                {"1100101","1"},
                {"1100110","1"},
                {"1100111","1"},
                {"1101000","1"},
                {"1101001","1"},
                {"1101010","1"},
                {"1101011","1"},
                {"1101100","1"},
                {"1101101","1"},
                {"1101110","1"},
                {"1101111","1"},
                {"1110000","1"},
                {"1110001","1"},
                {"1110010","1"},
                {"1110011","1"},
                {"1110100","1"},
                {"1110101","1"},
                {"1110110","1"},
                {"1110111","1"},
                {"1111000","1"},
                {"1111001","1"},
                {"1111010","1"},
                {"1111011","1"},
                {"1111100","1"},
                {"1111101","1"},
                {"1111110","1"},
                {"1111111","1"},
                {"10000000","1"},
                {"10000001","1"}, //bug: no permission Create, has permision Clone >> still can clone >> fixed
                {"10000010","1"},
                {"10000011","1"},
                {"10000100","1"},
                {"10000101","1"},
                {"10000110","1"},
                {"10000111","1"},
                {"10001000","1"},
                {"10001001","1"},
                {"10001010","1"},
                {"10001011","1"},
                {"10001100","1"},
                {"10001101","1"},
                {"10001110","1"},
                {"10001111","1"},
                {"10010000","1"},
                {"10010001","1"},
                {"10010010","1"},
                {"10010011","1"},
                {"10010100","1"},
                {"10010101","1"},
                {"10010110","1"},
                {"10010111","1"},
                {"10011000","1"},
                {"10011001","1"},
                {"10011010","1"},
                {"10011011","1"},
                {"10011100","1"},
                {"10011101","1"},
                {"10011110","1"},
                {"10011111","1"},
                {"10100000","1"},
                {"10100001","1"},
                {"10100010","1"},
                {"10100011","1"},
                {"10100100","1"},
                {"10100101","1"},
                {"10100110","1"},
                {"10100111","1"},
                {"10101000","1"},
                {"10101001","1"},
                {"10101010","1"},
                {"10101011","1"},
                {"10101100","1"},
                {"10101101","1"},
                {"10101110","1"},
                {"10101111","1"},
                {"10110000","1"},
                {"10110001","1"},
                {"10110010","1"},
                {"10110011","1"},
                {"10110100","1"},
                {"10110101","1"},
                {"10110110","1"},
                {"10110111","1"},
                {"10111000","1"},
                {"10111001","1"},
                {"10111010","1"},
                {"10111011","1"},
                {"10111100","1"},
                {"10111101","1"},
                {"10111110","1"},
                {"10111111","1"},
                {"11000000","1"},
                {"11000001","1"},
                {"11000010","1"},
                {"11000011","1"},
                {"11000100","1"},
                {"11000101","1"},
                {"11000110","1"},
                {"11000111","1"},
                {"11001000","1"},
                {"11001001","1"},
                {"11001010","1"},
                {"11001011","1"},
                {"11001100","1"},
                {"11001101","1"},
                {"11001110","1"},
                {"11001111","1"},
                {"11010000","1"},
                {"11010001","1"},
                {"11010010","1"},
                {"11010011","1"},
                {"11010100","1"},
                {"11010101","1"},
                {"11010110","1"},
                {"11010111","1"},
                {"11011000","1"},
                {"11011001","1"},
                {"11011010","1"},
                {"11011011","1"},
                {"11011100","1"},
                {"11011101","1"},
                {"11011110","1"},
                {"11011111","1"},
                {"11100000","1"},
                {"11100001","1"},
                {"11100010","1"},
                {"11100011","1"},
                {"11100100","1"},
                {"11100101","1"},
                {"11100110","1"},
                {"11100111","1"},
                {"11101000","1"},
                {"11101001","1"},
                {"11101010","1"},
                {"11101011","1"},
                {"11101100","1"},
                {"11101101","1"},
                {"11101110","1"},
                {"11101111","1"},
                {"11110000","1"},
                {"11110001","1"},
                {"11110010","1"},
                {"11110011","1"},
                {"11110100","1"},
                {"11110101","1"},
                {"11110110","1"},
                {"11110111","1"},
                {"11111000","1"},
                {"11111001","1"},
                {"11111010","1"},
                {"11111011","1"},
                {"11111100","1"},
                {"11111101","1"},
                {"11111110","1"},
                {"11111111","1"},
                {"11111111","0"},
        };
        Object[][] randomResult = new Object[50][];
        for (int index = 0; index < 20; index++) {
            randomResult[index] = result[(int) (result.length * Math.random())];
        }
        return randomResult;
//        return result;
    }
    /* https://mediastep.atlassian.net/browse/BH-25137 */
    @Test(dataProvider = "LandingPagePermissionModel")
    public void checkLandingPagePermission(String landingPagePersBinary,String productListPersBinary){
        int publishedLandingId = callAPIGetPublishedLandingPageId();
        int draftLandingId  = callAPIGetDraftLandingPageId();

        //Set permission
        CreatePermission model = new CreatePermission();
        model.setHome_none("11");
        model.setProduct_productManagement(productListPersBinary);
        model.setMarketing_landingPage(landingPagePersBinary);

        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Vi edit permission", model);
        staffLoginInfo = new Login().getInfo(staffCredentials);

        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());

        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new LandingPage(driver).getLoginInformation(staffCredentials)
                .verifyLandingPagePermission(allPermissions,draftLandingId,publishedLandingId,productCreatedByShopOwner,productCreatedByStaff);
    }
    public void callAPISetUpDataToRunBuyLink(){
        int buyLinkId = new APIBuyLink(ownerCredentials).getNewestBuyLinkID();
        if(buyLinkId==0){
            new APIBuyLink(ownerCredentials).createBuyLink();
        }
        int productDiscountCodeList = new PromotionList(ownerCredentials).getDiscountId(DiscountType.PRODUCT_DISCOUNT_CODE, DiscountStatus.IN_PROGRESS);
        if(productDiscountCodeList==-1){
            new CreatePromotion(ownerCredentials).createProductDiscountCode();
        }
    }
    @DataProvider
    public Object[][] BuyLinkPermissionModel() {
        return new Object[][]{
                {"1","1"},
                {"10","1"}, //Bug: show restricted page when don't have View list permission > fixed
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
                {"1111","1"}
        };
    }
    /* https://mediastep.atlassian.net/browse/BH-25138 */
    @Test(dataProvider = "BuyLinkPermissionModel")
    public void checkBuyLinkPermission(String buyLinkPermissionBinary, String productPermissionBinary){
        //Ensure that buy link list and product discount list have data.
        callAPISetUpDataToRunBuyLink();

        //Set permission
        CreatePermission model = new CreatePermission();
        model.setHome_none("11");
        model.setProduct_productManagement(productPermissionBinary);
        model.setMarketing_buyLink(buyLinkPermissionBinary);

        //edit permisison
        staffLoginInfo = new Login().getInfo(staffCredentials);
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Vi edit permission", model);
        new CheckPermission(driver).waitUntilPermissionUpdated(staffLoginInfo.getStaffPermissionToken(),staffCredentials);
        staffLoginInfo = new Login().getInfo(staffCredentials);

        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        System.out.println("Permission Buy link: "+allPermissions.getMarketing().getBuyLink());
        System.out.println("Permission product: "+allPermissions.getProduct().getProductManagement());


        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new BuyLinkManagement(driver).getLoginInfo(staffCredentials).navigateUrl().clickExploreNow()
                .checkBuyLinkPermission(allPermissions,productCreatedByShopOwner,productCreatedByStaff);
    }
    public int callAPIGetDraftId(){
        int campaignId = new APIEmailCampaign(ownerCredentials).getDraftEmailCampaignId();
        if(campaignId==0){
            campaignId = new APIEmailCampaign(ownerCredentials).createEmailCampaign().getId();
        }
        return campaignId;
    }
    /* https://mediastep.atlassian.net/browse/BH-25142 */
    @DataProvider
    public Object[][] EmailCampaignPermissionModel() {
        return new Object[][]{
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
                {"1111","0"}
        };
    }
    @Test(dataProvider = "EmailCampaignPermissionModel")
    public void checkEmailCampaignPermission(String emailCampaignPersBinary, String customerSegmentPersBinary){
        //Ensure that has Draft email campaign.
        int draftId = callAPIGetDraftId();

        //Set permission
        CreatePermission model = new CreatePermission();
        model.setHome_none("11");
        model.setMarketing_emailCampaign(emailCampaignPersBinary);
        model.setCustomer_segment(customerSegmentPersBinary);

        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Vi edit permission", model);
        staffLoginInfo = new Login().getInfo(staffCredentials);

        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        System.out.println("Permission email campaign: "+allPermissions.getMarketing().getEmailCampaign());
        System.out.println("Permission segment: "+allPermissions.getCustomer().getSegment());
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new EmailCampaignManagement(driver).checkEmailCampaignPermission(allPermissions,draftId);
    }
    public int callAPIGetPushNotiCampaignId(){
        int id = new APIPushNotification(ownerCredentials).getActivePushNoti();
        if (id == 0) {
            id = new APIPushNotification(ownerCredentials).createEventPushNotification(PushNotiEvent.ACCOUNT_CREATED);
        }
        return id;
    }
    @DataProvider
    public Object[][] PushNotificationPermissionModel() {
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
                {"11001" },
                {"11010"},
                {"11011"},
                {"11100"},
                {"11101"},
                {"11110"},
                {"11111"}
        };
    }
    //https://mediastep.atlassian.net/browse/BH-25139
    @Test(dataProvider = "PushNotificationPermissionModel")
    public void checkPushNotificationPermission(String permissionBinary){
        //Ensure that has Event push noti campaign.
        int eventCampaignId = callAPIGetPushNotiCampaignId();

        //Set permission
        CreatePermission model = new CreatePermission();
        model.setHome_none("11");
        model.setProduct_productManagement("1");
        model.setService_serviceManagement("1");
        model.setProduct_collection("1");
        model.setService_serviceCollection("1");
        model.setOnlineStore_page("1");
        model.setCustomer_segment("1");
        model.setMarketing_pushNotification(permissionBinary);

        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Vi edit permission", model);
        staffLoginInfo = new Login().getInfo(staffCredentials);

        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        System.out.println("Permission Push Notification: "+allPermissions.getMarketing().getPushNotification());
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new PushNotificationManagement(driver).navigateByUrl().clickExploreNow()
                .checkPushNotificationPermission(allPermissions,eventCampaignId);

        //Get newest data to clear
        pushNotiCampaignIds = new APIPushNotification(ownerCredentials).getSomeActiveCampaign(eventCampaignId);
    }
    public int callAPIGetALoyalProgramId(){
        int id = new LoyaltyProgram(ownerCredentials).getAMembershipId();
        if(id == 0){
            id = new LoyaltyProgram(ownerCredentials).createNewMembership();
        }
        return id;
    }
    //https://mediastep.atlassian.net/browse/BH-25140
    @DataProvider
    public Object[][] LoyaltyProgramPermissionModel() {
        return new Object[][]{
                {"1"},
                {"10"}, //Bug1
                {"11"}, //Bug1: has Create permission, but Restricted popup show when click on Save
                {"100"},
                {"101"},
                {"110"},
                {"111"},
                {"1000"},
                {"1001"},
                {"1010"},   //Bug1
                {"1011"}, //Bug1
                {"1100"},
                {"1101"},
                {"1110"},   //Bug2
                {"1111"},
                {"10000"},
                {"10001"},
                {"10010"},  //BUg2
                {"10011"},  //Bug1
                {"10100"},
                {"10101"},
                {"10110"},    //Bug2
                {"10111"},
                {"11000"},
                {"11001"},
                {"11010"},  //Bug1
                {"11011"},  //Bug1
                {"11100"},
                {"11101"},
                {"11110"},  //Bug1
                {"11111"},
                {"100000"},
                {"100001"},
                {"100010"}, //Bug2
                {"100011"}, //Bug1
                {"100100"},
                {"100101"},
                {"100110"}, //Bug2
                {"100111"},
                {"101000"},
                {"101001"},
                {"101010"}, //Bug2
                {"101011"}, //Bug1
                {"101100"},
                {"101101"},
                {"101110"}, //Bug2
                {"101111"},
                {"110000"},
                {"110001"},
                {"110010"}, //Bug2
                {"110011"}, //Bug1
                {"110100"},
                {"110101"},
                {"110110"}, //Bug2
                {"110111"},
                {"111000"},
                {"111001"},
                {"111010"}, //Bug2
                {"111011"}, //Bug1
                {"111100"},
                {"111101"},
                {"111110"}, //Bug2: don't have View list, selected segment not disable
                {"111111"},
        };
    }
    //https://mediastep.atlassian.net/browse/BH-25140
    @Test(dataProvider = "LoyaltyProgramPermissionModel")
    public void checkLoyaltyProgramPermission(String permissionBinary){
        //Ensure that has a loyalty program.
        callAPIGetALoyalProgramId();

        //Set permission
        CreatePermission model = new CreatePermission();
        model.setHome_none("11");
        model.setCustomer_segment("1");
        model.setMarketing_loyaltyProgram(permissionBinary);

        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Vi edit permission", model);
        staffLoginInfo = new Login().getInfo(staffCredentials);

        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        System.out.println("Permission Loyalty Program: "+allPermissions.getMarketing().getLoyaltyProgram());

        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new web.Dashboard.marketing.loyaltyprogram.LoyaltyProgram(driver).navigateByUrl()
                .checkLoyaltyProgramPermission(allPermissions);
    }
    @DataProvider
    public Object[][] LoyaltyPointPermissionModel() {
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
        };
    }
    @Test (dataProvider = "LoyaltyPointPermissionModel")
    public void checkLoyaltyPointPermission(String binary){
        //Set permission
        CreatePermission model = new CreatePermission();
        model.setHome_none("11");
        model.setMarketing_loyaltyPoint(binary);

        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Vi edit permission", model);
        staffLoginInfo = new Login().getInfo(staffCredentials);

        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        System.out.println("Permission Loyalty point: "+allPermissions.getMarketing().getLoyaltyPoint());

        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new LoyaltyPoint(driver).getLoginInformation(ownerCredentials).navigateByUrl()
                .checkLoyaltyPointPermission(allPermissions);
    }
}

