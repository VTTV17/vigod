package web.Dashboard;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import api.Seller.setting.APIAccount;
import utilities.account.AccountTest;
import utilities.api.thirdparty.APIMailnesia;
import utilities.api.thirdparty.KibanaAPI;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.data.DataGenerator.TimeUnits;
import utilities.driver.InitWebdriver;
import utilities.enums.PaymentMethod;
import utilities.enums.newpackage.NewPackage;
import utilities.links.Links;
import utilities.model.dashboard.setting.packageinfo.PackageInfo;
import utilities.model.dashboard.setting.packageinfo.PaymentCompleteInfo;
import utilities.model.dashboard.setting.packageinfo.PlanPaymentReview;
import utilities.model.dashboard.setting.plan.PlanStatus;
import utilities.model.dashboard.setupstore.PurchasePlanDG;
import utilities.model.dashboard.setupstore.SetupStoreDG;
import utilities.packageplan.PlanMoney;
import utilities.thirdparty.Mailnesia;
import utilities.thirdparty.Mailnesia.MailType;
import web.Dashboard.home.HomePage;
import web.Dashboard.login.LoginPage;
import web.Dashboard.settings.account.AccountPage;
import web.Dashboard.settings.plans.PackagePayment;
import web.Dashboard.settings.plans.PlansPage;
import web.Dashboard.settings.storeinformation.StoreInformation;
import web.Dashboard.signup.SetUpStorePage;
import web.Dashboard.signup.SignupPage;
import web.Dashboard.signup.VerifyMailContent;

public class RefactorSignupDB extends BaseTest {

	SignupPage signupPage;
	LoginPage loginPage;
	HomePage homePage;
	PlansPage plansPage;
	SetUpStorePage setupStorePage;
	PackagePayment packagePaymentPage;
	AccountPage accountPage;
	StoreInformation storeInfoPage;

	SetupStoreDG storeDG;
	PurchasePlanDG planPaymentDG;

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
		storeDG = new SetupStoreDG();
		planPaymentDG = new PurchasePlanDG();
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

	String getActivationKey(SetupStoreDG store) {
		return new KibanaAPI().getKeyFromKibana(store.getUsername().matches("^\\d+$") ? "%s:%s".formatted(store.getPhoneCode(), store.getUsername()) : store.getUsername(), "activationKey");
	}	

	void registerAccount(SetupStoreDG store) {
		signupPage.navigate(store.getDomain());
		boolean isLanguageDisplayed = signupPage.isLanguageDropdownDisplayed();

		//Validate if Select Display Language dropdown appears
		Assert.assertEquals(isLanguageDisplayed, canSwitchLanguage(store.getDomain()));

		if (isLanguageDisplayed) signupPage.selectDisplayLanguage("ENG");
		signupPage.fillOutSignupForm(store);
		signupPage.provideVerificationCode(store);
	}

	void selectPackage(SetupStoreDG store, PurchasePlanDG packagePayment) {
		List<String> periodOptions = plansPage.getPackagePeriodOptions();

		//Validate package period options
		Assert.assertEquals(periodOptions.size(), periodOptionCount(store.getDomain()));

		plansPage.selectDuration(packagePayment.getPeriod());

		//Validate if Free Trial button appears
		Assert.assertEquals(plansPage.isFreeTrialBtnDisplayed(), isFreeTrialOffered(store.getDomain()));

		List<PackageInfo> availablePackages = plansPage.getPackageInfo();

		//Validate the number of packages available
		Assert.assertEquals(availablePackages.size(), availablePackageCount(store.getCountry()));

		//Validate currency
		availablePackages.stream().forEach(e -> Assert.assertTrue(e.getTotalPrice().matches(currencyRegex(store.getCountry()))));

		plansPage.subscribeToPackage(packagePayment.getNewPackage());
	}

	PaymentCompleteInfo selectPayment(SetupStoreDG store, PurchasePlanDG packageAndPayment) {
		List<String> periodPaymentOptions = packagePaymentPage.getPackagePeriodOptions();

		//Validate package period options
		Assert.assertEquals(periodPaymentOptions.size(), periodOptionCount(store.getDomain()));

		//Validate available payment options
		List<String> onlinePaymentOptions = packagePaymentPage.getOnlinePaymentOptions();
		Assert.assertEquals(onlinePaymentOptions, onlinePaymentOptions(store.getCountry()));

		//Validate currency
		periodPaymentOptions.stream().forEach(e -> Assert.assertTrue(e.matches(pricePerYearRegex(store.getCountry()))));

		//Validate currency
		PlanPaymentReview planPaymentReview = packagePaymentPage.getFinalizePackageInfo();
		Assert.assertTrue(planPaymentReview.getBasePrice().matches(currencyRegex(store.getCountry())));
		Assert.assertTrue(planPaymentReview.getFinalTotal().matches(currencyRegex(store.getCountry())));
		if (store.getDomain().contains("biz")) {
			Assert.assertNull(planPaymentReview.getVatPrice());
		} else {
			Assert.assertTrue(planPaymentReview.getVatPrice().matches(currencyRegex(store.getCountry())));
		}

		packagePaymentPage.selectPaymentMethod(packageAndPayment.getPaymentMethod());
		String orderId = packagePaymentPage.completePayment(packageAndPayment.getPaymentMethod());
		packagePaymentPage.approvePackageInInternalTool(packageAndPayment.getPaymentMethod(), orderId);

		//Validate currency
		PaymentCompleteInfo paymentCompleteInfo = packagePaymentPage.getPaymentCompleteInfo();
		Assert.assertTrue(paymentCompleteInfo.getTotal().matches(currencyRegex(store.getCountry())));
		packagePaymentPage.clickBackToDashboardBtn();

		return paymentCompleteInfo;
	}

	void validatePackageIsEnabled(PurchasePlanDG packageAndPayment) {
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab();
		Assert.assertEquals(accountPage.getPlanInfo().get(0).get(2), NewPackage.getValue(packageAndPayment.getNewPackage()));
	}

	void validatePackageIsEnabledExp(PurchasePlanDG packageAndPayment, String subscriptionDate, String expiryDate) {
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab();

		List<List<String>> planInfo = accountPage.getPlanInfo();
		Assert.assertEquals(planInfo.get(0).get(0), subscriptionDate);
		Assert.assertEquals(planInfo.get(0).get(1), expiryDate);
		Assert.assertEquals(planInfo.get(0).get(2), NewPackage.getValue(packageAndPayment.getNewPackage()));
	}

