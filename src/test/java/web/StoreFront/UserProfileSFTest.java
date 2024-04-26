package web.StoreFront;

import api.Seller.login.Login;
import api.Seller.products.all_products.CreateProduct;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utilities.model.dashboard.storefront.AddressInfo;
import web.Dashboard.customers.allcustomers.AllCustomers;
import web.Dashboard.home.HomePage;
import web.StoreFront.checkout.checkoutOneStep.Checkout;
import web.StoreFront.detail_product.ProductDetailPage;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.login.LoginPage;
import web.StoreFront.signup.SignupPage;
import web.StoreFront.userprofile.MyAccount.MyAccount;
import web.StoreFront.userprofile.MyAddress;
import web.StoreFront.userprofile.userprofileinfo.UserProfileInfo;
import utilities.constant.Constant;
import utilities.utils.PropertiesUtil;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.file.FileNameAndPath;
import utilities.model.sellerApp.login.LoginInformation;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import static utilities.account.AccountTest.*;
import static utilities.character_limit.CharacterLimit.MAX_CHAR_ADDRESS;
import static utilities.character_limit.CharacterLimit.MAX_PRICE;
import static utilities.links.Links.*;

public class UserProfileSFTest extends BaseTest {
    LoginPage loginSF;
    UserProfileInfo userProfileInfo;
    HeaderSF headerSF;
    MyAccount myAccount;
    SignupPage signupSF;
    ProductDetailPage productDetailSF;
    web.Dashboard.login.LoginPage loginDb;
    AllCustomers allCustomers;
    MyAddress myAddress;
    HomePage homePage;
    String userName;
    String passWordDashboard;
    String passWordSF;
    String passWordDashboardShopB;
    String displayName;
    String membershipLevel;
    String barcodeNumber;
    String userName_EditInfo_HasBirthday;
    String displayName_Edit = "";
    String companyName_Edit;
    String taxCode_Edit;
    String phoneNumber_Edit = "";
    String gender_Edit = "";
    String buyerDisplayName_Signup = "";
    String buyerAccount_Signup = "";
    String shopDomain;
    String shopDomainB;
    String email_Edit = "";
    String birthday_Edit;
    String productIDToBuyNow;
    String productIDToBuyNowShopB;
    String userName_NonVN;
    String phoneNumber = "";
    String userName_ChangeAddress;
    String address_Edit_Invalid;
    String userNameDb_ShopVi;
    String userNameDb_ShopB;
    String userName_UpdateAddress;
    String fullName_UpdateAddress;
    String languageDb;
    String languageSF;
    String userName_PhoneAccount_EditInfo_HasBirthday;
    LoginInformation loginInformation;

    @BeforeClass
    public void getData() {
        languageDb = language;
        languageSF = language;
        userNameDb_ShopVi = ADMIN_SHOP_VI_USERNAME;
        userNameDb_ShopB = ADMIN_SHOP_COFFEE_USERNAME;
        userName_ChangeAddress = SF_USERNAME_VI_2;
        userName_UpdateAddress = SF_USERNAME_VI_3;
        userName_EditInfo_HasBirthday = SF_USERNAME_VI_4;
        userName_PhoneAccount_EditInfo_HasBirthday = SF_USERNAME_PHONE_VI_1;
        userName = SF_USERNAME_VI_1;
        userName_NonVN = SF_USERNAME_VI_5;
        passWordDashboardShopB = ADMIN_SHOP_COFFEE_PASSWORD;
        passWordDashboard = ADMIN_SHOP_VI_PASSWORD;
        passWordSF = SF_SHOP_VI_PASSWORD;
        shopDomain = SF_ShopVi;
        shopDomainB = SF_COFFEE;
        fullName_UpdateAddress = PropertiesUtil.getEnvironmentData("buyerName3");
        MAX_PRICE = 999999L;
        loginInformation = new Login().setLoginInformation("+84",userNameDb_ShopVi,passWordDashboard).getLoginInformation();
        productIDToBuyNow = String.valueOf(new CreateProduct(loginInformation).createWithoutVariationProduct(false,30).getProductID());
        loginInformation = new Login().setLoginInformation(userNameDb_ShopB,passWordDashboardShopB).getLoginInformation();
        productIDToBuyNowShopB = String.valueOf(new CreateProduct(loginInformation).createWithoutVariationProduct(false,30).getProductID());
        displayName = PropertiesUtil.getEnvironmentData("buyerName1");
        membershipLevel = PropertiesUtil.getEnvironmentData("membershipLevel");
        barcodeNumber = PropertiesUtil.getEnvironmentData("barcodeBuyer1");
        address_Edit_Invalid = Constant.TEXT_256_CHAR;
        companyName_Edit = "GoSell";
        taxCode_Edit = "1058896666";
        tcsFileName = FileNameAndPath.FILE_USER_PROFILE_TCS;
        birthday_Edit = "20/10/1999";
        generate = new DataGenerator();
    }
    @BeforeMethod
    public void setUp(){
        driver = new InitWebdriver().getDriver(browser,"false");
        homePage = new HomePage(driver);
    }
    @AfterMethod
    public void writeResult(ITestResult result) throws IOException {
        super.writeResult(result);
        if (driver != null) driver.quit();
    }

