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
import utilities.api.thirdparty.KibanaAPI;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.driver.InitWebdriver;
import utilities.enums.Domain;
import utilities.model.dashboard.setting.languages.AdditionalLanguages;
import utilities.model.dashboard.setting.languages.translation.StorefrontCSR;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.customers.allcustomers.AllCustomers;
import web.Dashboard.customers.allcustomers.details.CustomerDetails;
import web.StoreFront.header.ChangePasswordDialog;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.login.LoginPage;

/**
 * This suite tests Sign-in, Forgot password and Change password functionalities on Storefront
 */
public class RefactoredLoginSF extends BaseTest {
	
	GeneralSF generalSFAction;
	LoginPage loginPage;
	HeaderSF headerSection;

	String sellerCountry, sellerUsername, sellerPassword, sellerSFURL, sfDomain;
	LoginInformation sellerCredentials;
	
	String sfDisplayLanguage;
	List<StorefrontCSR> translation;
	
	@BeforeClass
	void loadData() {
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			sellerCountry = AccountTest.ADMIN_COUNTRY_TIEN;
			sellerUsername = AccountTest.ADMIN_USERNAME_TIEN;
			sellerPassword = AccountTest.ADMIN_PASSWORD_TIEN;
			sfDomain = SF_DOMAIN;
		} else {
			sellerCountry = AccountTest.ADMIN_MAIL_BIZ_COUNTRY;
			sellerUsername = AccountTest.ADMIN_MAIL_BIZ_USERNAME;
			sellerPassword = AccountTest.ADMIN_MAIL_BIZ_PASSWORD;
			sfDomain = SF_DOMAIN_BIZ;
		}
		
		sellerCredentials = new Login().setLoginInformation(DataGenerator.getPhoneCode(sellerCountry), sellerUsername, sellerPassword).getLoginInformation();
		
		var storeLanguageAPI = new StoreLanguageAPI(sellerCredentials);
        sfDisplayLanguage = randomSFDisplayLanguage(storeLanguageAPI.getAdditionalLanguages());
        translation = storeLanguageAPI.getTranslation(sfDisplayLanguage).getStorefrontCSR();
		
		sellerSFURL = "https://%s".formatted(new StoreInformation(sellerCredentials).getInfo().getStoreURL() + sfDomain);
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
	
	@BeforeMethod
	void instantiatePageObjects() {
		driver = new InitWebdriver().getDriver(browser, headless);
		generalSFAction = new GeneralSF(driver);
		headerSection = new HeaderSF(driver);
		loginPage = new LoginPage(driver);
		commonAction = new UICommonAction(driver);
	}	

	String randomSFDisplayLanguage(List<AdditionalLanguages> publishedLanguages) {
        return publishedLanguages.stream()
        		.filter(AdditionalLanguages::getPublished)
        		.map(AdditionalLanguages::getLangCode)
        		.collect(Collectors.collectingAndThen(Collectors.toList(), collected -> collected.get(new Random().nextInt(collected.size()))));
	}	
	
	String localizedText(List<StorefrontCSR> translation, String key) {
		return translation.stream()
				.filter(e -> e.getKey().contentEquals(key))
				.findFirst()
				.map(e -> e.getValue())
				.orElse("");
	}
	String localizedEmptyUsernameError() {
		return localizedText(translation, "gosell.welcome.required.username");
	}
	String localizedEmptyPasswordError() {
		return localizedText(translation, "gosell.welcome.required.pwd");
	}
	String localizedInvalidUsernameError() {
		return localizedText(translation, "gosell.welcome.invalid.username");
	}
	String localizedWrongCredentialsError() {
		return localizedText(translation, "gosell.welcome.loginFail");
	}
	String localizedWrongCurrentPasswordError() {
		return localizedText(translation, "gosell.welcome.invalid.password");
	}
	String localizedInvalidNewPasswordError() {
		return localizedText(translation, "gosell.welcome.invalid.pwd");
	}
	String localizedSame4PasswordsError() {
		return localizedText(translation, "gosell.error.pwd.matchCurrent");
	}
	String localizedEmailNotExistError() {
		return localizedText(translation, "gosell.welcome.notexist.email");
	}
	String localizedPhoneNotExistError() {
		return localizedText(translation, "gosell.welcome.notexist.phone");
	}

