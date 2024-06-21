package web.Dashboard;

import api.Seller.login.Login;
import api.Seller.products.all_products.APIAllProducts;
import api.Seller.products.all_products.APIEditProduct;
import api.Seller.products.all_products.CreateProduct;
import api.Seller.products.all_products.APIProductDetail;
import api.Seller.promotion.FlashSale;
import api.Seller.setting.PermissionAPI;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.products.productInfomation.ProductInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.promotion.flashsale.FlashSalePage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utilities.account.AccountTest.*;
import static utilities.account.AccountTest.STAFF_SHOP_VI_PASSWORD;

public class FlashSalePermissionTest extends BaseTest{
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
        CreateProduct productInfo = new CreateProduct(ownerCredentials).createWithoutVariationProduct(false,100,100);
        productCreatedByShopOwner = productInfo.getProductName();
        productIds.add(productInfo.getProductID());

        //Create full permission for staff
        groupPermissionId = new PermissionAPI(ownerCredentials).createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);

        //Staff create product
        productInfo = new CreateProduct(ownerCredentials).createWithoutVariationProduct(false,100,100);
        productCreatedByStaff = productInfo.getProductName();
        productIds.add(productInfo.getProductID());
    }
    @AfterClass
    public void afterClass(){
        //delete product
        for (int productId:productIds) {
            new APIEditProduct(ownerCredentials).deleteProduct(productId);
        }
    }
    @BeforeMethod
    public void beforeMethod() {
        driver = new InitWebdriver().getDriver(browser, "false");
    }
    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        new FlashSale(ownerCredentials).endEarlyFlashSale();
        super.writeResult(result);
        driver.quit();
    }
    public int callAPIGetFlashScheduleId(){
        int flashSaleId = new FlashSale(ownerCredentials).getAFlashSaleScheduled();
        if(flashSaleId==0){
            int productId = new APIAllProducts(ownerCredentials).getProductIDWithoutVariationAndInStock(false, false, true);
            ProductInfo productInfo = new APIProductDetail(ownerCredentials).getInfo(productId);
            new FlashSale(ownerCredentials).createFlashSale(productInfo);
            flashSaleId = new FlashSale(ownerCredentials).getAFlashSaleScheduled();
        }
        return flashSaleId;
    }
    @DataProvider
    public Object[] FlashSalePermissionModel() {
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
                {"111111"},
                {"1000000"},
                {"1000001"},
                {"1000010"},
                {"1000011"},
                {"1000100"},
                {"1000101"},
                {"1000110"},
                {"1000111"},
                {"1001000"},
                {"1001001"},
                {"1001010"},
                {"1001011"},
                {"1001100"},
                {"1001101"},
                {"1001110"},
                {"1001111"},
                {"1010000"},
                {"1010001"},
                {"1010010"},
                {"1010011"},
                {"1010100"},
                {"1010101"},
                {"1010110"},
                {"1010111"},
                {"1011000"},
                {"1011001"},
                {"1011010"},
                {"1011011"},
                {"1011100"},
                {"1011101"},
                {"1011110"},
                {"1011111"},
                {"1100000"},
//                {"1100001"},
//                {"1100010"},
//                {"1100011"},
//                {"1100100"},
//                {"1100101"},
//                {"1100110"},
//                {"1100111"},
//                {"1101000"},
//                {"1101001"},
                {"1101010"},
//                {"1101011"},
//                {"1101100"},
//                {"1101101"},
//                {"1101110"},
//                {"1101111"},
//                {"1110000"},
//                {"1110001"},
//                {"1110010"},
//                {"1110011"},
//                {"1110100"},
//                {"1110101"},
//                {"1110110"},
//                {"1110111"},
//                {"1111000"},
//                {"1111001"},
                {"1111010"},
//                {"1111011"},
//                {"1111100"},
//                {"1111101"},
//                {"1111110"},
//                {"1111111"}
        };
    }
    //BH-24965
    @Test(dataProvider = "FlashSalePermissionModel")
    public void checkPermissionFlashSale(String flashSalePermissionBinary){
        //Get flashsale has status = schedule
        int flashSaleId = callAPIGetFlashScheduleId();
        CreatePermission model = new CreatePermission();
        model.setHome_none("11");
        model.setProduct_productManagement("1");
        model.setPromotion_flashSale(flashSalePermissionBinary);
        //edit a permisison and assign permision to staff.
        new PermissionAPI(ownerCredentials).editPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials,groupPermissionId,model);
        //Get permission
        staffLoginInfo = new Login().getInfo(staffCredentials);
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        System.out.println("All permission: "+allPermissions.getPromotion().getFlashSale());
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble().navigateToPage("Promotion","Flash Sale");
        new FlashSalePage(driver).clickExploreNow()
                .verifyPermissionFlashSale(allPermissions,flashSaleId,productCreatedByShopOwner,productCreatedByStaff);
    }
}
