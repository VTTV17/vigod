package app.android.Buyer;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Dimension;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mifmif.common.regex.Generex;

import api.Buyer.login.LoginSF;
import api.Seller.login.Login;
import api.Seller.setting.StoreLanguageAPI;
import app.Buyer.account.BuyerAccountPage;
import app.Buyer.account.BuyerMyProfile;
import app.Buyer.account.ChangePasswordPage;
import app.Buyer.buyergeneral.BuyerGeneral;
import app.Buyer.login.ForgotPasswordPage;
import app.Buyer.login.LoginPage;
import app.Buyer.navigationbar.NavigationBar;
import app.Buyer.signup.SignupPage;
import utilities.account.AccountTest;
import utilities.commons.UICommonMobile;
import utilities.data.DataGenerator;
import utilities.driver.InitAndroidDriver;
import utilities.environment.goBUYEREnvironment;
import utilities.model.LoginCredentials;
import utilities.model.dashboard.setting.languages.AdditionalLanguages;
import utilities.model.dashboard.setting.languages.translation.MobileAndroid;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.utils.ListUtils;
import utilities.utils.PropertiesUtil;

public class LoginBuyer extends BaseTest{

	BuyerAccountPage accountTab;
	NavigationBar navigationBar;
	LoginPage loginPage;
	SignupPage signupPage;
	BuyerGeneral buyerGeneral;
	UICommonMobile commonAction;
	
	LoginInformation sellerCredentialsForAPI;
	
	LoginCredentials loginAccount;
	
	AdditionalLanguages sfDisplayLanguage;
	List<MobileAndroid> translation;
	
	@BeforeClass
	void loadData() {
		loginAccount = new LoginCredentials(AccountTest.ADMIN_COUNTRY_TIEN, AccountTest.ADMIN_USERNAME_TIEN, AccountTest.ADMIN_PASSWORD_TIEN);
		
		sellerCredentialsForAPI = new Login().setLoginInformation(loginAccount).getLoginInformation();
		
		var storeLanguageAPI = new StoreLanguageAPI(sellerCredentialsForAPI);
		sfDisplayLanguage = ListUtils.getRandomListElement(storeLanguageAPI.getAdditionalLanguages().stream().filter(AdditionalLanguages::getPublished).toList());
        translation = storeLanguageAPI.getTranslation(sfDisplayLanguage.getLangCode()).getMobileAndroid();
	}	

	@BeforeMethod
	void beforeEachMethod() {
		driver = new InitAndroidDriver().getBuyerDriver(PropertiesUtil.getEnvironmentData("udidAndroidThang"), goBUYEREnvironment.goBUYERAppName_Tien);
		navigationBar = new NavigationBar(driver);
		accountTab = new BuyerAccountPage(driver);
		loginPage = new LoginPage(driver);
		signupPage = new SignupPage(driver);
		buyerGeneral = new BuyerGeneral(driver);
		commonAction = new UICommonMobile(driver);
		
		commonAction.waitSplashScreenLoaded();
	}	

	@AfterMethod(alwaysRun = true)
	void tearDown() {
		super.tearDown();
	}	

	/**
	 * Pool of existing buyer accounts
	 */
	@DataProvider
	Object[][] buyerAccountDP() {
		return new Object[][] { 
//			{AccountTest.SF_EMAIL_COUNTRY, AccountTest.SF_EMAIL_USERNAME, AccountTest.BUYER_MASTER_PASSWORD},
			{AccountTest.SF_PHONE_COUNTRY, AccountTest.SF_PHONE_USERNAME, AccountTest.BUYER_MASTER_PASSWORD},
			{AccountTest.GOMUA_EMAIL_COUNTRY, AccountTest.GOMUA_EMAIL_USERNAME, AccountTest.BUYER_MASTER_PASSWORD},
//			{AccountTest.GOMUA_PHONE_COUNTRY, AccountTest.GOMUA_PHONE_USERNAME, AccountTest.BUYER_MASTER_PASSWORD},
		};
	}

