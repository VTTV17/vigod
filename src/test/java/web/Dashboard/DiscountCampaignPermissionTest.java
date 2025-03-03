package web.Dashboard;

import api.Seller.login.Login;
import api.Seller.products.all_products.APIEditProduct;
import api.Seller.products.all_products.APICreateProduct;
import api.Seller.promotion.PromotionList;
import api.Seller.services.CreateServiceAPI;
import api.Seller.setting.PermissionAPI;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utilities.driver.InitWebdriver;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.dashboard.services.ServiceInfo;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.model.staffPermission.AllPermissions;
import utilities.model.staffPermission.CreatePermission;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.promotion.discount.DiscountPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static utilities.account.AccountTest.*;
import static utilities.account.AccountTest.STAFF_SHOP_VI_PASSWORD;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;

public class DiscountCampaignPermissionTest extends BaseTest{
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
    String serviceCreateByStaff;
    String serviceCreatedByShowOwner;
    @BeforeClass
    public void beforeClass() {
        sellerUserName = ADMIN_SHOP_VI_USERNAME;
        sellerPassword = ADMIN_SHOP_VI_PASSWORD;
        staffUserName = STAFF_SHOP_VI_USERNAME;
        staffPass = STAFF_SHOP_VI_PASSWORD;
        languageDB = language;
        MAX_PRICE = 999999L;
        ownerCredentials = new Login().setLoginInformation("+84", sellerUserName, sellerPassword).getLoginInformation();
        staffCredentials = new Login().setLoginInformation("+84", staffUserName, staffPass).getLoginInformation();
        // Shop owner create product, service
        APICreateProduct productInfo = new APICreateProduct(ownerCredentials).createWithoutVariationProduct(false,100,100);
        productCreatedByShopOwner = productInfo.getProductName();
        productIds.add(productInfo.getProductID());
        serviceCreatedByShowOwner = new CreateServiceAPI(ownerCredentials).createService(new ServiceInfo()).getServiceName();

        //Create full permission for staff
        groupPermissionId = new PermissionAPI(ownerCredentials).createPermissionGroupThenGrantItToStaff(ownerCredentials, staffCredentials);

        //Staff create product
        productInfo = new APICreateProduct(ownerCredentials).createWithoutVariationProduct(false,100,100);
        productCreatedByStaff = productInfo.getProductName();
        productIds.add(productInfo.getProductID());

        //Staff Create service
        serviceCreateByStaff = new CreateServiceAPI(staffCredentials).createService(new ServiceInfo()).getServiceName();
    }
    @AfterClass
    public void afterClass(){
        //delete product
        for (int productId:productIds) {
            new APIEditProduct(ownerCredentials).deleteProduct(productId);
        }
        new PromotionList(ownerCredentials).endEarlyInprogressDiscountCampaign();
    }
    @BeforeMethod
    public void beforeMethod() {
        driver = new InitWebdriver().getDriver(browser, "false");
    }

    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        //clear data - delete all created group permission
//        super.writeResult(result);
        driver.quit();
    }

    @DataProvider
    public Object[] DiscountCampaignPermissionModel() {
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
////                {"110000"},
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
//                {"1110010"},
//                {"1110011"},
//                {"1110100"},
//                {"1110101"},
//                {"1110110"},
//                {"1110111"},
//                {"1111000"},
//                {"1111001"},
//                {"1111010"},
//                {"1111011"},
//                {"1111100"},
//                {"1111101"},
//                {"1111110"},
//                {"1111111"},
//                {"10000000"},
//                {"10000001"},
//                {"10000010"},
//                {"10000011"},
//                {"10000100"},
//                {"10000101"},
//                {"10000110"},
//                {"10000111"},
//                {"10001000"},
//                {"10001001"},
//                {"10001010"},
//                {"10001011"},
//                {"10001100"},
//                {"10001101"},
//                {"10001110"},
//                {"10001111"},
//                {"10010000"},
//                {"10010001"},
//                {"10010010"},
//                {"10010011"},
//                {"10010100"},
//                {"10010101"},
//                {"10010110"},
//                {"10010111"},
//                {"10011000"},
//                {"10011001"},
//                {"10011010"},
//                {"10011011"},
//                {"10011100"},
//                {"10011101"},
//                {"10011110"},
//                {"10011111"},
//                {"10100000"},
//                {"10100001"},
//                {"10100010"},
//                {"10100011"},
//                {"10100100"},
//                {"10100101"},
//                {"10100110"},
//                {"10100111"},
//                {"10101000"},
//                {"10101001"},
//                {"10101010"},
//                {"10101011"},
//                {"10101100"},
//                {"10101101"},
//                {"10101110"},
//                {"10101111"},
//                {"10110000"},
//                {"10110001"},
//                {"10110010"},
//                {"10110011"},
//                {"10110100"},
//                {"10110101"},
//                {"10110110"},
//                {"10110111"},
//                {"10111000"},
//                {"10111001"},
//                {"10111010"},
//                {"10111011"},
//                {"10111100"},
//                {"10111101"},
//                {"10111110"},
//                {"10111111"},
//                {"11000000"},
//                {"11000001"},
//                {"11000010"},
//                {"11000011"},
//                {"11000100"},
//                {"11000101"},
//                {"11000110"},
//                {"11000111"},
//                {"11001000"},
//                {"11001001"},
//                {"11001010"},
//                {"11001011"},
//                {"11001100"},
//                {"11001101"},
//                {"11001110"},
//                {"11001111"},
//                {"11010000"},
//                {"11010001"},
//                {"11010010"},
//                {"11010011"},
//                {"11010100"},
//                {"11010101"},
//                {"11010110"},
//                {"11010111"},
//                {"11011000"},
//                {"11011001"},
//                {"11011010"},
//                {"11011011"},
//                {"11011100"},
//                {"11011101"},
//                {"11011110"},
//                {"11011111"},
//                {"11100000"},
//                {"11100001"},
//                {"11100010"},
//                {"11100011"},
//                {"11100100"},
//                {"11100101"},
//                {"11100110"},
//                {"11100111"},
//                {"11101000"},
//                {"11101001"},
//                {"11101010"},
//                {"11101011"},
//                {"11101100"},
//                {"11101101"},
//                {"11101110"},
//                {"11101111"},
//                {"11110000"},
//                {"11110001"},
//                {"11110010"},
//                {"11110011"},
//                {"11110100"},
//                {"11110101"},
//                {"11110110"},
//                {"11110111"},
//                {"11111000"},
//                {"11111001"},
//                {"11111010"},
//                {"11111011"},
//                {"11111100"},
//                {"11111101"},
//                {"11111110"},
//                {"11111111"},
//                {"100000000"},
//                {"100000001"},
//                {"100000010"},
//                {"100000011"},
//                {"100000100"},
//                {"100000101"},
//                {"100000110"},
//                {"100000111"},
//                {"100001000"},
//                {"100001001"},
//                {"100001010"},
//                {"100001011"},
//                {"100001100"},
//                {"100001101"},
//                {"100001110"},
//                {"100001111"},
//                {"100010000"},
//                {"100010001"},
//                {"100010010"},
//                {"100010011"},
//                {"100010100"},
//                {"100010101"},
//                {"100010110"},
//                {"100010111"},
//                {"100011000"},
//                {"100011001"},
//                {"100011010"},
//                {"100011011"},
//                {"100011100"},
//                {"100011101"},
//                {"100011110"},
//                {"100011111"},
//                {"100100000"},
//                {"100100001"},
//                {"100100010"},
//                {"100100011"},
//                {"100100100"},
//                {"100100101"},
//                {"100100110"},
//                {"100100111"},
//                {"100101000"},
//                {"100101001"},
//                {"100101010"},
//                {"100101011"},
//                {"100101100"},
//                {"100101101"},
//                {"100101110"},
//                {"100101111"},
//                {"100110000"},
//                {"100110001"},
//                {"100110010"},
//                {"100110011"},
//                {"100110100"},
//                {"100110101"},
//                {"100110110"},
//                {"100110111"},
//                {"100111000"},
//                {"100111001"},
//                {"100111010"},
//                {"100111011"},
//                {"100111100"},
//                {"100111101"},
//                {"100111110"},
//                {"100111111"},
//                {"101000000"},
//                {"101000001"},
//                {"101000010"},
//                {"101000011"},
//                {"101000100"},
//                {"101000101"},
//                {"101000110"},
//                {"101000111"},
//                {"101001000"},
//                {"101001001"},
//                {"101001010"},
//                {"101001011"},
//                {"101001100"},
//                {"101001101"},
//                {"101001110"},
//                {"101001111"},
//                {"101010000"},
//                {"101010001"},
//                {"101010010"},
//                {"101010011"},
//                {"101010100"},
//                {"101010101"},
//                {"101010110"},
//                {"101010111"},
//                {"101011000"},
//                {"101011001"},
//                {"101011010"},
//                {"101011011"},
//                {"101011100"},
//                {"101011101"},
//                {"101011110"},
//                {"101011111"},
//                {"101100000"},
//                {"101100001"},
//                {"101100010"},
//                {"101100011"},
//                {"101100100"},
//                {"101100101"},
//                {"101100110"},
//                {"101100111"},
//                {"101101000"},
//                {"101101001"},
//                {"101101010"},
//                {"101101011"},
//                {"101101100"},
//                {"101101101"},
//                {"101101110"},
//                {"101101111"},
//                {"101110000"},
//                {"101110001"},
//                {"101110010"},
//                {"101110011"},
//                {"101110100"},
//                {"101110101"},
//                {"101110110"},
//                {"101110111"},
//                {"101111000"},
//                {"101111001"},
//                {"101111010"},
//                {"101111011"},
//                {"101111100"},
//                {"101111101"},
//                {"101111110"},
//                {"101111111"},
//                {"110000000"},
//                {"110000001"},
//                {"110000010"},
//                {"110000011"},
//                {"110000100"},
//                {"110000101"},
//                {"110000110"},
//                {"110000111"},
//                {"110001000"},
//                {"110001001"},
//                {"110001010"},
//                {"110001011"},
//                {"110001100"},
//                {"110001101"},
//                {"110001110"},
//                {"110001111"},
//                {"110010000"},
//                {"110010001"},
//                {"110010010"},
//                {"110010011"},
//                {"110010100"},
//                {"110010101"},
//                {"110010110"},
//                {"110010111"},
//                {"110011000"},
//                {"110011001"},
//                {"110011010"},
//                {"110011011"},
//                {"110011100"},
//                {"110011101"},
//                {"110011110"},
//                {"110011111"},
//                {"110100000"},
//                {"110100001"},
//                {"110100010"},
//                {"110100011"},
//                {"110100100"},
//                {"110100101"},
//                {"110100110"},
//                {"110100111"},
//                {"110101000"},
//                {"110101001"},
//                {"110101010"},
//                {"110101011"},
//                {"110101100"},
//                {"110101101"},
//                {"110101110"},
//                {"110101111"},
//                {"110110000"},
//                {"110110001"},
//                {"110110010"},
//                {"110110011"},
//                {"110110100"},
//                {"110110101"},
//                {"110110110"},
//                {"110110111"},
//                {"110111000"},
//                {"110111001"},
//                {"110111010"},
//                {"110111011"},
//                {"110111100"},
//                {"110111101"},
//                {"110111110"},
//                {"110111111"},
//                {"111000000"},
//                {"111000001"},
//                {"111000010"},
//                {"111000011"},
//                {"111000100"},
//                {"111000101"},
//                {"111000110"},
//                {"111000111"},
//                {"111001000"},
//                {"111001001"},
//                {"111001010"},
//                {"111001011"},
//                {"111001100"},
//                {"111001101"},
//                {"111001110"},
//                {"111001111"},
//                {"111010000"},
//                {"111010001"},
//                {"111010010"},
//                {"111010011"},
//                {"111010100"},
//                {"111010101"},
//                {"111010110"},
//                {"111010111"},
//                {"111011000"},
//                {"111011001"},
//                {"111011010"},
//                {"111011011"},
//                {"111011100"},
//                {"111011101"},
//                {"111011110"},
//                {"111011111"},
//                {"111100000"},
//                {"111100001"},
//                {"111100010"},
//                {"111100011"},
//                {"111100100"},
//                {"111100101"},
//                {"111100110"},
//                {"111100111"},
//                {"111101000"},
//                {"111101001"},
//                {"111101010"},
//                {"111101011"},
//                {"111101100"},
//                {"111101101"},
//                {"111101110"},
//                {"111101111"},
//                {"111110000"},
//                {"111110001"},
//                {"111110010"},
//                {"111110011"},
//                {"111110100"},
//                {"111110101"},
//                {"111110110"},
//                {"111110111"},
//                {"111111000"},
//                {"111111001"},
//                {"111111010"},
//                {"111111011"},
//                {"111111100"},
//                {"111111101"},
//                {"111111110"},
//                {"111111111"},
//                {"1000000000"},
//                {"1000000001"},
//                {"1000000010"},
//                {"1000000011"},
//                {"1000000100"},
//                {"1000000101"},
//                {"1000000110"},
//                {"1000000111"},
//                {"1000001000"},
//                {"1000001001"},
//                {"1000001010"},
//                {"1000001011"},
//                {"1000001100"},
//                {"1000001101"},
//                {"1000001110"},
//                {"1000001111"},
//                {"1000010000"},
//                {"1000010001"},
//                {"1000010010"},
//                {"1000010011"},
//                {"1000010100"},
//                {"1000010101"},
//                {"1000010110"},
//                {"1000010111"},
//                {"1000011000"},
//                {"1000011001"},
//                {"1000011010"},
//                {"1000011011"},
//                {"1000011100"},
//                {"1000011101"},
//                {"1000011110"},
//                {"1000011111"},
//                {"1000100000"},
//                {"1000100001"},
//                {"1000100010"},
//                {"1000100011"},
//                {"1000100100"},
//                {"1000100101"},
//                {"1000100110"},
//                {"1000100111"},
//                {"1000101000"},
//                {"1000101001"},
//                {"1000101010"},
//                {"1000101011"},
//                {"1000101100"},
//                {"1000101101"},
//                {"1000101110"},
//                {"1000101111"},
//                {"1000110000"},
//                {"1000110001"},
//                {"1000110010"},
//                {"1000110011"},
//                {"1000110100"},
//                {"1000110101"},
//                {"1000110110"},
//                {"1000110111"},
//                {"1000111000"},
//                {"1000111001"},
//                {"1000111010"},
//                {"1000111011"},
//                {"1000111100"},
//                {"1000111101"},
//                {"1000111110"},
//                {"1000111111"},
//                {"1001000000"},
//                {"1001000001"},
//                {"1001000010"},
//                {"1001000011"},
//                {"1001000100"},
//                {"1001000101"},
//                {"1001000110"},
//                {"1001000111"},
//                {"1001001000"},
//                {"1001001001"},
//                {"1001001010"},
//                {"1001001011"},
//                {"1001001100"},
//                {"1001001101"},
//                {"1001001110"},
//                {"1001001111"},
//                {"1001010000"},
//                {"1001010001"},
//                {"1001010010"},
//                {"1001010011"},
//                {"1001010100"},
//                {"1001010101"},
//                {"1001010110"},
//                {"1001010111"},
//                {"1001011000"},
//                {"1001011001"},
//                {"1001011010"},
//                {"1001011011"},
//                {"1001011100"},
//                {"1001011101"},
//                {"1001011110"},
//                {"1001011111"},
//                {"1001100000"},
//                {"1001100001"},
//                {"1001100010"},
//                {"1001100011"},
//                {"1001100100"},
//                {"1001100101"},
//                {"1001100110"},
//                {"1001100111"},
//                {"1001101000"},
//                {"1001101001"},
//                {"1001101010"},
//                {"1001101011"},
//                {"1001101100"},
//                {"1001101101"},
//                {"1001101110"},
//                {"1001101111"},
//                {"1001110000"},
//                {"1001110001"},
//                {"1001110010"},
//                {"1001110011"},
//                {"1001110100"},
//                {"1001110101"},
//                {"1001110110"},
//                {"1001110111"},
//                {"1001111000"},
//                {"1001111001"},
//                {"1001111010"},
//                {"1001111011"},
//                {"1001111100"},
//                {"1001111101"},
//                {"1001111110"},
//                {"1001111111"},
//                {"1010000000"},
//                {"1010000001"},
//                {"1010000010"},
//                {"1010000011"},
//                {"1010000100"},
//                {"1010000101"},
//                {"1010000110"},
//                {"1010000111"},
//                {"1010001000"},
//                {"1010001001"},
//                {"1010001010"},
//                {"1010001011"},
//                {"1010001100"},
//                {"1010001101"},
//                {"1010001110"},
//                {"1010001111"},
//                {"1010010000"},
//                {"1010010001"},
//                {"1010010010"},
//                {"1010010011"},
//                {"1010010100"},
//                {"1010010101"},
//                {"1010010110"},
//                {"1010010111"},
//                {"1010011000"},
//                {"1010011001"},
//                {"1010011010"},
//                {"1010011011"},
//                {"1010011100"},
//                {"1010011101"},
//                {"1010011110"},
//                {"1010011111"},
//                {"1010100000"},
//                {"1010100001"},
//                {"1010100010"},
//                {"1010100011"},
//                {"1010100100"},
//                {"1010100101"},
//                {"1010100110"},
//                {"1010100111"},
//                {"1010101000"},
//                {"1010101001"},
//                {"1010101010"},
//                {"1010101011"},
//                {"1010101100"},
//                {"1010101101"},
//                {"1010101110"},
//                {"1010101111"},
//                {"1010110000"},
//                {"1010110001"},
//                {"1010110010"},
//                {"1010110011"},
//                {"1010110100"},
//                {"1010110101"},
//                {"1010110110"},
//                {"1010110111"},
//                {"1010111000"},
//                {"1010111001"},
//                {"1010111010"},
//                {"1010111011"},
//                {"1010111100"},
//                {"1010111101"},
//                {"1010111110"},
//                {"1010111111"},
//                {"1011000000"},
//                {"1011000001"},
//                {"1011000010"},
//                {"1011000011"},
//                {"1011000100"},
//                {"1011000101"},
//                {"1011000110"},
//                {"1011000111"},
//                {"1011001000"},
//                {"1011001001"},
//                {"1011001010"},
//                {"1011001011"},
//                {"1011001100"},
//                {"1011001101"},
//                {"1011001110"},
//                {"1011001111"},
//                {"1011010000"},
//                {"1011010001"},
//                {"1011010010"},
//                {"1011010011"},
//                {"1011010100"},
//                {"1011010101"},
//                {"1011010110"},
//                {"1011010111"},
//                {"1011011000"},
//                {"1011011001"},
//                {"1011011010"},
//                {"1011011011"},
//                {"1011011100"},
//                {"1011011101"},
//                {"1011011110"},
//                {"1011011111"},
//                {"1011100000"},
//                {"1011100001"},
//                {"1011100010"},
//                {"1011100011"},
//                {"1011100100"},
//                {"1011100101"},
//                {"1011100110"},
//                {"1011100111"},
//                {"1011101000"},
//                {"1011101001"},
//                {"1011101010"},
//                {"1011101011"},
//                {"1011101100"},
//                {"1011101101"},
//                {"1011101110"},
//                {"1011101111"},
//                {"1011110000"},
//                {"1011110001"},
//                {"1011110010"},
//                {"1011110011"},
//                {"1011110100"},
//                {"1011110101"},
//                {"1011110110"},
//                {"1011110111"},
//                {"1011111000"},
//                {"1011111001"},
//                {"1011111010"},
//                {"1011111011"},
//                {"1011111100"},
//                {"1011111101"},
//                {"1011111110"},
//                {"1011111111"},
//                {"1100000000"},
//                {"1100000001"},
//                {"1100000010"},
//                {"1100000011"},
//                {"1100000100"},
//                {"1100000101"},
//                {"1100000110"},
//                {"1100000111"},
//                {"1100001000"},
//                {"1100001001"},
//                {"1100001010"},
//                {"1100001011"},
//                {"1100001100"},
//                {"1100001101"},
//                {"1100001110"},
//                {"1100001111"},
//                {"1100010000"},
//                {"1100010001"},
//                {"1100010010"},
//                {"1100010011"},
//                {"1100010100"},
//                {"1100010101"},
//                {"1100010110"},
//                {"1100010111"},
//                {"1100011000"},
//                {"1100011001"},
//                {"1100011010"},
//                {"1100011011"},
//                {"1100011100"},
//                {"1100011101"},
//                {"1100011110"},
//                {"1100011111"},
//                {"1100100000"},
//                {"1100100001"},
//                {"1100100010"},
//                {"1100100011"},
//                {"1100100100"},
//                {"1100100101"},
//                {"1100100110"},
//                {"1100100111"},
//                {"1100101000"},
//                {"1100101001"},
//                {"1100101010"},
//                {"1100101011"},
//                {"1100101100"},
//                {"1100101101"},
//                {"1100101110"},
//                {"1100101111"},
//                {"1100110000"},
//                {"1100110001"},
//                {"1100110010"},
//                {"1100110011"},
//                {"1100110100"},
//                {"1100110101"},
//                {"1100110110"},
//                {"1100110111"},
//                {"1100111000"},
//                {"1100111001"},
//                {"1100111010"},
//                {"1100111011"},
//                {"1100111100"},
//                {"1100111101"},
//                {"1100111110"},
//                {"1100111111"},
//                {"1101000000"},
//                {"1101000001"},
//                {"1101000010"},
//                {"1101000011"},
//                {"1101000100"},
//                {"1101000101"},
//                {"1101000110"},
//                {"1101000111"},
//                {"1101001000"},
//                {"1101001001"},
//                {"1101001010"},
//                {"1101001011"},
//                {"1101001100"},
//                {"1101001101"},
//                {"1101001110"},
//                {"1101001111"},
//                {"1101010000"},
//                {"1101010001"},
//                {"1101010010"},
//                {"1101010011"},
//                {"1101010100"},
//                {"1101010101"},
//                {"1101010110"},
//                {"1101010111"},
//                {"1101011000"},
//                {"1101011001"},
//                {"1101011010"},
//                {"1101011011"},
//                {"1101011100"},
//                {"1101011101"},
//                {"1101011110"},
//                {"1101011111"},
//                {"1101100000"},
//                {"1101100001"},
//                {"1101100010"},
//                {"1101100011"},
//                {"1101100100"},
//                {"1101100101"},
//                {"1101100110"},
//                {"1101100111"},
//                {"1101101000"},
//                {"1101101001"},
//                {"1101101010"},
//                {"1101101011"},
//                {"1101101100"},
//                {"1101101101"},
//                {"1101101110"},
//                {"1101101111"},
//                {"1101110000"},
//                {"1101110001"},
//                {"1101110010"},
//                {"1101110011"},
//                {"1101110100"},
//                {"1101110101"},
//                {"1101110110"},
//                {"1101110111"},
//                {"1101111000"},
//                {"1101111001"},
//                {"1101111010"},
//                {"1101111011"},
//                {"1101111100"},
//                {"1101111101"},
//                {"1101111110"},
//                {"1101111111"},
//                {"1110000000"},
//                {"1110000001"},
//                {"1110000010"},
//                {"1110000011"},
//                {"1110000100"},
//                {"1110000101"},
//                {"1110000110"},
//                {"1110000111"},
//                {"1110001000"},
//                {"1110001001"},
//                {"1110001010"},
//                {"1110001011"},
//                {"1110001100"},
//                {"1110001101"},
//                {"1110001110"},
//                {"1110001111"},
//                {"1110010000"},
//                {"1110010001"},
//                {"1110010010"},
//                {"1110010011"},
//                {"1110010100"},
//                {"1110010101"},
//                {"1110010110"},
//                {"1110010111"},
//                {"1110011000"},
//                {"1110011001"},
//                {"1110011010"},
//                {"1110011011"},
//                {"1110011100"},
//                {"1110011101"},
//                {"1110011110"},
//                {"1110011111"},
//                {"1110100000"},
//                {"1110100001"},
//                {"1110100010"},
//                {"1110100011"},
//                {"1110100100"},
//                {"1110100101"},
//                {"1110100110"},
//                {"1110100111"},
//                {"1110101000"},
//                {"1110101001"},
//                {"1110101010"},
//                {"1110101011"},
//                {"1110101100"},
//                {"1110101101"},
//                {"1110101110"},
//                {"1110101111"},
//                {"1110110000"},
//                {"1110110001"},
//                {"1110110010"},
//                {"1110110011"},
//                {"1110110100"},
//                {"1110110101"},
//                {"1110110110"},
//                {"1110110111"},
//                {"1110111000"},
//                {"1110111001"},
//                {"1110111010"},
//                {"1110111011"},
//                {"1110111100"},
//                {"1110111101"},
//                {"1110111110"},
//                {"1110111111"},
//                {"1111000000"},
//                {"1111000001"},
//                {"1111000010"},
//                {"1111000011"},
//                {"1111000100"},
//                {"1111000101"},
//                {"1111000110"},
//                {"1111000111"},
//                {"1111001000"},
//                {"1111001001"},
//                {"1111001010"},
//                {"1111001011"},
//                {"1111001100"},
//                {"1111001101"},
//                {"1111001110"},
//                {"1111001111"},
//                {"1111010000"},
//                {"1111010001"},
//                {"1111010010"},
//                {"1111010011"},
//                {"1111010100"},
//                {"1111010101"},
//                {"1111010110"},
//                {"1111010111"},
//                {"1111011000"},
//                {"1111011001"},
//                {"1111011010"},
//                {"1111011011"},
//                {"1111011100"},
//                {"1111011101"},
//                {"1111011110"},
//                {"1111011111"},
//                {"1111100000"},
//                {"1111100001"},
//                {"1111100010"},
//                {"1111100011"},
//                {"1111100100"},
//                {"1111100101"},
//                {"1111100110"},
//                {"1111100111"},
//                {"1111101000"},
//                {"1111101001"},
//                {"1111101010"},
//                {"1111101011"},
//                {"1111101100"},
//                {"1111101101"},
//                {"1111101110"},
//                {"1111101111"},
//                {"1111110000"},
//                {"1111110001"},
//                {"1111110010"},
//                {"1111110011"},
//                {"1111110100"},
//                {"1111110101"},
//                {"1111110110"},
//                {"1111110111"},
//                {"1111111000"},
//                {"1111111001"},
//                {"1111111010"},
                {"1111111011"},
//                {"1111111100"},
                {"1111111101"},
//                {"1111111110"},
                {"1111111111"}
        };
    }
    CreatePermission setPermissionDiscountCampaignModel(String discountCampaignPermissionBinary) {
        CreatePermission model = new CreatePermission();
        model.setHome_none("11");
        //hard code
        model.setCustomer_segment("1");
        model.setProduct_productManagement("1");
        model.setProduct_collection("1");
        model.setService_serviceManagement("1");
        model.setService_serviceCollection("1");
        //random
        Random rd = new Random();
//        if (rd.nextBoolean()) {
//            model.setCustomer_segment("1");
//        } else model.setCustomer_segment("0");
//        if (rd.nextBoolean()) {
//            model.setProduct_productManagement("1");
//        } else if (rd.nextBoolean()) {
//            model.setProduct_productManagement("01");
//        } else model.setProduct_productManagement("00");
//        if (rd.nextBoolean()) {
//            model.setProduct_collection("1");
//        }else model.setProduct_collection("0");
//        if (rd.nextBoolean()) {
//            model.setService_serviceManagement("1");
//        } else if (rd.nextBoolean()) {
//            model.setService_serviceManagement("01");
//        } else model.setService_serviceManagement("00");
//        if (rd.nextBoolean()) {
//            model.setService_serviceCollection("1");
//        }else model.setService_serviceCollection("0");
        model.setPromotion_discountCampaign(discountCampaignPermissionBinary);
        return model;
    }
    //BH-24962
    @Test(dataProvider = "DiscountCampaignPermissionModel")
    public void checkPermissionDiscountCampaign(String discountCampaignPermissionBinary){
//        //edit permisison
        new PermissionAPI(ownerCredentials).editGroupPermissionAndGetID(groupPermissionId, "Vi's Permission "+groupPermissionId, "Description Tien's Permission", setPermissionDiscountCampaignModel(discountCampaignPermissionBinary));
        //Get info of the staff after being granted the permission
        staffLoginInfo = new Login().getInfo(staffCredentials);
        //Get permission
        AllPermissions allPermissions = new AllPermissions(staffLoginInfo.getStaffPermissionToken());
        //Check on UI
        new LoginPage(driver).staffLogin(staffUserName, staffPass);
        new HomePage(driver).waitTillSpinnerDisappear1().selectLanguage(languageDB).hideFacebookBubble();
        new DiscountPage(driver).getLoginInformation(ownerCredentials,staffCredentials).verifyPermissionDiscountCampaign(allPermissions,productCreatedByShopOwner,productCreatedByStaff,serviceCreatedByShowOwner,serviceCreateByStaff,productIds.get(0));
    }
}
