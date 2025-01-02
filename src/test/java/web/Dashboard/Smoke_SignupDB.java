package web.Dashboard;


import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import utilities.account.AccountTest;
import utilities.commons.UICommonAction;
import utilities.driver.InitWebdriver;
import utilities.enums.DisplayLanguage;
import utilities.enums.Domain;
import utilities.model.dashboard.setupstore.SetupStoreDG;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.signup.SetUpStorePage;
import web.Dashboard.signup.SignupPage;

public class Smoke_SignupDB extends BaseTest {
	
	SetupStoreDG storeDG;

	@BeforeMethod
	public void setup() {
		driver = new InitWebdriver().getDriver(browser, headless);
		commonAction = new UICommonAction(driver);
		storeDG = new SetupStoreDG(Domain.valueOf(domain));
	}

	@DataProvider
	public String[][] accountType() {
		return new String[][] { 
			{"EMAIL"},
			{"MOBILE"}
		};
	}		
	
	@Test
	public void SignupWithExistingAccount() throws Exception {
		
		String existingCountry, existingUsername, existingPassword;
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			existingCountry = AccountTest.ADMIN_PLAN_MAIL_VN_COUNTRY;
			existingUsername = AccountTest.ADMIN_PLAN_MAIL_VN_USERNAME;
			existingPassword = AccountTest.ADMIN_PLAN_MAIL_VN_PASSWORD;
		} else {
			existingCountry = AccountTest.ADMIN_PLAN_PHONE_BIZ_COUNTRY;
			existingUsername = AccountTest.ADMIN_PLAN_PHONE_BIZ_USERNAME;
			existingPassword = AccountTest.ADMIN_PLAN_PHONE_BIZ_PASSWORD;
		}
		
		//Verify errors appear when users attempt to create an account that already exists
		new SignupPage(driver, Domain.valueOf(domain)).navigate()
			.changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.fillOutSignupForm(existingCountry, existingUsername, existingPassword)
			.verifyUsernameExistError(DisplayLanguage.valueOf(language).name());
	}

	@Test(dataProvider = "accountType")
	public void RegisterThenLoginToContinue(String accountType) {

		//Randomize data
		storeDG.setAccountType(accountType);
		storeDG.randomStoreData();
		System.out.println(storeDG);

		//Register for an account on Dashboard
		new SignupPage(driver, Domain.valueOf(domain)).navigate()
			.changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.fillOutSignupForm(storeDG)
			.provideVerificationCode(storeDG);
		
		//Login with the account
		new LoginPage(driver, Domain.valueOf(domain)).navigate()
			.changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.performLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());		

		//Verify users can resume store setup process
		new SetUpStorePage(driver).setupShopExp(storeDG);
		
		//Verify users are taken to Dashboard
		new HomePage(driver).verifyPageLoaded();
	}	

	@Test(dataProvider = "accountType")
	public void SignupByMailOrPhone(String accountType) {

		//Randomize data
		storeDG.setAccountType(accountType);
		storeDG.randomStoreData();
		System.out.println(storeDG);

		//Register for an account on Dashboard
		new SignupPage(driver, Domain.valueOf(domain)).navigate()
			.changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.fillOutSignupForm(storeDG)
			.provideVerificationCode(storeDG);

		//Setup store
		new SetUpStorePage(driver).setupShopExp(storeDG);
		
		//Verify users are taken to Dashboard
		new HomePage(driver).verifyPageLoaded();
	}

	@AfterMethod
	public void writeResult(ITestResult result) throws Exception {
		super.writeResult(result);
		driver.quit();
	}

}
