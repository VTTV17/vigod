package web.StoreFront;

import static utilities.links.Links.SF_DOMAIN;
import static utilities.links.Links.SF_DOMAIN_BIZ;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.mifmif.common.regex.Generex;

import api.Buyer.login.LoginSF;
import api.Seller.login.Login;
import api.Seller.setting.StoreInformation;
import api.Seller.setting.StoreLanguageAPI;
import utilities.account.AccountTest;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.enums.DisplayLanguage;
import utilities.enums.Domain;
import utilities.model.dashboard.setting.languages.AdditionalLanguages;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.customers.allcustomers.AllCustomers;
import web.Dashboard.customers.allcustomers.details.CustomerDetails;
import web.StoreFront.header.ChangePasswordDialog;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.login.ForgotPasswordDialog;
import web.StoreFront.login.LoginPage;

public class RefactoredLoginSF extends BaseTest {
	
	GeneralSF generalSFAction;
	LoginPage loginPage;
	HeaderSF headerSection;

	String sellerCountry, sellerUsername, sellerPassword, sellerSFURL, sfDomain;
	LoginInformation sellerCredentials;
	
	List<AdditionalLanguages> publishedLanguages;
	
	@BeforeClass
	void loadData() {
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			sellerCountry = AccountTest.ADMIN_COUNTRY_TIEN;
			sellerUsername = AccountTest.ADMIN_USERNAME_TIEN;
			sellerPassword = AccountTest.ADMIN_PASSWORD_TIEN;
			sfDomain = SF_DOMAIN;
		} else {
			sellerCountry = AccountTest.ADMIN_PHONE_BIZ_COUNTRY;
			sellerUsername = AccountTest.ADMIN_PHONE_BIZ_USERNAME;
			sellerPassword = AccountTest.ADMIN_PHONE_BIZ_PASSWORD;
			sfDomain = SF_DOMAIN_BIZ;
		}
		
		sellerCredentials = new Login().setLoginInformation(DataGenerator.getPhoneCode(sellerCountry), sellerUsername, sellerPassword).getLoginInformation();
		
        publishedLanguages = new StoreLanguageAPI(sellerCredentials).getAdditionalLanguages();
		
