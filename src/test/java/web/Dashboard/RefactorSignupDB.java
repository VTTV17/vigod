package web.Dashboard;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.kibana.KibanaAPI;
import utilities.account.AccountTest;
import utilities.commons.UICommonAction;
import utilities.driver.InitWebdriver;
import utilities.enums.PaymentMethod;
import utilities.enums.newpackage.NewPackage;
import utilities.links.Links;
import utilities.model.dashboard.setting.packageinfo.FinalizedPackageTotal;
import utilities.model.dashboard.setting.packageinfo.PackageInfo;
import utilities.model.dashboard.setupstore.PurchasePackage;
import utilities.model.dashboard.setupstore.SetupStore;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.settings.account.AccountPage;
import web.Dashboard.settings.plans.PackagePayment;
import web.Dashboard.settings.plans.PlansPage;
import web.Dashboard.settings.storeinformation.StoreInformation;
import web.Dashboard.signup.SetUpStorePage;
import web.Dashboard.signup.SignupPage;

public class RefactorSignupDB extends BaseTest {

	SignupPage signupPage;
	LoginPage loginPage;
	HomePage homePage;
	PlansPage plansPage;
	SetUpStorePage setupStorePage;
	PackagePayment packagePaymentPage;
	AccountPage accountPage;
	StoreInformation storeInfoPage;
	
	SetupStore storeModel;
	PurchasePackage packagePaymentModel;
	
	@BeforeMethod
	public void setup() {
		driver = new InitWebdriver().getDriver(browser, headless);
		signupPage = new SignupPage(driver);
		homePage = new HomePage(driver);
		setupStorePage = new SetUpStorePage(driver);
		packagePaymentPage = new PackagePayment(driver);
		accountPage = new AccountPage(driver);
		storeInfoPage = new StoreInformation(driver);
		commonAction = new UICommonAction(driver);
		plansPage = new PlansPage(driver);
		storeModel = new SetupStore();
		packagePaymentModel = new PurchasePackage();
	}

	boolean canSwitchLanguage(String domain) {
		//https://mediastep.atlassian.net/browse/BH-29615
		//https://mediastep.atlassian.net/browse/BH-29611
		return domain.contains("biz") ? false : true;
	}	
	boolean isFreeTrialOffered(String domain) {
		//https://mediastep.atlassian.net/browse/BH-30121
		return domain.contains("biz") ? true : false;
	}	
	int periodOptionCount(String domain) {
		//https://mediastep.atlassian.net/browse/BH-29680
		return domain.contains("biz") ? 1 : 3;
	}	
	int availablePackageCount(String country) {
		//https://mediastep.atlassian.net/browse/BH-32182
		//https://mediastep.atlassian.net/browse/BH-29680
		return country.contentEquals("Vietnam") ? NewPackage.forVNStore().size() : NewPackage.forForeignStore().size();
	}	
	String currencyRegex(String country) {
		//https://mediastep.atlassian.net/browse/BH-32182
		return country.contentEquals("Vietnam") ? "\\d.*\\d+đ$" : "^\\$\\d+.*\\d$";
	}
	List<String> onlinePaymentOptions(String country) {
		return country.contentEquals("Vietnam") ? Arrays.asList(new String[] {PaymentMethod.ATM.name(), PaymentMethod.VISA.name(), PaymentMethod.PAYPAL.name()}) : Arrays.asList(new String[] {PaymentMethod.PAYPAL.name()});
	}
	String pricePerYearRegex(String country) {
		return country.contentEquals("Vietnam") ? "^(?:\\d+[,\\.]?)+đ / \\d .{3,5} - \\d{2} x (?:\\d+[,\\.]?)+đ / .{5}$" : "^\\$(?:\\d+[,\\.]?)+ / \\d .{3,5} - \\d{2} x \\$(?:\\d+[,\\.]?)+ / .{5}$";
	}	
	String defaultFreeTrialPackage(String country) {
		return country.contentEquals("Vietnam") ? NewPackage.STARTUP_PLUS.name() : NewPackage.BASIC.name();
	}	
	
