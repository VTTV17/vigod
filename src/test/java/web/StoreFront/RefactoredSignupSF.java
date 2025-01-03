package web.StoreFront;

import static utilities.account.AccountTest.ADMIN_COUNTRY_TIEN;
import static utilities.account.AccountTest.ADMIN_PASSWORD_TIEN;
import static utilities.account.AccountTest.ADMIN_USERNAME_TIEN;
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

import api.Buyer.login.LoginSF;
import api.Seller.login.Login;
import api.Seller.setting.StoreInformation;
import api.Seller.setting.StoreLanguageAPI;
import io.restassured.path.json.JsonPath;
import utilities.account.AccountTest;
import utilities.api.thirdparty.KibanaAPI;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.data.testdatagenerator.SignupBuyerTDG;
import utilities.driver.InitWebdriver;
import utilities.enums.AccountType;
import utilities.enums.DisplayLanguage;
import utilities.enums.Domain;
import utilities.model.dashboard.setting.languages.AdditionalLanguages;
import utilities.model.dashboard.storefront.BuyerSignupData;
import utilities.model.sellerApp.login.LoginInformation;
import web.Dashboard.customers.allcustomers.AllCustomers;
import web.Dashboard.customers.allcustomers.details.CustomerDetails;
import web.StoreFront.header.HeaderSF;
import web.StoreFront.login.LoginPage;
import web.StoreFront.signup.SignupPage;
import web.StoreFront.userprofile.MyAccount.MyAccount;
import web.StoreFront.userprofile.userprofileinfo.UserProfileInfo;

public class RefactoredSignupSF extends BaseTest {
	
	GeneralSF generalSFAction;
	SignupPage signupPage;
	HeaderSF headerSection;

	String sellerCountry, sellerUsername, sellerPassword, sellerSFURL, sfDomain;
	LoginInformation sellerCredentials;
	
	LoginSF loginSFAPI;
	
	List<AdditionalLanguages> publishedLanguages;
	
	@BeforeClass
	void loadData() {
		
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			sellerCountry = ADMIN_COUNTRY_TIEN;
			sellerUsername = ADMIN_USERNAME_TIEN;
			sellerPassword = ADMIN_PASSWORD_TIEN;
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
		
		loginSFAPI = new LoginSF(sellerCredentials);
        
	}
	
	String getActivationKey(BuyerSignupData data) {
		return new KibanaAPI().getKeyFromKibana(data.getUsername().matches("^\\d+$") ? "%s:%s".formatted(data.getPhoneCode(), data.getUsername()) : data.getUsername(), "activationKey");
	}	
	
	String randomSFDisplayLanguage() {
        return publishedLanguages.stream()
        		.filter(AdditionalLanguages::getPublished)
        		.map(AdditionalLanguages::getLangCode)
        		.collect(Collectors.collectingAndThen(Collectors.toList(), collected -> collected.get(new Random().nextInt(collected.size()))));
	}	

	@BeforeMethod
	void instantiatePageObjects() {
		driver = new InitWebdriver().getDriver(browser, headless);
		generalSFAction = new GeneralSF(driver);
		signupPage = new SignupPage(driver);
		commonAction = new UICommonAction(driver);
		headerSection = new HeaderSF(driver);
	}	

	/**
	 * Quickly create an account on SF to test other functionalities
	 * @param buyerData
	 * @param langCode language code of the store's published language. Eg. vi, en-au, lo
	 */
	void createAccountOnSF(BuyerSignupData buyerData, String langCode) {
		generalSFAction.navigateToURL(sellerSFURL);
		
		headerSection.clickUserInfoIcon()
			.changeLanguageByLangCode(langCode);
		
		signupPage.fillOutSignupForm(buyerData)
			.inputVerificationCode(getActivationKey(buyerData))
			.clickConfirmBtn();
		
		if (buyerData.getType().equals(AccountType.MOBILE)) 
			signupPage.inputEmail(buyerData.getEmail()).clickCompleteBtn();
		
		headerSection.clickUserInfoIcon()
			.clickLogout();
	}
	
	@DataProvider
	Object[][] accountDataProvider() {
		return new Object[][] { 
			{SignupBuyerTDG.buildSignupByEmailData()},
			{SignupBuyerTDG.buildSignupByPhoneData()},
		};
	}
	
