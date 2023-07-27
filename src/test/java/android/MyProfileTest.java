package android;

import api.dashboard.login.Login;
import api.dashboard.products.CreateProduct;
import api.storefront.login.LoginSF;
import api.storefront.productdetail.APIProductDetail;
import api.storefront.signup.SignUp;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.buyerapp.account.BuyerAccountPage;
import pages.buyerapp.account.BuyerMyProfile;
import pages.buyerapp.account.address.BuyerAddress;
import pages.buyerapp.login.LoginPage;
import pages.buyerapp.navigationbar.NavigationBar;
import utilities.Constant;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.driver.InitAppiumDriver;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.screenshot.Screenshot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static utilities.account.AccountTest.*;

public class MyProfileTest extends BaseTest{
    String buyer;
    String passBuyer;
    NavigationBar navigationBar;
    BuyerAccountPage accountPage;
    String displayName;
    String membershipLevel;
    String barcodeNumber;
    String appActivity;
    String appPackage;
    DataGenerator generator;
    String sellerUsername;
    String sellerPass;
    String userName_EditInfo_HasBirthday;
    String userName_PhoneAccount_EditInfo_HasBirthday;
    int productIDToAddToCart;
    int branchID;
    String addressCheckout;
    String cityProvinceCheckout;
    String districtCheckout;
    String wardCheckout;
    String countryCheckout;
    String address2Checkout;
    String cityInputCheckout;
    String zipCodeCheckout;
    String stateCheckout;
    String userName_UpdateAddress;
    String language;
    LoginInformation loginInformation;

