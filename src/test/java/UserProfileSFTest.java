import org.testng.annotations.Test;
import pages.gomua.headergomua.HeaderGoMua;
import pages.gomua.logingomua.LoginGoMua;
import pages.gomua.myprofile.MyProfileGoMua;
import pages.storefront.HeaderSF;
import pages.storefront.LoginPage;
import pages.storefront.SignupPage;
import pages.storefront.detail_product.ProductDetailPage;
import pages.storefront.userprofile.MyAccount.MyAccount;
import pages.storefront.userprofile.MyAddress;
import pages.storefront.userprofile.userprofileinfo.UserProfileInfo;

import java.sql.SQLException;

import static utilities.links.Links.SF_ShopVi;

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
    pages.dashboard.LoginPage loginDb;
    MyAddress myAddress;
    String userName = "qcgosell01@gmail.com";
    String passWord = "Psso12!@";
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
    String email_Edit = "";
    String birthday_Edit = "20/10/1999";
    String productIDToBuyNow = "210130";
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
    String phoneNumber ="";
    String userName_ChangeAddress = "qcgosell0055@mailnesia.com";
    String address_Edit = "update address 22";
    String cityProvince_Edit = "Bạc Liêu";
    String district_Edit = "Huyện Hòa Bình";
    String ward_Edit = "Minh Diệu";
    String address_Edit_Invalid = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has sur";
    int maximumCharAddress = 255;
    String userNameDb_ShopVi = "070618433";



    public UserProfileInfo goToUserProfile(String userName) {
        loginSF = new LoginPage(driver);
        loginSF.navigate(shopDomain)
                .performLogin(userName, passWord);
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile();
        return new UserProfileInfo(driver);
    }

    @Test
    public void UP01_BH_4604_ViewAccountInfo() {
        goToUserProfile(userName);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.verifyDisplayName(displayName)
                .verifyMembershipLevel(membershipLevel)
                .verifyBarcode(barcodeNumber)
                .verifyAvatarDisplay();
    }

    @Test
    public void UP02_BH_1290_BH_4605_UpdateUserProfile_HasBirthdayBefore_EmailAccount() {
        goToUserProfile(userName_EditInfo_HasBirthday);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.clickMyAccountSection();
        myAccount = new MyAccount(driver);
        displayName_Edit = generate.generateString(10);
        phoneNumber_Edit = "01" + generate.generateNumber(9);
        gender_Edit = myAccount.editGender();
        myAccount.inputFullName(displayName_Edit)
                .inputPhoneNumber(phoneNumber_Edit)
                .clickOnSaveButton()
                .verifyBirthdayDisabled();
        myAccount.verifyDisplayName(displayName_Edit)
                .verifyPhoneNumber("+84:" + phoneNumber_Edit)
                .verifyGender(gender_Edit)
                .verifyEmailDisabled();
        headerGoMua = new HeaderGoMua(driver);
        headerGoMua.navigateToGoMua()
                .clickOnLogInBTN();
        loginGoMua = new LoginGoMua(driver);
        loginGoMua.loginWithUserName(userName_EditInfo_HasBirthday, passWord);
        headerGoMua = new HeaderGoMua(driver);
        headerGoMua.changeLanguage("English")
                .goToMyProfile();
        myProfileGoMua = new MyProfileGoMua(driver);
        myProfileGoMua.clickOnEditProfile()
                .verifyDisplayName(displayName_Edit)
                .verifyPhoneNumber("+84 " + phoneNumber_Edit)
                .verifyGender(gender_Edit);
    }

    @Test
    public void UP03_BH_1290_UpdateUserProfile_NoBirthdayBefore() throws SQLException {
        String generateName = generate.generateString(10);
        buyerAccount_Signup = generateName + "@mailnesia.com";
        buyerDisplayName_Signup = generateName;
        signupSF = new SignupPage(driver);
        signupSF.navigate(SF_ShopVi).signUpWithEmail("Vietnam", buyerAccount_Signup, passWord, buyerDisplayName_Signup, "");
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().clickLogout();
        goToUserProfile(buyerAccount_Signup);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.clickMyAccountSection();
        myAccount = new MyAccount(driver);
        myAccount.inputBirthday(birthday_Edit)
                .clickOnSaveButton();
        myAccount.verifyBirday(birthday_Edit);
    }

    @Test
    public void UP04_BH_4606_UpdateUserProfile_NoBirthdayBefore_PhoneAccount() throws SQLException {
        String generateName = generate.generateString(10);
        buyerAccount_Signup = "01" + generate.generateNumber(9);
        email_Edit = generateName + "@mailnesia.com";
        buyerDisplayName_Signup = generateName;
        signupSF = new SignupPage(driver);
        signupSF.navigate(SF_ShopVi).signUpWithPhoneNumber("Vietnam", buyerAccount_Signup, passWord, buyerDisplayName_Signup, "");
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().clickLogout();
        goToUserProfile(buyerAccount_Signup);
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
    public void UP_05_BH_7850_CheckUserHasAddressBefore_ExistedAccount_VietNam() {
        myAddress = goToUserProfile(userName)
                .clickMyAddressSection();
        String countryExpected = myAddress.getCountry();
        String addressExpected= myAddress.getAddress();
        String cityExpected = myAddress.getCity();
        String districtExpected = myAddress.getDistrict();
        String wardExpected = myAddress.getWard();
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain,productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_VN(countryExpected,addressExpected,cityExpected,districtExpected,wardExpected)
                .inputAddressInfo_VN("",addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout)
                .selectPaymentMethod("COD")
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_VN(countryExpected,addressExpected,cityExpected,districtExpected,wardExpected);
    }
    @Test
    public void UP_06_BH_7850_CheckUserHasAddressBefore_ExistedAccount_NonVietNam() {
        myAddress = goToUserProfile(userName_NonVN)
                .clickMyAddressSection();
        String countryExpected = myAddress.getCountry();
        String addressExpected = myAddress.getAddress();
        String address2Expected = myAddress.getAddress2();
        String cityExpected = myAddress.getInputtedCity();
        String stateExpected = myAddress.getState();
        String zipCodeExpected = myAddress.getZipCode();
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain,productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_NonVN(countryExpected,addressExpected,address2Expected,stateExpected,cityExpected,zipCodeExpected)
                .inputAddressInfo_NonVN(countryCheckout,addressCheckout,address2Checkout,stateCheckout,cityInputCheckout,zipCodeCheckout)
                .selectPaymentMethod("COD")
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_NonVN(countryExpected,addressExpected,address2Expected,cityExpected,stateExpected,zipCodeExpected);
    }
    @Test
    public void UP_07_BH_7850_CheckUserHasAddressBefore_NewAccount() throws SQLException {
        String generateName = generate.generateString(10);
        buyerAccount_Signup = generateName + "@mailnesia.com";
        buyerDisplayName_Signup = generateName;
        phoneNumber = "01"+generate.generateNumber(9);
        signupSF = new SignupPage(driver);
        signupSF.navigate(SF_ShopVi).signUpWithEmail("Vietnam", buyerAccount_Signup, passWord, buyerDisplayName_Signup, "");
        headerSF = new HeaderSF(driver);
        headerSF.clickUserInfoIcon().clickLogout();
        goToUserProfile(buyerAccount_Signup)
                .clickMyAddressSection()
                .inputAddressInfo_VN("",addressProfile,cityProfile,districtProfile,wardProfile)
                .clickOnSave();
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain,productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_VN("",addressProfile,cityProfile,districtProfile,wardProfile)
                .inputPhoneNumber(phoneNumber)
                .inputAddressInfo_VN("",addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout)
                .selectPaymentMethod("COD")
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_VN("",addressProfile,cityProfile,districtProfile,wardProfile);
    }
    @Test
    public void UP_08_BH_7851_CheckUserHasNoAddress_NewAccount() throws SQLException {
        //SignUp
        String generateName = generate.generateString(10);
        buyerAccount_Signup = generateName + "@mailnesia.com";
        buyerDisplayName_Signup = generateName;
        phoneNumber = "01"+generate.generateNumber(9);
        signupSF = new SignupPage(driver);
        signupSF.navigate(SF_ShopVi).signUpWithEmail("Vietnam", buyerAccount_Signup, passWord, buyerDisplayName_Signup, "");
        // first checkout
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain,productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .inputPhoneNumber(phoneNumber)
                .inputAddressInfo_VN("",addressProfile,cityProfile,districtProfile,wardProfile)
                .selectPaymentMethod("COD")
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_VN("",addressProfile,cityProfile,districtProfile,wardProfile);
        // second checkout
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain,productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_VN("",addressProfile,cityProfile,districtProfile,wardProfile)
                .inputPhoneNumber(phoneNumber)
                .inputAddressInfo_VN("",addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout)
                .selectPaymentMethod("COD")
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_VN("",addressProfile,cityProfile,districtProfile,wardProfile);
    }
    @Test
    public void UP_09_BH_7852_CheckUserChangeAddress_ExistedAccount(){
       goToUserProfile(userName_ChangeAddress)
               .clickMyAddressSection();
        String addressCurrent = myAddress.getAddress();
        String cityCurrent= myAddress.getCity();
        String districtCurrent = myAddress.getDistrict();
        String wardCurrent = myAddress.getWard();
        myAddress.inputAddressInfo_VN("",address_Edit,cityProvince_Edit,district_Edit,ward_Edit)
                .clickOnSave();
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain,productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_VN("",address_Edit,cityProvince_Edit,district_Edit,ward_Edit)
                .inputAddressInfo_VN("",addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout)
                .selectPaymentMethod("COD")
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnNextButton()
                .clickOnBackToMarket();
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile()
                .clickMyAddressSection()
                .verifyAddressInfo_VN("",address_Edit,cityProvince_Edit,district_Edit,ward_Edit)
                .inputAddressInfo_VN("",addressCurrent,cityCurrent,districtCurrent,wardCurrent)
                .clickOnSave()
                .verifyAddressInfo_VN("",addressCurrent,cityCurrent,districtCurrent,wardCurrent);
    }
    @Test
    public void UP_10_BH_8273_CheckUserChangeAddress_ExistedAccount() {
        goToUserProfile(userName_ChangeAddress)
                .clickMyAddressSection();
        String addressCurrent = myAddress.getAddress();
        String cityCurrent= myAddress.getCity();
        String districtCurrent = myAddress.getDistrict();
        String wardCurrent = myAddress.getWard();
        myAddress.inputAddress(address_Edit_Invalid)
                .verifyAddressDisplayMaximumCharacter(maximumCharAddress);
        myAddress.inputAddressInfo_VN("",address_Edit,cityProvince_Edit,district_Edit,ward_Edit)
                .clickOnSave();
        productDetailSF = new ProductDetailPage(driver);
        productDetailSF.accessToProductDetailPageByURL(shopDomain,productIDToBuyNow)
                .clickOnBuyNow()
                .clickOnContinue()
                .verifyAddressInfo_VN("",address_Edit,cityProvince_Edit,district_Edit,ward_Edit);
        loginDb = new pages.dashboard.LoginPage(driver);
        loginDb.navigate().performLogin(userNameDb_ShopVi,passWord);
    }
}