	@Test(dataProvider = "accountDataProvider")
	void TC_CreateAccountWithInvalidData(BuyerSignupData buyerData) {
		
		//Try creating an account with invalid data
		generalSFAction.navigateToURL(sellerSFURL);
		headerSection.clickUserInfoIcon()
			.changeLanguage(language);
		
		commonAction.refreshPage();
		var actualEmptyUsernameError = signupPage.fillOutSignupForm(buyerData.getCountry(), "", buyerData.getPassword(), buyerData.getDisplayName(), buyerData.getBirthday())
				.getUsernameError();

		commonAction.refreshPage();
		var actualEmptyPasswordError = signupPage.fillOutSignupForm(buyerData.getCountry(), buyerData.getUsername(), "", buyerData.getDisplayName(), buyerData.getBirthday())
				.getPasswordError();

		commonAction.refreshPage();
		var actualEmptyNameError = signupPage.fillOutSignupForm(buyerData.getCountry(), buyerData.getUsername(), buyerData.getPassword(), "", buyerData.getBirthday())
				.getDisplayNameError();
		
		commonAction.refreshPage();
		var invalidUsername = buyerData.getType().equals(AccountType.MOBILE) ? "098745" : "automation_mail.com";
		var actualInvalidUsernameFormatError = signupPage.fillOutSignupForm(buyerData.getCountry(), invalidUsername, buyerData.getPassword(), buyerData.getDisplayName(), buyerData.getBirthday())
				.getUsernameError();

		//Assertions for errors
		Assert.assertEquals(actualEmptyUsernameError, SignupPage.localizedEmptyUsernameError(DisplayLanguage.valueOf(language)));
		Assert.assertEquals(actualEmptyPasswordError, SignupPage.localizedEmptyPasswordError(DisplayLanguage.valueOf(language)));
		Assert.assertEquals(actualEmptyNameError, SignupPage.localizedEmptyNameError(DisplayLanguage.valueOf(language)));
		Assert.assertEquals(actualInvalidUsernameFormatError, SignupPage.localizedInvalidUsernameError(DisplayLanguage.valueOf(language)));
	}	

	@Test(dataProvider = "accountDataProvider")
	void TC_CreateAccountWithExistingUsername(BuyerSignupData buyerData) {
		
		//Arrange expected results
		var expectedError = buyerData.getType().equals(AccountType.MOBILE) 
				? SignupPage.localizedPhoneAlreadyExistError(DisplayLanguage.valueOf(language)) 
						: SignupPage.localizedEmailAlreadyExistError(DisplayLanguage.valueOf(language));
		
		
		//Create an account
		createAccountOnSF(buyerData, randomSFDisplayLanguage());
		
		//Try creating an account with the same username
		headerSection.clickUserInfoIcon()
			.changeLanguage(language);
		
		if (buyerData.getType().equals(AccountType.MOBILE)) 
			UICommonAction.sleepInMiliSecond(50000, "The time interval between 2 Register API calls is 60s"); //https://mediastep.atlassian.net/browse/BH-37703
		
		String actualError = signupPage.fillOutSignupForm(buyerData)
			.getUsernameExistError();
		
		//Assertions for error
		Assert.assertEquals(actualError, expectedError);
		
		
		//Delete account afterwards
		loginSFAPI.deleteAccount(buyerData.getUsername(), buyerData.getPassword(), buyerData.getPhoneCode());
	}

