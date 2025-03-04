package web.Dashboard;

import api.Seller.login.Login;
import api.Seller.marketing.APIBuyLink;
import api.Seller.marketing.membership.APIEditLoyaltyProgram;
import api.Seller.sale_channel.onlineshop.APIPreferences;
import api.Seller.products.all_products.APIEditProduct;
import api.Seller.products.all_products.APICreateProduct;
import api.Seller.promotion.CreatePromotion;
import api.Seller.promotion.ProductDiscountCampaign;
import org.testng.ITestResult;
import org.testng.annotations.*;

import utilities.api.API;
import utilities.enums.Domain;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.marketing.buylink.BuyLinkManagement;
import web.Dashboard.marketing.buylink.CreateBuyLink;
import web.Dashboard.products.all_products.crud.ProductPage;
import web.StoreFront.GeneralSF;
import web.StoreFront.checkout.checkoutOneStep.Checkout;
import web.StoreFront.checkout.ordercomplete.OrderComplete;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.quicklycheckout.QuicklyCheckout;
import web.StoreFront.signup.SignupPage;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.model.sellerApp.login.LoginInformation;

import java.util.ArrayList;
import java.util.List;

import static utilities.account.AccountTest.COUNTRY_BIZ;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;
import static utilities.file.FileNameAndPath.FILE_BUY_LINK_TCS;
import static utilities.links.Links.SF_ShopVi;
import static utilities.links.Links.SF_ShopViBIZ;

