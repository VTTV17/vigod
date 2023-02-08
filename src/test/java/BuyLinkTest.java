import api.dashboard.login.Login;
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
import pages.storefront.GeneralSF;
import pages.storefront.checkout.checkoutstep1.CheckOutStep1;
import pages.storefront.checkout.checkoutstep2.CheckOutStep2;
import pages.storefront.checkout.checkoutstep3.CheckOutStep3;
import utilities.Constant;
import utilities.PropertiesUtil;
import utilities.account.AccountTest;
import utilities.links.Links;

import static utilities.file.FileNameAndPath.FILE_CREATE_SERVICE_TCS;
import static utilities.links.Links.URI;

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
    pages.storefront.login.LoginPage loginSF;
    CheckOutStep1 checkOutStep1;
    CheckOutStep2 checkOutStep2;
    CheckOutStep3 checkOutStep3;
    String discountCodeName;
    int productPrice;
    float discountAmount;

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
    public void BL03_CheckCreateBuyLinkWithoutAndCheckout() throws Exception {
        testCaseId = "BL03";
        new CreateProduct().createWithoutVariationProduct(false,1);
        productName = new String[]{CreateProduct.apiProductName};
        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .clickOnFinishBTN();
        buyLinkManagement.NavigateToBuyLink();
        generalSF = new GeneralSF(driver);
        loginSF = generalSF.clickOnLoginButtonOnRequiredLoginModal();
        loginSF.inputEmailOrPhoneNumber(userNameSF).inputPassword(passWordSF).clickLoginBtn();
        checkOutStep1 = new CheckOutStep1(driver);
        checkOutStep1.selectPaymentMethod("COD")
                .clickOnNextButton()
                .selectShippingMethod("Self delivery")
                .clickOnNextButton()
                .clickOnNextButton()
                .verifyProductNames(productName)
                .verifyDiscountAmount("0đ");
    }
    @Test
    public void BL04_CheckBuyLinkFixAmountDiscountCodeAndCheckout() throws Exception {
//        testCaseId = "BL04";
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
                .clickOnFinishBTN();
        buyLinkManagement.NavigateToBuyLink();
        generalSF = new GeneralSF(driver);
        loginSF = generalSF.clickOnLoginButtonOnRequiredLoginModal();
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
        discountAmount = CreatePromotion.apiCouponValue*productPrice/100;

        createBuyLink = LoginAndNavigateToCreateBuyLinkPage();
        buyLinkManagement = createBuyLink.searchAndSelectProduct(productName)
                .clickOnNextBtn()
                .searchAndSelectPromotion(discountCodeName)
                .clickOnFinishBTN();
        buyLinkManagement.NavigateToBuyLink();
        generalSF = new GeneralSF(driver);
        loginSF = generalSF.clickOnLoginButtonOnRequiredLoginModal();
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
}