	List<MailType> decideOnMailTypeToCheck(SetupStoreDG store, PurchasePlanDG purchasePackage) {

		List<MailType> mailTypes = new ArrayList<>();

		if (store.getAccountType().contentEquals("EMAIL")) {
			mailTypes.add(MailType.VERIFICATION_CODE);
			mailTypes.add(MailType.ACCOUNT_REGISTRATION);
			mailTypes.add(MailType.WELCOME);
			if (purchasePackage.getPaymentMethod() == PaymentMethod.BANKTRANSFER) mailTypes.add(MailType.PAYMENT_CONFIRMATION);
			mailTypes.add(MailType.SUCCESSFUL_PAYMENT);
		} else {
			if (store.isContactProvided()) {
				mailTypes.add(MailType.WELCOME);
				if (purchasePackage.getPaymentMethod() == PaymentMethod.BANKTRANSFER) mailTypes.add(MailType.PAYMENT_CONFIRMATION);
				mailTypes.add(MailType.SUCCESSFUL_PAYMENT);
			}
		}
		return mailTypes;
	}	
	List<MailType> decidedOnMailTypeToCheckOnRenewing(SetupStoreDG store, PurchasePlanDG purchasePackage) {
		
		List<MailType> mailTypes = new ArrayList<>();

		if (!store.getUsername().matches("\\d+")) {
			if (purchasePackage.getPaymentMethod() == PaymentMethod.BANKTRANSFER) mailTypes.add(MailType.PAYMENT_CONFIRMATION);
			mailTypes.add(MailType.SUCCESSFUL_PAYMENT);
		}
		return mailTypes;
	}		
	
	void checkMail(List<MailType> mailTypes, SetupStoreDG store, PaymentCompleteInfo paymentCompleteInfo, String expiryDate) {
		
		Mailnesia mailPage = new Mailnesia(driver);

		mailPage.waitTillThereAreMails(store.getEmail(), mailTypes.size());

		for (MailType e : mailTypes) {
			String content = new Mailnesia(driver).getEmailBody(e);
			switch (e) {
			case VERIFICATION_CODE -> VerifyMailContent.verificationCode(content, store);
			case ACCOUNT_REGISTRATION -> VerifyMailContent.successfulAccountRegistration(content, store);
			case WELCOME -> VerifyMailContent.welcome(content, store);
			case PAYMENT_CONFIRMATION -> VerifyMailContent.paymentConfirmation(content, store, paymentCompleteInfo);
			case SUCCESSFUL_PAYMENT -> VerifyMailContent.successfulPayment(content, store, paymentCompleteInfo, expiryDate);
			default -> throw new IllegalArgumentException("Unexpected value: " + e);
			}
		}
	}

	void validateTimezoneShopName() {
		storeInfoPage.clickStoreInformationTab();
		Assert.assertEquals(storeInfoPage.getShopName(), storeDG.getName());
		Assert.assertEquals(storeInfoPage.getTimezone(), storeDG.getTimezone());
	}

//	@Test
	public void SignupWithExistingAccount() throws Exception {

		/*Domain .vn*/
		storeDG.randomStoreData();
		storeDG.setCountry("Vietnam");
		storeDG.setUsername(AccountTest.ADMIN_SHOP_VI_USERNAME);
		storeDG.setPassword(AccountTest.ADMIN_SHOP_VI_PASSWORD);

		signupPage.navigate(Links.DOMAIN + Links.SIGNUP_PATH).selectDisplayLanguage("ENG");
		signupPage.fillOutSignupForm(storeDG);
		signupPage.verifyUsernameExistError(language).completeVerify();

		storeDG.setCountry("Vietnam");
		storeDG.setUsername(AccountTest.ADMIN_ACCOUNT_THANG);
		storeDG.setPassword(AccountTest.ADMIN_PASSWORD_THANG);

		signupPage.navigate(Links.DOMAIN + Links.SIGNUP_PATH).selectDisplayLanguage("ENG");
		signupPage.fillOutSignupForm(storeDG);
		signupPage.verifyUsernameExistError(language).completeVerify();

		/*Domain .biz*/
		storeDG.setCountry("Taiwan");
		storeDG.setUsername("905283336");
		storeDG.setPassword(AccountTest.ADMIN_PASSWORD_TIEN);

		signupPage.navigate(Links.DOMAIN_BIZ + Links.SIGNUP_PATH);
		signupPage.fillOutSignupForm(storeDG);
		signupPage.verifyUsernameExistError(language).completeVerify();

		storeDG.setCountry("Australia");
		storeDG.setUsername("tienbirdfeedbiz@mailnesia.com");
		storeDG.setPassword(AccountTest.ADMIN_PASSWORD_TIEN);

		signupPage.navigate(Links.DOMAIN_BIZ + Links.SIGNUP_PATH);
		signupPage.fillOutSignupForm(storeDG);
		signupPage.verifyUsernameExistError("ENG").completeVerify();
	}

//	@Test
	public void RegisterThenLoginToContinue() {

		//Randomize data
		storeDG.randomStoreData();
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		System.out.println(storeDG);
		System.out.println(planPaymentDG);

		/* Sign up */
		registerAccount(storeDG);

		if (storeDG.getDomain().contains("biz")) {
			new LoginPage(driver).navigateBiz();
		} else {
			new LoginPage(driver).navigate();
		}

		new LoginPage(driver).performLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());		

		/* Setup store */
		setupStorePage.setupShopExp(storeDG);

		/* Select package */
		selectPackage(storeDG, planPaymentDG);

		/* Select payment */
		selectPayment(storeDG, planPaymentDG);

		//Validate package plan is activated
		validatePackageIsEnabled(planPaymentDG);

		//Validate Shop Name and timezone are displayed as expected
		validateTimezoneShopName();
	}	

//	@Test
	public void ResendOTPForMailAccount() throws Exception {
		storeDG.setAccountType("EMAIL");
		storeDG.randomStoreData();
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		System.out.println(storeDG);
		System.out.println(planPaymentDG);

		/*Register account*/
		signupPage.navigate(storeDG.getDomain());
		if (signupPage.isLanguageDropdownDisplayed()) signupPage.selectDisplayLanguage("ENG");
		signupPage.fillOutSignupForm(storeDG);

		String firstKey = getActivationKey(storeDG);

		signupPage.inputVerificationCode(firstKey);
		signupPage.clickResendOTP().clickConfirmOTPBtn();
		signupPage.verifyVerificationCodeError("ENG");

		commonAction.sleepInMiliSecond(5000);
		String resentCode = getActivationKey(storeDG);
		signupPage.inputVerificationCode(resentCode);

		Assert.assertNotEquals(resentCode, firstKey, "Resent verification code");

		signupPage.clickConfirmOTPBtn();

		/* Setup store */
		setupStorePage.setupShopExp(storeDG);

		/* Select package */
		selectPackage(storeDG, planPaymentDG);

		/* Select payment */
		selectPayment(storeDG, planPaymentDG);

		//Validate package plan is activated
		validatePackageIsEnabled(planPaymentDG);

		//Validate Shop Name and timezone are displayed as expected
		validateTimezoneShopName();
	}