	@Test(dataProvider = "accountDataProvider")
	void TC_CreateAccountWithWrongVerificationCode(BuyerSignupData buyerData) {
		
		//Create an account on SF
		generalSFAction.navigateToURL(sellerSFURL);
		
		headerSection.clickUserInfoIcon()
			.changeLanguage(language);
		
		signupPage.fillOutSignupForm(buyerData);
		
		//Get verification code
		String code = getActivationKey(buyerData);
		
		//Input a wrong verification code
		signupPage.inputVerificationCode(String.valueOf(Integer.parseInt(code) - 1))
			.clickConfirmBtn();
		
		var actualError = signupPage.getVerificationCodeError();
		
		//Assertions for error
		Assert.assertEquals(actualError, SignupPage.localizedWrongVerificationCodeError(DisplayLanguage.valueOf(language)));
		
		
		//Input the correct code
		signupPage.inputVerificationCode(code)
			.clickConfirmBtn();
		
		if (buyerData.getType().equals(AccountType.MOBILE)) 
			signupPage.inputEmail(buyerData.getEmail()).clickCompleteBtn();
		
		// Logout
		headerSection.clickUserInfoIcon()
			.clickLogout();
		
		// Re-login
		generalSFAction.navigateToURL(sellerSFURL);
		new LoginPage(driver).performLogin(buyerData.getCountry(), buyerData.getUsername(), buyerData.getPassword());
		
		//Retrieve info on SF
		MyAccount accountPage = headerSection.clickUserInfoIcon()
			.clickUserProfile()
			.clickMyAccountSection();
		var actual_SF_Name = accountPage.getDisplayName();
		var actual_SF_Email = accountPage.getEmail();
		var actual_SF_Birthday = accountPage.getBirthday();
		var actual_SF_Phone = accountPage.getPhoneNumber();
		var actual_SF_Country = new UserProfileInfo(driver).clickMyAddressSection().getCountry();
		
		//Retrieve info on Dashboard
		new web.Dashboard.login.LoginPage(driver, Domain.valueOf(domain)).navigate()
			.changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.performValidLogin(sellerCountry, sellerUsername, sellerPassword);

		CustomerDetails customerDetailPage = new AllCustomers(driver, Domain.valueOf(domain)).navigateUsingURL()
			.selectBranch("NONE")
			.clickUser(buyerData.getDisplayName());
		var actualDashboardEmail = customerDetailPage.getEmail();
		var actualDashboardPhone = customerDetailPage.getPhoneNumber();
		var actualDashboardCountry = customerDetailPage.getCountry();
		
		//Retrieve info from API
		JsonPath buyerAdditionalData = loginSFAPI.getAccountInfo(buyerData.getUsername(), buyerData.getPassword(), buyerData.getPhoneCode()).jsonPath();
		var actual_API_langKey = buyerAdditionalData.getString("langKey");
		var actual_API_LocationCode = buyerAdditionalData.getString("locationCode");
		System.out.println(actual_API_langKey);
		
		
		//Assertions for SF
		Assert.assertEquals(actual_SF_Name, buyerData.getDisplayName());
		Assert.assertEquals(actual_SF_Email, buyerData.getEmail());
		Assert.assertEquals(actual_SF_Birthday, buyerData.getBirthday());
		if (buyerData.getType().equals(AccountType.MOBILE)) 
			Assert.assertEquals(actual_SF_Phone, "%s:%s".formatted(buyerData.getPhoneCode(), buyerData.getUsername()));
		Assert.assertEquals(actual_SF_Country, buyerData.getCountry());
		
		//Assertions for Dashboard
		Assert.assertEquals(actualDashboardEmail, buyerData.getEmail());
		if (buyerData.getType().equals(AccountType.MOBILE)) 
			Assert.assertEquals(actualDashboardPhone, "%s:%s".formatted(buyerData.getPhoneCode(), buyerData.getUsername()));
		Assert.assertEquals(actualDashboardCountry, buyerData.getCountry());
		
		//Assertions for API
		Assert.assertEquals(actual_API_LocationCode, buyerData.getCountryCode());
		
		
		//Delete account afterwards
		loginSFAPI.deleteAccount(buyerData.getUsername(), buyerData.getPassword(), buyerData.getPhoneCode());
	}		