public class BuyLinkTest extends BaseTest {
    String userNameDb;
    String passWordDb;
    String userNameSF;
    String passWordSF;
    String languageDB;
    String languageSF;
    String country;
    LoginPage login;
    HomePage home;
    BuyLinkManagement buyLinkManagement;
    CreateBuyLink createBuyLink;
    String[] productName;
    GeneralSF generalSF;
    HeaderSF headerSF;
    web.StoreFront.login.LoginPage loginSF;
    String discountCodeName;
    double productPrice;
    double productPriceAfterDiscount;
    double discountAmount;
    SignupPage signupSF;
    String shopDomain;
    QuicklyCheckout quicklyCheckout;
    LoginInformation loginInformation;
    List<Integer> productIds = new ArrayList<>();
    @BeforeClass
    public void beforeClass() {
        if(domain.equals(Domain.VN.name())){
            userNameDb = AccountTest.ADMIN_SHOP_VI_USERNAME;
            passWordDb = AccountTest.ADMIN_SHOP_VI_PASSWORD;
            userNameSF = AccountTest.SF_USERNAME_VI_1;
            passWordSF = AccountTest.SF_SHOP_VI_PASSWORD;
            shopDomain = SF_ShopVi;
            country = "Vietnam";
        }else {
            userNameDb = AccountTest.ADMIN_SHOP_VI_USERNAME_BIZ;
            passWordDb = AccountTest.ADMIN_SHOP_VI_PASSWORD_BIZ;
            userNameSF = AccountTest.SF_USERNAME_VI_1;
            passWordSF = AccountTest.SF_SHOP_VI_PASSWORD;
            shopDomain = SF_ShopViBIZ;
            country = COUNTRY_BIZ;
        }
        languageDB = language;
        languageSF = language;
        MAX_PRICE = 999999L;
        loginInformation = new Login().setLoginInformation("+84",userNameDb,passWordDb).getLoginInformation();
        tcsFileName = FILE_BUY_LINK_TCS;
        generate = new DataGenerator();
        new ProductDiscountCampaign(loginInformation).endEarlyDiscountCampaign();
        new APIEditLoyaltyProgram(loginInformation).turnOffMembershipBenefits();
    }
    @BeforeMethod
    public void setUp(){
        driver = new InitWebdriver().getDriver(browser,headless);
    }
    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
//        super.writeResult(result);
        if (driver != null) driver.quit();
    }
    @AfterClass
    public void afterClass(){
        //delete product
        for (int productId:productIds) {
            new APIEditProduct(loginInformation).deleteProduct(productId);
        }
    }
    public void deleteNewestBuyLink(){
        APIBuyLink apiBuyLink = new APIBuyLink(loginInformation);
        int id = apiBuyLink.getNewestBuyLinkID();
        apiBuyLink.deleteBuyLinkById(id);
    }
    public BuyLinkManagement LoginAndNavigateToBuyLinkPage() {
        login = new LoginPage(driver, Domain.valueOf(domain));
        login.navigate().performValidLogin(country,userNameDb, passWordDb);
        home = new HomePage(driver);
        home.waitTillSpinnerDisappear1();
        if(domain.equals(Domain.VN.name())) home.selectLanguage(languageDB);
        home.hideFacebookBubble();
        return new BuyLinkManagement(driver, Domain.valueOf(domain)).navigateUrl();
    }
    public CreateBuyLink LoginAndNavigateToCreateBuyLinkPage() {
        login = new LoginPage(driver, Domain.valueOf(domain));
        login.navigate().performValidLogin(country, userNameDb, passWordDb);
        home = new HomePage(driver);
        home.waitTillSpinnerDisappear1();
        if(domain.equals(Domain.VN.name())) home.selectLanguage(languageDB);
        home.hideFacebookBubble();
        buyLinkManagement = new BuyLinkManagement(driver, Domain.valueOf(domain));
        buyLinkManagement.navigateUrl().clickExploreNow();
        return buyLinkManagement.clickCreateBuyLink();
    }
    public void changeLanguageSF(){
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon();
        if(domain.equals(Domain.VN.name()))
            headerSF.changeLanguage(languageSF).waitTillLoaderDisappear();
    }
    @Test
    public void BL01_CheckTextOnBuyLinkManagementPage() throws Exception {
        testCaseId = "BL01";
        buyLinkManagement = LoginAndNavigateToBuyLinkPage();
        buyLinkManagement.clickExploreNow().VerifyText();
    }

    @Test
    public void BL02_CheckTextOnCreateBuyLinkPage() throws Exception {
        testCaseId = "BL02";
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        createBuyLink.VerifyText();
    }

    @Test
    public void BL03_CheckCreateBuyLinkWithoutDiscountAndCheckout() throws Exception {
        testCaseId = "BL03";
        new APIPreferences(loginInformation).setUpGuestCheckout(false);
        APICreateProduct productInfo = new APICreateProduct(loginInformation).createWithoutVariationProduct(false,1);
        int productId =  productInfo.getProductID();
        productIds.add(productId); // add to list to clear data
        productName = new String[]{new APICreateProduct(loginInformation).getProductName()};
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        changeLanguageSF();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        loginSF = new GeneralSF(driver).clickOnLoginButtonOnRequiredLoginModal();
        loginSF.inputEmailOrPhoneNumber(userNameSF).inputPassword(passWordSF).clickLoginBtn();
        new Checkout(driver, Domain.valueOf(domain)).clickOnCompleteBtn()
                .verifyProductNames(productName);

        new OrderComplete(driver, Domain.valueOf(domain))
                .verifyDiscountAmount(0);
        deleteNewestBuyLink();
    }
    @Test
    public void BL04_CheckBuyLinkFixAmountDiscountCodeAndCheckout() throws Exception {
        testCaseId = "BL04";
        CreatePromotion.apiIsLimitToOne=false;
        CreatePromotion.apiIsLimitToUsage=false;
        CreatePromotion.apiDiscountCodeType=1;
        CreatePromotion.apiIsEnabledReward=false;
        CreatePromotion.apiSegmentConditionType=0;
        CreatePromotion.apiAppliesCondtionType=0;
        CreatePromotion.apiMinimumRequiredType=0;
        CreatePromotion.apiApplicableBranchCondition=0;
        new APIPreferences(loginInformation).setUpGuestCheckout(false);
        APICreateProduct productInfo = new APICreateProduct(loginInformation).createWithoutVariationProduct(false,10);
        int productId =  productInfo.getProductID();
        productIds.add(productId);
        new CreatePromotion(loginInformation).createProductDiscountCode(0);

        productName = new String[]{new APICreateProduct(loginInformation).getProductName()};
        discountCodeName = CreatePromotion.apiDiscountName;
        productPrice = new APICreateProduct(loginInformation).getProductSellingPrice().get(0);
        discountAmount = CreatePromotion.apiCouponValue;
        if(productPrice<discountAmount){
            discountAmount = productPrice;
        }

        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .searchAndSelectPromotion(discountCodeName)
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        changeLanguageSF();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        loginSF = new GeneralSF(driver).clickOnLoginButtonOnRequiredLoginModal();
        loginSF.inputEmailOrPhoneNumber(userNameSF).inputPassword(passWordSF).clickLoginBtn();
        new Checkout(driver, Domain.valueOf(domain))
                .verifyDicountAmount(discountAmount)
                .clickOnCompleteBtn()
                .verifyProductNames(productName)
                .verifyDiscountAmount(discountAmount);
        deleteNewestBuyLink();
    }
    @Test
    public void BL05_CheckBuyLinkPercentDiscountCodeAndCheckout() throws Exception {
        testCaseId = "BL05";
        CreatePromotion.apiIsLimitToOne=false;
        CreatePromotion.apiIsLimitToUsage=false;
        CreatePromotion.apiDiscountCodeType=0;
        CreatePromotion.apiIsEnabledReward=false;
        CreatePromotion.apiSegmentConditionType=0;
        CreatePromotion.apiAppliesCondtionType=0;
        CreatePromotion.apiMinimumRequiredType=0;
        CreatePromotion.apiApplicableBranchCondition=0;
        APICreateProduct productInfo = new APICreateProduct(loginInformation).createWithoutVariationProduct(false,10);
        int productId =  productInfo.getProductID();
        productIds.add(productId);
        new CreatePromotion(loginInformation).createProductDiscountCode(0);
        productName = new String[]{new APICreateProduct(loginInformation).getProductName()};
        discountCodeName = CreatePromotion.apiDiscountName;
        productPrice = new APICreateProduct(loginInformation).getProductSellingPrice().get(0);
        double percent =  CreatePromotion.apiCouponValue;
        if(domain.equals(Domain.VN.name())) {
            productPriceAfterDiscount = Math.ceil(productPrice - percent * productPrice / 100);
            discountAmount = productPrice - productPriceAfterDiscount;
        }
        else{
            productPriceAfterDiscount = productPrice - (productPrice * percent / 100);
            System.out.println("productPriceAfterDiscount: "+productPriceAfterDiscount);
            discountAmount =  Math.floor((productPrice - productPriceAfterDiscount) * 100) / 100;
        }

        System.out.println("productPrice: "+productPrice);
        System.out.println("percent: "+percent);
        System.out.println("productPriceAfterDiscount: "+productPriceAfterDiscount);
        System.out.println("discountAmount: "+discountAmount);

        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .searchAndSelectPromotion(discountCodeName)
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        changeLanguageSF();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        loginSF = new GeneralSF(driver).clickOnLoginButtonOnRequiredLoginModal();
        loginSF.inputEmailOrPhoneNumber(userNameSF).inputPassword(passWordSF).clickLoginBtn();
        new Checkout(driver, Domain.valueOf(domain))
                .verifyDicountAmount(discountAmount)
                .clickOnCompleteBtn()
                .verifyProductNames(productName)
                .verifyDiscountAmount(discountAmount);
        deleteNewestBuyLink();
    }
    @Test
    public void BL06_CreateBuyLinkWithFreeShippingDiscountCodeAndCheckout() throws Exception {
        testCaseId = "BL06";
        CreatePromotion.apiIsLimitToOne=false;
        CreatePromotion.apiIsLimitToUsage=false;
        CreatePromotion.apiDiscountCodeType=2;
        CreatePromotion.apiIsEnabledReward=false;
        CreatePromotion.apiSegmentConditionType=0;
        CreatePromotion.apiAppliesCondtionType=0;
        CreatePromotion.apiMinimumRequiredType=0;
        CreatePromotion.apiApplicableBranchCondition=0;
        APICreateProduct productInfo = new APICreateProduct(loginInformation).createWithoutVariationProduct(false,10);
        int productId =  productInfo.getProductID();
        productIds.add(productId); // add to list to clear data
        new CreatePromotion(loginInformation).createProductDiscountCode(0,10);
        productName = new String[]{new APICreateProduct(loginInformation).getProductName()};
        discountCodeName = CreatePromotion.apiDiscountName;
        discountAmount = CreatePromotion.apiCouponValue;
        productPrice = new APICreateProduct(loginInformation).getProductSellingPrice().get(0);
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .searchAndSelectPromotion(discountCodeName)
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        changeLanguageSF();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        loginSF = new GeneralSF(driver).clickOnLoginButtonOnRequiredLoginModal();
        loginSF.inputEmailOrPhoneNumber(userNameSF).inputPassword(passWordSF).clickLoginBtn();
        new GeneralSF(driver).waitTillLoaderDisappear();
        double shippingFee = new Checkout(driver).getShippingFee();
        if(discountAmount>shippingFee){
            discountAmount = shippingFee;
        }

        new Checkout(driver, Domain.valueOf(domain))
                .verifyDicountAmount(discountAmount)
                .clickOnCompleteBtn()
                .verifyProductNames(productName)
                .verifyShippingFee(String.format("%.2f",discountAmount));
        deleteNewestBuyLink();
    }
    @Test
    public void BL07_CreateBuyLinkWithRewardDiscountCodeAndCheckout() throws Exception {
        testCaseId = "BL07";
        CreatePromotion.apiIsLimitToOne = false;
        CreatePromotion.apiIsLimitToUsage = false;
        CreatePromotion.apiDiscountCodeType = 0;
        CreatePromotion.apiIsEnabledReward = true;
        CreatePromotion.apiSegmentConditionType = 0;
        CreatePromotion.apiAppliesCondtionType = 0;
        CreatePromotion.apiMinimumRequiredType = 0;
        CreatePromotion.apiApplicableBranchCondition = 0;
        int productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false, 10).getProductID();
        productIds.add(productId); // add to list to clear data
        new CreatePromotion(loginInformation).createProductDiscountCode(0);
        productName = new String[]{new APICreateProduct(loginInformation).getProductName()};
        discountCodeName = CreatePromotion.apiDiscountName;
        productPrice = new APICreateProduct(loginInformation).getProductSellingPrice().get(0);
        double percent = CreatePromotion.apiCouponValue;
        if(domain.equals(Domain.VN.name())) {
            productPriceAfterDiscount = Math.ceil(productPrice - percent * productPrice / 100);
            discountAmount = productPrice - productPriceAfterDiscount;
        }
        else{
            productPriceAfterDiscount = productPrice - (productPrice * percent / 100);
            System.out.println("productPriceAfterDiscount: "+productPriceAfterDiscount);
            discountAmount =  Math.floor((productPrice - productPriceAfterDiscount) * 100) / 100;
        }