//	@Test
	public void ResendOTPForPhoneAccount() throws Exception {
		storeDG.setAccountType("MOBILE");
		storeDG.randomStoreData();
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		System.out.println(storeDG);
		System.out.println(planPaymentDG);

		/*Register account*/
		signupPage.navigate(storeDG.getDomain());
		if (signupPage.isLanguageDropdownDisplayed()) signupPage.selectDisplayLanguage("ENG");
		signupPage.fillOutSignupForm(storeDG);

		String firstKey = getActivationKey(storeDG);

		signupPage.inputVerificationCode(firstKey);
		commonAction.sleepInMiliSecond(5000, "Wait a little before triggering another API"); 
		signupPage.clickResendOTP().clickConfirmOTPBtn();
		signupPage.verifyVerificationCodeError("ENG");

		//		commonAction.sleepInMiliSecond(5000);
		String resentCode = getActivationKey(storeDG);
		signupPage.inputVerificationCode(resentCode);

		Assert.assertNotEquals(resentCode, firstKey, "Resent verification code");

		signupPage.clickConfirmOTPBtn();

		/* Setup store */
		setupStorePage.setupShopExp(storeDG);

		/* Select package */
		selectPackage(storeDG, planPaymentDG);

		/* Select payment */
		selectPayment(storeDG, planPaymentDG);

		//Validate package plan is activated
		validatePackageIsEnabled(planPaymentDG);

		//Validate Shop Name and timezone are displayed as expected
		validateTimezoneShopName();
	}

//	@Test
	public void SignupByMail() {

		//Randomize data
		storeDG.setAccountType("EMAIL");
		storeDG.randomStoreData();
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		System.out.println(storeDG);
		System.out.println(planPaymentDG);

		/* Sign up */
		registerAccount(storeDG);

		/* Setup store */
		setupStorePage.setupShopExp(storeDG);

		/* Select package */
		selectPackage(storeDG, planPaymentDG);

		/* Select payment */
		selectPayment(storeDG, planPaymentDG);

		//Validate package plan is activated
		validatePackageIsEnabled(planPaymentDG);

		//Validate Shop Name and timezone are displayed as expected
		validateTimezoneShopName();
	}

//	@Test
	public void SignupByPhone() {

		//Randomize data
		storeDG.setAccountType("MOBILE");
		storeDG.randomStoreData();
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		System.out.println(storeDG);
		System.out.println(planPaymentDG);

		/* Sign up */
		registerAccount(storeDG);

		/* Setup store */
		setupStorePage.setupShopExp(storeDG);

		/* Select package */
		selectPackage(storeDG, planPaymentDG);

		/* Select payment */
		selectPayment(storeDG, planPaymentDG);

		//Validate package plan is activated
		validatePackageIsEnabled(planPaymentDG);

		//Validate Shop Name and timezone are displayed as expected
		validateTimezoneShopName();
	}



//	@Test
	public void ActivateFreeTrialWhenCreatingShop() {

		//Randomize data
		storeDG.setDomain(Links.DOMAIN_BIZ + Links.SIGNUP_PATH);
		storeDG.randomStoreData();
		System.out.println(storeDG);

		/* Sign up */
		signupPage.navigate(storeDG.getDomain());
		signupPage.fillOutSignupForm(storeDG);
		signupPage.provideVerificationCode(storeDG);

		/* Setup store */
		setupStorePage.setupShopExp(storeDG);

		/* Select package */
		List<String> periodOptions = plansPage.getPackagePeriodOptions();
		plansPage.selectDuration(new Random().nextInt(0, periodOptions.size()) +1);
		plansPage.clickFreeTrialBtn();
		homePage.getToastMessage();
		homePage.navigateToPage("Home");

		//Validate package plan is activated
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab();
		Assert.assertEquals(accountPage.getPlanInfo().get(0).get(2), defaultFreeTrialPackage(storeDG.getCountry()));
	}

//	@Test
	public void ActivateFreeTrialAfterShopCreated() {

		//Randomize data
		storeDG.setDomain(Links.DOMAIN_BIZ + Links.SIGNUP_PATH);
		storeDG.randomStoreData();
		System.out.println(storeDG);

		/* Sign up */
		signupPage.navigate(storeDG.getDomain());
		signupPage.fillOutSignupForm(storeDG);
		signupPage.provideVerificationCode(storeDG);

		/* Setup store */
		setupStorePage.setupShopExp(storeDG);

		/* Select package */
		plansPage.getPackagePeriodOptions();

		driver.quit();

		driver = new InitWebdriver().getDriver("chrome", "false");
		homePage = new HomePage(driver);
		plansPage = new PlansPage(driver);
		accountPage = new AccountPage(driver);
		storeInfoPage = new StoreInformation(driver);

		new LoginPage(driver).navigateBiz().performLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());
		homePage.clickUpgradeNow();
		plansPage.clickFreeTrialBtn();
		homePage.getToastMessage();

		//Validate package plan is activated
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab();
		Assert.assertEquals(accountPage.getPlanInfo().get(0).get(2), defaultFreeTrialPackage(storeDG.getCountry()));

		validateTimezoneShopName();
	}