	@Test(dataProvider = "accountDataProvider")
	void TC_CreateAccountWithResentVerificationCode(BuyerSignupData buyerData) {
		
		//Create an account on SF
		generalSFAction.navigateToURL(sellerSFURL);
		
		headerSection.clickUserInfoIcon()
			.changeLanguage(language);
		
		signupPage.fillOutSignupForm(buyerData);
		
		//Get 1st verification code
		var initialCode = getActivationKey(buyerData);
		
		UICommonAction.sleepInMiliSecond(5000, "Wait 5s before hitting Resend button");
		
		if (buyerData.getType().equals(AccountType.MOBILE)) 
			UICommonAction.sleepInMiliSecond(50000, "Wait 60s before hitting Resend button for phone account"); //https://mediastep.atlassian.net/browse/BH-37703
		
		//Resend verification code
		signupPage.clickResendOTP();
		
		//Input old verification code
		signupPage.inputVerificationCode(initialCode)
			.clickConfirmBtn();
		
		signupPage.getVerificationCodeError();

		var resentCode = getActivationKey(buyerData);
		
		
		//Assertions for resent code
		Assert.assertNotEquals(resentCode, initialCode);
		
		
		//Input resent verification code
		signupPage.inputVerificationCode(resentCode)
			.clickConfirmBtn();
		
		if (buyerData.getType().equals(AccountType.MOBILE)) 
			signupPage.inputEmail(buyerData.getEmail()).clickCompleteBtn();
		
		// Logout
		headerSection.clickUserInfoIcon()
			.clickLogout();
		
		// Re-login
		generalSFAction.navigateToURL(sellerSFURL);
		new LoginPage(driver).performLogin(buyerData.getCountry(), buyerData.getUsername(), buyerData.getPassword());
		
		//Retrieve info on SF
		MyAccount accountPage = headerSection.clickUserInfoIcon()
			.clickUserProfile()
			.clickMyAccountSection();
		var actual_SF_Name = accountPage.getDisplayName();
		var actual_SF_Email = accountPage.getEmail();
		var actual_SF_Birthday = accountPage.getBirthday();
		var actual_SF_Phone = accountPage.getPhoneNumber();
		var actual_SF_Country = new UserProfileInfo(driver).clickMyAddressSection().getCountry();
		
		//Retrieve info on Dashboard
		new web.Dashboard.login.LoginPage(driver, Domain.valueOf(domain)).navigate()
			.changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.performValidLogin(sellerCountry, sellerUsername, sellerPassword);

		CustomerDetails customerDetailPage = new AllCustomers(driver, Domain.valueOf(domain)).navigateUsingURL()
			.selectBranch("NONE")
			.clickUser(buyerData.getDisplayName());
		var actualDashboardEmail = customerDetailPage.getEmail();
		var actualDashboardPhone = customerDetailPage.getPhoneNumber();
		var actualDashboardCountry = customerDetailPage.getCountry();
		
		//Retrieve info from API
		JsonPath buyerAdditionalData = loginSFAPI.getAccountInfo(buyerData.getUsername(), buyerData.getPassword(), buyerData.getPhoneCode()).jsonPath();
		var actual_API_langKey = buyerAdditionalData.getString("langKey");
		var actual_API_LocationCode = buyerAdditionalData.getString("locationCode");
		System.out.println(actual_API_langKey);

		
		//Assertions for SF
		Assert.assertEquals(actual_SF_Name, buyerData.getDisplayName());
		Assert.assertEquals(actual_SF_Email, buyerData.getEmail());
		Assert.assertEquals(actual_SF_Birthday, buyerData.getBirthday());
		if (buyerData.getType().equals(AccountType.MOBILE)) Assert.assertEquals(actual_SF_Phone, "%s:%s".formatted(buyerData.getPhoneCode(), buyerData.getUsername()));
		Assert.assertEquals(actual_SF_Country, buyerData.getCountry());
		
		//Assertions for Dashboard
		Assert.assertEquals(actualDashboardEmail, buyerData.getEmail());
		if (buyerData.getType().equals(AccountType.MOBILE)) Assert.assertEquals(actualDashboardPhone, "%s:%s".formatted(buyerData.getPhoneCode(), buyerData.getUsername()));
		Assert.assertEquals(actualDashboardCountry, buyerData.getCountry());
		
		//Assertions for API
		Assert.assertEquals(actual_API_LocationCode, buyerData.getCountryCode());
		
		
		//Delete account afterwards
		loginSFAPI.deleteAccount(buyerData.getUsername(), buyerData.getPassword(), buyerData.getPhoneCode());
	}
	
