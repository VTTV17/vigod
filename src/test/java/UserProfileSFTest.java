import api.dashboard.login.Login;
import api.dashboard.products.CreateProduct;
import io.restassured.path.json.JsonPath;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.dashboard.customers.allcustomers.AllCustomers;
import pages.dashboard.home.HomePage;
import pages.storefront.checkout.checkoutstep1.CheckOutStep1;
import pages.storefront.detail_product.ProductDetailPage;
import pages.storefront.header.HeaderSF;
import pages.storefront.login.LoginPage;
import pages.storefront.signup.SignupPage;
import pages.storefront.userprofile.MyAccount.MyAccount;
import pages.storefront.userprofile.MyAddress;
import pages.storefront.userprofile.userprofileinfo.UserProfileInfo;
import utilities.Constant;
import utilities.PropertiesUtil;
import utilities.UICommonAction;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.file.FileNameAndPath;
import utilities.model.dashboard.loginDashBoard.LoginDashboardInfo;
import utilities.model.sellerApp.login.LoginInformation;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

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
    pages.dashboard.login.LoginPage loginDb;
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
    String addressCheckout;
    String cityProvinceCheckout;
    String districtCheckout;
    String wardCheckout;
    String userName_NonVN;
    String countryCheckout;
    String address2Checkout;
    String cityInputCheckout;
    String zipCodeCheckout;
    String stateCheckout;
    String addressProfile;
    String cityProfile;
    String districtProfile;
    String wardProfile;
    String phoneNumber = "";
    String userName_ChangeAddress;
    String address_Edit_Invalid;
    String address_Edit;
    String cityProvince_Edit;
    String district_Edit;
    String ward_Edit;
    String userNameDb_ShopVi;
    String userNameDb_ShopB;
    String userName_UpdateAddress;
    String fullName_UpdateAddress;
    String country_Edit;
    String state_Edit;
    String addressNonVN_Edit;
    String address2_Edit;
    String cityInput_Edit;
    String zipCode_Edit;
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
        MAX_PRICE = 9999999L;
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
        addressCheckout = "so 1 update";
        cityProvinceCheckout = "Bắc Ninh";
        districtCheckout = "Huyện Quế Võ";
        wardCheckout = "Bồng Lai";
        countryCheckout = "Andorra";
        address2Checkout = "address 2 update";
        cityInputCheckout = "city in non VN checkout";
        zipCodeCheckout = "5656565";
        stateCheckout = "Canillo";
        addressProfile = "so 9900";
        cityProfile = "Hồ Chí Minh";
        districtProfile = "Quận 1";
        wardProfile = "Bến Nghé";
        address_Edit = "update address 22";
        cityProvince_Edit = "Bạc Liêu";
        district_Edit = "Huyện Hòa Bình";
        ward_Edit = "Minh Diệu";
        country_Edit = "Afghanistan";
        state_Edit = "Badakhshan";
        addressNonVN_Edit = "street address non VN";
        address2_Edit = "address 2 update non VN";
        cityInput_Edit = "city in non VN checkout";
        zipCode_Edit = "74747474";
        generate = new DataGenerator();
    }
    @BeforeMethod
    public void setUp(){
        driver = new InitWebdriver().getDriver(browser,"false");
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
                .verifyAddressInfo_VN(countryExpected, addressExpected, cityExpected, districtExpected, wardExpected)
                .inputAddressInfo_VN("", addressCheckout, cityProvinceCheckout, districtCheckout, wardCheckout)
                .selectPaymentMethod("COD")
                .clickOnNextButton()
                .selectShippingMethod("Self delivery")
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_VN(countryExpected, addressExpected, cityExpected, districtExpected, wardExpected)
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
                .verifyAddressInfo_NonVN(countryExpected, addressExpected, address2Expected, stateExpected, cityExpected, zipCodeExpected)
                .inputAddressInfo_NonVN(countryCheckout, addressCheckout, address2Checkout, stateCheckout, cityInputCheckout, zipCodeCheckout)
                .selectPaymentMethod("COD")
                .clickOnNextButton()
                .selectShippingMethod("Self delivery")
                .clickOnNextButton()
                .clickOnNextButton()
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
        loginAndGoToUserProfile(buyerAccount_Signup)
                .clickMyAddressSection()
                .inputAddressInfo_VN("", addressProfile, cityProfile, districtProfile, wardProfile)
                .clickOnSave()
                .verifyAddressInfo_VN("", addressProfile, cityProfile, districtProfile, wardProfile);
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_VN("", addressProfile, cityProfile, districtProfile, wardProfile)
                .inputPhoneNumber(phoneNumber)
                .inputAddressInfo_VN("", addressCheckout, cityProvinceCheckout, districtCheckout, wardCheckout)
                .selectPaymentMethod("COD")
                .clickOnNextButton()
                .selectShippingMethod("Self delivery")
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_VN("", addressProfile, cityProfile, districtProfile, wardProfile)
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
        myAddress.inputAddress(address_Edit_Invalid)
                .verifyAddressDisplayMaximumCharacter(MAX_CHAR_ADDRESS)
                .inputAddressInfo_VN("Vietnam", address_Edit, cityProvince_Edit, district_Edit, ward_Edit)
                .clickOnSave()
                .verifyAddressInfo_VN("Vietnam", address_Edit, cityProvince_Edit, district_Edit, ward_Edit);
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_VN("", address_Edit, cityProvince_Edit, district_Edit, ward_Edit);
        loginDb = new pages.dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopVi, passWordDashboard);
        homePage = new HomePage(driver);
        homePage.waitTillSpinnerDisappear().selectLanguage(languageDb);
        allCustomers = new AllCustomers(driver);
        allCustomers.navigate().searchAndGoToCustomerDetailByName(fullName_UpdateAddress)
                .verifyAddressInfo_VN("", address_Edit, cityProvince_Edit, district_Edit, ward_Edit)
                .clickLogout();
        //Update valid address in outside VietNam
        myAddress = goToUserProfile()
                .clickMyAddressSection()
                .inputAddressInfo_NonVN(country_Edit, addressNonVN_Edit, address2_Edit, cityInput_Edit, state_Edit, zipCode_Edit)
                .clickOnSave()
                .verifyAddressInfo_NonVN(country_Edit, addressNonVN_Edit, address2_Edit, cityInput_Edit, state_Edit, zipCode_Edit);
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_NonVN(country_Edit, addressNonVN_Edit, address2_Edit, state_Edit, cityInput_Edit, zipCode_Edit);
        loginDb = new pages.dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopVi, passWordDashboard);
        allCustomers = new AllCustomers(driver);
        allCustomers.waitTillSpinnerDisappear();
        allCustomers.navigate().searchAndGoToCustomerDetailByName(fullName_UpdateAddress)
                .verifyAddressInfo_NonVN(country_Edit, addressNonVN_Edit, address2_Edit, state_Edit, cityInput_Edit, zipCode_Edit)
                .clickLogout();
        //Buyer access SF B to verify address
        loginSF = new LoginPage(driver);
        loginSF.navigate(shopDomainB)
                .performLogin(userName_UpdateAddress, passWordSF);
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_VN("", addressCurrent, cityCurrent, districtCurrent, wardCurrent)
                .inputAddressInfo_VN("", addressCheckout, cityProvinceCheckout, districtCheckout, wardCheckout)
                .clickOnSave()
                .verifyAddressInfo_VN("", addressCheckout, cityProvinceCheckout, districtCheckout, wardCheckout);
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomainB, productIDToBuyNowShopB)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_VN("", addressCheckout, cityProvinceCheckout, districtCheckout, wardCheckout);
        loginDb = new pages.dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopB, passWordDashboardShopB);
        homePage = new HomePage(driver);
        homePage.waitTillSpinnerDisappear();
        allCustomers = new AllCustomers(driver);
        allCustomers.navigate().searchAndGoToCustomerDetailByName(fullName_UpdateAddress)
                .verifyAddressInfo_VN("Vietnam", addressCheckout, cityProvinceCheckout, districtCheckout, wardCheckout)
                .clickLogout();
        myAddress = goToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_NonVN(country_Edit, addressNonVN_Edit, address2_Edit, cityInput_Edit, state_Edit, zipCode_Edit);
        myAddress.clickUserInfoIcon().clickLogout();