//	@Test(invocationCount = 1)
	public void SignupPurchasePackageExp() {

		//Randomize data
		storeDG.randomStoreData();
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		System.out.println(storeDG);
		System.out.println(planPaymentDG);

		/* Sign up */
		registerAccount(storeDG);

		/* Setup store */
		setupStorePage.setupShopExp(storeDG);

		/* Select package */
		selectPackage(storeDG, planPaymentDG);

		/* Select payment */
		PaymentCompleteInfo paymentCompleteInfo = selectPayment(storeDG, planPaymentDG);

		String subcriptionDate = DataGenerator.getCurrentDate("dd-MM-yyyy");
		String expiryDate = DataGenerator.forwardTimeWithFormat(planPaymentDG.getPeriod(), TimeUnits.YEARS, "dd-MM-yyyy");

		//Validate package plan is activated
		validatePackageIsEnabledExp(planPaymentDG, subcriptionDate, expiryDate);

		//Validate Shop Name and timezone are displayed as expected
		validateTimezoneShopName();

		List<MailType> availableMail = decideOnMailTypeToCheck(storeDG, planPaymentDG);
		checkMail(availableMail, storeDG, paymentCompleteInfo, expiryDate.replaceAll("-", "/"));
	}

	@Test
	public void SignupPurchasePackage() {

		//Randomize data
		storeDG.randomStoreData("Malaysia");
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		System.out.println(storeDG);
		System.out.println(planPaymentDG);

		/* Sign up */
		signupPage.navigate(storeDG.getDomain());
		boolean isLanguageDisplayed = signupPage.isLanguageDropdownDisplayed();

		//Validate if Select Display Language dropdown appears
		Assert.assertEquals(isLanguageDisplayed, canSwitchLanguage(storeDG.getDomain()));

		if (isLanguageDisplayed) signupPage.selectDisplayLanguage("ENG");
		signupPage.fillOutSignupForm(storeDG);
		signupPage.provideVerificationCode(storeDG);

		/* Setup store */
		setupStorePage.setupShopExp(storeDG);

		/* Select package */
		List<String> periodOptions = plansPage.getPackagePeriodOptions();

		//Validate package period options
		Assert.assertEquals(periodOptions.size(), periodOptionCount(storeDG.getDomain()));

		plansPage.selectDuration(new Random().nextInt(0, periodOptions.size()) +1);

		//Validate if Free Trial button appears
		Assert.assertEquals(plansPage.isFreeTrialBtnDisplayed(), isFreeTrialOffered(storeDG.getDomain()));

		List<PackageInfo> availablePackages = plansPage.getPackageInfo();

		//Validate the number of packages available
		Assert.assertEquals(availablePackages.size(), availablePackageCount(storeDG.getCountry()));

		//Validate currency
		availablePackages.stream().forEach(e -> Assert.assertTrue(e.getTotalPrice().matches(currencyRegex(storeDG.getCountry()))));

		plansPage.subscribeToPackage(planPaymentDG.getNewPackage());

		/* Select payment */
		List<String> periodPaymentOptions = packagePaymentPage.getPackagePeriodOptions();

		//Validate package period options
		Assert.assertEquals(periodPaymentOptions.size(), periodOptionCount(storeDG.getDomain()));

		//Validate available payment options
		List<String> onlinePaymentOptions = packagePaymentPage.getOnlinePaymentOptions();
		Assert.assertEquals(onlinePaymentOptions, onlinePaymentOptions(storeDG.getCountry()));

		//Validate currency
		periodPaymentOptions.stream().forEach(e -> Assert.assertTrue(e.matches(pricePerYearRegex(storeDG.getCountry()))));

		//Validate currency
		PlanPaymentReview finalizedPackagePayment =  packagePaymentPage.getFinalizePackageInfo();
		Assert.assertTrue(finalizedPackagePayment.getBasePrice().matches(currencyRegex(storeDG.getCountry())));
		Assert.assertTrue(finalizedPackagePayment.getFinalTotal().matches(currencyRegex(storeDG.getCountry())));
		if (storeDG.getDomain().contains("biz")) {
			Assert.assertNull(finalizedPackagePayment.getVatPrice());
		} else {
			Assert.assertTrue(finalizedPackagePayment.getVatPrice().matches(currencyRegex(storeDG.getCountry())));
		}

		packagePaymentPage.selectPaymentMethod(planPaymentDG.getPaymentMethod());
		String orderId = packagePaymentPage.completePayment(planPaymentDG.getPaymentMethod());
		packagePaymentPage.approvePackageInInternalTool(planPaymentDG.getPaymentMethod(), orderId);

		//Validate currency
		Assert.assertTrue(packagePaymentPage.getPaymentCompleteInfo().getTotal().matches(currencyRegex(storeDG.getCountry())));
		packagePaymentPage.clickBackToDashboardBtn();

		//Validate package plan is activated
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab();
		Assert.assertEquals(accountPage.getPlanInfo().get(0).get(2), NewPackage.getValue(planPaymentDG.getNewPackage()));

		//Validate Shop Name and timezone are displayed as expected
		storeInfoPage.clickStoreInformationTab();
		Assert.assertEquals(storeInfoPage.getShopName(), storeDG.getName());
		Assert.assertEquals(storeInfoPage.getTimezone(), storeDG.getTimezone());
	}	

//	@Test(invocationCount = 2)
	public void AbortPaymentProcessVN() {
		
		//Randomize data
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("0830659539");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Canada");
//		storeDG.setUsername("bao142@mailnesia.com");
//		storeDG.setEmail("bao142@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN_BIZ + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Cameron");
//		storeDG.setUsername("665803698");
//		storeDG.setDomain(Links.DOMAIN_BIZ + Links.LOGIN_PATH);		
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("automation0-shop995@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
		storeDG.randomStoreData("Vietnam");
		storeDG.setUsername("auto0-shop0777031268@mailnesia.com");
		storeDG.setEmail("auto0-shop0777031268@mailnesia.com");
		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
		System.out.println(storeDG);
		
		PlanStatus currentPlan = new APIAccount(new Login().setLoginInformation(storeDG.getPhoneCode(), storeDG.getUsername(), storeDG.getPassword()).getLoginInformation()).getAvailablePlanInfo().get(0);
		NewPackage currentPlanName = NewPackage.getKeyFromValue(currentPlan.getBundlePackagePlanName());
		Instant registeredDate = Instant.parse(currentPlan.getRegisterPackageDate());
		Instant expiryDate = Instant.parse(currentPlan.getExpiredPackageDate());
		
		new LoginPage(driver).navigate(storeDG.getDomain()).performLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());
		
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab().clickRenew();
		
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		planPaymentDG.setNewPackage(currentPlanName);
		
		plansPage.selectDuration(planPaymentDG.getPeriod());
		plansPage.subscribeToPackage(planPaymentDG.getNewPackage());
		
		//Bug cache not cleared
		if (planPaymentDG.getPeriod() !=1) plansPage.clickContinueOnFeatureComparisionDialog();
		
		packagePaymentPage.selectPaymentMethod(planPaymentDG.getPaymentMethod());
		packagePaymentPage.abandonPayment(planPaymentDG.getPaymentMethod());
		
		commonAction.navigateBack();
		
		//Validate package plan is activated
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab();
		
		String dateFormat = "dd-MM-yyyy";
		String subcriptionDate = DateTimeFormatter.ofPattern(dateFormat).format(LocalDateTime.ofInstant(registeredDate, ZoneId.systemDefault()));
		String newExpiryDate = DateTimeFormatter.ofPattern(dateFormat).format(LocalDateTime.ofInstant(expiryDate, ZoneId.systemDefault()));
		validatePackageIsEnabledExp(planPaymentDG, subcriptionDate, newExpiryDate);
	}		