//        productPriceAfterDiscount = Math.ceil(productPrice - percent*productPrice/100);
//        discountAmount = productPrice - productPriceAfterDiscount;
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .searchAndSelectPromotion(discountCodeName)
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        changeLanguageSF();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        loginSF = new GeneralSF(driver).clickOnLoginButtonOnRequiredLoginModal();
        loginSF.inputEmailOrPhoneNumber(userNameSF).inputPassword(passWordSF).clickLoginBtn();
        new Checkout(driver, Domain.valueOf(domain))
                .verifyDicountAmount(discountAmount)
                .clickOnCompleteBtn()
                .verifyProductNames(productName)
                .verifyDiscountAmount(discountAmount);
        deleteNewestBuyLink();
    }
    //don't run on cloud
    @Test
    public void BL08_CheckNoAccountAndCheckoutWithBuyLink() throws Exception {
        testCaseId = "BL08";
        int productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false,1).getProductID();
        productIds.add(productId); // add to list to clear data
        productName = new String[]{new APICreateProduct(loginInformation).getProductName()};
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        changeLanguageSF();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        loginSF = new GeneralSF(driver).clickOnRegisterButtonOnRequiredLoginModal();
        signupSF = new SignupPage(driver);
        String generateName = generate.generateString(10);
        String buyerAccount_Signup = "09" + generate.generateNumber(8);
        String buyerDisplayName_Signup = generateName;
        signupSF.signUpWithPhoneNumber(country, buyerAccount_Signup, passWordSF, buyerDisplayName_Signup, "");
        new Checkout(driver)
                .verifyDicountAmount("0")
                .updateAddressVN()
                .clickOnCompleteBtn()
                .verifyProductNames(productName)
                .verifyDiscountAmount("0");
        deleteNewestBuyLink();
    }
    @Test
    public void BL09_CheckNavigateToBuyLink_EnableGuestCheckout() throws Exception {
        testCaseId = "BL09";
        new APIPreferences(loginInformation).setUpGuestCheckout(true);
        int productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false,1).getProductID();
        productIds.add(productId); // add to list to clear data
        productName = new String[]{new APICreateProduct(loginInformation).getProductName()};
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        changeLanguageSF();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        new Checkout(driver, Domain.valueOf(domain))
                .verifyDicountAmount("0")
                .clickOnEditIcon()
                .inputPhoneNumber(new DataGenerator().randomVNPhone())
                .inputAddressInfo_VN();
        new Checkout(driver, Domain.valueOf(domain)).clickOnConfirmButtonOnUpdateAddresModal()
                .clickOnCompleteBtn()
                .verifyProductNames(productName)
                .verifyDiscountAmount("0");
        new APIPreferences(loginInformation).setUpGuestCheckout(false);
        deleteNewestBuyLink();
        new APIEditProduct(loginInformation).deleteProduct(productId);
    }
    @Test
    public void BL10_CheckoutWithBuyLinkHasDiscountExpired() throws Exception {
        testCaseId = "BL10";
        CreatePromotion.apiIsLimitToOne=false;
        CreatePromotion.apiIsLimitToUsage=false;
        CreatePromotion.apiDiscountCodeType=0;
        CreatePromotion.apiIsEnabledReward=false;
        CreatePromotion.apiSegmentConditionType=0;
        CreatePromotion.apiAppliesCondtionType=0;
        CreatePromotion.apiMinimumRequiredType=0;
        CreatePromotion.apiApplicableBranchCondition=0;
        new APIPreferences(loginInformation).setUpGuestCheckout(true);
        int productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false,10).getProductID();
        productIds.add(productId); // add to list to clear data
        new CreatePromotion(loginInformation).createProductDiscountCode(0);
        productName = new String[]{new APICreateProduct(loginInformation).getProductName()};
        discountCodeName = CreatePromotion.apiDiscountName;
        productPrice = new APICreateProduct(loginInformation).getProductSellingPrice().get(0);
        discountAmount = CreatePromotion.apiCouponValue;
        int discountId = CreatePromotion.apiDiscountId;
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .searchAndSelectPromotion(discountCodeName)
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        new CreatePromotion(loginInformation).endEarlyDiscount(discountId);
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        changeLanguageSF();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        quicklyCheckout = new QuicklyCheckout(driver);
        quicklyCheckout.verifyShopCartHeader()
                .verifyDiscountInvalidError();
        deleteNewestBuyLink();
        new APIEditProduct(loginInformation).deleteProduct(productId);
        new APIPreferences(loginInformation).setUpGuestCheckout(false);

    }
    @Test
    public void BL11_CheckoutWithBuyLinkHasDiscountExceedMaximumUse() throws Exception {
        testCaseId = "BL11";
        CreatePromotion.apiIsLimitToOne=false;
        CreatePromotion.apiIsLimitToUsage=true;
        CreatePromotion.apiDiscountCodeType=0;
        CreatePromotion.apiIsEnabledReward=false;
        CreatePromotion.apiSegmentConditionType=0;
        CreatePromotion.apiAppliesCondtionType=0;
        CreatePromotion.apiMinimumRequiredType=0;
        CreatePromotion.apiApplicableBranchCondition=0;
        CreatePromotion.apiLimitTimesUse=1;
        new APIPreferences(loginInformation).setUpGuestCheckout(false);
        int productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false,10).getProductID();
        productIds.add(productId); // add to list to clear data
        new CreatePromotion(loginInformation).createProductDiscountCode(0);
        productName = new String[]{new APICreateProduct(loginInformation).getProductName()};
        discountCodeName = CreatePromotion.apiDiscountName;
        productPrice = new APICreateProduct(loginInformation).getProductSellingPrice().get(0);
        double percent =  CreatePromotion.apiCouponValue;
        if(domain.equals(Domain.VN.name())) {
            productPriceAfterDiscount = Math.ceil(productPrice - percent * productPrice / 100);
            discountAmount = productPrice - productPriceAfterDiscount;
        }
        else{
            productPriceAfterDiscount = productPrice - (productPrice * percent / 100);
            System.out.println("productPriceAfterDiscount: "+productPriceAfterDiscount);
            discountAmount =  Math.floor((productPrice - productPriceAfterDiscount) * 100) / 100;
        }
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .searchAndSelectPromotion(discountCodeName)
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        loginSF = new web.StoreFront.login.LoginPage(driver);
        loginSF.navigate(shopDomain)
                .performLogin(userNameSF, passWordSF);
        changeLanguageSF();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        new Checkout(driver, Domain.valueOf(domain))
                .verifyDicountAmount(discountAmount)
                .clickOnCompleteBtn()
                .verifyProductNames(productName)
                .verifyDiscountAmount(discountAmount);
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        quicklyCheckout = new QuicklyCheckout(driver);
        quicklyCheckout.verifyShopCartHeader()
                .verifyDiscountInvalidError();
        deleteNewestBuyLink();
        new APIPreferences(loginInformation).setUpGuestCheckout(false);
    }
    @Test
    public void BL12_CheckoutWithBuyLinkHasDiscountExceedMaximumUsePerUser() throws Exception {
        testCaseId = "BL12";
        CreatePromotion.apiIsLimitToOne=true;
        CreatePromotion.apiIsLimitToUsage=false;
        CreatePromotion.apiDiscountCodeType=0;
        CreatePromotion.apiIsEnabledReward=false;
        CreatePromotion.apiSegmentConditionType=0;
        CreatePromotion.apiAppliesCondtionType=0;
        CreatePromotion.apiMinimumRequiredType=0;
        CreatePromotion.apiApplicableBranchCondition=0;
        CreatePromotion.apiLimitTimesUse=1;
        new APIPreferences(loginInformation).setUpGuestCheckout(false);
        int productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false,10).getProductID();
        productIds.add(productId); // add to list to clear data
        new CreatePromotion(loginInformation).createProductDiscountCode(0);
        productName = new String[]{new APICreateProduct(loginInformation).getProductName()};
        discountCodeName = CreatePromotion.apiDiscountName;
        productPrice = new APICreateProduct(loginInformation).getProductSellingPrice().get(0);
        double percent =  CreatePromotion.apiCouponValue;

        if(domain.equals(Domain.VN.name())) {
            productPriceAfterDiscount = Math.ceil(productPrice - percent * productPrice / 100);
            discountAmount = productPrice - productPriceAfterDiscount;
        }
        else{
            productPriceAfterDiscount = productPrice - (productPrice * percent / 100);
            System.out.println("productPriceAfterDiscount: "+productPriceAfterDiscount);
            discountAmount =  Math.floor((productPrice - productPriceAfterDiscount) * 100) / 100;
        }
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .searchAndSelectPromotion(discountCodeName)
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        loginSF = new web.StoreFront.login.LoginPage(driver);
        loginSF.navigate(shopDomain)
                .performLogin(userNameSF, passWordSF);
        changeLanguageSF();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        new Checkout(driver, Domain.valueOf(domain))
                .verifyDicountAmount(discountAmount)
                .clickOnCompleteBtn()
                .verifyProductNames(productName)
                .verifyDiscountAmount(discountAmount);
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        quicklyCheckout = new QuicklyCheckout(driver);
        quicklyCheckout.verifyShopCartHeader()
                .verifyDiscountInvalidError();
        deleteNewestBuyLink();
        new APIPreferences(loginInformation).setUpGuestCheckout(false);
    }
    @Test
    public void BL13_CheckoutWithBuyLinkHasDeletedProduct() throws Exception {
        testCaseId = "BL13";
        new APIPreferences(loginInformation).setUpGuestCheckout(false);
        int productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false,10).getProductID();
        productIds.add(productId); // add to list to clear data
        productName = new String[]{new APICreateProduct(loginInformation).getProductName()};
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        new APIEditProduct(loginInformation).deleteProduct(productId);

        loginSF = new web.StoreFront.login.LoginPage(driver);
        loginSF.navigate(shopDomain)
                .performLogin(userNameSF, passWordSF);
        changeLanguageSF();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        quicklyCheckout = new QuicklyCheckout(driver);
        quicklyCheckout.verifyShopCartHeader()
                .verifyNoProductShow();
        deleteNewestBuyLink();
    }
    @Test
    public void BL14_CheckoutWithBuyLinkHasDeletedVariation() throws Exception {
        testCaseId = "BL14";
        int productId = new APICreateProduct(loginInformation).createVariationProduct(false,10,11).getProductID();
        productIds.add(productId); // add to list to clear data
        productName = new String[]{new APICreateProduct(loginInformation).getProductName()};
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        new ProductPage(driver, Domain.valueOf(domain)).navigateToProductAndDeleteAllVariation(productId);
        loginSF = new web.StoreFront.login.LoginPage(driver);
        loginSF.navigate(shopDomain)
                .performLogin(userNameSF, passWordSF);
        changeLanguageSF();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        quicklyCheckout = new QuicklyCheckout(driver);
        quicklyCheckout.verifyShopCartHeader()
                .verifyNoProductShow();
        deleteNewestBuyLink();
    }
    @Test
    public void BL15_CheckTextByLanguageOnSF_InvalidBuyLink() throws Exception {
        testCaseId = "BL15";
        //create discount
        CreatePromotion.apiIsLimitToOne=false;
        CreatePromotion.apiIsLimitToUsage=false;
        CreatePromotion.apiDiscountCodeType=0;
        CreatePromotion.apiIsEnabledReward=false;
        CreatePromotion.apiSegmentConditionType=0;
        CreatePromotion.apiAppliesCondtionType=0;
        CreatePromotion.apiMinimumRequiredType=0;
        CreatePromotion.apiApplicableBranchCondition=0;
        new APIPreferences(loginInformation).setUpGuestCheckout(true);
        int productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false,10).getProductID();
        productIds.add(productId); // add to list to clear data
        new CreatePromotion(loginInformation).createProductDiscountCode(0);
        discountCodeName = CreatePromotion.apiDiscountName;
        int discountId = CreatePromotion.apiDiscountId;
        productName = new String[]{new APICreateProduct(loginInformation).getProductName()};
        String description = new APICreateProduct(loginInformation).getProductDescription();
        //create buy link with discount
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .searchAndSelectPromotion(discountCodeName)
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        //end discount
        new CreatePromotion(loginInformation).endEarlyDiscount(discountId);
        //Check on SF
        String productNameEN = productName[0];
        if(domain.equals(Domain.VN.name())) {
            loginSF = new web.StoreFront.login.LoginPage(driver);
            loginSF.navigate(shopDomain)
                    .performLogin(userNameSF, passWordSF);
            headerSF = new HeaderSF(driver);
            headerSF.clickUserInfoIcon().changeLanguage("VIE").waitTillLoaderDisappear();
            generalSF = new GeneralSF(driver);
            generalSF.navigateToURL(buyLinkURL);
            quicklyCheckout = new QuicklyCheckout(driver);
            quicklyCheckout.checkTextByLanguage("VIE");
            quicklyCheckout.checkProductNames(productName);
            APIEditProduct apiEditProduct = new APIEditProduct(loginInformation);
            productNameEN = productName[0] + " updated en";
            String productDescriptionEN = description + " updated en";
            apiEditProduct.ediTranslation(productId, productDescriptionEN, productNameEN, "ENG");
        }
        //Check ENG
        loginSF = new web.StoreFront.login.LoginPage(driver);
        loginSF.navigate(shopDomain);
        if(domain.equals(Domain.VN.name())) {
            headerSF = new HeaderSF(driver);
            headerSF.clickUserInfoIcon().changeLanguage("ENG").waitTillLoaderDisappear();
        }
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        quicklyCheckout = new QuicklyCheckout(driver);
        quicklyCheckout.checkTextByLanguage("ENG");
        quicklyCheckout.checkProductNames(productNameEN);
        //clear data
        deleteNewestBuyLink();
        new APIPreferences(loginInformation).setUpGuestCheckout(false);
    }
    @Test
    public void BL16_CheckProductNameByLanguage_ValidBuyLink() throws Exception {
        testCaseId = "BL16";
        //create product
        new APIPreferences(loginInformation).setUpGuestCheckout(false);
        int productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false,10).getProductID();
        productIds.add(productId); // add to list to clear data
        productName = new String[]{new APICreateProduct(loginInformation).getProductName()};
        String description = new APICreateProduct(loginInformation).getProductDescription();
        loginSF = new web.StoreFront.login.LoginPage(driver);
        loginSF.navigate(shopDomain)
                .performLogin(userNameSF, passWordSF);
        //create buy link
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        //check on SF
        String productNameEN=productName[0];
        if(domain.equals(Domain.VN.name())) {
            generalSF = new GeneralSF(driver);
            generalSF.navigateToURL(shopDomain);
            headerSF = new HeaderSF(driver);
            headerSF.clickUserInfoIcon().changeLanguage("VIE").waitTillLoaderDisappear();
            generalSF = new GeneralSF(driver);
            generalSF.navigateToURL(buyLinkURL);
//            loginSF = new GeneralSF(driver).clickOnLoginButtonOnRequiredLoginModal();
//            loginSF.inputEmailOrPhoneNumber(userNameSF).inputPassword(passWordSF).clickLoginBtn();
            new Checkout(driver)
                    .verifyProductName(productName)
                    .clickOnCompleteBtn()
                    .verifyProductNames(productName)
                    .clickOnBackToMarket();
            APIEditProduct apiEditProduct = new APIEditProduct(loginInformation);
            productNameEN = productName[0] + " updated en";
            String productDescriptionEN = description + " updated en";
            apiEditProduct.ediTranslation(productId, productDescriptionEN, productNameEN, "ENG");
        }
        //Check ENG
        loginSF = new web.StoreFront.login.LoginPage(driver);
        loginSF.navigate(shopDomain);
        if(domain.equals(Domain.VN.name())) {
            headerSF = new HeaderSF(driver);
            headerSF.clickUserInfoIcon().changeLanguage("ENG").waitTillLoaderDisappear();
        }
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        new Checkout(driver, Domain.valueOf(domain))
                .verifyProductName(productNameEN)
                .clickOnCompleteBtn()
                .verifyProductNames(productNameEN);
        new APIPreferences(loginInformation).setUpGuestCheckout(false);
    }
    @Test
    public void BL17_CheckCopyLink() throws Exception {
        testCaseId = "BL17";
        String buyLinkExpected = LoginAndNavigateToBuyLinkPage()
                .clickExploreNow()
                .getNewestBuyLinkURL();
        new BuyLinkManagement(driver).verifyCopiedLink(buyLinkExpected).verifyCopiedMessage();
    }
    @Test
    public void BL18_EditBuyLink() throws Exception {
        testCaseId = "BL18";
        //create discount
        CreatePromotion.apiIsLimitToOne=false;
        CreatePromotion.apiIsLimitToUsage=false;
        CreatePromotion.apiDiscountCodeType=0;
        CreatePromotion.apiIsEnabledReward=false;
        CreatePromotion.apiSegmentConditionType=0;
        CreatePromotion.apiAppliesCondtionType=0;
        CreatePromotion.apiMinimumRequiredType=0;
        CreatePromotion.apiApplicableBranchCondition=0;
        new APIPreferences(loginInformation).setUpGuestCheckout(false);
        int productId = new APICreateProduct(loginInformation).createWithoutVariationProduct(false,10).getProductID();
        productIds.add(productId); // add to list to clear data
        new CreatePromotion(loginInformation).createProductDiscountCode(0);
        discountCodeName = CreatePromotion.apiDiscountName;
        productName = new String[]{new APICreateProduct(loginInformation).getProductName()};
        productPrice = new APICreateProduct(loginInformation).getProductSellingPrice().get(0);
        double percent =  CreatePromotion.apiCouponValue;
        if(domain.equals(Domain.VN.name())) {
            productPriceAfterDiscount = Math.ceil(productPrice - percent * productPrice / 100);
            discountAmount = productPrice - productPriceAfterDiscount;
        }
        else{
            productPriceAfterDiscount = productPrice - (productPrice * percent / 100);
            System.out.println("productPriceAfterDiscount: "+productPriceAfterDiscount);
            discountAmount =  Math.floor((productPrice - productPriceAfterDiscount) * 100) / 100;
        }
        //edit buy link with discount
        buyLinkManagement =  LoginAndNavigateToBuyLinkPage()
                .clickExploreNow()
                .clickEditNewestBuyLink()
                .deleteAllSelectedProduct()
                .searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .searchAndSelectPromotion(discountCodeName)
                .clickOnFinishBTN()
                .verifyUpdateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        //check on SF
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        changeLanguageSF();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        loginSF = new GeneralSF(driver).clickOnLoginButtonOnRequiredLoginModal();
        loginSF.inputEmailOrPhoneNumber(userNameSF).inputPassword(passWordSF).clickLoginBtn();
        new Checkout(driver, Domain.valueOf(domain))
                .verifyProductName(productName)
                .verifyDicountAmount(discountAmount)
                .clickOnCompleteBtn()
                .verifyProductNames(productName)
                .verifyDiscountAmount(discountAmount);
    }
    @Test
    public void BL19_CheckDeleteBuyLink(){
        testCaseId = "BL19";
        buyLinkManagement =  LoginAndNavigateToBuyLinkPage()
                .clickExploreNow();
        String buyLinkDelete = buyLinkManagement.getNewestBuyLinkURL();
        buyLinkManagement.clickDeleteNewestBuyLink()
                .clickDeleteBtnOnModal()
                .verifyAfterDeleteBuyLink(buyLinkDelete);
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        headerSF = new HeaderSF(driver);
        headerSF.verifyShopLogoDisplay();
    }
}
