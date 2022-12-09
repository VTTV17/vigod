import org.testng.ITestResult;
import org.testng.annotations.Test;
import pages.dashboard.customers.allcustomers.AllCustomers;
import pages.dashboard.home.HomePage;
import pages.gomua.headergomua.HeaderGoMua;
import pages.gomua.logingomua.LoginGoMua;
import pages.gomua.myprofile.MyProfileGoMua;
import pages.storefront.detail_product.ProductDetailPage;
import pages.storefront.header.HeaderSF;
import pages.storefront.login.LoginPage;
import pages.storefront.signup.SignupPage;
import pages.storefront.userprofile.MyAccount.MyAccount;
import pages.storefront.userprofile.MyAddress;
import pages.storefront.userprofile.userprofileinfo.UserProfileInfo;
import java.sql.SQLException;
import static utilities.account.AccountTest.*;
import static utilities.links.Links.*;
public class UserProfileSFTest extends BaseTest {
    LoginPage loginSF;
    UserProfileInfo userProfileInfo;
    HeaderGoMua headerGoMua;
    HeaderSF headerSF;
    MyAccount myAccount;
    LoginGoMua loginGoMua;
    MyProfileGoMua myProfileGoMua;
    SignupPage signupSF;
    ProductDetailPage productDetailSF;
    pages.dashboard.login.LoginPage loginDb;
    AllCustomers allCustomers;
    MyAddress myAddress;
    HomePage homePage;
    String userName = "qcgosell01@gmail.com";
    String passWordDashboard = ADMIN_SHOP_VI_PASSWORD;
    String passWordSF = SF_SHOP_VI_PASSWORD;
    String passWordDashboardShopB = ADMIN_SHOP_COFFEE_PASSWORD;
    String displayName = "qcgosell01";
    String membershipLevel = "Thanh Vien vang";
    String barcodeNumber = "505611";
    String userName_EditInfo_HasBirthday = "qcgosell0033@mailnesia.com";
    String displayName_Edit = "";
    String phoneNumber_Edit = "";
    String gender_Edit = "";
    String buyerDisplayName_Signup = "";
    String buyerAccount_Signup = "";
    String shopDomain = SF_ShopVi;
    String shopDomainB = SF_COFFEE;
    String email_Edit = "";
    String birthday_Edit = "20/10/1999";
    String productIDToBuyNow = "210130";
    String productIDToBuyNowShopB = "213172";
    String addressCheckout = "so 1 update";
    String cityProvinceCheckout = "Bắc Ninh";
    String districtCheckout = "Huyện Quế Võ";
    String wardCheckout = "Bồng Lai";
    String userName_NonVN = "qcgosell0044@mailnesia.com";
    String countryCheckout = "Andorra";
    String address2Checkout = "address 2 update";
    String cityInputCheckout = "city in non VN checkout";
    String zipCodeCheckout = "5656565";
    String stateCheckout = "Canillo";
    String addressProfile = "so 9900";
    String cityProfile = "Hồ Chí Minh";
    String districtProfile = "Quận 1";
    String wardProfile = "Bến Nghé";
    String phoneNumber = "";
    String userName_ChangeAddress = "qcgosell0055@mailnesia.com";
    String address_Edit = "update address 22";
    String cityProvince_Edit = "Bạc Liêu";
    String district_Edit = "Huyện Hòa Bình";
    String ward_Edit = "Minh Diệu";
    String address_Edit_Invalid = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has sur";
    int maximumCharAddress = 255;
    String userNameDb_ShopVi = ADMIN_SHOP_VI_USERNAME;
    String userNameDb_ShopB = ADMIN_SHOP_COFFEE_USERNAME;
    String userName_UpdateAddress = "qcgosell0066@mailnesia.com";
    String fullName_UpdateAddress = "qcgosell0066";
    String country_Edit = "Afghanistan";
    String state_Edit = "Badakhshan";
    String addressNonVN_Edit = "street address non VN";
    String address2_Edit = "address 2 update non VN";
    String cityInput_Edit = "city in non VN checkout";
    String zipCode_Edit = "74747474";
    String testCaseID = "";