//	@Test(invocationCount = 2)
	public void AbortPaymentProcessBiz() {
		
		//Randomize data
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("0830659539");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Canada");
//		storeDG.setUsername("bao142@mailnesia.com");
//		storeDG.setEmail("bao142@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN_BIZ + Links.LOGIN_PATH);
		storeDG.randomStoreData("Cameroon");
		storeDG.setUsername("665803698");
		storeDG.setDomain(Links.DOMAIN_BIZ + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("automation0-shop995@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("auto0-shop0777031268@mailnesia.com");
//		storeDG.setEmail("auto0-shop0777031268@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
		System.out.println(storeDG);
		
		PlanStatus currentPlan = new APIAccount(new Login().setLoginInformation(storeDG.getPhoneCode(), storeDG.getUsername(), storeDG.getPassword()).getLoginInformation()).getAvailablePlanInfo().get(0);
		NewPackage currentPlanName = NewPackage.getKeyFromValue(currentPlan.getBundlePackagePlanName());
		Instant registeredDate = Instant.parse(currentPlan.getRegisterPackageDate());
		Instant expiryDate = Instant.parse(currentPlan.getExpiredPackageDate());
		
		new LoginPage(driver).navigate(storeDG.getDomain()).performLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());
		
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab().clickRenew();
		
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		planPaymentDG.setNewPackage(currentPlanName);
		
		plansPage.selectDuration(planPaymentDG.getPeriod());
		plansPage.subscribeToPackage(planPaymentDG.getNewPackage());
		
		//Bug cache not cleared
		if (planPaymentDG.getPeriod() !=1) plansPage.clickContinueOnFeatureComparisionDialog();
		
		packagePaymentPage.selectPaymentMethod(planPaymentDG.getPaymentMethod());
		packagePaymentPage.abandonPayment(planPaymentDG.getPaymentMethod());
		
		commonAction.navigateBack();
		
		//Validate package plan is activated
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab();
		
		String dateFormat = "dd-MM-yyyy";
		String subcriptionDate = DateTimeFormatter.ofPattern(dateFormat).format(LocalDateTime.ofInstant(registeredDate, ZoneId.systemDefault()));
		String newExpiryDate = DateTimeFormatter.ofPattern(dateFormat).format(LocalDateTime.ofInstant(expiryDate, ZoneId.systemDefault()));
		validatePackageIsEnabledExp(planPaymentDG, subcriptionDate, newExpiryDate);
	}		
	
//	@Test
	public void AbandonPaymentThenEnableFreeTrial() {
		
		//Randomize data
		storeDG.setDomain(Links.DOMAIN_BIZ + Links.SIGNUP_PATH);
		storeDG.setAccountType("MOBILE");
		storeDG.randomStoreData();
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		planPaymentDG.setPaymentMethod(PaymentMethod.PAYPAL); //Reset payment method to PAYPAL
		System.out.println(storeDG);
		System.out.println(planPaymentDG);
		
		/* Sign up */
		signupPage.navigate(storeDG.getDomain());
		boolean isLanguageDisplayed = signupPage.isLanguageDropdownDisplayed();
		
		//Validate if Select Display Language dropdown appears
		Assert.assertEquals(isLanguageDisplayed, canSwitchLanguage(storeDG.getDomain()));
		
		if (isLanguageDisplayed) signupPage.selectDisplayLanguage("ENG");
		signupPage.fillOutSignupForm(storeDG);
		signupPage.provideVerificationCode(storeDG);
		
		/* Setup store */
		setupStorePage.setupShopExp(storeDG);
		
		/* Select package */
		List<String> periodOptions = plansPage.getPackagePeriodOptions();
		
		//Validate package period options
		Assert.assertEquals(periodOptions.size(), periodOptionCount(storeDG.getDomain()));
		
		plansPage.selectDuration(planPaymentDG.getPeriod());
		
		//Validate if Free Trial button appears
		Assert.assertEquals(plansPage.isFreeTrialBtnDisplayed(), isFreeTrialOffered(storeDG.getDomain()));
		
		List<PackageInfo> availablePackages = plansPage.getPackageInfo();
		
		//Validate the number of packages available
		Assert.assertEquals(availablePackages.size(), availablePackageCount(storeDG.getCountry()));
		
		//Validate currency
		availablePackages.stream().forEach(e -> Assert.assertTrue(e.getTotalPrice().matches(currencyRegex(storeDG.getCountry()))));
		
		plansPage.subscribeToPackage(planPaymentDG.getNewPackage());
		
		/* Select payment */
		List<String> periodPaymentOptions = packagePaymentPage.getPackagePeriodOptions();
		
		//Validate package period options
		Assert.assertEquals(periodPaymentOptions.size(), periodOptionCount(storeDG.getDomain()));
		
		//Validate available payment options
		List<String> onlinePaymentOptions = packagePaymentPage.getOnlinePaymentOptions();
		Assert.assertEquals(onlinePaymentOptions, onlinePaymentOptions(storeDG.getCountry()));
		
		//Validate currency
		periodPaymentOptions.stream().forEach(e -> Assert.assertTrue(e.matches(pricePerYearRegex(storeDG.getCountry()))));
		
		//Validate currency
		PlanPaymentReview finalizedPackagePayment =  packagePaymentPage.getFinalizePackageInfo();
		Assert.assertTrue(finalizedPackagePayment.getBasePrice().matches(currencyRegex(storeDG.getCountry())));
		Assert.assertTrue(finalizedPackagePayment.getFinalTotal().matches(currencyRegex(storeDG.getCountry())));
		if (storeDG.getDomain().contains("biz")) {
			Assert.assertNull(finalizedPackagePayment.getVatPrice());
		} else {
			Assert.assertTrue(finalizedPackagePayment.getVatPrice().matches(currencyRegex(storeDG.getCountry())));
		}
		
		packagePaymentPage.selectPaymentMethod(planPaymentDG.getPaymentMethod());
		packagePaymentPage.abandonPayment(planPaymentDG.getPaymentMethod());
		
		commonAction.navigateBack();
		
		plansPage.clickFreeTrialBtn();
		homePage.getToastMessage();
		homePage.navigateToPage("Home");

		//Validate package plan is activated
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab();
		Assert.assertEquals(accountPage.getPlanInfo().get(0).get(2), defaultFreeTrialPackage(storeDG.getCountry()));
		
		//Validate Shop Name and timezone are displayed as expected
		storeInfoPage.clickStoreInformationTab();
		Assert.assertEquals(storeInfoPage.getShopName(), storeDG.getName());
		Assert.assertEquals(storeInfoPage.getTimezone(), storeDG.getTimezone());
	}	

//	@Test(invocationCount = 4)
	public void ChangePackageVN() {

		//Randomize data
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("0830659539");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
		storeDG.randomStoreData("Vietnam");
		storeDG.setUsername("auto0-shop0777031268@mailnesia.com");
		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Cameroon");
//		storeDG.setUsername("665803698");
//		storeDG.setDomain(Links.DOMAIN_BIZ + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Germany");
//		storeDG.setUsername("auto0-shop15062030346@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
		System.out.println(storeDG);

		PlanStatus currentPlan = new APIAccount(new Login().setLoginInformation(storeDG.getPhoneCode(), storeDG.getUsername(), storeDG.getPassword()).getLoginInformation()).getAvailablePlanInfo().get(0);
		NewPackage currentPlanName = NewPackage.getKeyFromValue(currentPlan.getBundlePackagePlanName());
		Instant registeredDate = Instant.parse(currentPlan.getRegisterPackageDate());
		Instant expiryDate = Instant.parse(currentPlan.getExpiredPackageDate());
		int period = PlanMoney.deducePeriod(registeredDate, expiryDate);

		BigDecimal expectedRefund = PlanMoney.calculateRefund(storeDG.getCountry(), currentPlanName, period, PlanMoney.workoutRemainingDays(expiryDate));

		//Get new package
		for (int i=0; i<20; i++) {
			planPaymentDG.randomPackageAndPaymentMethod(storeDG);
			if (!planPaymentDG.getNewPackage().equals(currentPlanName)) break;
		}

		new LoginPage(driver).navigate(storeDG.getDomain()).performLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());

		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab().clickRenew();

		plansPage.selectDuration(planPaymentDG.getPeriod());

		plansPage.subscribeToPackage(planPaymentDG.getNewPackage()).clickContinueOnFeatureComparisionDialog();

		PlanPaymentReview planPaymentReview =  packagePaymentPage.getFinalizePackageInfo();

		BigDecimal actualTotalAmount = new BigDecimal(planPaymentReview.getFinalTotal().replaceAll("[^\\d+\\.]",""));
		
		//Compare refundAmount
		Assert.assertEquals(new BigDecimal(planPaymentReview.getRefundAmount().replaceAll("[^\\d+\\.]","")), expectedRefund);
		
		BigDecimal expectedTotal = PlanMoney.calculateTotalPrice(storeDG.getDomain(), planPaymentDG.getNewPackage(), planPaymentDG.getPeriod(), expectedRefund);
		
		if (expectedTotal.compareTo(BigDecimal.ZERO) == 0) {
			Assert.assertTrue(packagePaymentPage.isOnlinePaymentTabHidden());
		} else {
			Assert.assertFalse(packagePaymentPage.isOnlinePaymentTabHidden());
		}
		
		//Compare total amount
		Assert.assertEquals(actualTotalAmount.compareTo(expectedTotal), 0);
		
		if (expectedTotal.compareTo(BigDecimal.ZERO) == 0) {
			planPaymentDG.setPaymentMethod(PaymentMethod.BANKTRANSFER);
		}
		
		packagePaymentPage.selectPaymentMethod(planPaymentDG.getPaymentMethod());
		String orderId = packagePaymentPage.completePayment(planPaymentDG.getPaymentMethod());

		if (expectedTotal.compareTo(BigDecimal.ZERO) != 0) {
			packagePaymentPage.approvePackageInInternalTool(planPaymentDG.getPaymentMethod(), orderId);
		}

		packagePaymentPage.clickBackToDashboardBtn();

		//Validate package plan is activated
		String subcriptionDate = DataGenerator.getCurrentDate("dd-MM-yyyy");
		String newExpiryDate = DataGenerator.forwardTimeWithFormat(planPaymentDG.getPeriod(), TimeUnits.YEARS, "dd-MM-yyyy");
		validatePackageIsEnabledExp(planPaymentDG, subcriptionDate, newExpiryDate);

	}
	