	@Test
	void TC_00_SwitchBetweenSigninAndSignupForm() {
		
		navigationBar.tapOnAccountIcon().clickLoginBtn();
		
		var username = DataGenerator.randomCorrectFormatEmail();
		loginPage.inputUsername(username).clickSignupLinkText();
		Assert.assertEquals(signupPage.getUsernameFieldValue(), username);
		commonAction.navigateBack();
		
		username = DataGenerator.randomPhone();
		loginPage.clickPhoneTab().inputUsername(username).clickSignupLinkText().clickPhoneTab();
		Assert.assertEquals(signupPage.getUsernameFieldValue(), username);
	}	
	
	@Test
	void TC_01_LoginWithInvalidData() {
		
		navigationBar.tapOnAccountIcon().changeLanguageByLangName(sfDisplayLanguage).clickLoginBtn();
		
    	loginPage.clickUsername(); //Workaround to simulate a tap on username field
    	commonAction.hideKeyboard();
		
    	/**Email account**/
		//All fields left empty
		Assert.assertFalse(loginPage.isLoginBtnEnabled());
		
		//Empty username
		loginPage.performLogin(AccountTest.SF_EMAIL_COUNTRY, "", DataGenerator.randomValidPassword());
		Assert.assertFalse(loginPage.isLoginBtnEnabled());

		//Empty password
		loginPage.performLogin(AccountTest.SF_EMAIL_COUNTRY, DataGenerator.randomCorrectFormatEmail(), "");
		Assert.assertFalse(loginPage.isLoginBtnEnabled());
		
		//Email does not have symbol @
		loginPage.performLogin(AccountTest.SF_EMAIL_COUNTRY, new Generex("[a-z]{5,8}\\d{5,8}\\.[a-z]{2}").random(), DataGenerator.randomValidPassword());
		Assert.assertEquals(signupPage.getUsernameError(), LoginPage.localizedInvalidEmailError(translation));

		//Email does not have suffix '.<>'. Eg. '.com'
		loginPage.performLogin(AccountTest.SF_EMAIL_COUNTRY, new Generex("[a-z]{5,8}\\d{5,8}\\@").random(), DataGenerator.randomValidPassword());
		Assert.assertEquals(signupPage.getUsernameError(), LoginPage.localizedInvalidEmailError(translation));
		
		loginPage.performLogin(AccountTest.SF_EMAIL_COUNTRY, new Generex("[a-z]{5,8}\\d{5,8}\\@[a-z]mail\\.").random(), DataGenerator.randomValidPassword());
		Assert.assertEquals(signupPage.getUsernameError(), LoginPage.localizedInvalidEmailError(translation));		
		
		/**Phone account**/
		loginPage.clickPhoneTab();
		
		//All fields left empty
		Assert.assertFalse(loginPage.isLoginBtnEnabled());
		
		//Empty username
		loginPage.performLogin(AccountTest.SF_EMAIL_COUNTRY, "", DataGenerator.randomValidPassword());
		Assert.assertFalse(loginPage.isLoginBtnEnabled());
		
		//Empty password
		loginPage.performLogin(AccountTest.SF_EMAIL_COUNTRY, DataGenerator.randomPhone(), "");
		Assert.assertFalse(loginPage.isLoginBtnEnabled());
		
		//7-digit phone number
		loginPage.performLogin(AccountTest.SF_EMAIL_COUNTRY, DataGenerator.generatePhoneFromRegex("\\d{7}"), DataGenerator.randomValidPassword());
		Assert.assertEquals(signupPage.getUsernameError(), LoginPage.localizedInvalidPhoneError(translation));
		
		//16-digit phone number
		loginPage.performLogin(AccountTest.SF_EMAIL_COUNTRY, DataGenerator.generatePhoneFromRegex("\\d{16}"), DataGenerator.randomValidPassword());
		Assert.assertEquals(signupPage.getUsernameError(), LoginPage.localizedInvalidPhoneError(translation));
	}	
	