//        Update addres in shop B as before
        loginSF = new LoginPage(driver);
        loginSF.navigate(shopDomainB);
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection();
        myAddress.inputAddressInfo_VN("Vietnam", addressCurrent, cityCurrent, districtCurrent, wardCurrent)
                .clickOnSave()
                .clickUserInfoIcon().clickLogout();
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
        myAddress.inputAddress(address_Edit_Invalid)
                .verifyAddressDisplayMaximumCharacter(MAX_CHAR_ADDRESS)
                .inputAddressInfo_VN("Vietnam", addressProfile, cityProfile, districtProfile, wardProfile)
                .clickOnSave()
                .verifyAddressInfo_VN("Vietnam", addressProfile, cityProfile, districtProfile, wardProfile);
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_VN("", addressProfile, cityProfile, districtProfile, wardProfile);
        loginDb = new pages.dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopVi, passWordDashboard);
        allCustomers = new AllCustomers(driver);
        allCustomers.waitTillSpinnerDisappear().selectLanguage(languageDb).waitTillSpinnerDisappear1();
        allCustomers.navigate().searchAndGoToCustomerDetailByName(buyerDisplayName_Signup)
                .verifyAddressInfo_VN("", addressProfile, cityProfile, districtProfile, wardProfile)
                .clickLogout();
        //Update valid address in outside VietNam
        myAddress = goToUserProfile()
                .clickMyAddressSection()
                .inputAddressInfo_NonVN(country_Edit, addressNonVN_Edit, address2_Edit, cityInput_Edit, state_Edit, zipCode_Edit)
                .clickOnSave()
                .verifyAddressInfo_NonVN(country_Edit, addressNonVN_Edit, address2_Edit, cityInput_Edit, state_Edit, zipCode_Edit);
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_NonVN(country_Edit, addressNonVN_Edit, address2_Edit, state_Edit, cityInput_Edit, zipCode_Edit);
        loginDb = new pages.dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopVi, passWordDashboard);
        allCustomers = new AllCustomers(driver);
        allCustomers.waitTillSpinnerDisappear();
        allCustomers.navigate().searchAndGoToCustomerDetailByName(buyerDisplayName_Signup)
                .verifyAddressInfo_NonVN(country_Edit, addressNonVN_Edit, address2_Edit, state_Edit, cityInput_Edit, zipCode_Edit)
                .clickLogout();
        //Buyer access SF B to verify address
        loginSF = new LoginPage(driver);
        loginSF.navigate(shopDomainB)
                .performLogin(buyerAccount_Signup, passWordSF);
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressEmpty()
                .inputAddressInfo_VN("Vietnam", addressCheckout, cityProvinceCheckout, districtCheckout, wardCheckout)
                .clickOnSave();
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomainB, productIDToBuyNowShopB)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_VN("Vietnam", addressCheckout, cityProvinceCheckout, districtCheckout, wardCheckout);
        loginDb = new pages.dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopB, passWordDashboardShopB);
        allCustomers = new AllCustomers(driver);
        allCustomers.waitTillSpinnerDisappear();
        allCustomers.navigate().searchAndGoToCustomerDetailByName(buyerDisplayName_Signup)
                .verifyAddressInfo_VN("Vietnam", addressCheckout, cityProvinceCheckout, districtCheckout, wardCheckout)
                .clickLogout();
        myAddress = goToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_NonVN(country_Edit, addressNonVN_Edit, address2_Edit, cityInput_Edit, state_Edit, zipCode_Edit);
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
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .inputPhoneNumber(phoneNumber)
                .inputAddressInfo_VN("", addressProfile, cityProfile, districtProfile, wardProfile)
                .selectPaymentMethod("COD")
                .clickOnNextButton()
                .selectShippingMethod("Self delivery")
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection();
        new UserProfileInfo(driver).clickMyAddressSection()
                  .verifyAddressInfo_VN("", addressProfile, cityProfile, districtProfile, wardProfile);
        // second checkout
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_VN("", addressProfile, cityProfile, districtProfile, wardProfile)
                .inputPhoneNumber(phoneNumber)
                .inputAddressInfo_VN("", addressCheckout, cityProvinceCheckout, districtCheckout, wardCheckout)
                .selectPaymentMethod("COD")
                .clickOnNextButton()
                .selectShippingMethod("Self delivery")
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_VN("", addressProfile, cityProfile, districtProfile, wardProfile);
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
        CheckOutStep1 checkOutStep1 = productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue();
        Map otherPhoneOnCheckout = checkOutStep1.getOtherPhoneMap();
        Map otherEmailOnCheckout = checkOutStep1.getOtherEmailMap();
        checkOutStep1.verifyOtherPhoneList(otherPhoneOnCheckout, otherPhoneMapOrigin)
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
        CheckOutStep1 checkOutStep1 = productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue();
        Map otherPhoneOnCheckout = checkOutStep1.getOtherPhoneMap();
        Map otherEmailOnCheckout = checkOutStep1.getOtherEmailMap();
        checkOutStep1.verifyOtherPhoneList(otherPhoneOnCheckout, otherPhoneEdit)
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
