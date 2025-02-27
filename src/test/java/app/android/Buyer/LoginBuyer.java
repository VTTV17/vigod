package app.android.Buyer;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mifmif.common.regex.Generex;

import api.Seller.login.Login;
import api.Seller.setting.StoreLanguageAPI;
import app.Buyer.account.BuyerAccountPage;
import app.Buyer.buyergeneral.BuyerGeneral;
import app.Buyer.login.LoginPage;
import app.Buyer.navigationbar.NavigationBar;
import app.Buyer.signup.SignupPage;
import utilities.account.AccountTest;
import utilities.commons.UICommonMobile;
import utilities.data.DataGenerator;
import utilities.driver.InitAndroidDriver;
import utilities.environment.goBUYEREnvironment;
import utilities.model.dashboard.setting.languages.AdditionalLanguages;
import utilities.model.dashboard.setting.languages.translation.MobileAndroid;
import utilities.model.sellerApp.login.LoginInformation;
import utilities.utils.PropertiesUtil;

public class LoginBuyer extends BaseTest{

	BuyerAccountPage accountTab;
	NavigationBar navigationBar;
	LoginPage loginPage;
	SignupPage signupPage;
	BuyerGeneral buyerGeneral;
	UICommonMobile commonAction;
	
	
	String sellerCountry, sellerUsername, sellerPassword;
	LoginInformation sellerCredentials;
	
	AdditionalLanguages sfDisplayLanguage;
	String sfLanguageCode, sfLanguageName;
	List<MobileAndroid> translation;
	
	@BeforeClass
	void loadData() {
		sellerCountry = AccountTest.ADMIN_COUNTRY_TIEN;
		sellerUsername = AccountTest.ADMIN_USERNAME_TIEN;
		sellerPassword = AccountTest.ADMIN_PASSWORD_TIEN;
		
		sellerCredentials = new Login().setLoginInformation(DataGenerator.getPhoneCode(sellerCountry), sellerUsername, sellerPassword).getLoginInformation();
		
		var storeLanguageAPI = new StoreLanguageAPI(sellerCredentials);
		sfDisplayLanguage = randomSFDisplayLanguage(storeLanguageAPI.getAdditionalLanguages());
		sfLanguageName = sfDisplayLanguage.getLangName();
        sfLanguageCode = sfDisplayLanguage.getLangCode();
        translation = storeLanguageAPI.getTranslation(sfLanguageCode).getMobileAndroid();
	}	

	AdditionalLanguages randomSFDisplayLanguage(List<AdditionalLanguages> publishedLanguages) {
        return publishedLanguages.stream()
        		.filter(AdditionalLanguages::getPublished)
        		.collect(Collectors.collectingAndThen(Collectors.toList(), collected -> collected.get(new Random().nextInt(collected.size()))));
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
			{AccountTest.SF_EMAIL_COUNTRY, AccountTest.SF_EMAIL_USERNAME, AccountTest.BUYER_MASTER_PASSWORD},
			{AccountTest.SF_PHONE_COUNTRY, AccountTest.SF_PHONE_USERNAME, AccountTest.BUYER_MASTER_PASSWORD},
			{AccountTest.GOMUA_EMAIL_COUNTRY, AccountTest.GOMUA_EMAIL_USERNAME, AccountTest.BUYER_MASTER_PASSWORD},
			{AccountTest.GOMUA_PHONE_COUNTRY, AccountTest.GOMUA_PHONE_USERNAME, AccountTest.BUYER_MASTER_PASSWORD},
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
    	commonAction.hideKeyboard("android");
		
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
		
		navigationBar.tapOnAccountIcon().changeLanguageByLangName(sfDisplayLanguage).clickLoginBtn();
		
		loginPage.performLogin(AccountTest.SF_EMAIL_COUNTRY, DataGenerator.randomCorrectFormatEmail(), DataGenerator.randomValidPassword());
		Assert.assertEquals(buyerGeneral.getToastMessage(), LoginPage.localizedWrongEmailOrPasswordError(translation));
		
		loginPage.performLogin(AccountTest.SF_EMAIL_COUNTRY, DataGenerator.randomPhone(), DataGenerator.randomValidPassword());
		Assert.assertEquals(buyerGeneral.getToastMessage(), LoginPage.localizedWrongPhoneOrPasswordError(translation));
	}	

	@Test(dataProvider = "buyerAccountDP")
	void TC_03_LoginWithCorrectCredentials(String country, String username, String password) {
		
		navigationBar.tapOnAccountIcon().clickLoginBtn();
		
		loginPage.performLogin(country, username, password);
		
		accountTab.clickProfile();
	}
	
}