	String getActivationKey(SetupStore store) {
		return new KibanaAPI().getKeyFromKibana(store.getUsername().matches("^\\d+$") ? "%s:%s".formatted(store.getPhoneCode(), store.getUsername()) : store.getUsername(), "activationKey");
	}	
	
	void registerAccount(SetupStore store) {
		signupPage.navigate(store.getDomain());
		boolean isLanguageDisplayed = signupPage.isLanguageDropdownDisplayed();
		
		//Validate if Select Display Language dropdown appears
		Assert.assertEquals(isLanguageDisplayed, canSwitchLanguage(store.getDomain()));
		
		if (isLanguageDisplayed) signupPage.selectDisplayLanguage("ENG");
		signupPage.fillOutSignupForm(store);
		signupPage.provideVerificationCode(store);
	}
	
	void selectPackage(SetupStore store, PurchasePackage packagePayment) {
		List<String> periodOptions = plansPage.getPackagePeriodOptions();
		
		//Validate package period options
		Assert.assertEquals(periodOptions.size(), periodOptionCount(store.getDomain()));
		
		plansPage.selectDuration(new Random().nextInt(0, periodOptions.size()) +1);
		
		//Validate if Free Trial button appears
		Assert.assertEquals(plansPage.isFreeTrialBtnDisplayed(), isFreeTrialOffered(store.getDomain()));
		
		List<PackageInfo> availablePackages = plansPage.getPackageInfo();
		
		//Validate the number of packages available
		Assert.assertEquals(availablePackages.size(), availablePackageCount(store.getCountry()));
		
		//Validate currency
		availablePackages.stream().forEach(e -> Assert.assertTrue(e.getTotalPrice().matches(currencyRegex(store.getCountry()))));
		
		plansPage.subscribeToPackage(packagePayment.getNewPackage());
	}
	
	void selectPayment(SetupStore store, PurchasePackage packageAndPayment) {
		List<String> periodPaymentOptions = packagePaymentPage.getPackagePeriodOptions();
		
		//Validate package period options
		Assert.assertEquals(periodPaymentOptions.size(), periodOptionCount(store.getDomain()));
		
		//Validate available payment options
		List<String> onlinePaymentOptions = packagePaymentPage.getOnlinePaymentOptions();
		Assert.assertEquals(onlinePaymentOptions, onlinePaymentOptions(store.getCountry()));
		
		//Validate currency
		periodPaymentOptions.stream().forEach(e -> Assert.assertTrue(e.matches(pricePerYearRegex(store.getCountry()))));
		
		//Validate currency
		FinalizedPackageTotal finalizedPackagePayment =  packagePaymentPage.getFinalizePackageInfo();
		Assert.assertTrue(finalizedPackagePayment.getBasePrice().matches(currencyRegex(store.getCountry())));
		Assert.assertTrue(finalizedPackagePayment.getFinalTotal().matches(currencyRegex(store.getCountry())));
		if (store.getDomain().contains("biz")) {
			Assert.assertNull(finalizedPackagePayment.getVatPrice());
		} else {
			Assert.assertTrue(finalizedPackagePayment.getVatPrice().matches(currencyRegex(store.getCountry())));
		}
		
		packagePaymentPage.selectPaymentMethod(packageAndPayment.getPaymentMethod());
		String orderId = packagePaymentPage.completePayment(packageAndPayment.getPaymentMethod());
		packagePaymentPage.approvePackageInInternalTool(packageAndPayment.getPaymentMethod(), orderId);
		
		//Validate currency
		Assert.assertTrue(packagePaymentPage.getPaymentCompleteInfo().getTotal().matches(currencyRegex(store.getCountry())));
		packagePaymentPage.clickBackToDashboardBtn();
	}

	void validatePackageIsEnabled(PurchasePackage packageAndPayment) {
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab();
		Assert.assertEquals(accountPage.getPlanInfo().get(0).get(2), NewPackage.getValue(packageAndPayment.getNewPackage()));
	}
	
