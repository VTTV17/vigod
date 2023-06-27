package android;

import api.dashboard.login.Login;
import api.storefront.signup.SignUp;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.StartsActivity;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import pages.buyerapp.NavigationBar;
import pages.buyerapp.account.BuyerAccountPage;
import utilities.PropertiesUtil;
import utilities.UICommonMobile;
import utilities.account.AccountTest;
import utilities.api.API;
import utilities.data.DataGenerator;
import utilities.driver.InitAppiumDriver;
import utilities.model.dashboard.services.ServiceInfo;

import static utilities.account.AccountTest.SF_USERNAME_PHONE_VI_1;
import static utilities.account.AccountTest.SF_USERNAME_VI_4;

public class MyProfileTest {
    WebDriver driver;
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
    @BeforeClass
    public void setUp() throws Exception {
        String udid = "R5CR92R4K7V";
        String platformName = "Android";
        appPackage = "com.mediastep.shop0037";
        appActivity = "com.mediastep.gosell.ui.modules.splash.SplashScreenActivity";
        String url = "http://127.0.0.1:4723/wd/hub";
        PropertiesUtil.setEnvironment("STAG");
        PropertiesUtil.setSFLanguage("VIE");
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
        login();
    }
    @AfterClass
    public void tearDown(){
        driver.quit();
    }
    @AfterMethod
    public void restartApp(){
        Activity activity = new Activity(appPackage,appActivity);
        activity.setStopApp(false);
        ((StartsActivity) driver).startActivity(activity);
        new UICommonMobile(driver).waitSplashScreenLoaded();
    }
    public void login(){
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnAccountIcon()
                .clickLoginBtn()
                .performLogin(buyer,passBuyer);
    }
    @Test
    public void MUP01_CheckTextOfMyProfilePage() throws Exception {
        accountPage = new BuyerAccountPage(driver);
        accountPage.clickProfile()
                .verifyTextMyProfile();
    }
    @Test
    public void MUP02_ViewAccountInformation(){
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnAccountIcon();
        accountPage = new BuyerAccountPage(driver);
        accountPage.verifyAvatarDisplay()
                .verifyDisplayName(displayName)
                .verifyMemberShipLevel(membershipLevel)
                .tapOnBarcodeIcon()
                .verifyBarcode(barcodeNumber);
    }
    @Test
    public void MUP03_UpdateUserProfile_EmailAccount_NoBirthdayBefore(){
        new Login().loginToDashboardWithPhone("+84",sellerUsername,sellerPass);
        String emailAccount = "email"+generator.randomNumberGeneratedFromEpochTime(7)+"@mailnesia.com";
        new SignUp().signUpByMail(emailAccount,passBuyer);
        String randomNumber = generator.randomNumberGeneratedFromEpochTime(8);
        String nameEdit = "update name "+randomNumber;
        String identityCardEdit = randomNumber;
        String phoneCodeEdit = "+84";
        String phoneNumberEdit = "0"+randomNumber;
        String companyEdit = generator.generateString(7);
        String taxEdit = randomNumber;
        String birthdayEdit =generator.generateDateTime("dd-MM-yyyy");
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnAccountIcon()
                .scrollDown()
                .logOut()
                .clickLoginBtn()
                .performLogin(emailAccount,passBuyer);
        accountPage = new BuyerAccountPage(driver);
        accountPage.clickProfile()
                .inputYourName(nameEdit)
                .verifyEmailDisabled()
                .inputIdentityCard(identityCardEdit)
                .inputPhone(phoneCodeEdit,phoneNumberEdit)
                .scrollDown()
                .inputCompanyName(companyEdit)
                .inputTaxCode(taxEdit)
                .selectBirdayAsCurrentDate()
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
    @Test
    public void MUP04_UpdateUserProfile_EmailAccount_HasBirthdayBefore(){
        String randomNumber = generator.randomNumberGeneratedFromEpochTime(8);
        String nameEdit = "update name "+randomNumber;
        String identityCardEdit = randomNumber;
        String phoneCodeEdit = "+84";
        String phoneNumberEdit = "0"+randomNumber;
        String companyEdit = generator.generateString(7);
        String taxEdit = randomNumber;
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnAccountIcon()
                .scrollDown()
                .logOut()
                .clickLoginBtn()
                .performLogin(userName_EditInfo_HasBirthday,passBuyer);
        accountPage = new BuyerAccountPage(driver);
        accountPage.clickProfile()
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
    @Test
    public void MUP05_UpdateUserProfile_PhoneAccount_HasBirthdayBefore(){
        String phoneNumber = "01"+generator.randomNumberGeneratedFromEpochTime(7);
        new Login().loginToDashboardWithPhone("+84",sellerUsername,sellerPass);
        new SignUp().signUpByPhoneNumber(passBuyer,phoneNumber,"+84");
        String randomNumber = generator.randomNumberGeneratedFromEpochTime(8);
        String nameEdit = "update name "+randomNumber;
        String identityCardEdit = randomNumber;
        String emailEdit = "email"+generator.randomNumberGeneratedFromEpochTime(7)+"@mailnesia.com";
        String companyEdit = generator.generateString(7);
        String taxEdit = randomNumber;
        String birthdayEdit =generator.generateDateTime("dd-MM-yyyy");
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnAccountIcon()
                .scrollDown()
                .logOut()
                .clickLoginBtn()
                .performLogin(phoneNumber,passBuyer);
        accountPage = new BuyerAccountPage(driver);
        accountPage.clickProfile()
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
    @Test
    public void MUP06_UpdateUserProfile_PhoneAccount_HasBirthdayBefore(){
        String randomNumber = generator.randomNumberGeneratedFromEpochTime(8);
        String nameEdit = "update name "+randomNumber;
        String identityCardEdit = randomNumber;
        String emailEdit = "email"+generator.randomNumberGeneratedFromEpochTime(7)+"@mailnesia.com";
        String companyEdit = generator.generateString(7);
        String taxEdit = randomNumber;
        navigationBar = new NavigationBar(driver);
        navigationBar.tapOnAccountIcon()
                .scrollDown()
                .logOut()
                .clickLoginBtn()
                .performLogin(userName_PhoneAccount_EditInfo_HasBirthday,passBuyer);
        accountPage = new BuyerAccountPage(driver);
        accountPage.clickProfile()
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
    @Test
    public void MUP07_CheckAddress_UserHasAddressThenCheckout(){

    }
}