	@Test(dataProvider = "accountDataProvider")
	void TC_CreateAccountAfterLeaving(BuyerSignupData buyerData) {
		
		var expected_API_LangKey = randomSFDisplayLanguage();
		
		//Create an account on SF
		generalSFAction.navigateToURL(sellerSFURL);
		
		headerSection.clickUserInfoIcon()
			.changeLanguageByLangCode(expected_API_LangKey);
		
		signupPage.fillOutSignupForm(buyerData);
		
		//Delete cookies at Verification Code screen
		driver.manage().deleteAllCookies();
		commonAction.refreshPage();		
		
		//Create the account again
		headerSection.clickUserInfoIcon()
			.changeLanguageByLangCode(expected_API_LangKey);
		
		if (buyerData.getType().equals(AccountType.MOBILE)) 
			UICommonAction.sleepInMiliSecond(60000, "The time interval between 2 Register API calls is 60s"); //https://mediastep.atlassian.net/browse/BH-37703
		
		signupPage.fillOutSignupForm(buyerData)
			.inputVerificationCode(getActivationKey(buyerData))
			.clickConfirmBtn();
		
		if (buyerData.getType().equals(AccountType.MOBILE)) 
			signupPage.inputEmail(buyerData.getEmail()).clickCompleteBtn();
		
		// Logout
		headerSection.clickUserInfoIcon()
			.clickLogout();
		
		// Re-login
		generalSFAction.navigateToURL(sellerSFURL);
		new LoginPage(driver).performLogin(buyerData.getCountry(), buyerData.getUsername(), buyerData.getPassword());
		
		//Retrieve info on SF
		MyAccount accountPage = headerSection.clickUserInfoIcon()
				.clickUserProfile()
				.clickMyAccountSection();
		var actual_SF_Name = accountPage.getDisplayName();
		var actual_SF_Email = accountPage.getEmail();
		var actual_SF_Birthday = accountPage.getBirthday();
		var actual_SF_Phone = accountPage.getPhoneNumber();
		var actual_SF_Country = new UserProfileInfo(driver).clickMyAddressSection().getCountry();
		
		//Retrieve info on Dashboard
		new web.Dashboard.login.LoginPage(driver, Domain.valueOf(domain)).navigate()
			.changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.performValidLogin(sellerCountry, sellerUsername, sellerPassword);
		
		CustomerDetails customerDetailPage = new AllCustomers(driver, Domain.valueOf(domain)).navigateUsingURL()
				.selectBranch("NONE")
				.clickUser(buyerData.getDisplayName());
		var actualDashboardEmail = customerDetailPage.getEmail();
		var actualDashboardPhone = customerDetailPage.getPhoneNumber();
		var actualDashboardCountry = customerDetailPage.getCountry();
		
		//Retrieve info from API
		JsonPath buyerAdditionalData = loginSFAPI.getAccountInfo(buyerData.getUsername(), buyerData.getPassword(), buyerData.getPhoneCode()).jsonPath();
		var actual_API_langKey = buyerAdditionalData.getString("langKey");
		var actual_API_LocationCode = buyerAdditionalData.getString("locationCode");
		
		
		//Assertions for SF
		Assert.assertEquals(actual_SF_Name, buyerData.getDisplayName());
		Assert.assertEquals(actual_SF_Email, buyerData.getEmail());
		Assert.assertEquals(actual_SF_Birthday, buyerData.getBirthday());
		if (buyerData.getType().equals(AccountType.MOBILE)) Assert.assertEquals(actual_SF_Phone, "%s:%s".formatted(buyerData.getPhoneCode(), buyerData.getUsername()));
		Assert.assertEquals(actual_SF_Country, buyerData.getCountry());
		
		//Assertions for Dashboard
		Assert.assertEquals(actualDashboardEmail, buyerData.getEmail());
		if (buyerData.getType().equals(AccountType.MOBILE)) Assert.assertEquals(actualDashboardPhone, "%s:%s".formatted(buyerData.getPhoneCode(), buyerData.getUsername()));
		Assert.assertEquals(actualDashboardCountry, buyerData.getCountry());
		
		//Assertions for API
		Assert.assertEquals(actual_API_LocationCode, buyerData.getCountryCode());
		Assert.assertEquals(actual_API_langKey, expected_API_LangKey);
		
		
		//Delete account afterwards
		loginSFAPI.deleteAccount(buyerData.getUsername(), buyerData.getPassword(), buyerData.getPhoneCode());
	}
	
