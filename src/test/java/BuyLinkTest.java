import api.dashboard.customers.Customers;
import api.dashboard.login.Login;
import api.dashboard.marketing.APIBuyLink;
import api.dashboard.onlineshop.APIPreferences;
import api.dashboard.products.APIEditProduct;
import api.dashboard.products.CreateProduct;
import api.dashboard.promotion.CreatePromotion;
import api.dashboard.setting.BranchManagement;
import api.dashboard.setting.VAT;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.dashboard.home.HomePage;
import pages.dashboard.login.LoginPage;
import pages.dashboard.marketing.buylink.BuyLinkManagement;
import pages.dashboard.marketing.buylink.CreateBuyLink;
import pages.dashboard.products.all_products.ProductPage;
import pages.storefront.GeneralSF;
import pages.storefront.checkout.checkoutstep1.CheckOutStep1;
import pages.storefront.checkout.checkoutstep2.CheckOutStep2;
import pages.storefront.checkout.checkoutstep3.CheckOutStep3;
import pages.storefront.header.HeaderSF;
import pages.storefront.quicklycheckout.QuicklyCheckout;
import pages.storefront.signup.SignupPage;
import utilities.Constant;
import utilities.PropertiesUtil;
import utilities.account.AccountTest;
import static utilities.file.FileNameAndPath.FILE_BUY_LINK_TCS;
import static utilities.links.Links.SF_ShopVi;

