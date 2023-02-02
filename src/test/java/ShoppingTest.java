//import api.dashboard.customers.Customers;
//import api.dashboard.login.Login;
//import api.dashboard.products.CreateProduct;
//import api.dashboard.promotion.CreatePromotion;
//import api.dashboard.setting.BranchManagement;
//import api.dashboard.setting.StoreInformation;
//import api.dashboard.setting.VAT;
//import api.storefront.login.LoginSF;
//import api.storefront.signup.SignUp;
//import org.testng.annotations.BeforeGroups;
//import org.testng.annotations.BeforeSuite;
//import org.testng.annotations.Test;
//import pages.storefront.detail_product.ProductDetailPage;
//import pages.storefront.login.LoginPage;
//import pages.storefront.shoppingcart.ShoppingCart;
//
//import java.io.IOException;
//import java.sql.SQLException;
//
//import static api.dashboard.setting.StoreInformation.apiStoreURL;
//import static api.storefront.signup.SignUp.*;
//import static java.lang.Thread.sleep;
//import static utilities.links.Links.SF_DOMAIN;
//
//public class ShoppingTest extends BaseTest {
//
//    String sfDomain;
//
//    @BeforeSuite
//    void initPreCondition() throws SQLException, InterruptedException {
//        new Login().loginToDashboardByMail(sellerAccount, sellerPassword);
//
//        new BranchManagement().getBranchInformation();
//        new StoreInformation().getStoreInformation();
//
//        new VAT().getTaxList();
//
//        new SignUp().signUpByPhoneNumber();
//
//        new LoginSF().LoginByPhoneNumber();
//
//        sleep(3000);
//
//        new Customers().addCustomerTag(customerName).createSegment();
//
//        sfDomain = "https://%s%s/".formatted(apiStoreURL, SF_DOMAIN);
//
//    }
//
//    @BeforeGroups(groups = "Normal product - Without variation")
//    void preCondition_G1() {
//        boolean isIMEIProduct = false;
//        int branchStock = 5;
//        new CreateProduct().createWithoutVariationProduct(isIMEIProduct,
//                        branchStock,
//                        branchStock,
//                        branchStock,
//                        branchStock,
//                        branchStock,
//                        branchStock)
//                .addWholesalePriceProduct()
//                .createCollection();
//    }
//
//    @BeforeGroups(groups = "IMEI product - Without variation")
//    void preCondition_G2() {
//        boolean isIMEIProduct = true;
//        int branchStock = 5;
//        new CreateProduct().createWithoutVariationProduct(isIMEIProduct,
//                        branchStock,
//                        branchStock,
//                        branchStock,
//                        branchStock,
//                        branchStock,
//                        branchStock)
//                .addWholesalePriceProduct()
//                .createCollection();
//    }
//
//    @BeforeGroups(groups = "Normal product - Variation")
//    void preCondition_G3() {
//        boolean isIMEIProduct = false;
//        int branchStock = 2;
//        int increaseNum = 1;
//        new CreateProduct().createVariationProduct(isIMEIProduct,
//                        increaseNum,
//                        branchStock,
//                        branchStock,
//                        branchStock,
//                        branchStock,
//                        branchStock,
//                        branchStock)
//                .addWholesalePriceProduct()
//                .createCollection();
//    }
//
//    @BeforeGroups(groups = "IMEI product - Variation")
//    void preCondition_G4() {
//        boolean isIMEIProduct = true;
//        int branchStock = 2;
//        int increaseNum = 1;
//        new CreateProduct().createVariationProduct(isIMEIProduct,
//                        increaseNum,
//                        branchStock,
//                        branchStock,
//                        branchStock,
//                        branchStock,
//                        branchStock,
//                        branchStock,
//                        branchStock)
//                .addWholesalePriceProduct()
//                .createCollection();
//    }
//
//    @Test(groups = "Normal product - Without variation")
//    void BH_SPC_G1_Case1_2_FlashSaleIsInProgress() throws IOException {
//        new LoginPage(driver)
//                .navigate(sfDomain)
//                .performLogin(phoneNumber, password);
//
//        new ProductDetailPage(driver).accessToProductDetailPageByProductID().addProductToCart();
//
//        new ShoppingCart(driver).navigateToShoppingCartByURL().getShoppingCartInfo();
//    }
//
//    @Test(groups = "Normal product - Variation")
//    void BH_SPC_G3_Case1_2_FlashSaleIsInProgress() throws IOException, InterruptedException {
//        int startMin = 1;
//        int endMin = 60;
//        new LoginPage(driver)
//                .navigate(sfDomain)
//                .performLogin(phoneNumber, password);
//
//        new CreatePromotion()
//                .createFlashSale(startMin, endMin)
//                .createProductDiscountCampaign(startMin, endMin);
//
//        sleep(startMin * 60 * 1000);
//
//        new ProductDetailPage(driver).accessToProductDetailPageByProductID().addProductToCart();
//
//        new ShoppingCart(driver).navigateToShoppingCartByURL().getShoppingCartInfo();
//    }
//}