	@Test
	void TC_02_LoginWithWrongCredentials() {
		
		BuyerGeneral buyerGeneral = new BuyerGeneral(driver);
		
		navigationBar.tapOnAccountIcon().changeLanguageByLangName(sfDisplayLanguage).clickLoginBtn();
		
		loginPage.performLogin(AccountTest.SF_EMAIL_COUNTRY, DataGenerator.randomCorrectFormatEmail(), DataGenerator.randomValidPassword());
		Assert.assertEquals(buyerGeneral.getToastMessage(), LoginPage.localizedWrongEmailOrPasswordError(translation));
		
		loginPage.performLogin(AccountTest.SF_EMAIL_COUNTRY, DataGenerator.randomPhone(), DataGenerator.randomValidPassword());
		Assert.assertEquals(buyerGeneral.getToastMessage(), LoginPage.localizedWrongPhoneOrPasswordError(translation));
	}	

	@Test(dataProvider = "buyerAccountDP")
	void TC_03_LoginWithCorrectCredentials(String country, String username, String password) {
		
		navigationBar.tapOnAccountIcon().changeLanguageByLangName(sfDisplayLanguage).clickLoginBtn();
		
		loginPage.performLogin(country, username, password);
		
    	accountTab.clickProfile();
	}

	@Test(dataProvider = "buyerAccountDP")
	void TC_04_ChangePasswordWithInvalidData(String country, String username, String password) {
		
		navigationBar.tapOnAccountIcon().clickLoginBtn().performLogin(country, username, password);
		
		accountTab.waitUntilScreenIsReady().changeLanguageByLangName(sfDisplayLanguage);
		
		BuyerMyProfile myProfilePage = accountTab.clickProfile().waitUntilScreenIsReady();
		
		commonAction.swipeByCoordinatesInPercent(0.5, 0.9, 0.5, 0.1);
		
		//When leaving the fields empty, no validation errors are seen. The button is disabled
		
		//Wrong current password
		var error = myProfilePage.clickChangePassword()
			.inputCurrentPassword(DataGenerator.randomValidPassword())
			.inputNewPassword(DataGenerator.randomValidPassword())
			.clickChangePasswordDoneBtn()
			.getCurrentPasswordError();
		Assert.assertEquals(error, ChangePasswordPage.localizedWrongCurrentPasswordError(translation));
		
		commonAction.navigateBack();
		
		//New password lacks special characters
		error = myProfilePage.clickChangePassword()
			.inputCurrentPassword(password)
			.clickNewPassword()
			.inputNewPassword(new Generex("[a-z]{7}\\d").random())
			.clickChangePasswordDoneBtn()
			.getNewPasswordError();
		Assert.assertEquals(error, ChangePasswordPage.localizedInvalidPasswordFormatError(translation));
		
		commonAction.hideKeyboard();
		commonAction.navigateBack();
		
		//New password lacks digits
		error = myProfilePage.clickChangePassword()
			.inputCurrentPassword(password)
			.clickNewPassword()
			.inputNewPassword(new Generex("[a-z]{7}[!#@]").random())
			.clickChangePasswordDoneBtn()
			.getNewPasswordError();
		Assert.assertEquals(error, ChangePasswordPage.localizedInvalidPasswordFormatError(translation));
		
		commonAction.hideKeyboard();
		commonAction.navigateBack();
		
		//New password doesn't meet minimum number of characters
		error = myProfilePage.clickChangePassword()
			.inputCurrentPassword(password)
			.clickNewPassword()
			.inputNewPassword(new Generex("[a-z]{4}\\d{2}[!#@]").random())
			.clickChangePasswordDoneBtn()
			.getNewPasswordError();
		Assert.assertEquals(error, ChangePasswordPage.localizedInvalidPasswordFormatError(translation));
		
		commonAction.hideKeyboard();
		commonAction.navigateBack();
		
		//New password matches the last 4 old passwords
		String newPassword = DataGenerator.randomValidPassword();
		var loginSFAPI = new LoginSF(sellerCredentialsForAPI);
		String currentPassword = "";
		List<String> oldPasswords = new ArrayList<String>();
		
		for (int i=0; i<5; i++) {
			currentPassword = (i==0) ? password : newPassword;
			
			newPassword = (i==4) ? password : DataGenerator.randomValidPassword();
			
			loginSFAPI.changePassword(username, currentPassword, DataGenerator.getPhoneCode(country), newPassword);
			
    		if (!List.of(0, 4).contains(i)) oldPasswords.add(newPassword); //First and last changed passwords aren't added to the list
		}		
		
		for (String pw : oldPasswords) {
			myProfilePage.clickChangePassword()
				.inputCurrentPassword(password)
				.inputNewPassword(pw)
				.clickChangePasswordDoneBtn();
			Assert.assertEquals(buyerGeneral.getToastMessage(), ChangePasswordPage.localizedNewPasswordMatchLastOld4PasswordError(translation));
			commonAction.navigateBack();
		}		
	}
	