	void validateTimezoneShopName() {
		storeInfoPage.clickStoreInformationTab();
		Assert.assertEquals(storeInfoPage.getShopName(), storeModel.getName());
		Assert.assertEquals(storeInfoPage.getTimezone(), storeModel.getTimezone());
	}
	
	@Test
	public void SignupWithExistingAccount() throws Exception {
		
		/*Domain .vn*/
		storeModel.randomStoreData();
		storeModel.setCountry("Vietnam");
		storeModel.setUsername(AccountTest.ADMIN_SHOP_VI_USERNAME);
		storeModel.setPassword(AccountTest.ADMIN_SHOP_VI_PASSWORD);
		
		signupPage.navigate(Links.DOMAIN + Links.SIGNUP_PATH).selectDisplayLanguage("ENG");
		signupPage.fillOutSignupForm(storeModel);
		signupPage.verifyUsernameExistError(language).completeVerify();
		
		storeModel.setCountry("Vietnam");
		storeModel.setUsername(AccountTest.ADMIN_ACCOUNT_THANG);
		storeModel.setPassword(AccountTest.ADMIN_PASSWORD_THANG);
		
		signupPage.navigate(Links.DOMAIN + Links.SIGNUP_PATH).selectDisplayLanguage("ENG");
		signupPage.fillOutSignupForm(storeModel);
		signupPage.verifyUsernameExistError(language).completeVerify();
		
		/*Domain .biz*/
		storeModel.setCountry("Taiwan");
		storeModel.setUsername("905283336");
		storeModel.setPassword(AccountTest.ADMIN_PASSWORD_TIEN);
		
		signupPage.navigate(Links.DOMAIN_BIZ + Links.SIGNUP_PATH);
		signupPage.fillOutSignupForm(storeModel);
		signupPage.verifyUsernameExistError(language).completeVerify();
		
		storeModel.setCountry("Australia");
		storeModel.setUsername("tienbirdfeedbiz@mailnesia.com");
		storeModel.setPassword(AccountTest.ADMIN_PASSWORD_TIEN);
		
		signupPage.navigate(Links.DOMAIN_BIZ + Links.SIGNUP_PATH);
		signupPage.fillOutSignupForm(storeModel);
		signupPage.verifyUsernameExistError("ENG").completeVerify();
	}

	@Test
	public void Signup11() {
		
		//Randomize data
		storeModel.randomStoreData();
		packagePaymentModel.randomPackageAndPaymentMethod(storeModel);
		System.out.println(storeModel);
		System.out.println(packagePaymentModel);
		
		/* Sign up */
		registerAccount(storeModel);
		
		if (storeModel.getDomain().contains("biz")) {
			new LoginPage(driver).navigateBiz();
		} else {
			new LoginPage(driver).navigate();
		}
		
		new LoginPage(driver).performLogin(storeModel.getCountry(), storeModel.getUsername(), storeModel.getPassword());		
		
		/* Setup store */
		setupStorePage.setupShopExp(storeModel);
		
		/* Select package */
		selectPackage(storeModel, packagePaymentModel);
		
		/* Select payment */
		selectPayment(storeModel, packagePaymentModel);
		
		//Validate package plan is activated
		validatePackageIsEnabled(packagePaymentModel);
		
		//Validate Shop Name and timezone are displayed as expected
		validateTimezoneShopName();
	}	
	