//	@Test(invocationCount = 3)
	public void ChangePackageBiz() {
		
		//Randomize data
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("0830659539");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("auto0-shop0777031268@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
		storeDG.randomStoreData("Cameroon");
		storeDG.setUsername("665803698");
		storeDG.setDomain(Links.DOMAIN_BIZ + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Germany");
//		storeDG.setUsername("auto0-shop15062030346@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
		System.out.println(storeDG);
		
		PlanStatus currentPlan = new APIAccount(new Login().setLoginInformation(storeDG.getPhoneCode(), storeDG.getUsername(), storeDG.getPassword()).getLoginInformation()).getAvailablePlanInfo().get(0);
		NewPackage currentPlanName = NewPackage.getKeyFromValue(currentPlan.getBundlePackagePlanName());
		Instant registeredDate = Instant.parse(currentPlan.getRegisterPackageDate());
		Instant expiryDate = Instant.parse(currentPlan.getExpiredPackageDate());
		int period = PlanMoney.deducePeriod(registeredDate, expiryDate);
		
		BigDecimal expectedRefund = PlanMoney.calculateRefund(storeDG.getCountry(), currentPlanName, period, PlanMoney.workoutRemainingDays(expiryDate));
		
		//Get new package
		for (int i=0; i<20; i++) {
			planPaymentDG.randomPackageAndPaymentMethod(storeDG);
			if (!planPaymentDG.getNewPackage().equals(currentPlanName)) break;
		}
		
		new LoginPage(driver).navigate(storeDG.getDomain()).performLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());
		
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab().clickRenew();
		
		plansPage.selectDuration(planPaymentDG.getPeriod());
		
		plansPage.subscribeToPackage(planPaymentDG.getNewPackage()).clickContinueOnFeatureComparisionDialog();
		
		PlanPaymentReview planPaymentReview =  packagePaymentPage.getFinalizePackageInfo();
		
		BigDecimal actualTotalAmount = new BigDecimal(planPaymentReview.getFinalTotal().replaceAll("[^\\d+\\.]",""));
		
		//Compare refundAmount
		Assert.assertEquals(new BigDecimal(planPaymentReview.getRefundAmount().replaceAll("[^\\d+\\.]","")), expectedRefund);
		
		BigDecimal expectedTotal = PlanMoney.calculateTotalPrice(storeDG.getDomain(), planPaymentDG.getNewPackage(), planPaymentDG.getPeriod(), expectedRefund);
		
		if (expectedTotal.compareTo(BigDecimal.ZERO) == 0) {
			Assert.assertTrue(packagePaymentPage.isOnlinePaymentTabHidden());
		} else {
			Assert.assertFalse(packagePaymentPage.isOnlinePaymentTabHidden());
		}
		
		//Compare total amount
		Assert.assertEquals(actualTotalAmount.compareTo(expectedTotal), 0);
		
		if (expectedTotal.compareTo(BigDecimal.ZERO) == 0) {
			planPaymentDG.setPaymentMethod(PaymentMethod.BANKTRANSFER);
		}
		
		packagePaymentPage.selectPaymentMethod(planPaymentDG.getPaymentMethod());
		String orderId = packagePaymentPage.completePayment(planPaymentDG.getPaymentMethod());
		
		if (expectedTotal.compareTo(BigDecimal.ZERO) != 0) {
			packagePaymentPage.approvePackageInInternalTool(planPaymentDG.getPaymentMethod(), orderId);
		}
		
		packagePaymentPage.clickBackToDashboardBtn();
		
		//Validate package plan is activated
		String subcriptionDate = DataGenerator.getCurrentDate("dd-MM-yyyy");
		String newExpiryDate = DataGenerator.forwardTimeWithFormat(planPaymentDG.getPeriod(), TimeUnits.YEARS, "dd-MM-yyyy");
		validatePackageIsEnabledExp(planPaymentDG, subcriptionDate, newExpiryDate);
		
	}	
	
//	@Test(invocationCount = 4)
	public void RenewPackageVN() {
		
		//Randomize data
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("0830659539");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Canada");
//		storeDG.setUsername("bao142@mailnesia.com");
//		storeDG.setEmail("bao142@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN_BIZ + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("automation0-shop995@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
		storeDG.randomStoreData("Vietnam");
		storeDG.setUsername("auto0-shop0777031268@mailnesia.com");
		storeDG.setEmail("auto0-shop0777031268@mailnesia.com");
		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
		System.out.println(storeDG);
		
		APIMailnesia.deleteAllEmails(storeDG.getEmail());
		
		PlanStatus currentPlan = new APIAccount(new Login().setLoginInformation(storeDG.getPhoneCode(), storeDG.getUsername(), storeDG.getPassword()).getLoginInformation()).getAvailablePlanInfo().get(0);
		NewPackage currentPlanName = NewPackage.getKeyFromValue(currentPlan.getBundlePackagePlanName());
		Instant registeredDate = Instant.parse(currentPlan.getRegisterPackageDate());
		Instant expiryDate = Instant.parse(currentPlan.getExpiredPackageDate());
		int period = PlanMoney.deducePeriod(registeredDate, expiryDate);
		
		new LoginPage(driver).navigate(storeDG.getDomain()).performLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());
		
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab().clickRenew();
		
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		planPaymentDG.setNewPackage(currentPlanName);
		
		plansPage.selectDuration(planPaymentDG.getPeriod());
		plansPage.subscribeToPackage(planPaymentDG.getNewPackage());
		
		//Bug cache not cleared
		if (planPaymentDG.getPeriod() !=1) plansPage.clickContinueOnFeatureComparisionDialog();
		
		PlanPaymentReview planPaymentReview =  packagePaymentPage.getFinalizePackageInfo();
		
		BigDecimal actualTotal = new BigDecimal(planPaymentReview.getFinalTotal().replaceAll("[^\\d+\\.]",""));
		
		BigDecimal expectedTotal = PlanMoney.calculatePriceIncludingTax(storeDG.getDomain(), planPaymentDG.getNewPackage(), planPaymentDG.getPeriod());
		
		//Compare total amount
		Assert.assertEquals(actualTotal.compareTo(expectedTotal), 0, "Actual: %s, expected: %s".formatted(actualTotal, expectedTotal));
		
		packagePaymentPage.selectPaymentMethod(planPaymentDG.getPaymentMethod());
		String orderId = packagePaymentPage.completePayment(planPaymentDG.getPaymentMethod());
		PaymentCompleteInfo paymentCompleteInfo = packagePaymentPage.getPaymentCompleteInfo();
		packagePaymentPage.approvePackageInInternalTool(planPaymentDG.getPaymentMethod(), orderId);
		
		packagePaymentPage.clickBackToDashboardBtn();
		
		//Validate package plan is activated
		String dateFormat = "dd-MM-yyyy";
		String subcriptionDate = DataGenerator.getCurrentDate(dateFormat);
		String newExpiryDate = DataGenerator.forwardTimeWithFormat(LocalDateTime.ofInstant(expiryDate, ZoneId.systemDefault()), planPaymentDG.getPeriod(), TimeUnits.YEARS, dateFormat);
		validatePackageIsEnabledExp(planPaymentDG, subcriptionDate, newExpiryDate);
		
		List<MailType> availableMail = decidedOnMailTypeToCheckOnRenewing(storeDG, planPaymentDG);
		checkMail(availableMail, storeDG, paymentCompleteInfo, newExpiryDate.replaceAll("-", "/"));
	}	
	