    public UserProfileInfo loginAndGoToUserProfile(String userName) {
        loginSF = new LoginPage(driver);
        loginSF.navigate(shopDomain)
                .performLogin(userName, passWordSF);
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF).waitTillLoaderDisappear();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile();
        return new UserProfileInfo(driver);
    }

    public UserProfileInfo goToUserProfile() {
        loginSF = new LoginPage(driver);
        loginSF.navigate(shopDomain);
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile();
        return new UserProfileInfo(driver);
    }

    public void CheckUserHasAddressBefore_ExistedAccount_VietNam() throws Exception {
        myAddress = loginAndGoToUserProfile(userName)
                .clickMyAddressSection();
        String countryExpected = myAddress.getCountry();
        String addressExpected = myAddress.getAddress();
        String cityExpected = myAddress.getCity();
        String districtExpected = myAddress.getDistrict();
        String wardExpected = myAddress.getWard();
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .goToEditMyAddress()
                .verifyAddressInfo_VN("", addressExpected, cityExpected, districtExpected, wardExpected)
                .inputAddressInfo_VN();
        new Checkout(driver).completeEditAddress()
                .clickOnCompleteBtn()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_VN("", addressExpected, cityExpected, districtExpected, wardExpected)
                .clickUserInfoIcon().clickLogout();
    }

    public void CheckUserHasAddressBefore_ExistedAccount_NonVietNam() throws Exception {
        myAddress = loginAndGoToUserProfile(userName_NonVN)
                .clickMyAddressSection();
        String countryExpected = myAddress.getCountry();
        String addressExpected = myAddress.getAddress();
        String address2Expected = myAddress.getAddress2();
        String cityExpected = myAddress.getInputtedCity();
        String stateExpected = myAddress.getState();
        String zipCodeExpected = myAddress.getZipCode();
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .goToEditMyAddress()
                .verifyAddressInfo_NonVN(countryExpected, addressExpected, address2Expected, stateExpected, cityExpected, zipCodeExpected)
                .inputAddressInfo_NonVN();
         new Checkout(driver).completeEditAddress()
                .clickOnCompleteBtn()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_NonVN(countryExpected, addressExpected, address2Expected, cityExpected, stateExpected, zipCodeExpected)
                .clickUserInfoIcon().clickLogout();
    }

    public void CheckUserHasAddressBefore_NewAccount() throws Exception {
        String generateName = generate.generateString(10);
        buyerAccount_Signup = generateName + "@mailnesia.com";
        buyerDisplayName_Signup = generateName;
        phoneNumber = "01" + generate.generateNumber(9);
        signupSF = new SignupPage(driver);
        signupSF.navigateToSignUp(shopDomain).signUpWithEmail("Vietnam", buyerAccount_Signup, passWordSF, buyerDisplayName_Signup, "");
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().clickLogout();
        AddressInfo addressInfo = new AddressInfo();
        addressInfo = loginAndGoToUserProfile(buyerAccount_Signup)
                .clickMyAddressSection()
                .inputAddressInfo_VN();
             new MyAddress(driver).clickOnSave()
                .verifyAddressInfo_VN(addressInfo.getCountry(), addressInfo.getAddress(), addressInfo.getCityProvince(), addressInfo.getDistrict(), addressInfo.getWard());
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .goToEditMyAddress()
                .verifyAddressInfo_VN(addressInfo.getCountry(), addressInfo.getAddress(), addressInfo.getCityProvince(), addressInfo.getDistrict(), addressInfo.getWard())
                .inputPhoneNumber(phoneNumber)
                .inputAddressInfo_VN();
              new Checkout(driver).completeEditAddress()
                .clickOnCompleteBtn()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_VN(addressInfo.getCountry(), addressInfo.getAddress(), addressInfo.getCityProvince(), addressInfo.getDistrict(), addressInfo.getWard())
                .clickUserInfoIcon().clickLogout();
    }

    public void CheckUserUpdateAddress_ExistedAccount() {
        //Buyer access SF B to get address
        loginSF = new LoginPage(driver);
        loginSF.navigate(shopDomainB)
                .performLogin(userName_UpdateAddress, passWordSF);
        headerSF = new HeaderSF(driver);
        myAddress = headerSF.navigateToUserProfile().clickMyAddressSection();
        String addressCurrent = myAddress.getAddress();
        String cityCurrent = myAddress.getCity();
        String districtCurrent = myAddress.getDistrict();
        String wardCurrent = myAddress.getWard();
        myAddress.clickUserInfoIcon().clickLogout();
        //Buyer access SF A to update address
        myAddress = loginAndGoToUserProfile(userName_UpdateAddress)
                .clickMyAddressSection();
        //Update valid address in VietNam
        AddressInfo addressInfo = new AddressInfo();
        addressInfo = myAddress.inputAddress(address_Edit_Invalid)
                .verifyAddressDisplayMaximumCharacter(MAX_CHAR_ADDRESS)
                .inputAddressInfo_VN();
          new MyAddress(driver).clickOnSave()
                .verifyAddressInfo_VN(addressInfo.getCountry(), addressInfo.getAddress(), addressInfo.getCityProvince(), addressInfo.getDistrict(), addressInfo.getWard());
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .goToEditMyAddress()
                .verifyAddressInfo_VN(addressInfo.getCountry(), addressInfo.getAddress(), addressInfo.getCityProvince(), addressInfo.getDistrict(), addressInfo.getWard());
        loginDb = new web.Dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopVi, passWordDashboard);
        homePage = new HomePage(driver);
        homePage.waitTillSpinnerDisappear1().selectLanguage(languageDb);
        allCustomers = new AllCustomers(driver);
        allCustomers.navigate().searchAndGoToCustomerDetailByName(fullName_UpdateAddress)
                .verifyAddressInfo_VN(addressInfo.getCountry(), addressInfo.getAddress(), addressInfo.getCityProvince(), addressInfo.getDistrict(), addressInfo.getWard())
                .clickLogout();
        //Update valid address in outside VietNam
        AddressInfo addressInfoNonVN = new AddressInfo();
        addressInfoNonVN = goToUserProfile()
                .clickMyAddressSection()
                .inputAddressInfo_NonVN();
          new MyAddress(driver).clickOnSave()
                .verifyAddressInfo_NonVN(addressInfoNonVN.getCountry(), addressInfoNonVN.getStreetAddress(), addressInfoNonVN.getAddress2(), addressInfoNonVN.getCity(), addressInfoNonVN.getStateRegionProvince(), addressInfoNonVN.getZipCode());
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .goToEditMyAddress()
                .verifyAddressInfo_NonVN(addressInfoNonVN.getCountry(), addressInfoNonVN.getStreetAddress(), addressInfoNonVN.getAddress2(), addressInfoNonVN.getCity(), addressInfoNonVN.getStateRegionProvince(), addressInfoNonVN.getZipCode());
        loginDb = new web.Dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopVi, passWordDashboard);
        allCustomers = new AllCustomers(driver);
        homePage.waitTillSpinnerDisappear1();
        allCustomers.navigate().searchAndGoToCustomerDetailByName(fullName_UpdateAddress)
                .verifyAddressInfo_NonVN(addressInfoNonVN.getCountry(), addressInfoNonVN.getStreetAddress(), addressInfoNonVN.getAddress2(), addressInfoNonVN.getCity(), addressInfoNonVN.getStateRegionProvince(), addressInfoNonVN.getZipCode())
                .clickLogout();
        //Buyer access SF B to verify address
        loginSF = new LoginPage(driver);
        loginSF.navigate(shopDomainB)
                .performLogin(userName_UpdateAddress, passWordSF);
        headerSF = new HeaderSF(driver);
        AddressInfo addressInfoVN = new AddressInfo();
        addressInfoVN = headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_VN("", addressCurrent, cityCurrent, districtCurrent, wardCurrent)
                .inputAddressInfo_VN();
          new MyAddress(driver).clickOnSave()
                .verifyAddressInfo_VN(addressInfoVN.getCountry(), addressInfoVN.getAddress(), addressInfoVN.getCityProvince(), addressInfoVN.getDistrict(), addressInfoVN.getWard());
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomainB, productIDToBuyNowShopB)
                .clickOnBuyNow()
                .clickOnContinue()
                .goToEditMyAddress()
                .verifyAddressInfo_VN(addressInfoVN.getCountry(), addressInfoVN.getAddress(), addressInfoVN.getCityProvince(), addressInfoVN.getDistrict(), addressInfoVN.getWard());
        loginDb = new web.Dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopB, passWordDashboardShopB);
        homePage = new HomePage(driver);
        homePage.waitTillSpinnerDisappear1();
        allCustomers = new AllCustomers(driver);
        allCustomers.navigate().searchAndGoToCustomerDetailByName(fullName_UpdateAddress)
                .verifyAddressInfo_VN(addressInfoVN.getCountry(), addressInfoVN.getAddress(), addressInfoVN.getCityProvince(), addressInfoVN.getDistrict(), addressInfoVN.getWard())
                .clickLogout();
        myAddress = goToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_NonVN(addressInfoNonVN.getCountry(), addressInfoNonVN.getStreetAddress(), addressInfoNonVN.getAddress2(), addressInfoNonVN.getCity(), addressInfoNonVN.getStateRegionProvince(), addressInfoNonVN.getZipCode());
    }

    public void CheckUserUpdateAddress_NewAccount() throws SQLException {
        //SignUp
        String generateName = generate.generateString(10);
        buyerAccount_Signup = generateName + "@mailnesia.com";
        buyerDisplayName_Signup = generateName;
        phoneNumber = "01" + generate.generateNumber(9);
        signupSF = new SignupPage(driver);
        signupSF.navigateToSignUp(shopDomain).signUpWithEmail("Vietnam", buyerAccount_Signup, passWordSF, buyerDisplayName_Signup, "");
        myAddress = goToUserProfile()
                .clickMyAddressSection();
        //Update valid address in VietNam
        AddressInfo addressInfoVN = new AddressInfo();
        addressInfoVN = myAddress.inputAddress(address_Edit_Invalid)
                .verifyAddressDisplayMaximumCharacter(MAX_CHAR_ADDRESS)
                .inputAddressInfo_VN();
         new MyAddress(driver).clickOnSave()
                .verifyAddressInfo_VN(addressInfoVN.getCountry(), addressInfoVN.getAddress(), addressInfoVN.getCityProvince(), addressInfoVN.getDistrict(), addressInfoVN.getWard());
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .goToEditMyAddress()
                .verifyAddressInfo_VN(addressInfoVN.getCountry(), addressInfoVN.getAddress(), addressInfoVN.getCityProvince(), addressInfoVN.getDistrict(), addressInfoVN.getWard());
        loginDb = new web.Dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopVi, passWordDashboard);
        allCustomers = new AllCustomers(driver);
        homePage.waitTillSpinnerDisappear1().selectLanguage(languageDb).waitTillSpinnerDisappear1();
        allCustomers.navigate().searchAndGoToCustomerDetailByName(buyerDisplayName_Signup)
                .verifyAddressInfo_VN(addressInfoVN.getCountry(), addressInfoVN.getAddress(), addressInfoVN.getCityProvince(), addressInfoVN.getDistrict(), addressInfoVN.getWard())
                .clickLogout();
        //Update valid address in outside VietNam
        AddressInfo addressInfoNonVN = new AddressInfo();
        addressInfoNonVN = goToUserProfile()
                .clickMyAddressSection()
                .inputAddressInfo_NonVN();
         new MyAddress(driver).clickOnSave()
                .verifyAddressInfo_NonVN(addressInfoNonVN.getCountry(), addressInfoNonVN.getStreetAddress(), addressInfoNonVN.getAddress2(), addressInfoNonVN.getCity(), addressInfoNonVN.getStateRegionProvince(), addressInfoNonVN.getZipCode());
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .goToEditMyAddress()
                .verifyAddressInfo_NonVN(addressInfoNonVN.getCountry(), addressInfoNonVN.getStreetAddress(), addressInfoNonVN.getAddress2(), addressInfoNonVN.getCity(), addressInfoNonVN.getStateRegionProvince(), addressInfoNonVN.getZipCode());
        loginDb = new web.Dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopVi, passWordDashboard);
        allCustomers = new AllCustomers(driver);
        homePage.waitTillSpinnerDisappear1();
        allCustomers.navigate().searchAndGoToCustomerDetailByName(buyerDisplayName_Signup)
                .verifyAddressInfo_NonVN(addressInfoNonVN.getCountry(), addressInfoNonVN.getStreetAddress(), addressInfoNonVN.getAddress2(), addressInfoNonVN.getCity(), addressInfoNonVN.getStateRegionProvince(), addressInfoNonVN.getZipCode())
                .clickLogout();
        //Buyer access SF B to verify address
        loginSF = new LoginPage(driver);
        loginSF.navigate(shopDomainB)
                .performLogin(buyerAccount_Signup, passWordSF);
        headerSF = new HeaderSF(driver);
        AddressInfo addressInfo = new AddressInfo();
        addressInfo = headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressEmpty()
                .inputAddressInfo_VN();
             new MyAddress(driver).clickOnSave();
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomainB, productIDToBuyNowShopB)
                .clickOnBuyNow()
                .clickOnContinue()
                .goToEditMyAddress()
                .verifyAddressInfo_VN(addressInfo.getCountry(), addressInfo.getAddress(), addressInfo.getCityProvince(), addressInfo.getDistrict(), addressInfo.getWard());
        loginDb = new web.Dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopB, passWordDashboardShopB);
        allCustomers = new AllCustomers(driver);
        homePage.waitTillSpinnerDisappear1();
        allCustomers.navigate().searchAndGoToCustomerDetailByName(buyerDisplayName_Signup)
                .verifyAddressInfo_VN(addressInfo.getCountry(), addressInfo.getAddress(), addressInfo.getCityProvince(), addressInfo.getDistrict(), addressInfo.getWard())
                .clickLogout();
        //Check again on SF A
        myAddress = goToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_NonVN(addressInfoNonVN.getCountry(), addressInfoNonVN.getStreetAddress(), addressInfoNonVN.getAddress2(), addressInfoNonVN.getCity(), addressInfoNonVN.getStateRegionProvince(), addressInfoNonVN.getZipCode());
        myAddress.clickUserInfoIcon().clickLogout();
    }

    @Test
    public void UP01_ViewAccountInfo() {
        testCaseId = "UP01";
        loginAndGoToUserProfile(userName);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.verifyDisplayName(displayName)
                .verifyMembershipLevel(membershipLevel)
                .verifyBarcode(barcodeNumber)
                .verifyAvatarDisplay();
    }

    @Test
    public void UP02_UpdateMyAccountAndVerifyOnSF_NoBirthdayBefore_EmailAccount() throws Exception {
        testCaseId = "UP02";
        String generateName = generate.generateString(10);
        buyerAccount_Signup = generateName + "@mailnesia.com";
        buyerDisplayName_Signup = generateName;
        signupSF = new SignupPage(driver);
        signupSF.navigateToSignUp(shopDomain).waitTillLoaderDisappear();
        signupSF = new SignupPage(driver);
        signupSF.signUpWithEmail("Vietnam", buyerAccount_Signup, passWordSF, buyerDisplayName_Signup, "");
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().clickLogout();
        loginAndGoToUserProfile(buyerAccount_Signup);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.clickMyAccountSection();
        myAccount = new MyAccount(driver);
        myAccount.verifyEmailDisabled();
        displayName_Edit = generate.generateString(10);
        phoneNumber_Edit = "01" + generate.generateNumber(9);
        gender_Edit = myAccount.editGender();
        myAccount.inputFullName(displayName_Edit)
                .inputCompanyName(companyName_Edit)
                .inputTaxCode(taxCode_Edit)
                .inputPhoneNumber(phoneNumber_Edit)
                .inputBirthday(birthday_Edit)
                .clickOnSaveButton()
                .verifyBirday(birthday_Edit)
                .verifyDisplayName(displayName_Edit)
                .verifyPhoneNumber("+84:" + phoneNumber_Edit)
                .verifyGender(gender_Edit)
                .verifyCompanyName(companyName_Edit)
                .verifyTaxCode(taxCode_Edit)
                .verifyBirthdayDisabled();
    }

    @Test
    public void UP03_UpdateUserProfileAndVerifyOnSF_HasBirthdayBefore_EmailAccount() {
        testCaseId = "UP03";
        loginAndGoToUserProfile(userName_EditInfo_HasBirthday);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.clickMyAccountSection();
        myAccount = new MyAccount(driver);
        displayName_Edit = generate.generateString(10);
        phoneNumber_Edit = "01" + generate.generateNumber(9);
        gender_Edit = myAccount.editGender();
        myAccount.verifyEmailDisabled();
        myAccount.inputFullName(displayName_Edit)
                .inputPhoneNumber(phoneNumber_Edit)
                .inputCompanyName(companyName_Edit)
                .inputTaxCode(taxCode_Edit)
                .clickOnSaveButton()
                .verifyBirthdayDisabled()
                .verifyDisplayName(displayName_Edit)
                .verifyPhoneNumber("+84:" + phoneNumber_Edit)
                .verifyGender(gender_Edit)
                .verifyCompanyName(companyName_Edit)
                .verifyTaxCode(taxCode_Edit);
    }

    @Test
    public void UP04_UpdateUserProfile_NoBirthdayBefore_PhoneAccount() throws Exception {
        testCaseId = "UP04";
        String generateName = generate.generateString(10);
        buyerAccount_Signup = "01" + generate.generateNumber(9);
        email_Edit = generateName + "@mailnesia.com";
        buyerDisplayName_Signup = generateName;
        signupSF = new SignupPage(driver);
        signupSF.navigateToSignUp(shopDomain).signUpWithPhoneNumber("Vietnam", buyerAccount_Signup, passWordSF, buyerDisplayName_Signup, "");
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().clickLogout();
        loginAndGoToUserProfile(buyerAccount_Signup);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.clickMyAccountSection();
        myAccount = new MyAccount(driver);
        displayName_Edit = generate.generateString(10);
        gender_Edit = myAccount.editGender();
        myAccount.inputFullName(displayName_Edit)
                .inputCompanyName(companyName_Edit)
                .inputTaxCode(taxCode_Edit)
                .verifyPhoneDisabled()
                .inputEmail(email_Edit)
                .inputBirthday(birthday_Edit)
                .clickOnSaveButton()
                .verifyBirthdayDisabled()
                .verifyDisplayName(displayName_Edit)
                .verifyGender(gender_Edit)
                .verifyCompanyName(companyName_Edit)
                .verifyTaxCode(taxCode_Edit)
                .verifyEmail(email_Edit);
    }

    @Test
    public void UP05_UpdateUserProfile_HasBirthdayBefore_PhoneAccount() throws Exception {
        testCaseId = "UP05";
        loginAndGoToUserProfile(userName_PhoneAccount_EditInfo_HasBirthday);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.clickMyAccountSection();
        myAccount = new MyAccount(driver);
        displayName_Edit = generate.generateString(10);
        gender_Edit = myAccount.editGender();
        email_Edit = displayName_Edit + "@mailnesia.com";
        myAccount.inputFullName(displayName_Edit)
                .inputCompanyName(companyName_Edit)
                .inputTaxCode(taxCode_Edit)
                .verifyPhoneDisabled()
                .inputEmail(email_Edit)
                .inputBirthday(birthday_Edit)
                .clickOnSaveButton()
                .verifyBirthdayDisabled()
                .verifyDisplayName(displayName_Edit)
                .verifyGender(gender_Edit)
                .verifyCompanyName(companyName_Edit)
                .verifyTaxCode(taxCode_Edit)
                .verifyEmail(email_Edit);
    }

    @Test
    public void UP06_CheckAddressWhenUserHasAddressBeforeThenCheckout() throws Exception {
        testCaseId = "UP06";
        CheckUserHasAddressBefore_ExistedAccount_VietNam();
        CheckUserHasAddressBefore_ExistedAccount_NonVietNam();
        CheckUserHasAddressBefore_NewAccount();
    }

    @Test
    public void UP07_CheckAddressWhenUserHasNoAddressThenCheckout() throws Exception {
        testCaseId = "UP07";
        //SignUp
        String generateName = generate.generateString(10);
        buyerAccount_Signup = generateName + "@mailnesia.com";
        buyerDisplayName_Signup = generateName;
        phoneNumber = "01" + generate.generateNumber(9);
        signupSF = new SignupPage(driver);
        signupSF.navigateToSignUp(shopDomain).signUpWithEmail("Vietnam", buyerAccount_Signup, passWordSF, buyerDisplayName_Signup, "");
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().changeLanguage(languageSF).waitTillLoaderDisappear();
        // first checkout
        productDetailSF = new ProductDetailPage(driver);
        AddressInfo addressInfo = new AddressInfo();
        addressInfo = productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .goToEditMyAddress()
                .inputPhoneNumber(phoneNumber)
                .inputAddressInfo_VN();
             new Checkout(driver).completeEditAddress()
                .clickOnCompleteBtn()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection();
        new UserProfileInfo(driver).clickMyAddressSection()
                  .verifyAddressInfo_VN("", addressInfo.getAddress(), addressInfo.getCityProvince(), addressInfo.getDistrict(), addressInfo.getWard());
        // second checkout
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .goToEditMyAddress()
                .verifyAddressInfo_VN(addressInfo.getCountry(), addressInfo.getAddress(), addressInfo.getCityProvince(), addressInfo.getDistrict(), addressInfo.getWard())
                .inputPhoneNumber(phoneNumber)
                .inputAddressInfo_VN();
         new Checkout(driver).completeEditAddress()
                .clickOnCompleteBtn()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_VN("", addressInfo.getAddress(), addressInfo.getCityProvince(), addressInfo.getDistrict(), addressInfo.getWard());
    }

    @Test
    public void UP08_CheckUserUpdateAddress() throws Exception {
        testCaseId = "UP08";
        CheckUserUpdateAddress_ExistedAccount();
        CheckUserUpdateAddress_NewAccount();
    }
    @Test
    public void UP09_CheckAddInvalidOtherPhoneOtherEmail() throws Exception {
        testCaseId = "UP09";
        loginAndGoToUserProfile(userName);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.clickMyAccountSection();
        myAccount = new MyAccount(driver);
        myAccount.checkErrorWhenInputOtherPhoneOutOfRange()
                .checkErrorWhenInputOtherPhoneWithExistingValue()
                .checkErrorWhenSaveOtherPhoneWithBlankField()
                .checkErrorWhenInputInvalidEmail()
                .checkErrorWhenSaveOtherEmailWithBlankField();
    }

    @Test
    public void UP10_CheckAddValidOtherPhoneNumberOtherEmail() throws Exception {
        testCaseId = "UP10";
        loginAndGoToUserProfile(userName);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.clickMyAccountSection();
        myAccount = new MyAccount(driver);
        Map<String, String> otherPhoneMapOrigin = myAccount.getOtherPhoneMap();
        String phone1 = "01" + generate.generateNumber(8);
        String phone2 = "01" + generate.generateNumber(8);
        String phoneCode1 = "+84";
        String phoneCode2 = "+95";
        myAccount.addOtherPhones("Other phone", phoneCode1, phone1);
        otherPhoneMapOrigin.put(phoneCode1 + phone1, "Other phone");
        myAccount.addOtherPhones("Other phone", phoneCode2, phone2);
        otherPhoneMapOrigin.put(phoneCode2 + phone2, "Other phone");
        String otherEmail1 = generate.generateString(5) + "@mailnesia.com";
        String otherEmail2 = generate.generateString(5) + "@mailnesia.com";
        Map<String, String> otherEmailMapOrigin = myAccount.getOtherEmailMap();
        myAccount.addOtherEmails("Other mail", otherEmail1, otherEmail2);
        otherEmailMapOrigin.put(otherEmail1, "Other mail");
        otherEmailMapOrigin.put(otherEmail2, "Other mail");
        myAccount.clickOnSaveButton();
        Map otherPhoneActual = myAccount.getOtherPhoneMap();
        Map otherEmailActual = myAccount.getOtherEmailMap();
        myAccount.verifyOtherPhoneNumber(otherPhoneActual, otherPhoneMapOrigin)
                .verifyOtherEmail(otherEmailActual, otherEmailMapOrigin);
        productDetailSF = new ProductDetailPage(driver);
        Checkout checkout = productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .goToEditMyAddress();
        Map otherPhoneOnCheckout = checkout.getOtherPhoneMap();
        Map otherEmailOnCheckout = checkout.getOtherEmailMap();
        checkout.verifyOtherPhoneList(otherPhoneOnCheckout, otherPhoneMapOrigin)
                .verifyOtherEmailList(otherEmailOnCheckout, otherEmailMapOrigin);

    }
    @Test
    public void UP11_CheckEditOtherNumberOtherEmail(){
        testCaseId = "UP11";
        loginAndGoToUserProfile(userName);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.clickMyAccountSection();
        myAccount = new MyAccount(driver);
        Map<String,String> otherPhoneEdit = myAccount.editOtherPhoneNumber();
        Map<String,String> otherEmailEdit = myAccount.editOtherEmail();
        myAccount.clickOnSaveButton()
                .verifyOtherPhoneNumber(myAccount.getOtherPhoneMap(),otherPhoneEdit)
                .verifyOtherEmail(myAccount.getOtherEmailMap(),otherEmailEdit);
        productDetailSF = new ProductDetailPage(driver);
        Checkout checkout = productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .goToEditMyAddress();
        Map otherPhoneOnCheckout = checkout.getOtherPhoneMap();
        Map otherEmailOnCheckout = checkout.getOtherEmailMap();
        checkout.verifyOtherPhoneList(otherPhoneOnCheckout, otherPhoneEdit)
                .verifyOtherEmailList(otherEmailOnCheckout, otherEmailEdit);
    }
    @Test
    public void UP12_CheckDeleteOtherPhoneOtherEmail(){
        testCaseId = "UP12";
        loginAndGoToUserProfile(userName);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.clickMyAccountSection();
        myAccount = new MyAccount(driver);
        myAccount.deleteAllOtherPhone()
                .deleteAllOtherEmail()
                .clickOnSaveButton()
                .verifyOtherPhoneListSize(0)
                .verifyOtherEmailListSize(0);
    }
    @Test
    public void UP13_VerifyTextOfMyAccountPage() throws Exception {
        testCaseId = "UP13";
        loginAndGoToUserProfile(userName);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.clickMyAccountSection();
        myAccount = new MyAccount(driver);
        myAccount.verifyTextOfMyAccountPage();
    }
    @Test
    public void UP14_VerifyTextOfMyAddressPage() throws Exception {
        testCaseId = "UP14";
        loginAndGoToUserProfile(userName);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.clickMyAddressSection();
        myAddress = new MyAddress(driver);
        myAddress.verifyText();
    }
}