		sellerSFURL = "https://%s".formatted(new StoreInformation(sellerCredentials).getInfo().getStoreURL() + sfDomain);
	}
	
	@BeforeMethod
	void instantiatePageObjects() {
		driver = new InitWebdriver().getDriver(browser, headless);
		generalSFAction = new GeneralSF(driver);
		headerSection = new HeaderSF(driver);
		loginPage = new LoginPage(driver);
		commonAction = new UICommonAction(driver);
	}	

	String randomSFDisplayLanguage() {
        return publishedLanguages.stream()
        		.filter(AdditionalLanguages::getPublished)
        		.map(AdditionalLanguages::getLangCode)
        		.collect(Collectors.collectingAndThen(Collectors.toList(), collected -> collected.get(new Random().nextInt(collected.size()))));
	}	
	
	@Test
	void TC_LoginWithInvalidData() {
		
		String langCode = randomSFDisplayLanguage();
		
		DisplayLanguage localizedLanguage = langCode.startsWith("vi") ? DisplayLanguage.VIE : DisplayLanguage.ENG;
		
		generalSFAction.navigateToURL(sellerSFURL);
		
		headerSection.clickUserInfoIcon().changeLanguageByLangCode(langCode);
		
		//Empty username
		loginPage.performLogin("", DataGenerator.randomValidPassword());
		Assert.assertEquals(loginPage.getUsernameError(), LoginPage.localizedEmptyUsernameError(localizedLanguage));
		commonAction.refreshPage();
		
		//Empty password
		loginPage.performLogin(DataGenerator.randomPhone(), "");
		Assert.assertEquals(loginPage.getPasswordError(), LoginPage.localizedEmptyPasswordError(localizedLanguage));
		commonAction.refreshPage();
		
		//Empty username and password
		loginPage.performLogin("", "");
		Assert.assertEquals(loginPage.getUsernameError(), LoginPage.localizedEmptyUsernameError(localizedLanguage));
		Assert.assertEquals(loginPage.getPasswordError(), LoginPage.localizedEmptyPasswordError(localizedLanguage));
		commonAction.refreshPage();
		
		//7-digit phone number
		loginPage.performLogin(DataGenerator.generatePhoneFromRegex("\\d{7}"), DataGenerator.randomValidPassword());
		Assert.assertEquals(loginPage.getUsernameError(), LoginPage.localizedInvalidUsernameError(DisplayLanguage.valueOf(language)));
		commonAction.refreshPage();
		
		//16-digit phone number
		loginPage.performLogin(DataGenerator.generatePhoneFromRegex("\\d{7}"), DataGenerator.randomValidPassword());
		Assert.assertEquals(loginPage.getUsernameError(), LoginPage.localizedInvalidUsernameError(DisplayLanguage.valueOf(language)));
		commonAction.refreshPage();
		
		//Mail does not have symbol @
		loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\.[a-z]{2}").random(), DataGenerator.randomValidPassword());
		Assert.assertEquals(loginPage.getUsernameError(), LoginPage.localizedInvalidUsernameError(DisplayLanguage.valueOf(language)));
		commonAction.refreshPage();
		
		//Mail does not have suffix '.<>'. Eg. '.com'
		loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\@").random(), DataGenerator.randomValidPassword());
		Assert.assertEquals(loginPage.getUsernameError(), LoginPage.localizedInvalidUsernameError(DisplayLanguage.valueOf(language)));
		commonAction.refreshPage();
		
		loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\@[a-z]mail\\.").random(), DataGenerator.randomValidPassword());
		Assert.assertEquals(loginPage.getUsernameError(), LoginPage.localizedInvalidUsernameError(DisplayLanguage.valueOf(language)));
	}
	
	@Test
	void TC_LoginWithWrongCredentials() {
		
		String langCode = randomSFDisplayLanguage();
		
		DisplayLanguage localizedLanguage = langCode.startsWith("vi") ? DisplayLanguage.VIE : DisplayLanguage.ENG;
		
		generalSFAction.navigateToURL(sellerSFURL);
		
		headerSection.clickUserInfoIcon().changeLanguageByLangCode(langCode);
		
		//Email
		loginPage.performLogin(DataGenerator.randomCorrectFormatEmail(), DataGenerator.randomValidPassword());
		Assert.assertEquals(loginPage.getLoginFailError(), LoginPage.localizedWrongCredentialsError(localizedLanguage));
		commonAction.refreshPage();
		
		//Mobile
		loginPage.performLogin(DataGenerator.randomPhone(), DataGenerator.randomValidPassword());
		Assert.assertEquals(loginPage.getLoginFailError(), LoginPage.localizedWrongCredentialsError(localizedLanguage));
	}
	
	@DataProvider
	Object[][] accounts() {
		return new Object[][] { 
			{AccountTest.ADMIN_COUNTRY_TIEN, AccountTest.SF_USERNAME_VI_1, AccountTest.SF_SHOP_VI_PASSWORD},
			{AccountTest.ADMIN_COUNTRY_TIEN, AccountTest.SF_USERNAME_PHONE_VI_1, AccountTest.SF_SHOP_VI_PASSWORD},
			{AccountTest.ADMIN_COUNTRY_TIEN, AccountTest.GOMUA_USERNAME_EMAIL, AccountTest.ADMIN_PASSWORD_TIEN},
			{AccountTest.ADMIN_COUNTRY_TIEN, AccountTest.GOMUA_USERNAME_PHONE, AccountTest.ADMIN_PASSWORD_TIEN},
		};
	}	
	@Test(dataProvider = "accounts")
	void TC_LoginWithCorrectCredentials(String country, String username, String password) {
		
		generalSFAction.navigateToURL(sellerSFURL);
		
		loginPage.performLogin(country, username, password);
		
		var myAccountPage = headerSection.clickUserInfoIcon().clickUserProfile().clickMyAccountSection();
		
		var displayName = myAccountPage.getDisplayName();
		var phone = myAccountPage.getPhoneNumber();
		var email = myAccountPage.getEmail();
		
		new web.Dashboard.login.LoginPage(driver, Domain.valueOf(domain)).navigate()
			.performValidLogin(sellerCountry, sellerUsername, sellerPassword);
		
		CustomerDetails customerDetailPage = new AllCustomers(driver, Domain.valueOf(domain)).navigateUsingURL()
				.inputSearchTerm(displayName).clickUser(displayName);
		
		var actualDashboardEmail = customerDetailPage.getEmail();
		var actualDashboardPhone = customerDetailPage.getPhoneNumber();
		
		if (username.matches("\\d+")) {
			Assert.assertEquals(actualDashboardPhone, phone);
		} else {
			Assert.assertEquals(actualDashboardEmail, email);
		}
	}

	@Test(dataProvider = "accounts")
	void TC_ChangeInvalidPassword(String country, String username, String password) {
		
		String langCode = randomSFDisplayLanguage();
		
		DisplayLanguage localizedLanguage = langCode.startsWith("vi") ? DisplayLanguage.VIE : DisplayLanguage.ENG;
		
		generalSFAction.navigateToURL(sellerSFURL);
		
		headerSection.clickUserInfoIcon().changeLanguageByLangCode(langCode);
		
		loginPage.performLogin(country, username, password);
		
		//Empty current password
		String error = headerSection.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword("")
				.inputNewPassword(DataGenerator.randomValidPassword())
				.clickDoneBtn()
				.getCurrentPasswordError();
		
		Assert.assertEquals(error, ForgotPasswordDialog.localizedWrongCurrentPasswordError(localizedLanguage));
		commonAction.refreshPage();
		
		//Empty new password
		error = headerSection.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword(DataGenerator.randomValidPassword())
				.inputNewPassword("")
				.clickDoneBtn()
				.getNewPasswordError();
		
		Assert.assertEquals(error, LoginPage.localizedEmptyPasswordError(localizedLanguage));
		commonAction.refreshPage();
		
		//Incorrect current password
		error = headerSection.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword(DataGenerator.randomValidPassword())
				.inputNewPassword(DataGenerator.randomValidPassword())
				.clickDoneBtn()
				.getCurrentPasswordError();
		
		Assert.assertEquals(error, ForgotPasswordDialog.localizedWrongCurrentPasswordError(localizedLanguage));
		commonAction.refreshPage();
		
		//Inadequate number of characters
		error = headerSection.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword(password)
				.inputNewPassword(new Generex("[a-z]{4}\\d{2}[!#@]").random())
				.clickDoneBtn()
				.getNewPasswordError();
		
		Assert.assertEquals(error, ForgotPasswordDialog.localizedInvalidNewPasswordError(localizedLanguage));
		commonAction.refreshPage();	
		
		//Absence of digits
		error = headerSection.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword(password)
				.inputNewPassword(new Generex("[a-z]{7}[!#@]").random())
				.clickDoneBtn()
				.getNewPasswordError();
		
		Assert.assertEquals(error, ForgotPasswordDialog.localizedInvalidNewPasswordError(localizedLanguage));
		commonAction.refreshPage();	
		
		//Absence of special characters
		error = headerSection.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword(password)
				.inputNewPassword(new Generex("[a-z]{7}\\d").random())
				.clickDoneBtn()
				.getNewPasswordError();
		
		Assert.assertEquals(error, ForgotPasswordDialog.localizedInvalidNewPasswordError(localizedLanguage));
//		commonAction.refreshPage();	
		
	}	
	
	@Test(dataProvider = "accounts")
	void TC_ChangeValidPassword(String country, String username, String password) {
		
		String newPassword = DataGenerator.randomValidPassword();
		
		String langCode = randomSFDisplayLanguage();
		
		DisplayLanguage localizedLanguage = langCode.startsWith("vi") ? DisplayLanguage.VIE : DisplayLanguage.ENG;
		
		generalSFAction.navigateToURL(sellerSFURL);
		
		headerSection.clickUserInfoIcon().changeLanguageByLangCode(langCode);
		
		loginPage.performLogin(country, username, password);
		
		//Change password
		headerSection.clickUserInfoIcon().clickChangePassword().inputCurrentPassword(password).inputNewPassword(newPassword).clickDoneBtn();
		
		//Try logging in with old password
		loginPage.performLogin(country, username, password);
		Assert.assertEquals(loginPage.getLoginFailError(), LoginPage.localizedWrongCredentialsError(localizedLanguage));
		
		commonAction.refreshPage();
		
		//Login with new password
		loginPage.performLogin(country, username, newPassword);		
		
		//Change password back to the first password
		var loginSFAPI = new LoginSF(sellerCredentials);
		String currentPassword = "";
		for (int i=0; i<4; i++) {
			currentPassword = newPassword;
			newPassword = (i==3) ? password : DataGenerator.randomValidPassword();
			loginSFAPI.changePassword(username, currentPassword, DataGenerator.getPhoneCode(country), newPassword);
		}
	}	
	
	@Test(dataProvider = "accounts")
	void TC_ChangePasswordThatResemblesLast4Passwords(String country, String username, String password) {
		
		String langCode = randomSFDisplayLanguage();
		
		DisplayLanguage localizedLanguage = langCode.startsWith("vi") ? DisplayLanguage.VIE : DisplayLanguage.ENG;
		
		String newPassword = DataGenerator.randomValidPassword();
		
		//Change password 5 times
		var loginSFAPI = new LoginSF(sellerCredentials);
		String currentPassword = "";
		List<String> oldPasswords = new ArrayList<String>(); 
		for (int i=0; i<5; i++) {
			currentPassword = (i==0) ? password : newPassword;
			
			newPassword = (i==4) ? password : DataGenerator.randomValidPassword();
			
			loginSFAPI.changePassword(username, currentPassword, DataGenerator.getPhoneCode(country), newPassword);
			
    		if (!List.of(0, 4).contains(i)) oldPasswords.add(newPassword); //First and last changed passwords aren't added to the list
		}
		
		generalSFAction.navigateToURL(sellerSFURL);
		
		loginPage.performLogin(country, username, password);
		
		headerSection.clickUserInfoIcon().changeLanguageByLangCode(langCode);
		
		//Change password to the latest 4 passwords
		for (String pw : oldPasswords) {
			ChangePasswordDialog changePasswordDlg = headerSection.clickUserInfoIcon().clickChangePassword().inputCurrentPassword(newPassword).inputNewPassword(pw).clickDoneBtn();
			Assert.assertEquals(changePasswordDlg.getCurrentPasswordError(), ForgotPasswordDialog.localizedSame4PasswordsError(localizedLanguage));
			changePasswordDlg.clickCloseBtn();
		}

	}	
	
    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
        driver.quit();
    }	
	
}
