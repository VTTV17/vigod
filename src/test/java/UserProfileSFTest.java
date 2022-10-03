import org.testng.annotations.Test;
import pages.Mailnesia;
import pages.gomua.headergomua.HeaderGoMua;
import pages.gomua.logingomua.LoginGoMua;
import pages.gomua.myprofile.MyProfileGoMua;
import pages.storefront.HeaderSF;
import pages.storefront.LoginPage;
import pages.storefront.SignupPage;
import pages.storefront.userprofile.MyAccount.MyAccount;
import pages.storefront.userprofile.userprofileinfo.UserProfileInfo;
import utilities.database.InitConnection;

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
    Mailnesia mailnesia;
    String userName = "qcgosell01@gmail.com";
    String passWord = "Psso12!@";
    String displayName = "qcgosell01";
    String membershipLevel = "Thanh Vien vang";
    String barcodeNumber = "505611";
    String userName_EditInfo_HasBirthday = "qcgosell0033@mailnesia.com";
    String displayName_Edit = "";
    String phoneNumber_Edit = "";
    String gender_Edit = "";
    String buyerDisplayName_Signup ="";
    String buyerAccount_Signup = "";
    String shopDomain = SF_ShopVi;
    String email_Edit = "";
    String birthday_Edit = "20/10/1999";
    public void goToUserProfile(String userName){
        loginSF = new LoginPage(driver);
        loginSF.navigate(shopDomain)
                .performLogin(userName,passWord);
        headerSF = new HeaderSF(driver);
        headerSF.navigateToUserProfile();
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
    public void UP02_BH_1290_BH_4605_UpdateUserProfile_HasBirthdayBefore_EmailAccount(){
        goToUserProfile(userName_EditInfo_HasBirthday);
        userProfileInfo = new UserProfileInfo(driver);
        userProfileInfo.clickMyAccountSection();
        myAccount = new MyAccount(driver);
        displayName_Edit = generate.generateString(10);
        phoneNumber_Edit = "01"+generate.generateNumber(9);
        gender_Edit = myAccount.editGender();
        myAccount.inputFullName(displayName_Edit)
                .inputPhoneNumber(phoneNumber_Edit)
                .clickOnSaveButton()
                .verifyBirthdayDisabled();
        myAccount.verifyDisplayName(displayName_Edit)
                .verifyPhoneNumber("+84:"+phoneNumber_Edit)
                .verifyGender(gender_Edit)
                .verifyEmailDisabled();
        headerGoMua = new HeaderGoMua(driver);
        headerGoMua.navigateToGoMua()
                .clickOnLogInBTN();
        loginGoMua = new LoginGoMua(driver);
        loginGoMua.loginWithUserName(userName_EditInfo_HasBirthday,passWord);
        headerGoMua = new HeaderGoMua(driver);
        headerGoMua.changeLanguage("English")
                .goToMyProfile();
        myProfileGoMua = new MyProfileGoMua(driver);
        myProfileGoMua.clickOnEditProfile()
                .verifyDisplayName(displayName_Edit)
                .verifyPhoneNumber("+84 "+phoneNumber_Edit)
                .verifyGender(gender_Edit);
    }
    @Test
    public void UP03_BH_1290_UpdateUserProfile_NoBirthdayBefore() throws SQLException {
        String generateName = generate.generateString(10);
        buyerAccount_Signup = generateName + "@mailnesia.com";
        buyerDisplayName_Signup = generateName;
        signupSF = new SignupPage(driver);
        signupSF.navigate().signUpWithEmail("Vietnam",buyerAccount_Signup,passWord,buyerDisplayName_Signup,"");
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
        buyerAccount_Signup = "01"+generate.generateNumber(9);
        email_Edit = generateName + "@mailnesia.com";
        buyerDisplayName_Signup = generateName;
        signupSF = new SignupPage(driver);
        signupSF.navigate(SF_ShopVi).signUpWithPhoneNumber("Vietnam",buyerAccount_Signup,passWord,buyerDisplayName_Signup,"");
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
    public void UP_05_BH_7850_CheckUserHasAddressBefore(){

    }
}