	@Test
	void TC_LoginWithInvalidData() {
		
		generalSFAction.navigateToURL(sellerSFURL);
		
		headerSection.clickUserInfoIcon().changeLanguageByLangCode(sfDisplayLanguage);
		
		//Empty username
		loginPage.performLogin("", DataGenerator.randomValidPassword());
		Assert.assertEquals(loginPage.getUsernameError(), localizedEmptyUsernameError());
		commonAction.refreshPage();
		
		//Empty password
		loginPage.performLogin(DataGenerator.randomPhone(), "");
		Assert.assertEquals(loginPage.getPasswordError(), localizedEmptyPasswordError());
		commonAction.refreshPage();
		
		//Empty username and password
		loginPage.performLogin("", "");
		Assert.assertEquals(loginPage.getUsernameError(), localizedEmptyUsernameError());
		Assert.assertEquals(loginPage.getPasswordError(), localizedEmptyPasswordError());
		commonAction.refreshPage();
		
		//7-digit phone number
		loginPage.performLogin(DataGenerator.generatePhoneFromRegex("\\d{7}"), DataGenerator.randomValidPassword());
		Assert.assertEquals(loginPage.getUsernameError(), localizedInvalidUsernameError());
		commonAction.refreshPage();
		
		//16-digit phone number
		loginPage.performLogin(DataGenerator.generatePhoneFromRegex("\\d{7}"), DataGenerator.randomValidPassword());
		Assert.assertEquals(loginPage.getUsernameError(), localizedInvalidUsernameError());
		commonAction.refreshPage();
		
		//Mail does not have symbol @
		loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\.[a-z]{2}").random(), DataGenerator.randomValidPassword());
		Assert.assertEquals(loginPage.getUsernameError(), localizedInvalidUsernameError());
		commonAction.refreshPage();
		
		//Mail does not have suffix '.<>'. Eg. '.com'
		loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\@").random(), DataGenerator.randomValidPassword());
		Assert.assertEquals(loginPage.getUsernameError(), localizedInvalidUsernameError());
		commonAction.refreshPage();
		
		loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\@[a-z]mail\\.").random(), DataGenerator.randomValidPassword());
		Assert.assertEquals(loginPage.getUsernameError(), localizedInvalidUsernameError());
	}
	
	@Test
	void TC_LoginWithWrongCredentials() {
		
		generalSFAction.navigateToURL(sellerSFURL);
		
		headerSection.clickUserInfoIcon().changeLanguageByLangCode(sfDisplayLanguage);
		
		//Email
		loginPage.performLogin(DataGenerator.randomCorrectFormatEmail(), DataGenerator.randomValidPassword());
		Assert.assertEquals(loginPage.getLoginFailError(), localizedWrongCredentialsError());
		commonAction.refreshPage();
		
		//Mobile
		loginPage.performLogin(DataGenerator.randomPhone(), DataGenerator.randomValidPassword());
		Assert.assertEquals(loginPage.getLoginFailError(), localizedWrongCredentialsError());
	}
	
	@Test(dataProvider = "buyerAccountDP")
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

	@Test(dataProvider = "buyerAccountDP")
	void TC_ChangeInvalidPassword(String country, String username, String password) {
		
		generalSFAction.navigateToURL(sellerSFURL);
		
		loginPage.performLogin(country, username, password);
		
		headerSection.clickUserInfoIcon().changeLanguageByLangCode(sfDisplayLanguage);
		
		//Empty current password
		String error = headerSection.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword("")
				.inputNewPassword(DataGenerator.randomValidPassword())
				.clickDoneBtn()
				.getCurrentPasswordError();
		
		Assert.assertEquals(error, localizedWrongCurrentPasswordError());
		commonAction.refreshPage();
		
		//Empty new password
		error = headerSection.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword(DataGenerator.randomValidPassword())
				.inputNewPassword("")
				.clickDoneBtn()
				.getNewPasswordError();
		
		Assert.assertEquals(error, localizedEmptyPasswordError());
		commonAction.refreshPage();
		
		//Incorrect current password
		error = headerSection.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword(DataGenerator.randomValidPassword())
				.inputNewPassword(DataGenerator.randomValidPassword())
				.clickDoneBtn()
				.getCurrentPasswordError();
		
		Assert.assertEquals(error, localizedWrongCurrentPasswordError());
		commonAction.refreshPage();
		
		//Inadequate number of characters
		error = headerSection.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword(password)
				.inputNewPassword(new Generex("[a-z]{4}\\d{2}[!#@]").random())
				.clickDoneBtn()
				.getNewPasswordError();
		
		Assert.assertEquals(error, localizedInvalidNewPasswordError());
		commonAction.refreshPage();	
		
		//Absence of digits
		error = headerSection.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword(password)
				.inputNewPassword(new Generex("[a-z]{7}[!#@]").random())
				.clickDoneBtn()
				.getNewPasswordError();
		
		Assert.assertEquals(error, localizedInvalidNewPasswordError());
		commonAction.refreshPage();	
		
		//Absence of special characters
		error = headerSection.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword(password)
				.inputNewPassword(new Generex("[a-z]{7}\\d").random())
				.clickDoneBtn()
				.getNewPasswordError();
		
		Assert.assertEquals(error, localizedInvalidNewPasswordError());
	}	
	