	@Test
	public void ResendOTPForMailAccount() throws Exception {
		storeModel.setAccountType("EMAIL");
		storeModel.randomStoreData();
		packagePaymentModel.randomPackageAndPaymentMethod(storeModel);
		System.out.println(storeModel);
		System.out.println(packagePaymentModel);
		
		/*Register account*/
		signupPage.navigate(storeModel.getDomain());
		if (signupPage.isLanguageDropdownDisplayed()) signupPage.selectDisplayLanguage("ENG");
		signupPage.fillOutSignupForm(storeModel);
		
		String firstKey = getActivationKey(storeModel);
		
		signupPage.inputVerificationCode(firstKey);
		signupPage.clickResendOTP().clickConfirmOTPBtn();
		signupPage.verifyVerificationCodeError("ENG");
		
		commonAction.sleepInMiliSecond(5000);
		String resentCode = getActivationKey(storeModel);
		signupPage.inputVerificationCode(resentCode);
		
		Assert.assertNotEquals(resentCode, firstKey, "Resent verification code");
		
		signupPage.clickConfirmOTPBtn();
		
		/* Setup store */
		setupStorePage.setupShopExp(storeModel);
		
		/* Select package */
		selectPackage(storeModel, packagePaymentModel);
		
		/* Select payment */
		selectPayment(storeModel, packagePaymentModel);
		
		//Validate package plan is activated
		validatePackageIsEnabled(packagePaymentModel);
		
		//Validate Shop Name and timezone are displayed as expected
		validateTimezoneShopName();
	}
	
	@Test
	public void ResendOTPForPhoneAccount() throws Exception {
		storeModel.setAccountType("MOBILE");
		storeModel.randomStoreData();
		packagePaymentModel.randomPackageAndPaymentMethod(storeModel);
		System.out.println(storeModel);
		System.out.println(packagePaymentModel);
		
		/*Register account*/
		signupPage.navigate(storeModel.getDomain());
		if (signupPage.isLanguageDropdownDisplayed()) signupPage.selectDisplayLanguage("ENG");
		signupPage.fillOutSignupForm(storeModel);
		
		String firstKey = getActivationKey(storeModel);
		
		signupPage.inputVerificationCode(firstKey);
		commonAction.sleepInMiliSecond(5000, "Wait a little before triggering another API"); 
		signupPage.clickResendOTP().clickConfirmOTPBtn();
		signupPage.verifyVerificationCodeError("ENG");
		
//		commonAction.sleepInMiliSecond(5000);
		String resentCode = getActivationKey(storeModel);
		signupPage.inputVerificationCode(resentCode);
		
		Assert.assertNotEquals(resentCode, firstKey, "Resent verification code");
		
		signupPage.clickConfirmOTPBtn();
		
		/* Setup store */
		setupStorePage.setupShopExp(storeModel);
		
		/* Select package */
		selectPackage(storeModel, packagePaymentModel);
		
		/* Select payment */
		selectPayment(storeModel, packagePaymentModel);
		
		//Validate package plan is activated
		validatePackageIsEnabled(packagePaymentModel);
		
		//Validate Shop Name and timezone are displayed as expected
		validateTimezoneShopName();
	}
	
	@Test
	public void SignupByMail() {
		
		//Randomize data
		storeModel.setAccountType("EMAIL");
		storeModel.randomStoreData();
		packagePaymentModel.randomPackageAndPaymentMethod(storeModel);
		System.out.println(storeModel);
		System.out.println(packagePaymentModel);
		
		/* Sign up */
		registerAccount(storeModel);
		
		/* Setup store */
		setupStorePage.setupShopExp(storeModel);
		
		/* Select package */
		selectPackage(storeModel, packagePaymentModel);
		
		/* Select payment */
		selectPayment(storeModel, packagePaymentModel);
		
		//Validate package plan is activated
		validatePackageIsEnabled(packagePaymentModel);
		
		//Validate Shop Name and timezone are displayed as expected
		validateTimezoneShopName();
	}
	