    public UserProfileInfo loginAndGoToUserProfile(String userName) {
        loginSF = new LoginPage(driver);
        loginSF.navigate(shopDomain)
                .performLogin(userName, passWordSF);
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

    public void UpdateUserProfileAndVerifyOnSF_HasBirthdayBefore_EmailAccount() {
        loginAndGoToUserProfile(userName_EditInfo_HasBirthday);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.clickMyAccountSection();
        myAccount = new MyAccount(driver);
        displayName_Edit = generate.generateString(10);
        phoneNumber_Edit = "01" + generate.generateNumber(9);
        gender_Edit = myAccount.editGender();
        myAccount.inputFullName(displayName_Edit)
                .inputPhoneNumber(phoneNumber_Edit)
                .clickOnSaveButton()
                .verifyBirthdayDisabled()
                .verifyDisplayName(displayName_Edit)
                .verifyPhoneNumber("+84:" + phoneNumber_Edit)
                .verifyGender(gender_Edit)
                .verifyEmailDisabled();
//                .clickUserInfoIcon().clickLogout();
//        headerGoMua = new HeaderGoMua(driver);
//        headerGoMua.navigateToGoMua()
//                .clickOnLogInBTN();
//        loginGoMua = new LoginGoMua(driver);
//        loginGoMua.loginWithUserName(userName_EditInfo_HasBirthday, passWordSF);
//        headerGoMua = new HeaderGoMua(driver);
//        headerGoMua.changeLanguage("English")
//                .goToMyProfile();
//        myProfileGoMua = new MyProfileGoMua(driver);
//        myProfileGoMua.clickOnEditProfile()
//                .verifyDisplayName(displayName_Edit)
//                .verifyPhoneNumber("+84 " + phoneNumber_Edit)
//                .verifyGender(gender_Edit);
    }

    public void UpdateUserProfile_NoBirthdayBefore() throws SQLException {
        String generateName = generate.generateString(10);
        buyerAccount_Signup = generateName + "@mailnesia.com";
        buyerDisplayName_Signup = generateName;
        signupSF = new SignupPage(driver);
        signupSF.navigate(shopDomain).signUpWithEmail("Vietnam", buyerAccount_Signup, passWordSF, buyerDisplayName_Signup, "");
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().clickLogout();
        loginAndGoToUserProfile(buyerAccount_Signup);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.clickMyAccountSection();
        myAccount = new MyAccount(driver);
        myAccount.inputBirthday(birthday_Edit)
                .clickOnSaveButton();
        myAccount.verifyBirday(birthday_Edit);
    }

    public void CheckUserHasAddressBefore_ExistedAccount_VietNam() {
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
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_VN(countryExpected, addressExpected, cityExpected, districtExpected, wardExpected)
                .clickUserInfoIcon().clickLogout();
        ;
    }
    public void CheckUserHasAddressBefore_ExistedAccount_NonVietNam() {
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
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_NonVN(countryExpected, addressExpected, address2Expected, cityExpected, stateExpected, zipCodeExpected)
                .clickUserInfoIcon().clickLogout();
    }
    public void CheckUserHasAddressBefore_NewAccount() throws SQLException {
        String generateName = generate.generateString(10);
        buyerAccount_Signup = generateName + "@mailnesia.com";
        buyerDisplayName_Signup = generateName;
        phoneNumber = "01" + generate.generateNumber(9);
        signupSF = new SignupPage(driver);
        signupSF.navigate(shopDomain).signUpWithEmail("Vietnam", buyerAccount_Signup, passWordSF, buyerDisplayName_Signup, "");
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().clickLogout();
        loginAndGoToUserProfile(buyerAccount_Signup)
                .clickMyAddressSection()
                .inputAddressInfo_VN("", addressProfile, cityProfile, districtProfile, wardProfile)
                .clickOnSave();
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_VN("", addressProfile, cityProfile, districtProfile, wardProfile)
                .inputPhoneNumber(phoneNumber)
                .inputAddressInfo_VN("", addressCheckout, cityProvinceCheckout, districtCheckout, wardCheckout)
                .selectPaymentMethod("COD")
                .clickOnNextButton()
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
        myAddress= headerSF.navigateToUserProfile().clickMyAddressSection();
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
                .verifyAddressDisplayMaximumCharacter(maximumCharAddress)
                .inputAddressInfo_VN("Vietnam", address_Edit, cityProvince_Edit, district_Edit, ward_Edit)
                .clickOnSave();
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_VN("", address_Edit, cityProvince_Edit, district_Edit, ward_Edit);
        loginDb = new pages.dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopVi, passWordDashboard);
        homePage = new HomePage(driver);
        homePage.waitTillSpinnerDisappear();
        allCustomers = new AllCustomers(driver);
        allCustomers.navigate().searchAndGoToCustomerDetailByName(fullName_UpdateAddress)
                .verifyAddressInfo_VN("", address_Edit, cityProvince_Edit, district_Edit, ward_Edit)
                .clickLogout();
        //Update valid address in outside VietNam
        myAddress = goToUserProfile()
                .clickMyAddressSection()
                .inputAddressInfo_NonVN(country_Edit, addressNonVN_Edit,address2_Edit, cityInput_Edit, state_Edit, zipCode_Edit)
                .clickOnSave();
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_NonVN(country_Edit, addressNonVN_Edit,address2_Edit, state_Edit, cityInput_Edit, zipCode_Edit);
        loginDb = new pages.dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopVi, passWordDashboard);
        allCustomers = new AllCustomers(driver);
        allCustomers.waitTillSpinnerDisappear();
        allCustomers.navigate().searchAndGoToCustomerDetailByName(fullName_UpdateAddress)
                .verifyAddressInfo_NonVN(country_Edit, addressNonVN_Edit,address2_Edit, state_Edit,cityInput_Edit, zipCode_Edit)
                .clickLogout();
        //Buyer access SF B to verify address
        loginSF = new LoginPage(driver);
        loginSF.navigate(shopDomainB)
                .performLogin(userName_UpdateAddress, passWordSF);
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_VN("",addressCurrent,cityCurrent,districtCurrent,wardCurrent)
                .inputAddressInfo_VN("",addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout)
                .clickOnSave();
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomainB, productIDToBuyNowShopB)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_VN("",addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout);
        loginDb = new pages.dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopB, passWordDashboardShopB);
        homePage = new HomePage(driver);
        homePage.waitTillSpinnerDisappear();
        allCustomers = new AllCustomers(driver);
        allCustomers.navigate().searchAndGoToCustomerDetailByName(fullName_UpdateAddress)
                .verifyAddressInfo_VN("", addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout)
                .clickLogout();
        myAddress = goToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_NonVN(country_Edit, addressNonVN_Edit,address2_Edit, cityInput_Edit, state_Edit, zipCode_Edit);
        myAddress.clickUserInfoIcon().clickLogout();
        //Update addres in shop B as before
        loginSF = new LoginPage(driver);
        loginSF.navigate(shopDomainB);
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .inputAddressInfo_VN("",addressCurrent,cityCurrent,districtCurrent,wardCurrent)
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
        signupSF.navigate(shopDomain).signUpWithEmail("Vietnam", buyerAccount_Signup, passWordSF, buyerDisplayName_Signup, "");
        myAddress = goToUserProfile()
                .clickMyAddressSection();
        //Update valid address in VietNam
        myAddress.inputAddress(address_Edit_Invalid)
                .verifyAddressDisplayMaximumCharacter(maximumCharAddress)
                .inputAddressInfo_VN("Vietnam", addressProfile, cityProfile, districtProfile, wardProfile)
                .clickOnSave();
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_VN("", addressProfile, cityProfile, districtProfile, wardProfile);
        loginDb = new pages.dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopVi, passWordDashboard);
        allCustomers = new AllCustomers(driver);
        allCustomers.waitTillSpinnerDisappear();
        allCustomers.navigate().searchAndGoToCustomerDetailByName(buyerDisplayName_Signup)
                .verifyAddressInfo_VN("", addressProfile, cityProfile, districtProfile, wardProfile)
                .clickLogout();
        //Update valid address in outside VietNam
        myAddress = goToUserProfile()
                .clickMyAddressSection()
                .inputAddressInfo_NonVN(country_Edit, addressNonVN_Edit,address2_Edit, cityInput_Edit, state_Edit, zipCode_Edit)
                .clickOnSave();
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_NonVN(country_Edit, addressNonVN_Edit,address2_Edit, state_Edit, cityInput_Edit, zipCode_Edit);
        loginDb = new pages.dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopVi, passWordDashboard);
        allCustomers = new AllCustomers(driver);
        allCustomers.waitTillSpinnerDisappear();
        allCustomers.navigate().searchAndGoToCustomerDetailByName(buyerDisplayName_Signup)
                .verifyAddressInfo_NonVN(country_Edit, addressNonVN_Edit,address2_Edit, state_Edit,cityInput_Edit, zipCode_Edit)
                .clickLogout();
        //Buyer access SF B to verify address
        loginSF = new LoginPage(driver);
        loginSF.navigate(shopDomainB)
                .performLogin(buyerAccount_Signup, passWordSF);
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressEmpty()
                .inputAddressInfo_VN("",addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout)
                .clickOnSave();
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomainB, productIDToBuyNowShopB)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_VN("",addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout);
        loginDb = new pages.dashboard.login.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopB, passWordDashboardShopB);
        allCustomers = new AllCustomers(driver);
        allCustomers.waitTillSpinnerDisappear();
        allCustomers.navigate().searchAndGoToCustomerDetailByName(buyerDisplayName_Signup)
                .verifyAddressInfo_VN("", addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout)
                .clickLogout();
        myAddress = goToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_NonVN(country_Edit, addressNonVN_Edit,address2_Edit, cityInput_Edit, state_Edit, zipCode_Edit);
        myAddress.clickUserInfoIcon().clickLogout();

    }
    @Test
    public void UP01_BH_4604_ViewAccountInfo() {
        testCaseID = "BH_4604";
        loginAndGoToUserProfile(userName);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.verifyDisplayName(displayName)
                .verifyMembershipLevel(membershipLevel)
                .verifyBarcode(barcodeNumber)
                .verifyAvatarDisplay();
//        Assert.assertTrue(true);
//        testCaseID = "BH_4604";
//        reportResult("BH:BEEHIVE","Regression_Gosell",testCaseID,"GoSELL 3.7","Noted", TestLinkAPIResults.TEST_PASSED);

    }
    @Test
    public void UP02_BH_1290_UpdateUserProfile() throws SQLException {
        testCaseID = "BH_1290";
        UpdateUserProfileAndVerifyOnSF_HasBirthdayBefore_EmailAccount();
        UpdateUserProfile_NoBirthdayBefore();
    }
    @Test
    public void UP03_BH_4605_EditUserInformationForEmailAccount(){
        testCaseID = "BH_4605";
        UpdateUserProfileAndVerifyOnSF_HasBirthdayBefore_EmailAccount();
    }
    @Test
    public void UP04_BH_4606_UpdateUserProfile_NoBirthdayBefore_PhoneAccount() throws SQLException {
        testCaseID = "BH_4606";
        String generateName = generate.generateString(10);
        buyerAccount_Signup = "01" + generate.generateNumber(9);
        email_Edit = generateName + "@mailnesia.com";
        buyerDisplayName_Signup = generateName;
        signupSF = new SignupPage(driver);
        signupSF.navigate(shopDomain).signUpWithPhoneNumber("Vietnam", buyerAccount_Signup, passWordSF, buyerDisplayName_Signup, "");
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().clickLogout();
        loginAndGoToUserProfile(buyerAccount_Signup);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.clickMyAccountSection();
        myAccount = new MyAccount(driver);
        displayName_Edit = generate.generateString(10);
        gender_Edit = myAccount.editGender();
        myAccount.inputFullName(displayName_Edit)
                .inputEmail(email_Edit)
                .inputBirthday(birthday_Edit)
                .clickOnSaveButton()
                .verifyBirthdayDisabled()
                .verifyDisplayName(displayName_Edit)
                .verifyGender(gender_Edit)
                .verifyEmail(email_Edit);
    }
    @Test
    public void UP_05_BH_7334_ViewCustomerBarcode(){
        testCaseID = "BH_7334";
        loginAndGoToUserProfile(userName);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.verifyBarcode(barcodeNumber);
    }
    @Test
    public void UP_06_BH_7850_CheckUserHasAddressBefore() throws SQLException {
        testCaseID = "BH_7850";
        CheckUserHasAddressBefore_ExistedAccount_VietNam();
        CheckUserHasAddressBefore_ExistedAccount_NonVietNam();
        CheckUserHasAddressBefore_NewAccount();
    }
    @Test
    public void UP_07_BH_7851_CheckUserHasNoAddress_NewAccount() throws SQLException {
        testCaseID = "BH_7851";
        //SignUp
        String generateName = generate.generateString(10);
        buyerAccount_Signup = generateName + "@mailnesia.com";
        buyerDisplayName_Signup = generateName;
        phoneNumber = "01" + generate.generateNumber(9);
        signupSF = new SignupPage(driver);
        signupSF.navigate(shopDomain).signUpWithEmail("Vietnam", buyerAccount_Signup, passWordSF, buyerDisplayName_Signup, "");
        // first checkout
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .inputPhoneNumber(phoneNumber)
                .inputAddressInfo_VN("", addressProfile, cityProfile, districtProfile, wardProfile)
                .selectPaymentMethod("COD")
                .clickOnNextButton()
                .selectShippingMethod("Shop self delivery")
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
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
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_VN("", addressProfile, cityProfile, districtProfile, wardProfile);
    }
    @Test
    public void UP_08_BH_7852_CheckUserChangeAddress_ExistedAccount() {
        testCaseID = "BH_7852";
        myAddress = loginAndGoToUserProfile(userName_ChangeAddress)
                .clickMyAddressSection();
        String addressCurrent = myAddress.getAddress();
        String cityCurrent = myAddress.getCity();
        String districtCurrent = myAddress.getDistrict();
        String wardCurrent = myAddress.getWard();
        myAddress.inputAddressInfo_VN("", address_Edit, cityProvince_Edit, district_Edit, ward_Edit)
                .clickOnSave();
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain, productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_VN("", address_Edit, cityProvince_Edit, district_Edit, ward_Edit)
                .inputAddressInfo_VN("", addressCheckout, cityProvinceCheckout, districtCheckout, wardCheckout)
                .selectPaymentMethod("COD")
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_VN("", address_Edit, cityProvince_Edit, district_Edit, ward_Edit)
                .inputAddressInfo_VN("", addressCurrent, cityCurrent, districtCurrent, wardCurrent)
                .clickOnSave()
                .verifyAddressInfo_VN("", addressCurrent, cityCurrent, districtCurrent, wardCurrent);
    }
    @Test
    public void UP09_BH_8273_CheckUserUpdateAddress() throws SQLException {
        testCaseID = "BH_8273";
        CheckUserUpdateAddress_ExistedAccount();
        CheckUserUpdateAddress_NewAccount();
    }
//    @AfterTest
    public void updateTestLink(ITestResult result){
//         if (result.getStatus() == ITestResult.SUCCESS){
//             reportResult("BH:BEEHIVE","Regression_Gosell",);
//         }
    }
}