	@Test(dataProvider = "buyerAccountDP")
	void TC_05_ChangePasswordWithValidData(String country, String username, String password) {
		
		String newPassword = DataGenerator.randomValidPassword();
		
		navigationBar.tapOnAccountIcon().clickLoginBtn();
		
		loginPage.performLogin(country, username, password);
		
		BuyerMyProfile myProfilePage = accountTab.clickProfile().waitUntilScreenIsReady();
		
		//Change password
		commonAction.swipeByCoordinatesInPercent(0.5, 0.9, 0.5, 0.1);
		myProfilePage.clickChangePassword()
		.inputCurrentPassword(password)
		.inputNewPassword(newPassword)
		.clickChangePasswordDoneBtn();
		
		//Logout
		myProfilePage.tapOnBackIcon();
		accountTab.logOutOfApp();
		
		//Login with new password
		accountTab.clickLoginBtn().performLogin(country, username, newPassword);
		accountTab.clickProfile();

		//Change password back to the first password
		var loginSFAPI = new LoginSF(sellerCredentialsForAPI);
		String currentPassword = "";
		for (int i=0; i<4; i++) {
			currentPassword = newPassword;
			newPassword = (i==3) ? password : DataGenerator.randomValidPassword();
			loginSFAPI.changePassword(username, currentPassword, DataGenerator.getPhoneCode(country), newPassword);
		}
	}
	
	@Test
	void TC_06_ForgotPasswordForNonExistingAccount() {
		navigationBar.tapOnAccountIcon().changeLanguageByLangName(sfDisplayLanguage).clickLoginBtn();
		
		//Email account
		String error = loginPage.clickForgotPasswordLink()
			.inputUsername(DataGenerator.randomCorrectFormatEmail())
			.inputNewPassword(DataGenerator.randomValidPassword())
			.clickContinueBtn()
			.getUsernameError();
		
		Assert.assertEquals(error, ForgotPasswordPage.localizedEmailNotExistError(translation));
		
		commonAction.navigateBack();
		
		//Phone account
		error = loginPage.clickPhoneTab().clickForgotPasswordLink()
			.inputUsername(DataGenerator.randomPhone())
			.inputNewPassword(DataGenerator.randomValidPassword())
			.clickContinueBtn()
			.getUsernameError();
		Assert.assertEquals(error, ForgotPasswordPage.localizedPhoneNotExistError(translation));
	}

	//TODO Login with Facebook functionality => Left for manual testing
	//TODO Buyers logging in with Facebook can't change password nor reset password on Storefront => Left for manual testing	
	
}