	@Test
	public void SignupByPhone() {
		
		//Randomize data
		storeModel.setAccountType("MOBILE");
		storeModel.randomStoreData();
		packagePaymentModel.randomPackageAndPaymentMethod(storeModel);
		System.out.println(storeModel);
		System.out.println(packagePaymentModel);
		
		/* Sign up */
		registerAccount(storeModel);
		
		/* Setup store */
		setupStorePage.setupShopExp(storeModel);
		
		/* Select package */
		selectPackage(storeModel, packagePaymentModel);
		
		/* Select payment */
		selectPayment(storeModel, packagePaymentModel);
		
		//Validate package plan is activated
		validatePackageIsEnabled(packagePaymentModel);
		
		//Validate Shop Name and timezone are displayed as expected
		validateTimezoneShopName();
	}
	

	
	@Test
	public void ActivateFreeTrialWhenCreatingShop() {
		
		//Randomize data
		storeModel.setDomain(Links.DOMAIN_BIZ + Links.SIGNUP_PATH);
		storeModel.randomStoreData();
		System.out.println(storeModel);
		
		/* Sign up */
		signupPage.navigate(storeModel.getDomain());
		signupPage.fillOutSignupForm(storeModel);
		signupPage.provideVerificationCode(storeModel);
		
		/* Setup store */
		setupStorePage.setupShopExp(storeModel);
		
		/* Select package */
		List<String> periodOptions = plansPage.getPackagePeriodOptions();
		plansPage.selectDuration(new Random().nextInt(0, periodOptions.size()) +1);
		plansPage.clickFreeTrialBtn();
		homePage.getToastMessage();
		homePage.navigateToPage("Home");
		
		//Validate package plan is activated
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab();
		Assert.assertEquals(accountPage.getPlanInfo().get(0).get(2), defaultFreeTrialPackage(storeModel.getCountry()));
	}
	
	@Test
	public void ActivateFreeTrialAfterShopCreated() {
		
		//Randomize data
		storeModel.setDomain(Links.DOMAIN_BIZ + Links.SIGNUP_PATH);
		storeModel.randomStoreData();
		System.out.println(storeModel);
		
		/* Sign up */
		signupPage.navigate(storeModel.getDomain());
		signupPage.fillOutSignupForm(storeModel);
		signupPage.provideVerificationCode(storeModel);
		
		/* Setup store */
		setupStorePage.setupShopExp(storeModel);
		
		/* Select package */
		plansPage.getPackagePeriodOptions();
		
		driver.quit();
		
		driver = new InitWebdriver().getDriver("chrome", "false");
		homePage = new HomePage(driver);
		plansPage = new PlansPage(driver);
		accountPage = new AccountPage(driver);
		storeInfoPage = new StoreInformation(driver);
		
		new LoginPage(driver).navigateBiz().performLogin(storeModel.getCountry(), storeModel.getUsername(), storeModel.getPassword());
		homePage.clickUpgradeNow();
		plansPage.clickFreeTrialBtn();
		homePage.getToastMessage();
		
		//Validate package plan is activated
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab();
		Assert.assertEquals(accountPage.getPlanInfo().get(0).get(2), defaultFreeTrialPackage(storeModel.getCountry()));
		
		validateTimezoneShopName();
	}

	@Test(invocationCount = 1)
	public void SignupPurchasePackageExp() {
		
		//Randomize data
		storeModel.randomStoreData();
		packagePaymentModel.randomPackageAndPaymentMethod(storeModel);
		System.out.println(storeModel);
		System.out.println(packagePaymentModel);
		
		/* Sign up */
		registerAccount(storeModel);
		
		/* Setup store */
		setupStorePage.setupShopExp(storeModel);
		
		/* Select package */
		selectPackage(storeModel, packagePaymentModel);
		
		/* Select payment */
		selectPayment(storeModel, packagePaymentModel);
		
		//Validate package plan is activated
		validatePackageIsEnabled(packagePaymentModel);
		
		//Validate Shop Name and timezone are displayed as expected
		validateTimezoneShopName();
	}
	
