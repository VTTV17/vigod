package web.StoreFront;

import static utilities.links.Links.SF_DOMAIN;
import static utilities.links.Links.SF_DOMAIN_BIZ;

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
		loginPage.performLogin("", new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random());
		Assert.assertEquals(loginPage.getUsernameError(), LoginPage.localizedEmptyUsernameError(localizedLanguage));
		commonAction.refreshPage();
		
		//Empty password
		loginPage.performLogin(new Generex("\\d{9}").random(), "");
		Assert.assertEquals(loginPage.getPasswordError(), LoginPage.localizedEmptyPasswordError(localizedLanguage));
		commonAction.refreshPage();
		
		//Empty username and password
		loginPage.performLogin("", "");
		Assert.assertEquals(loginPage.getUsernameError(), LoginPage.localizedEmptyUsernameError(localizedLanguage));
		Assert.assertEquals(loginPage.getPasswordError(), LoginPage.localizedEmptyPasswordError(localizedLanguage));
		commonAction.refreshPage();
		
		//7-digit phone number
		loginPage.performLogin(new Generex("\\d{7}").random(), new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random());
		Assert.assertEquals(loginPage.getUsernameError(), LoginPage.localizedInvalidUsernameError(DisplayLanguage.valueOf(language)));
		commonAction.refreshPage();
		
		//16-digit phone number
		loginPage.performLogin(new Generex("\\d{16}").random(), new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random());
		Assert.assertEquals(loginPage.getUsernameError(), LoginPage.localizedInvalidUsernameError(DisplayLanguage.valueOf(language)));
		commonAction.refreshPage();
		
		//Mail does not have symbol @
		loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\.[a-z]{2}").random(), new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random());
		Assert.assertEquals(loginPage.getUsernameError(), LoginPage.localizedInvalidUsernameError(DisplayLanguage.valueOf(language)));
		commonAction.refreshPage();
		
		//Mail does not have suffix '.<>'. Eg. '.com'
		loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\@").random(), new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random());
		Assert.assertEquals(loginPage.getUsernameError(), LoginPage.localizedInvalidUsernameError(DisplayLanguage.valueOf(language)));
		commonAction.refreshPage();
		
		loginPage.performLogin(new Generex("[a-z]{5,8}\\d{5,8}\\@[a-z]mail\\.").random(), new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random());
		Assert.assertEquals(loginPage.getUsernameError(), LoginPage.localizedInvalidUsernameError(DisplayLanguage.valueOf(language)));
	}
	
	@Test
	void TC_LoginWithWrongCredentials() {
		
		String langCode = randomSFDisplayLanguage();
		
		DisplayLanguage localizedLanguage = langCode.startsWith("vi") ? DisplayLanguage.VIE : DisplayLanguage.ENG;
		
		generalSFAction.navigateToURL(sellerSFURL);
		
		headerSection.clickUserInfoIcon().changeLanguageByLangCode(langCode);
		
		//Email
		loginPage.performLogin(new Generex("[a-z]{5}\\d{5}\\@[a-z]mail\\.[a-z]{2,3}").random(), new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random());
		Assert.assertEquals(loginPage.getLoginFailError(), LoginPage.localizedWrongCredentialsError(localizedLanguage));
		commonAction.refreshPage();
		
		//Mobile
		loginPage.performLogin(new Generex("\\d{8,15}").random(), new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random());
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
				.inputNewPassword(new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random())
				.clickDoneBtn()
				.getCurrentPasswordError();
		
		Assert.assertEquals(error, ForgotPasswordDialog.localizedWrongCurrentPasswordError(localizedLanguage));
		commonAction.refreshPage();
		
		//Empty new password
		error = headerSection.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword(new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random())
				.inputNewPassword("")
				.clickDoneBtn()
				.getNewPasswordError();
		
		Assert.assertEquals(error, LoginPage.localizedEmptyPasswordError(localizedLanguage));
		commonAction.refreshPage();
		
		//Incorrect current password
		error = headerSection.clickUserInfoIcon()
				.clickChangePassword()
				.inputCurrentPassword(new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random())
				.inputNewPassword(new Generex("[a-z]{5,8}\\d{5,8}[!#@]").random())
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
	
    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
        driver.quit();
    }	
	
}