	@Test(dataProvider = "buyerAccountDP")
	void TC_ChangeValidPassword(String country, String username, String password) {
		
		String newPassword = DataGenerator.randomValidPassword();
		
		generalSFAction.navigateToURL(sellerSFURL);
		
		loginPage.performLogin(country, username, password);
		
		//Change password
		headerSection.clickUserInfoIcon().clickChangePassword().inputCurrentPassword(password).inputNewPassword(newPassword).clickDoneBtn();
		
		//Try logging in with old password
		headerSection.clickUserInfoIcon().changeLanguageByLangCode(sfDisplayLanguage);
		loginPage.performLogin(country, username, password);
		Assert.assertEquals(loginPage.getLoginFailError(), localizedWrongCredentialsError());
		
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
	
	@Test(dataProvider = "buyerAccountDP")
	void TC_ChangePasswordThatResemblesLast4Passwords(String country, String username, String password) {
		
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
		
		headerSection.clickUserInfoIcon().changeLanguageByLangCode(sfDisplayLanguage);
		
		//Change password to the latest 4 passwords
		for (String pw : oldPasswords) {
			ChangePasswordDialog changePasswordDlg = headerSection.clickUserInfoIcon().clickChangePassword().inputCurrentPassword(newPassword).inputNewPassword(pw).clickDoneBtn();
			Assert.assertEquals(changePasswordDlg.getCurrentPasswordError(), localizedSame4PasswordsError());
			changePasswordDlg.clickCloseBtn();
		}
	}	
	
	//Temporarily commented out for CI env
//	@Test
	void TC_ForgotPasswordForNonExistingAccount() {
		
		generalSFAction.navigateToURL(sellerSFURL);
		
		headerSection.clickUserInfoIcon().changeLanguageByLangCode(sfDisplayLanguage);
		
		//Wrong email as username
		headerSection.clickUserInfoIcon().clickLoginIcon();
		String error = loginPage.clickForgotPassword()
			.inputUsername(DataGenerator.randomCorrectFormatEmail())
			.clickContinueBtn()
			.getUsernameError();
		Assert.assertEquals(error, localizedEmailNotExistError());
		
		commonAction.refreshPage();
		
		//Wrong phone as username
		headerSection.clickUserInfoIcon().clickLoginIcon();
		error = loginPage.clickForgotPassword()
			.inputUsername(DataGenerator.randomPhone())
			.clickContinueBtn()
			.getUsernameError();
		Assert.assertEquals(error, localizedPhoneNotExistError());
	}	

	/**
	 * Be cautious because this TC may fail due to bot detection mechanisms https://mediastep.atlassian.net/browse/BH-37703
	 * @param country
	 * @param username
	 * @param password
	 */
	//Temporarily commented out for CI env
//	@Test(dataProvider = "buyerAccountDP")
	void TC_ForgotPasswordForEmailOrPhoneAccount(String country, String username, String password) {
		
		String newPassword = DataGenerator.randomValidPassword();
		
		generalSFAction.navigateToURL(sellerSFURL);
		
		headerSection.clickUserInfoIcon().clickLoginIcon();
		
		//Forgot password
		loginPage.clickForgotPassword()
			.selectCountry(country)
			.inputUsername(username)
			.clickContinueBtn()
			.inputPassword(newPassword)
			.inputVerificationCode(new KibanaAPI().getKeyFromKibana(username.matches("\\d+") ? DataGenerator.getPhoneCode(country)+":"+username : username, "resetKey"))
			.clickConfirmBtn();
		
		//Log out
		headerSection.clickUserInfoIcon().clickLogout();
		
		//Login with new password
		loginPage.performLogin(country, username, newPassword);
		
		//Log out
		headerSection.clickUserInfoIcon().clickLogout();
		
		//Change password back to the original value
		var loginSFAPI = new LoginSF(sellerCredentials);
		String currentPassword = "";
		for (int i=0; i<4; i++) {
			currentPassword = newPassword;
			newPassword = (i==3) ? password : DataGenerator.randomValidPassword();
			loginSFAPI.changePassword(username, currentPassword, DataGenerator.getPhoneCode(country), newPassword);
		}
	}	
	
	//TODO Login with Facebook functionality => Left for manual testing
	//TODO Buyers logging in with Facebook can't change password nor reset password on Storefront => Left for manual testing
	
    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
        driver.quit();
    }	
	
}