	public void SignupPurchasePackage() {
		
		//Randomize data
		storeModel.randomStoreData("Vietnam");
		packagePaymentModel.randomPackageAndPaymentMethod(storeModel);
		System.out.println(storeModel);
		System.out.println(packagePaymentModel);
		
		/* Sign up */
		signupPage.navigate(storeModel.getDomain());
		boolean isLanguageDisplayed = signupPage.isLanguageDropdownDisplayed();
		
		//Validate if Select Display Language dropdown appears
		Assert.assertEquals(isLanguageDisplayed, canSwitchLanguage(storeModel.getDomain()));
		
		if (isLanguageDisplayed) signupPage.selectDisplayLanguage("ENG");
		signupPage.fillOutSignupForm(storeModel);
		signupPage.provideVerificationCode(storeModel);
		
		/* Setup store */
		setupStorePage.setupShopExp(storeModel);
		
		/* Select package */
		List<String> periodOptions = plansPage.getPackagePeriodOptions();
		
		//Validate package period options
		Assert.assertEquals(periodOptions.size(), periodOptionCount(storeModel.getDomain()));
		
		plansPage.selectDuration(new Random().nextInt(0, periodOptions.size()) +1);
		
		//Validate if Free Trial button appears
		Assert.assertEquals(plansPage.isFreeTrialBtnDisplayed(), isFreeTrialOffered(storeModel.getDomain()));
		
		List<PackageInfo> availablePackages = plansPage.getPackageInfo();
		
		//Validate the number of packages available
		Assert.assertEquals(availablePackages.size(), availablePackageCount(storeModel.getCountry()));
		
		//Validate currency
		availablePackages.stream().forEach(e -> Assert.assertTrue(e.getTotalPrice().matches(currencyRegex(storeModel.getCountry()))));
		
		plansPage.subscribeToPackage(packagePaymentModel.getNewPackage());
		
		/* Select payment */
		List<String> periodPaymentOptions = packagePaymentPage.getPackagePeriodOptions();
		
		//Validate package period options
		Assert.assertEquals(periodPaymentOptions.size(), periodOptionCount(storeModel.getDomain()));
		
		//Validate available payment options
		List<String> onlinePaymentOptions = packagePaymentPage.getOnlinePaymentOptions();
		Assert.assertEquals(onlinePaymentOptions, onlinePaymentOptions(storeModel.getCountry()));
		
		//Validate currency
		periodPaymentOptions.stream().forEach(e -> Assert.assertTrue(e.matches(pricePerYearRegex(storeModel.getCountry()))));
		
		//Validate currency
		FinalizedPackageTotal finalizedPackagePayment =  packagePaymentPage.getFinalizePackageInfo();
		Assert.assertTrue(finalizedPackagePayment.getBasePrice().matches(currencyRegex(storeModel.getCountry())));
		Assert.assertTrue(finalizedPackagePayment.getFinalTotal().matches(currencyRegex(storeModel.getCountry())));
		if (storeModel.getDomain().contains("biz")) {
			Assert.assertNull(finalizedPackagePayment.getVatPrice());
		} else {
			Assert.assertTrue(finalizedPackagePayment.getVatPrice().matches(currencyRegex(storeModel.getCountry())));
		}
		
		packagePaymentPage.selectPaymentMethod(packagePaymentModel.getPaymentMethod());
		String orderId = packagePaymentPage.completePayment(packagePaymentModel.getPaymentMethod());
		packagePaymentPage.approvePackageInInternalTool(packagePaymentModel.getPaymentMethod(), orderId);
		
		//Validate currency
		Assert.assertTrue(packagePaymentPage.getPaymentCompleteInfo().getTotal().matches(currencyRegex(storeModel.getCountry())));
		packagePaymentPage.clickBackToDashboardBtn();
		
		//Validate package plan is activated
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab();
		Assert.assertEquals(accountPage.getPlanInfo().get(0).get(2), NewPackage.getValue(packagePaymentModel.getNewPackage()));
		
		//Validate Shop Name and timezone are displayed as expected
		storeInfoPage.clickStoreInformationTab();
		Assert.assertEquals(storeInfoPage.getShopName(), storeModel.getName());
		Assert.assertEquals(storeInfoPage.getTimezone(), storeModel.getTimezone());
	}	
	
	@AfterMethod
	public void writeResult(ITestResult result) throws IOException {
		super.writeResult(result);
		driver.quit();
	}

}