	@Test(dataProvider = "accountDataProvider")
	void TC_CreateAccount(BuyerSignupData buyerData) {
		
		var expected_API_LangKey = randomSFDisplayLanguage();
		
		//Create an account on SF
		generalSFAction.navigateToURL(sellerSFURL);
		
		headerSection.clickUserInfoIcon()
			.changeLanguageByLangCode(expected_API_LangKey);
		
		signupPage.fillOutSignupForm(buyerData)
			.inputVerificationCode(getActivationKey(buyerData))
			.clickConfirmBtn();
		
		if (buyerData.getType().equals(AccountType.MOBILE)) 
			signupPage.inputEmail(buyerData.getEmail()).clickCompleteBtn();
		
		// Logout
		headerSection.clickUserInfoIcon()
			.clickLogout();
		
		// Re-login
		new LoginPage(driver).navigate(sellerSFURL)
			.performLogin(buyerData.getCountry(), buyerData.getUsername(), buyerData.getPassword());
		
		//Retrieve info on SF
		MyAccount accountPage = headerSection.clickUserInfoIcon()
			.clickUserProfile()
			.clickMyAccountSection();
		var actual_SF_Name = accountPage.getDisplayName();
		var actual_SF_Email = accountPage.getEmail();
		var actual_SF_Birthday = accountPage.getBirthday();
		var actual_SF_Phone = accountPage.getPhoneNumber();
		var actual_SF_Country = new UserProfileInfo(driver).clickMyAddressSection().getCountry();
		
		//Retrieve info on Dashboard
		new web.Dashboard.login.LoginPage(driver, Domain.valueOf(domain)).navigate()
			.changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.performValidLogin(sellerCountry, sellerUsername, sellerPassword);

		CustomerDetails customerDetailPage = new AllCustomers(driver, Domain.valueOf(domain)).navigateUsingURL()
			.selectBranch("NONE")
			.clickUser(buyerData.getDisplayName());
		var actualDashboardEmail = customerDetailPage.getEmail();
		var actualDashboardPhone = customerDetailPage.getPhoneNumber();
		var actualDashboardCountry = customerDetailPage.getCountry();
		
		//Retrieve info from API
		JsonPath buyerAdditionalData = loginSFAPI.getAccountInfo(buyerData.getUsername(), buyerData.getPassword(), buyerData.getPhoneCode()).jsonPath();
		var actual_API_langKey = buyerAdditionalData.getString("langKey");
		var actual_API_LocationCode = buyerAdditionalData.getString("locationCode");

		
		//Assertions for SF
		Assert.assertEquals(actual_SF_Name, buyerData.getDisplayName());
		Assert.assertEquals(actual_SF_Email, buyerData.getEmail());
		Assert.assertEquals(actual_SF_Birthday, buyerData.getBirthday());
		if (buyerData.getType().equals(AccountType.MOBILE)) Assert.assertEquals(actual_SF_Phone, "%s:%s".formatted(buyerData.getPhoneCode(), buyerData.getUsername()));
		Assert.assertEquals(actual_SF_Country, buyerData.getCountry());
		
		//Assertions for Dashboard
		Assert.assertEquals(actualDashboardEmail, buyerData.getEmail());
		if (buyerData.getType().equals(AccountType.MOBILE)) Assert.assertEquals(actualDashboardPhone, "%s:%s".formatted(buyerData.getPhoneCode(), buyerData.getUsername()));
		Assert.assertEquals(actualDashboardCountry, buyerData.getCountry());
		
		//Assertions for API
		Assert.assertEquals(actual_API_LocationCode, buyerData.getCountryCode());
		Assert.assertEquals(actual_API_langKey, expected_API_LangKey);
		
		
		//Delete account afterwards
		loginSFAPI.deleteAccount(buyerData.getUsername(), buyerData.getPassword(), buyerData.getPhoneCode());
	}

	@DataProvider
	Object[][] accountsToDelete() {
		return new Object[][] { 
			{"Chad", "auto-buyer99670972@mailnesia.com"},
		};
	}	
	@Test(dataProvider = "accountsToDelete")
	void deleteAccount(String country, String username) {
		new LoginSF(sellerCredentials).deleteAccount(username, "fortesting!1", DataGenerator.getPhoneCode(country));
	}		
	
    @AfterMethod
    public void writeResult(ITestResult result) throws Exception {
        super.writeResult(result);
        driver.quit();
    }	
	
}