public class BuyLinkTest extends BaseTest {
    String userNameDb;
    String passWordDb;
    String userNameSF;
    String passWordSF;
    String languageDB;
    String languageSF;
    LoginPage login;
    HomePage home;
    BuyLinkManagement buyLinkManagement;
    CreateBuyLink createBuyLink;
    String[] productName;
    GeneralSF generalSF;
    HeaderSF headerSF;
    pages.storefront.login.LoginPage loginSF;
    CheckOutStep1 checkOutStep1;
    CheckOutStep2 checkOutStep2;
    CheckOutStep3 checkOutStep3;
    String discountCodeName;
    double productPrice;
    double discountAmount;
    SignupPage signupSF;
    String shopDomain;
    QuicklyCheckout quicklyCheckout;
    @BeforeClass
    public void beforeClass() throws Exception {
        userNameDb = AccountTest.ADMIN_SHOP_VI_USERNAME;
        passWordDb = AccountTest.ADMIN_SHOP_VI_PASSWORD;
        userNameSF = "qcgosell01@gmail.com";
        passWordSF = AccountTest.SF_SHOP_VI_PASSWORD;
        languageDB = PropertiesUtil.getLanguageFromConfig("Dashboard");
        languageSF = PropertiesUtil.getLanguageFromConfig("Storefront");
        new Login().loginToDashboardWithPhone("+84",AccountTest.ADMIN_SHOP_VI_USERNAME,AccountTest.ADMIN_SHOP_VI_PASSWORD);
        new BranchManagement().getBranchInformation();
        new VAT().getTaxList();
        shopDomain = SF_ShopVi;
        tcsFileName = FILE_BUY_LINK_TCS;
    }
    public void deleteNewestBuyLink(){
        Login login = new Login();
        login.loginToDashboardWithPhone("+84",userNameDb,passWordDb);
        APIBuyLink apiBuyLink = new APIBuyLink();
        int id = apiBuyLink.getNewestBuyLinkID();
        apiBuyLink.deleteBuyLinkById(id);
    }
    public BuyLinkManagement LoginAndNavigateToBuyLinkPage() {
        login = new LoginPage(driver);
        login.navigate().performLogin(userNameDb, passWordDb);
        home = new HomePage(driver);
        home.waitTillSpinnerDisappear().selectLanguage(languageDB).hideFacebookBubble().navigateToPage(Constant.MARKETING_MENU_ITEM_NAME, Constant.BUYLINK_MENU_ITEM_NAME);
        return new BuyLinkManagement(driver);
    }
    public CreateBuyLink LoginAndNavigateToCreateBuyLinkPage() {
        login = new LoginPage(driver);
        login.navigate().performLogin(userNameDb, passWordDb);
        home = new HomePage(driver);
        home.waitTillSpinnerDisappear().selectLanguage(languageDB).hideFacebookBubble().navigateToPage(Constant.MARKETING_MENU_ITEM_NAME, Constant.BUYLINK_MENU_ITEM_NAME);
        buyLinkManagement = new BuyLinkManagement(driver);
        buyLinkManagement.clickExploreNow();
        return buyLinkManagement.clickCreateBuyLink();
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
        new CreateProduct().createWithoutVariationProduct(false,1);
        productName = new String[]{CreateProduct.apiProductName};
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF).waitTillLoaderDisappear();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        loginSF = new GeneralSF(driver).clickOnLoginButtonOnRequiredLoginModal();
        loginSF.inputEmailOrPhoneNumber(userNameSF).inputPassword(passWordSF).clickLoginBtn();
        checkOutStep1 = new CheckOutStep1(driver);
        checkOutStep1.selectPaymentMethod("COD")
                .clickOnNextButton()
                .selectShippingMethod("Self delivery")
                .clickOnNextButton()
                .clickOnNextButton()
                .verifyProductNames(productName)
                .verifyDiscountAmount("0đ");
        deleteNewestBuyLink();
        new APIEditProduct().deleteProduct(Login.accessToken,CreateProduct.apiProductID);
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
        new CreateProduct().createWithoutVariationProduct(false,10);
        new CreatePromotion().createProductDiscountCode(0);
        productName = new String[]{CreateProduct.apiProductName};
        discountCodeName = CreatePromotion.apiDiscountName;
        productPrice = CreateProduct.apiProductSellingPrice.get(0);
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
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF).waitTillLoaderDisappear();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        loginSF = new GeneralSF(driver).clickOnLoginButtonOnRequiredLoginModal();
        loginSF.inputEmailOrPhoneNumber(userNameSF).inputPassword(passWordSF).clickLoginBtn();
        checkOutStep1 = new CheckOutStep1(driver);
        checkOutStep1.selectPaymentMethod("COD")
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .clickOnNextButton()
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .selectShippingMethod("Self delivery")
                .clickOnNextButton()
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .clickOnNextButton()
                .verifyProductNames(productName)
                .verifyDiscountAmount(String.format("%.0f",discountAmount)+"đ");
        deleteNewestBuyLink();
        new APIEditProduct().deleteProduct(Login.accessToken,CreateProduct.apiProductID);
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
        new CreateProduct().createWithoutVariationProduct(false,10);
        new CreatePromotion().createProductDiscountCode(0);
        productName = new String[]{CreateProduct.apiProductName};
        discountCodeName = CreatePromotion.apiDiscountName;
        productPrice = CreateProduct.apiProductSellingPrice.get(0);
        double percent =  CreatePromotion.apiCouponValue;
        discountAmount = percent*productPrice/100;
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .searchAndSelectPromotion(discountCodeName)
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF).waitTillLoaderDisappear();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        loginSF = new GeneralSF(driver).clickOnLoginButtonOnRequiredLoginModal();
        loginSF.inputEmailOrPhoneNumber(userNameSF).inputPassword(passWordSF).clickLoginBtn();
        checkOutStep1 = new CheckOutStep1(driver);
        checkOutStep1.selectPaymentMethod("COD")
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .clickOnNextButton()
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .selectShippingMethod("Self delivery")
                .clickOnNextButton()
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .clickOnNextButton()
                .verifyProductNames(productName)
                .verifyDiscountAmount(String.format("%.0f",discountAmount)+"đ");
        deleteNewestBuyLink();
        new APIEditProduct().deleteProduct(Login.accessToken,CreateProduct.apiProductID);
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
        new CreateProduct().createWithoutVariationProduct(false,10);
        new CreatePromotion().createProductDiscountCode(0);
        productName = new String[]{CreateProduct.apiProductName};
        discountCodeName = CreatePromotion.apiDiscountName;
        productPrice = CreateProduct.apiProductSellingPrice.get(0);

        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .searchAndSelectPromotion(discountCodeName)
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF).waitTillLoaderDisappear();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        loginSF = new GeneralSF(driver).clickOnLoginButtonOnRequiredLoginModal();
        loginSF.inputEmailOrPhoneNumber(userNameSF).inputPassword(passWordSF).clickLoginBtn();
        checkOutStep1 = new CheckOutStep1(driver);
        checkOutStep1.selectPaymentMethod("COD")
                .clickOnNextButton()
                .verifyShippingFeeAfterDiscount("0đ")
                .selectShippingMethod("Self delivery")
                .clickOnNextButton()
                .verifyShippingFreeAfterDiscount("0đ")
                .clickOnNextButton()
                .verifyProductNames(productName)
                .verifyShippingFeeAfterDiscount("0đ");
        deleteNewestBuyLink();
        new APIEditProduct().deleteProduct(Login.accessToken,CreateProduct.apiProductID);
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
        new CreateProduct().createWithoutVariationProduct(false, 10);
        new CreatePromotion().createProductDiscountCode(0);
        productName = new String[]{CreateProduct.apiProductName};
        discountCodeName = CreatePromotion.apiDiscountName;
        productPrice = CreateProduct.apiProductSellingPrice.get(0);
        double percent = CreatePromotion.apiCouponValue;
        discountAmount = productPrice*percent/100;

        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .searchAndSelectPromotion(discountCodeName)
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF).waitTillLoaderDisappear();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        loginSF = new GeneralSF(driver).clickOnLoginButtonOnRequiredLoginModal();
        loginSF.inputEmailOrPhoneNumber(userNameSF).inputPassword(passWordSF).clickLoginBtn();
        checkOutStep1 = new CheckOutStep1(driver);
        checkOutStep1.selectPaymentMethod("COD")
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .clickOnNextButton()
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .selectShippingMethod("Self delivery")
                .clickOnNextButton()
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .clickOnNextButton()
                .verifyProductNames(productName)
                .verifyDiscountAmount(String.format("%.0f",discountAmount)+"đ");
        deleteNewestBuyLink();
        new APIEditProduct().deleteProduct(Login.accessToken,CreateProduct.apiProductID);
    }
    @Test
    public void BL08_CheckNoAccountAndCheckoutWithBuyLink() throws Exception {
        testCaseId = "BL08";
        new CreateProduct().createWithoutVariationProduct(false,1);
        productName = new String[]{CreateProduct.apiProductName};
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF).waitTillLoaderDisappear();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        loginSF = new GeneralSF(driver).clickOnRegisterButtonOnRequiredLoginModal();
        signupSF = new SignupPage(driver);
        String generateName = generate.generateString(10);
        String buyerAccount_Signup = "01" + generate.generateNumber(9);
        String buyerDisplayName_Signup = generateName;
        signupSF.signUpWithPhoneNumber("Vietnam", buyerAccount_Signup, passWordSF, buyerDisplayName_Signup, "");
        checkOutStep1 = new CheckOutStep1(driver);
        checkOutStep1.selectPaymentMethod("COD")
                .inputAddressInfo_VN("", "address1", "An Giang", "Huyện Tri Tôn", "An Tức")
                .clickOnNextButton()
                .selectShippingMethod("Self delivery")
                .clickOnNextButton()
                .clickOnNextButton()
                .verifyProductNames(productName)
                .verifyDiscountAmount("0đ");
        deleteNewestBuyLink();
        new APIEditProduct().deleteProduct(Login.accessToken,CreateProduct.apiProductID);
    }
    @Test
    public void BL09_CheckNavigateToBuyLink_EnableGuestCheckout() throws Exception {
        testCaseId = "BL09";
        new APIPreferences().setUpGuestCheckout(Login.accessToken,Login.apiStoreID,true);
        new CreateProduct().createWithoutVariationProduct(false,1);
        productName = new String[]{CreateProduct.apiProductName};
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF);
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        checkOutStep1 = new CheckOutStep1(driver);
        String phoneNumber = "01"+generate.generateNumber(7);
        checkOutStep1.selectPaymentMethod("COD")
                .inputPhoneNumber(phoneNumber)
                .inputAddressInfo_VN("", "address1", "An Giang", "Huyện Tri Tôn", "An Tức")
                .clickOnNextButton()
                .selectShippingMethod("Self delivery")
                .clickOnNextButton()
                .clickOnNextButton()
                .verifyProductNames(productName)
                .verifyDiscountAmount("0đ");
        new APIPreferences().setUpGuestCheckout(Login.accessToken,Login.apiStoreID,false);
        deleteNewestBuyLink();
        new APIEditProduct().deleteProduct(Login.accessToken,CreateProduct.apiProductID);

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
        new APIPreferences().setUpGuestCheckout(Login.accessToken,Login.apiStoreID,true);
        new CreateProduct().createWithoutVariationProduct(false,10);
        new CreatePromotion().createProductDiscountCode(0);
        productName = new String[]{CreateProduct.apiProductName};
        discountCodeName = CreatePromotion.apiDiscountName;
        productPrice = CreateProduct.apiProductSellingPrice.get(0);
        discountAmount = CreatePromotion.apiCouponValue;
        int discountId = CreatePromotion.apiDiscountId;
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .searchAndSelectPromotion(discountCodeName)
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        new CreatePromotion().endEarlyDiscount(Login.accessToken,discountId,Login.apiStoreID);
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF).waitTillLoaderDisappear();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        quicklyCheckout = new QuicklyCheckout(driver);
        quicklyCheckout.verifyShopCartHeader()
                .verifyDiscountInvalidError();
        deleteNewestBuyLink();
        new APIEditProduct().deleteProduct(Login.accessToken,CreateProduct.apiProductID);
        new APIPreferences().setUpGuestCheckout(Login.accessToken,Login.apiStoreID,false);

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
        new APIPreferences().setUpGuestCheckout(Login.accessToken,Login.apiStoreID,false);
        new CreateProduct().createWithoutVariationProduct(false,10);
        new CreatePromotion().createProductDiscountCode(0);
        productName = new String[]{CreateProduct.apiProductName};
        discountCodeName = CreatePromotion.apiDiscountName;
        productPrice = CreateProduct.apiProductSellingPrice.get(0);
        double percent =  CreatePromotion.apiCouponValue;
        discountAmount = percent*productPrice/100;
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .searchAndSelectPromotion(discountCodeName)
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(shopDomain)
                .performLogin(userNameSF, passWordSF);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF).waitTillLoaderDisappear();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        checkOutStep1 = new CheckOutStep1(driver);
        checkOutStep1.selectPaymentMethod("COD")
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .clickOnNextButton()
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .selectShippingMethod("Self delivery")
                .clickOnNextButton()
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .clickOnNextButton()
                .verifyProductNames(productName)
                .verifyDiscountAmount(String.format("%.0f",discountAmount)+"đ");
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        quicklyCheckout = new QuicklyCheckout(driver);
        quicklyCheckout.verifyShopCartHeader()
                .verifyMaximumAllowUsageError();
        deleteNewestBuyLink();
        new APIEditProduct().deleteProduct(Login.accessToken,CreateProduct.apiProductID);
        new APIPreferences().setUpGuestCheckout(Login.accessToken,Login.apiStoreID,false);
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
        new APIPreferences().setUpGuestCheckout(Login.accessToken,Login.apiStoreID,false);
        new CreateProduct().createWithoutVariationProduct(false,10);
        new CreatePromotion().createProductDiscountCode(0);
        productName = new String[]{CreateProduct.apiProductName};
        discountCodeName = CreatePromotion.apiDiscountName;
        productPrice = CreateProduct.apiProductSellingPrice.get(0);
        double percent =  CreatePromotion.apiCouponValue;
        discountAmount = percent*productPrice/100;
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .searchAndSelectPromotion(discountCodeName)
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(shopDomain)
                .performLogin(userNameSF, passWordSF);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF).waitTillLoaderDisappear();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        checkOutStep1 = new CheckOutStep1(driver);
        checkOutStep1.selectPaymentMethod("COD")
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .clickOnNextButton()
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .selectShippingMethod("Self delivery")
                .clickOnNextButton()
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .clickOnNextButton()
                .verifyProductNames(productName)
                .verifyDiscountAmount(String.format("%.0f",discountAmount)+"đ");
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        quicklyCheckout = new QuicklyCheckout(driver);
        quicklyCheckout.verifyShopCartHeader()
                .verifyMaximumAllowUsagePerUserError();
        deleteNewestBuyLink();
        new APIEditProduct().deleteProduct(Login.accessToken,CreateProduct.apiProductID);
        new APIPreferences().setUpGuestCheckout(Login.accessToken,Login.apiStoreID,false);
    }
    @Test
    public void BL13_CheckoutWithBuyLinkHasDeletedProduct() throws Exception {
        testCaseId = "BL13";
        new APIPreferences().setUpGuestCheckout(Login.accessToken,Login.apiStoreID,false);
        new CreateProduct().createWithoutVariationProduct(false,10);
        productName = new String[]{CreateProduct.apiProductName};
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        new APIEditProduct().deleteProduct(Login.accessToken,CreateProduct.apiProductID);
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(shopDomain)
                .performLogin(userNameSF, passWordSF);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF).waitTillLoaderDisappear();
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
        new CreateProduct().createVariationProduct(false,10,11);
        productName = new String[]{CreateProduct.apiProductName};
        int productId = CreateProduct.apiProductID;
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        new ProductPage(driver).navigateToProductAndDeleteAllVariation(productId);
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(shopDomain)
                .performLogin(userNameSF, passWordSF);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF).waitTillLoaderDisappear();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        quicklyCheckout = new QuicklyCheckout(driver);
        quicklyCheckout.verifyShopCartHeader()
                .verifyNoProductShow();
        deleteNewestBuyLink();
        new APIEditProduct().deleteProduct(Login.accessToken,CreateProduct.apiProductID);
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
        new APIPreferences().setUpGuestCheckout(Login.accessToken,Login.apiStoreID,true);
        new CreateProduct().createWithoutVariationProduct(false,10);
        new CreatePromotion().createProductDiscountCode(0);
        discountCodeName = CreatePromotion.apiDiscountName;
        int discountId = CreatePromotion.apiDiscountId;
        productName = new String[]{CreateProduct.apiProductName};
        int productId = CreateProduct.apiProductID;
        String description = CreateProduct.apiProductDescription;
        //create buy link with discount
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .searchAndSelectPromotion(discountCodeName)
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        //end discount
        new CreatePromotion().endEarlyDiscount(Login.accessToken,discountId,Login.apiStoreID);
        //Check on SF
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(shopDomain)
                .performLogin(userNameSF, passWordSF);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage("VIE").waitTillLoaderDisappear();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        quicklyCheckout = new QuicklyCheckout(driver);
        quicklyCheckout.checkTextByLanguage("VIE");
        quicklyCheckout.checkProductNames(productName);
        APIEditProduct apiEditProduct = new APIEditProduct();
        String productNameEN = productName[0]+" updated en";
        String productDescriptionEN = description+" updated en";
        apiEditProduct.ediTranslation(productId,productDescriptionEN,productNameEN,"ENG");
        //Check ENG
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(shopDomain);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage("ENG").waitTillLoaderDisappear();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        quicklyCheckout = new QuicklyCheckout(driver);
        quicklyCheckout.checkTextByLanguage("ENG");
        quicklyCheckout.checkProductNames(productNameEN);
        deleteNewestBuyLink();
        new APIEditProduct().deleteProduct(Login.accessToken,CreateProduct.apiProductID);
        new APIPreferences().setUpGuestCheckout(Login.accessToken,Login.apiStoreID,false);
    }
    @Test
    public void BL16_CheckProductNameByLanguage_ValidBuyLink() throws Exception {
        testCaseId = "BL16";
        //create product
        new APIPreferences().setUpGuestCheckout(Login.accessToken,Login.apiStoreID,false);
        new CreateProduct().createWithoutVariationProduct(false,10);
        productName = new String[]{CreateProduct.apiProductName};
        String description = CreateProduct.apiProductDescription;
        int productId = CreateProduct.apiProductID;
        //create buy link
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .clickOnFinishBTN()
                .verifyCreateBuyLinkSuccessfulMessage();
        String buyLinkURL = buyLinkManagement.getNewestBuyLinkURL();
        //check on SF
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(shopDomain);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage("VIE").waitTillLoaderDisappear();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        loginSF = new GeneralSF(driver).clickOnLoginButtonOnRequiredLoginModal();
        loginSF.inputEmailOrPhoneNumber(userNameSF).inputPassword(passWordSF).clickLoginBtn();
        checkOutStep1 = new CheckOutStep1(driver);
        checkOutStep1.selectPaymentMethod("COD")
                .clickOnNextButton()
                .verifyProductName(productName)
                .clickOnNextButton()
                .verifyProductName(productName)
                .clickOnNextButton()
                .verifyProductNames(productName)
                .clickOnBackToMarket();
        APIEditProduct apiEditProduct = new APIEditProduct();
        String productNameEN = productName[0]+" updated en";
        String productDescriptionEN = description+" updated en";
        apiEditProduct.ediTranslation(productId,productDescriptionEN,productNameEN,"ENG");
        loginSF = new pages.storefront.login.LoginPage(driver);
        loginSF.navigate(shopDomain);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage("ENG").waitTillLoaderDisappear();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        checkOutStep1 = new CheckOutStep1(driver);
        checkOutStep1.selectPaymentMethod("COD")
                .clickOnNextButton()
                .verifyProductName(productNameEN)
                .clickOnNextButton()
                .verifyProductName(productNameEN)
                .clickOnNextButton()
                .verifyProductNames(productNameEN);
        new APIPreferences().setUpGuestCheckout(Login.accessToken,Login.apiStoreID,false);
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
        new APIPreferences().setUpGuestCheckout(Login.accessToken,Login.apiStoreID,false);
        new CreateProduct().createWithoutVariationProduct(false,10);
        new CreatePromotion().createProductDiscountCode(0);
        discountCodeName = CreatePromotion.apiDiscountName;
        productName = new String[]{CreateProduct.apiProductName};
        productPrice = CreateProduct.apiProductSellingPrice.get(0);
        double percent =  CreatePromotion.apiCouponValue;
        discountAmount = percent*productPrice/100;
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
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF).waitTillLoaderDisappear();
        generalSF = new GeneralSF(driver);
        generalSF.navigateToURL(buyLinkURL);
        loginSF = new GeneralSF(driver).clickOnLoginButtonOnRequiredLoginModal();
        loginSF.inputEmailOrPhoneNumber(userNameSF).inputPassword(passWordSF).clickLoginBtn();
        checkOutStep1 = new CheckOutStep1(driver);
        checkOutStep1.selectPaymentMethod("COD")
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .clickOnNextButton()
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .selectShippingMethod("Self delivery")
                .clickOnNextButton()
                .clickOnArrowIcon().verifyDicountAmount(String.format("%.0f",discountAmount)+"đ")
                .clickOnNextButton()
                .verifyProductNames(productName)
                .verifyDiscountAmount(String.format("%.0f",discountAmount)+"đ");
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