//	@Test(invocationCount = 3)
	public void RenewPackageBiz() {
		
		//Randomize data
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("0830659539");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
		storeDG.randomStoreData("Canada");
		storeDG.setUsername("bao142@mailnesia.com");
		storeDG.setEmail("bao142@mailnesia.com");
		storeDG.setDomain(Links.DOMAIN_BIZ + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("automation0-shop995@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("auto0-shop0777031268@mailnesia.com");
//		storeDG.setEmail("auto0-shop0777031268@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
		System.out.println(storeDG);
		
		APIMailnesia.deleteAllEmails(storeDG.getEmail());
		
		PlanStatus currentPlan = new APIAccount(new Login().setLoginInformation(storeDG.getPhoneCode(), storeDG.getUsername(), storeDG.getPassword()).getLoginInformation()).getAvailablePlanInfo().get(0);
		NewPackage currentPlanName = NewPackage.getKeyFromValue(currentPlan.getBundlePackagePlanName());
		Instant registeredDate = Instant.parse(currentPlan.getRegisterPackageDate());
		Instant expiryDate = Instant.parse(currentPlan.getExpiredPackageDate());
		int period = PlanMoney.deducePeriod(registeredDate, expiryDate);
		
		new LoginPage(driver).navigate(storeDG.getDomain()).performLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());
		
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab().clickRenew();
		
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		planPaymentDG.setNewPackage(currentPlanName);
		
		plansPage.selectDuration(planPaymentDG.getPeriod());
		plansPage.subscribeToPackage(planPaymentDG.getNewPackage());
		
		//Bug cache not cleared
		if (planPaymentDG.getPeriod() !=1) plansPage.clickContinueOnFeatureComparisionDialog();
		
		PlanPaymentReview planPaymentReview =  packagePaymentPage.getFinalizePackageInfo();
		
		BigDecimal actualTotal = new BigDecimal(planPaymentReview.getFinalTotal().replaceAll("[^\\d+\\.]",""));
		
		BigDecimal expectedTotal = PlanMoney.calculatePriceIncludingTax(storeDG.getDomain(), planPaymentDG.getNewPackage(), planPaymentDG.getPeriod());
		
		//Compare total amount
		Assert.assertEquals(actualTotal.compareTo(expectedTotal), 0, "Actual: %s, expected: %s".formatted(actualTotal, expectedTotal));
		
		packagePaymentPage.selectPaymentMethod(planPaymentDG.getPaymentMethod());
		String orderId = packagePaymentPage.completePayment(planPaymentDG.getPaymentMethod());
		PaymentCompleteInfo paymentCompleteInfo = packagePaymentPage.getPaymentCompleteInfo();
		packagePaymentPage.approvePackageInInternalTool(planPaymentDG.getPaymentMethod(), orderId);
		
		packagePaymentPage.clickBackToDashboardBtn();
		
		//Validate package plan is activated
		String dateFormat = "dd-MM-yyyy";
		String subcriptionDate = DataGenerator.getCurrentDate(dateFormat);
		String newExpiryDate = DataGenerator.forwardTimeWithFormat(LocalDateTime.ofInstant(expiryDate, ZoneId.systemDefault()), planPaymentDG.getPeriod(), TimeUnits.YEARS, dateFormat);
		validatePackageIsEnabledExp(planPaymentDG, subcriptionDate, newExpiryDate);
		
		List<MailType> availableMail = decidedOnMailTypeToCheckOnRenewing(storeDG, planPaymentDG);
		checkMail(availableMail, storeDG, paymentCompleteInfo, newExpiryDate.replaceAll("-", "/"));
	}	
	
	@Test(invocationCount = 2)
	public void PackageBenefitsVN() {
		
		//Randomize data
		storeDG.randomStoreData("Vietnam");
		storeDG.setUsername("0830659539");
		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Canada");
//		storeDG.setUsername("bao142@mailnesia.com");
//		storeDG.setEmail("bao142@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN_BIZ + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("automation0-shop995@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("auto0-shop0777031268@mailnesia.com");
//		storeDG.setEmail("auto0-shop0777031268@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
		System.out.println(storeDG);

		new LoginPage(driver).navigate(storeDG.getDomain()).performLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());
		
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab().clickRenew();
		
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		
		int maxPlanCount = storeDG.getCountry().contentEquals("Vietnam") ? 3 : 1;
		List<String> langList = new ArrayList<>();
		langList.add("ENG");
		if (canSwitchLanguage(storeDG.getDomain())) {
			langList.add("VIE");
		}
		
		for (String lang : langList) {
			if (canSwitchLanguage(storeDG.getDomain())) homePage.selectLanguage(lang);
			
			for (int duration=1 ;duration<=maxPlanCount; duration++) {
				plansPage.selectDuration(duration);
				
				List<List<String>> actualBenefits = plansPage.getPackageBenefits(storeDG.getCountry());
				
				List<List<String>> expectedBenefits = DataGenerator.getBenefitsByPlan(storeDG.getCountry(), duration, lang.substring(0, 2).toLowerCase()); 
				
				Assert.assertEquals(actualBenefits, expectedBenefits);
			}		
		}
	}
	
	@Test
	public void PackageBenefitsBiz() {
		
		//Randomize data
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("0830659539");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
		storeDG.randomStoreData("Canada");
		storeDG.setUsername("bao142@mailnesia.com");
		storeDG.setEmail("bao142@mailnesia.com");
		storeDG.setDomain(Links.DOMAIN_BIZ + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("automation0-shop995@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("auto0-shop0777031268@mailnesia.com");
//		storeDG.setEmail("auto0-shop0777031268@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
		System.out.println(storeDG);
		
		new LoginPage(driver).navigate(storeDG.getDomain()).performLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());
		
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab().clickRenew();
		
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		
		int maxPlanCount = storeDG.getCountry().contentEquals("Vietnam") ? 3 : 1;
		List<String> langList = new ArrayList<>();
		langList.add("ENG");
		if (canSwitchLanguage(storeDG.getDomain())) {
			langList.add("VIE");
		}
		
		for (String lang : langList) {
			if (canSwitchLanguage(storeDG.getDomain())) homePage.selectLanguage(lang);
			
			for (int duration=1 ;duration<=maxPlanCount; duration++) {
				plansPage.selectDuration(duration);
				
				List<List<String>> actualBenefits = plansPage.getPackageBenefits(storeDG.getCountry());
				
				List<List<String>> expectedBenefits = DataGenerator.getBenefitsByPlan(storeDG.getCountry(), duration, lang.substring(0, 2).toLowerCase()); 
				
				Assert.assertEquals(actualBenefits, expectedBenefits);
			}		
		}
	}
	
	
	@Test(invocationCount = 3)
	public void CompareBenefitsVN() {
		
		//Randomize data
		storeDG.randomStoreData("Vietnam");
		storeDG.setUsername("0830659539");
		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Canada");
//		storeDG.setUsername("bao142@mailnesia.com");
//		storeDG.setEmail("bao142@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN_BIZ + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("automation0-shop995@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("auto0-shop0777031268@mailnesia.com");
//		storeDG.setEmail("auto0-shop0777031268@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
		System.out.println(storeDG);
		
		
		PlanStatus currentPlan = new APIAccount(new Login().setLoginInformation(storeDG.getPhoneCode(), storeDG.getUsername(), storeDG.getPassword()).getLoginInformation()).getAvailablePlanInfo().get(0);
		NewPackage currentPlanName = NewPackage.getKeyFromValue(currentPlan.getBundlePackagePlanName());
		Instant registeredDate = Instant.parse(currentPlan.getRegisterPackageDate());
		Instant expiryDate = Instant.parse(currentPlan.getExpiredPackageDate());
		int period = PlanMoney.deducePeriod(registeredDate, expiryDate);
		
		new LoginPage(driver).navigate(storeDG.getDomain()).performLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());
		
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab().clickRenew();
		
		//Get new package
		for (int i=0; i<20; i++) {
			planPaymentDG.randomPackageAndPaymentMethod(storeDG);
			if (!planPaymentDG.getNewPackage().equals(currentPlanName)) break;
		}
		
		List<String> langList = new ArrayList<>();
		langList.add("ENG");
		if (canSwitchLanguage(storeDG.getDomain())) {
			langList.add("VIE");
		}
		
		for (String lang : langList) {
			if (canSwitchLanguage(storeDG.getDomain())) homePage.selectLanguage(lang);
			
			plansPage.selectDuration(planPaymentDG.getPeriod());
			plansPage.subscribeToPackage(planPaymentDG.getNewPackage());
			
			List<List<String>> actualBenefits = plansPage.getPackageBenefitComparision(storeDG.getCountry());
			plansPage.clickContinueOnFeatureComparisionDialog();
			commonAction.navigateBack();	
		}
	}	
	@Test
	public void CompareBenefitsBiz() {
		
		//Randomize data
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("0830659539");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
		storeDG.randomStoreData("Canada");
		storeDG.setUsername("bao142@mailnesia.com");
		storeDG.setEmail("bao142@mailnesia.com");
		storeDG.setDomain(Links.DOMAIN_BIZ + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("automation0-shop995@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
//		storeDG.randomStoreData("Vietnam");
//		storeDG.setUsername("auto0-shop0777031268@mailnesia.com");
//		storeDG.setEmail("auto0-shop0777031268@mailnesia.com");
//		storeDG.setDomain(Links.DOMAIN + Links.LOGIN_PATH);
		System.out.println(storeDG);
		
		
		PlanStatus currentPlan = new APIAccount(new Login().setLoginInformation(storeDG.getPhoneCode(), storeDG.getUsername(), storeDG.getPassword()).getLoginInformation()).getAvailablePlanInfo().get(0);
		NewPackage currentPlanName = NewPackage.getKeyFromValue(currentPlan.getBundlePackagePlanName());
		Instant registeredDate = Instant.parse(currentPlan.getRegisterPackageDate());
		Instant expiryDate = Instant.parse(currentPlan.getExpiredPackageDate());
		int period = PlanMoney.deducePeriod(registeredDate, expiryDate);
		
		new LoginPage(driver).navigate(storeDG.getDomain()).performLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());
		
		homePage.navigateToPage("Settings");
		accountPage.clickAccountTab().clickRenew();
		
		//Get new package
		for (int i=0; i<20; i++) {
			planPaymentDG.randomPackageAndPaymentMethod(storeDG);
			if (!planPaymentDG.getNewPackage().equals(currentPlanName)) break;
		}
		
		List<String> langList = new ArrayList<>();
		langList.add("ENG");
		if (canSwitchLanguage(storeDG.getDomain())) {
			langList.add("VIE");
		}
		
		for (String lang : langList) {
			if (canSwitchLanguage(storeDG.getDomain())) homePage.selectLanguage(lang);
			
			plansPage.selectDuration(planPaymentDG.getPeriod());
			plansPage.subscribeToPackage(planPaymentDG.getNewPackage());
			
			List<List<String>> actualBenefits = plansPage.getPackageBenefitComparision(storeDG.getCountry());
			plansPage.clickContinueOnFeatureComparisionDialog();
			commonAction.navigateBack();	
		}
	}	
	

	@AfterMethod
	public void writeResult(ITestResult result) throws IOException {
		super.writeResult(result);
		driver.quit();
	}

}