    @BeforeClass
    public void setUp() throws Exception {
        String udid = "R5CR92R4K7V";
        String platformName = "Android";
        appPackage = "com.mediastep.shop0037";
        appActivity = "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity";
        String url = "http://127.0.0.1:4723/wd/hub";
        language = "VIE";
        PropertiesUtil.setEnvironment("STAG");
        PropertiesUtil.setSFLanguage(language);
        driver = new InitAppiumDriver().getAppiumDriver(udid, platformName, appPackage, appActivity, url);
        buyer = AccountTest.SF_USERNAME_VI_1;
        passBuyer = AccountTest.SF_SHOP_VI_PASSWORD;
        displayName = PropertiesUtil.getEnvironmentData("buyerName1");
        membershipLevel = PropertiesUtil.getEnvironmentData("membershipLevel");
        barcodeNumber = PropertiesUtil.getEnvironmentData("barcodeBuyer1");
        generator = new DataGenerator();
        sellerUsername = AccountTest.ADMIN_SHOP_VI_USERNAME;
        sellerPass = AccountTest.ADMIN_SHOP_VI_PASSWORD;
        userName_EditInfo_HasBirthday = SF_USERNAME_VI_4;
        userName_PhoneAccount_EditInfo_HasBirthday = SF_USERNAME_PHONE_VI_1;
        loginInformation = new Login().setLoginInformation("+84",sellerUsername,sellerPass).getLoginInformation();
        CreateProduct newProductInfo = new CreateProduct(loginInformation).createWithoutVariationProduct(false,30);
        productIDToAddToCart = newProductInfo.getProductID();
        branchID = newProductInfo.getBranchIds().get(0);
        new LoginSF(loginInformation).LoginToSF("qcgosell01@gmail.com","Psso12!@","+84");
        addressCheckout = "so 2 update";
        cityProvinceCheckout = "Gia Lai";
        districtCheckout = "Kbang";
        wardCheckout = "KBang";
        countryCheckout = "Algeria";
        address2Checkout = "89 address2 update";
        cityInputCheckout = "city in non VN checkout";
        zipCodeCheckout = "6987890";
        stateCheckout = "Annaba";
        userName_UpdateAddress = SF_USERNAME_VI_3;
    }
    @AfterClass
    public void tearDown(){
        driver.quit();
    }
    @AfterMethod
    public void restartApp(ITestResult result) {
        ((AndroidDriver) driver).resetApp();
    }
    public BuyerAccountPage login(String buyerAccount){
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnAccountIcon()
                .clickLoginBtn()
                .performLogin(buyerAccount,passBuyer);
        return new BuyerAccountPage(driver);
    }
    public void callAPIAddToCart(String buyerUsername){
        new LoginSF(loginInformation).LoginToSF(buyerUsername,passBuyer,"+84");
        new APIProductDetail(loginInformation).callAddToCart(productIDToAddToCart,branchID,1);
    }
    public String callAPISignUpAccount(boolean isEmailAccount){
        loginInformation = new Login().setLoginInformation("+84",sellerUsername,sellerPass).getLoginInformation();
        String userName;
        if(isEmailAccount){
            userName = "email"+generator.randomNumberGeneratedFromEpochTime(7)+"@mailnesia.com";
            new SignUp(loginInformation).signUpByMail(userName,passBuyer);
        }else {
            userName= "01"+generator.randomNumberGeneratedFromEpochTime(7);
            new SignUp(loginInformation).signUpByPhoneNumber(passBuyer,userName,"+84");
        }
        new UICommonMobile(driver).sleepInMiliSecond(5000);
        return userName;
    }
//    @Test
    public void MUP01_CheckTextOfMyProfilePage() throws Exception {
        login(buyer).
        changeLanguage(language).clickProfile()
                .verifyTextMyProfile();
    }
//    @Test
    public void MUP02_ViewAccountInformation(){
        login(buyer).
        changeLanguage(language).verifyAvatarDisplay()
                .verifyDisplayName(displayName)
                .verifyMemberShipLevel(membershipLevel)
                .tapOnBarcodeIcon()
                .verifyBarcode(barcodeNumber);
    }
//    @Test
    public void MUP03_UpdateUserProfile_EmailAccount_NoBirthdayBefore(){
        String emailAccount = callAPISignUpAccount(true);
        String randomNumber = generator.randomNumberGeneratedFromEpochTime(8);
        String nameEdit = "update name "+randomNumber;
        String identityCardEdit = randomNumber;
        String phoneCodeEdit = "+84";
        String phoneNumberEdit = "0"+randomNumber;
        String companyEdit = generator.generateString(7);
        String taxEdit = randomNumber;
        String birthdayEdit =generator.generateDateTime("dd-MM-yyyy");
        login(emailAccount).
        changeLanguage(language).clickProfile()
                .inputYourName(nameEdit)
                .verifyEmailDisabled()
                .inputIdentityCard(identityCardEdit)
                .inputPhone(phoneCodeEdit,phoneNumberEdit)
                .scrollDown()
                .inputCompanyName(companyEdit)
                .inputTaxCode(taxEdit)
                .selectBirdayAsCurrentDate().scrollUp()
                .tapOnSaveBtn()
                .verifyDisplayName(nameEdit)
                .clickProfile()
                .verifyYourName(nameEdit)
                .verifyIdentityCard(identityCardEdit)
                .verifyPhoneCode(phoneCodeEdit)
                .verifyPhoneNumber(phoneNumberEdit)
                .scrollDown()
                .verifyCompanyName(companyEdit)
                .verifyTaxCode(taxEdit)
                .verifyBirthday(birthdayEdit)
                .verifyBirthdayDisabled();
    }
//    @Test
    public void MUP04_UpdateUserProfile_EmailAccount_HasBirthdayBefore(){
        String randomNumber = generator.randomNumberGeneratedFromEpochTime(8);
        String nameEdit = "update name "+randomNumber;
        String identityCardEdit = randomNumber;
        String phoneCodeEdit = "+84";
        String phoneNumberEdit = "0"+randomNumber;
        String companyEdit = generator.generateString(7);
        String taxEdit = randomNumber;
        login(userName_EditInfo_HasBirthday).
        changeLanguage(language).clickProfile()
                .inputYourName(nameEdit)
                .verifyEmailDisabled()
                .inputIdentityCard(identityCardEdit)
                .inputPhone(phoneCodeEdit,phoneNumberEdit)
                .scrollDown()
                .inputCompanyName(companyEdit)
                .inputTaxCode(taxEdit)
                .tapOnSaveBtn()
                .verifyDisplayName(nameEdit)
                .clickProfile()
                .verifyYourName(nameEdit)
                .verifyIdentityCard(identityCardEdit)
                .verifyPhoneCode(phoneCodeEdit)
                .verifyPhoneNumber(phoneNumberEdit)
                .scrollDown()
                .verifyCompanyName(companyEdit)
                .verifyTaxCode(taxEdit)
                .verifyBirthdayDisabled();
    }
//    @Test
    public void MUP05_UpdateUserProfile_PhoneAccount_NoBirthdayBefore(){
        String phoneNumber = callAPISignUpAccount(false);
        String randomNumber = generator.randomNumberGeneratedFromEpochTime(8);
        String nameEdit = "update name "+randomNumber;
        String identityCardEdit = randomNumber;
        String emailEdit = "email"+generator.randomNumberGeneratedFromEpochTime(7)+"@mailnesia.com";
        String companyEdit = generator.generateString(7);
        String taxEdit = randomNumber;
        String birthdayEdit =generator.generateDateTime("dd-MM-yyyy");
        login(phoneNumber).
        changeLanguage(language).clickProfile()
                .inputYourName(nameEdit)
                .inputEmail(emailEdit)
                .inputIdentityCard(identityCardEdit)
                .verifyPhoneDisabled()
                .scrollDown()
                .inputCompanyName(companyEdit)
                .inputTaxCode(taxEdit)
                .selectBirdayAsCurrentDate()
                .tapOnSaveBtn()
                .verifyDisplayName(nameEdit)
                .clickProfile()
                .verifyYourName(nameEdit)
                .verifyIdentityCard(identityCardEdit)
                .verifyEmail(emailEdit)
                .scrollDown()
                .verifyCompanyName(companyEdit)
                .verifyTaxCode(taxEdit)
                .verifyBirthday(birthdayEdit)
                .verifyBirthdayDisabled();
    }
//    @Test
    public void MUP06_UpdateUserProfile_PhoneAccount_HasBirthdayBefore(){
        String randomNumber = generator.randomNumberGeneratedFromEpochTime(8);
        String nameEdit = "update name "+randomNumber;
        String identityCardEdit = randomNumber;
        String emailEdit = "email"+generator.randomNumberGeneratedFromEpochTime(7)+"@mailnesia.com";
        String companyEdit = generator.generateString(7);
        String taxEdit = randomNumber;
        navigationBar = new NavigationBar(driver);
        login(userName_PhoneAccount_EditInfo_HasBirthday).
        changeLanguage(language).clickProfile()
                .inputYourName(nameEdit)
                .inputEmail(emailEdit)
                .inputIdentityCard(identityCardEdit)
                .verifyPhoneDisabled()
                .scrollDown()
                .inputCompanyName(companyEdit)
                .inputTaxCode(taxEdit)
                .tapOnSaveBtn()
                .verifyDisplayName(nameEdit)
                .clickProfile()
                .verifyYourName(nameEdit)
                .verifyIdentityCard(identityCardEdit)
                .verifyEmail(emailEdit)
                .scrollDown()
                .verifyCompanyName(companyEdit)
                .verifyTaxCode(taxEdit)
                .verifyBirthdayDisabled();
    }
//    @Test
    public void MUP07_CheckAddress_UserHasAddressThenCheckout(){
        login(buyer).
        changeLanguage(language);
        //get addres information in My profile
        navigationBar = new NavigationBar(driver);
        BuyerAddress addressInfo = navigationBar.tapOnAccountIcon()
                .clickProfile().scrollDown().clickAddress();
        String country = addressInfo.getCountry();
        String address = addressInfo.getAddress();
        String cityProvince = addressInfo.getCityProvince();
        String district = addressInfo.getDistrict();
        String ward = addressInfo.getWard();
        addressInfo.tapOnBackIcon()
                .tapOnBackIcon();
        //Go to checkout to verify address, then checkout with new address
        callAPIAddToCart(buyer);
        new NavigationBar(driver).tapOnCartIcon()
                .tapOnContinueBtn().scrollDown()
                .verifyAddressVN(country,address,cityProvince,district,ward)
                .inputAddressVN("",addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout)
                .tapOnContinueBtn()
                .tapOnContinueBtn()
                .tapOnContinueBtn()
                .tapOnContinueShopping();
        //Go to My profile to verify
        new NavigationBar(driver).tapOnAccountIcon()
                .clickProfile().scrollDown()
                .clickAddress()
                .verifyAddressVN(country,address,cityProvince,district,ward)
                .tapOnBackIcon()
                .tapOnBackIcon();
        //Verify checkout with non VN address
        callAPIAddToCart(buyer);
        new NavigationBar(driver).tapOnCartIcon()
                .tapOnContinueBtn().scrollDown()
                .verifyAddressVN(country,address,cityProvince,district,ward)
                .inputAddressNonVN(countryCheckout,addressCheckout,address2Checkout,cityInputCheckout,stateCheckout,zipCodeCheckout)
                .tapOnContinueBtn()
                .tapOnContinueBtn()
                .tapOnContinueBtn()
                .tapOnContinueShopping();
        //Go to My profile to verify
        new NavigationBar(driver).tapOnAccountIcon()
                .clickProfile().scrollDown()
                .clickAddress()
                .verifyAddressVN(country,address,cityProvince,district,ward);
    }
//    @Test
    public void MUP08_CheckAddress_NoAddressThenCheckout(){
        String radomPhone = generator.randomVNPhone();
        String emailAccount = callAPISignUpAccount(true);
        login(emailAccount).
        changeLanguage(language);
        //Go to checkout to verify address, then checkout with new address
        callAPIAddToCart(emailAccount);
        new NavigationBar(driver).tapOnCartIcon()
                .tapOnContinueBtn().scrollDown()
                .inputPhone("+84",radomPhone)
                .inputAddressVN("",addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout)
                .tapOnContinueBtn()
                .tapOnContinueBtn()
                .tapOnContinueBtn()
                .tapOnContinueShopping();
        new UICommonMobile(driver).sleepInMiliSecond(7000);
        //Go to My profile to verify
        new NavigationBar(driver).tapOnAccountIcon()
                .clickProfile().scrollDown()
                .clickAddress()
                .verifyAddressVN(Constant.VIETNAM,addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout)
                .tapOnBackIcon()
                .tapOnBackIcon();
    }
//    @Test
    public void MUP09_UpdateAddress_ExistedAccount(){
        //Check update address VN
        login(userName_UpdateAddress).
        changeLanguage(language).clickProfile().scrollDown().clickAddress()
                .inputAddressVN(Constant.VIETNAM,addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout)
                .clickAddress()
                .verifyAddressVN(Constant.VIETNAM,addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout)
                .tapOnBackIcon()
                .tapOnBackIcon();
        callAPIAddToCart(userName_UpdateAddress);
        new NavigationBar(driver).tapOnCartIcon()
                .tapOnContinueBtn().scrollDown()
                .verifyAddressVN(Constant.VIETNAM,addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout)
                .tapOnContinueBtn()
                .tapOnContinueBtn()
                .tapOnContinueBtn()
                .tapOnContinueShopping();
        //Check update address non VN
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnAccountIcon()
                .clickProfile().scrollDown().clickAddress()
                .inputAddressNonVN(countryCheckout,addressCheckout,address2Checkout,cityInputCheckout,stateCheckout,zipCodeCheckout)
                .clickAddress()
                .verifyAddressNonVN(countryCheckout,addressCheckout,address2Checkout,cityInputCheckout,stateCheckout,zipCodeCheckout)
                .tapOnBackIcon()
                .tapOnBackIcon();
        callAPIAddToCart(userName_UpdateAddress);
        new NavigationBar(driver).tapOnCartIcon()
                .tapOnContinueBtn()
                .verifyAddressNonVN(countryCheckout,addressCheckout,address2Checkout,cityInputCheckout,stateCheckout,zipCodeCheckout);
    }
//    @Test
    public void MUP10_UpdateAddress_NewAccount(){
        //Call api create buyer
        String emailAccount = callAPISignUpAccount(true);
        //Check update address VN
        String radomPhone = generator.randomVNPhone();
        login(emailAccount).
        changeLanguage(language).clickProfile().scrollDown().clickAddress()
                .inputAddressVN(Constant.VIETNAM,addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout)
                .clickAddress()
                .verifyAddressVN(Constant.VIETNAM,addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout)
                .tapOnBackIcon()
                .tapOnBackIcon();
        callAPIAddToCart(emailAccount);
        new NavigationBar(driver).tapOnCartIcon()
                .tapOnContinueBtn().scrollDown()
                .inputPhone("+84",radomPhone)
                .verifyAddressVN(Constant.VIETNAM,addressCheckout,cityProvinceCheckout,districtCheckout,wardCheckout)
                .tapOnContinueBtn()
                .tapOnContinueBtn()
                .tapOnContinueBtn()
                .tapOnContinueShopping();
        //Call api create buyer
        String emailAccountNonVn = callAPISignUpAccount(true);
        //Check update address non VN
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnAccountIcon()
                .scrollDown()
                .logOut()
                .clickLoginBtn()
                .performLogin(emailAccountNonVn,passBuyer);
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnAccountIcon()
                .clickProfile().scrollDown().clickAddress()
                .inputAddressNonVN(countryCheckout,addressCheckout,address2Checkout,cityInputCheckout,stateCheckout,zipCodeCheckout)
                .clickAddress()
                .verifyAddressNonVN(countryCheckout,addressCheckout,address2Checkout,cityInputCheckout,stateCheckout,zipCodeCheckout)
                .tapOnBackIcon()
                .tapOnBackIcon();
        callAPIAddToCart(emailAccountNonVn);
        new NavigationBar(driver).tapOnCartIcon()
                .tapOnContinueBtn()
                .verifyAddressNonVN(countryCheckout,addressCheckout,address2Checkout,cityInputCheckout,stateCheckout,zipCodeCheckout);
    }
//    @Test
    public void MUP11_CheckAddInvalidOtherPhoneOtherEmail() throws Exception {
        login(buyer).
        changeLanguage(language).clickProfile()
                .tapOtherEmails()
                .checkErrorWhenInputInvalidEmail()
                .tapOtherPhones()
                .checkErrorWhenInputOtherPhoneOutOfRange();

    }
//    @Test
    public void MUP12_CheckAddValidOtherPhoneOtherEmail() throws Exception {
        login(buyer).
        changeLanguage(language);
        String email1 = "email"+generator.randomNumberGeneratedFromEpochTime(9)+"@mailnesia.com";
        String email2 = "email"+generator.randomNumberGeneratedFromEpochTime(10)+"@mailnesia.com";
        String[] emails = {email1,email2};
        String phone1 = "01"+generator.generateNumber(7);
        String phone2 = "02"+generator.generateNumber(7);
        String[] phones ={phone1,phone2};
        String phoneCode = "+84";
        navigationBar = new NavigationBar(driver);
        //Get other email avalible
        Map <String,String> otherEmailMapOriginal = navigationBar.tapOnAccountIcon()
                .clickProfile()
                .tapOtherEmails().getOtherPhonesOrEmailMap();
        new BuyerMyProfile(driver)
                .addOtherEmails("Other mail",emails);
        //Add new other mail to available map
        otherEmailMapOriginal.put(email1,"Other mail");
        otherEmailMapOriginal.put(email2,"Other mail");
        //Get other phone avalible
        Map <String,String> otherPhoneMapOriginal = new BuyerMyProfile(driver)
                .tapBackIcon_OtherEmail()
                .verifyOtherEmailNumber(otherEmailMapOriginal.size())
                .tapOtherPhones().getOtherPhonesOrEmailMap();
        new BuyerMyProfile(driver)
                .addOtherPhones("Other phone",phoneCode,phones);
        //Add new other mail to available map
        otherPhoneMapOriginal.put("("+phoneCode+") "+phone1,"Other phone");
        otherPhoneMapOriginal.put("("+phoneCode+") "+phone2,"Other phone");
        new BuyerMyProfile(driver).tapBackIcon_OtherPhone()
                .verifyOtherPhoneNumber(otherPhoneMapOriginal.size())
                .tapOnSaveBtn()
                .clickProfile();
        new BuyerMyProfile(driver)
                .verifyOtherEmailAfterAdded(otherEmailMapOriginal)
                .verifyOtherPhoneAfterAdded(otherPhoneMapOriginal);
    }
//    @Test
    public void MUP13_CheckEditOtherPhoneEmail() throws Exception {
        login(buyer).
        changeLanguage(language);
        Map<String,String> otherEmailEdited = navigationBar.tapOnAccountIcon()
                .clickProfile()
                .editOtherEmails();
        Map<String,String> otherPhoneEdited = new BuyerMyProfile(driver)
                .editOtherPhones();
        new BuyerMyProfile(driver).tapOnSaveBtn()
                .clickProfile()
                .verifyOtherEmailAfterAdded(otherEmailEdited)
                .verifyOtherPhoneAfterAdded(otherPhoneEdited);
    }
//    @Test
    public void MUP14_CheckDeleteOtherPhoneOtherEmail() throws Exception {
        login(buyer).
        changeLanguage(language);
        Map<String,String> emptyMap = new HashMap<>();
        new NavigationBar(driver).tapOnAccountIcon()
                .clickProfile().tapOtherEmails()
                .deleteAllOtherPhoneEmail().tapBackIcon_OtherEmail()
                .verifyOtherEmailNumber(0)
                .tapOtherPhones()
                .deleteAllOtherPhoneEmail().tapBackIcon_OtherPhone()
                .verifyOtherPhoneNumber(0)
                .tapOnSaveBtn()
                .clickProfile()
                .verifyOtherPhoneAfterAdded(emptyMap)
                .verifyOtherEmailAfterAdded(emptyMap);
    }
    @Test
    public void MUP15_CheckDeleteEmail() throws Exception {
        login(userName_PhoneAccount_EditInfo_HasBirthday).
        changeLanguage(language)
                .clickProfile().inputEmail("").tapOnSaveBtn()
                .clickProfile().verifyEmail(PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.emailHint"));
    }
    @Test
    public void MUP16_CheckDeletePhoneNumber() throws Exception {
        login(userName_EditInfo_HasBirthday).
        changeLanguage(language).clickProfile()
                .inputPhone("+84","").tapOnSaveBtn()
                .clickProfile().verifyPhoneNumber(PropertiesUtil.getPropertiesValueBySFLang("buyerApp.myProfile.phoneHint"));
    }
    @Test
    public void MUP17_CheckInputOtherPhoneEmailOutOfRange(){
        int currentOtherEmailNumber = login(userName_EditInfo_HasBirthday).changeLanguage(language).clickProfile()
                .getOtherEmailNumberFromText();
        new BuyerMyProfile(driver).tapOtherEmails()
                .addMultipleOtherEmail(100-currentOtherEmailNumber)
                .tapOnAddOtherEmailIcon()
                .verifyAddOtherEmailNotShow().tapBackIcon_OtherEmail();
        int currentOtherPhoneNumber = new BuyerMyProfile(driver).getOtherPhoneNumberFromText();
        new BuyerMyProfile(driver).tapOtherPhones()
                .addMultipleOtherPhone(100-currentOtherPhoneNumber)
                .tapOnAddOtherPhoneIcon()
                .verifyAddOtherPhoneNotShow();
    }
    @Test
    public void MUP18_CheckTextDeleteAccount() throws Exception {
        login(userName_EditInfo_HasBirthday).changeLanguage(language).clickProfile()
                .scrollDown().tapDeleteAccount()
                .verifyTextDeleteAccountPopup();
    }
    @Test
    public void MUP19_DeleteAccount() throws Exception {
        //delete account
        String emailAccount = callAPISignUpAccount(true);
        login(emailAccount).changeLanguage(language).clickProfile().scrollDown().tapDeleteAccount()
                .tapDeleteBTNOnDeletePopup();
        //check auto logout and login again
        new NavigationBar(driver).tapOnAccountIcon().verifyLoginButtonShow();
        login(emailAccount);
        new LoginPage(driver).verifyToastMessage(PropertiesUtil.getPropertiesValueBySFLang("buyerApp.login.loginError"));
        //check sign up again
        new SignUp(loginInformation).signUpByMail(emailAccount,passBuyer);
        new LoginPage(driver).performLogin(emailAccount,passBuyer);
        new BuyerAccountPage(driver).verifyAvatarDisplay();
    }
}
