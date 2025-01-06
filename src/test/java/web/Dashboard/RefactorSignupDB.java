package web.Dashboard;


import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import api.Seller.login.Login;
import api.Seller.setting.APIAccount;
import utilities.account.AccountTest;
import utilities.api.thirdparty.KibanaAPI;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.data.DataGenerator.TimeUnits;
import utilities.driver.InitWebdriver;
import utilities.enums.DisplayLanguage;
import utilities.enums.Domain;
import utilities.enums.PaymentMethod;
import utilities.enums.newpackage.NewPackage;
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
	HomePage homePage;
	PlansPage plansPage;
	SetUpStorePage setupStorePage;
	PackagePayment packagePaymentPage;
	AccountPage accountPage;
	StoreInformation storeInfoPage;

	SetupStoreDG storeDG;
	PurchasePlanDG planPaymentDG;

	String country, username, password;
	
	@BeforeClass
	void loadData() {
		if(Domain.valueOf(domain).equals(Domain.VN)) {
			country = AccountTest.ADMIN_PLAN_MAIL_VN_COUNTRY;
			username = AccountTest.ADMIN_PLAN_MAIL_VN_USERNAME;
			password = AccountTest.ADMIN_PLAN_MAIL_VN_PASSWORD;
		} else {
			country = AccountTest.ADMIN_PLAN_PHONE_BIZ_COUNTRY;
			username = AccountTest.ADMIN_PLAN_PHONE_BIZ_USERNAME;
			password = AccountTest.ADMIN_PLAN_PHONE_BIZ_PASSWORD;
		}
	}
	
	@BeforeMethod
	public void setup() {
		driver = new InitWebdriver().getDriver(browser, headless);
		signupPage = new SignupPage(driver, Domain.valueOf(domain));
		homePage = new HomePage(driver);
		setupStorePage = new SetUpStorePage(driver);
		packagePaymentPage = new PackagePayment(driver);
		accountPage = new AccountPage(driver, Domain.valueOf(domain));
		storeInfoPage = new StoreInformation(driver, Domain.valueOf(domain));
		commonAction = new UICommonAction(driver);
		plansPage = new PlansPage(driver);
		storeDG = new SetupStoreDG(Domain.valueOf(domain));
		planPaymentDG = new PurchasePlanDG();
	}

	boolean canSwitchLanguage(Domain domain) {
		//https://mediastep.atlassian.net/browse/BH-29615
		//https://mediastep.atlassian.net/browse/BH-29611
		return domain.equals(Domain.BIZ) ? false : true;
	}	
	boolean isFreeTrialOffered(Domain domain) {
		//https://mediastep.atlassian.net/browse/BH-30121
		return domain.equals(Domain.BIZ) ? true : false;
	}	
	int periodOptionCount(Domain domain) {
		//https://mediastep.atlassian.net/browse/BH-29680
		return domain.equals(Domain.BIZ) ? 1 : 3;
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

	List<PaymentMethod> availablePaymentOptions(String country) {
		List<PaymentMethod> payments = country.contentEquals("Vietnam") ? PaymentMethod.forVNShop() : PaymentMethod.forForeignShop();
		return payments.stream().sorted().collect(Collectors.toList());
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
		signupPage.navigate()
			.selectDisplayLanguage(DisplayLanguage.valueOf(language))
			.fillOutSignupForm(store)
			.provideVerificationCode(store);
	}

	void selectPackage(SetupStoreDG store, PurchasePlanDG packagePayment) {
		List<String> periodOptions = plansPage.getPackagePeriodOptions();

		//Validate package period options
		Assert.assertEquals(periodOptions.size(), periodOptionCount(store.getDomain()));

		plansPage.selectDuration(packagePayment.getPeriod());

		//Validate if Free Trial button appears
//		Assert.assertEquals(plansPage.isFreeTrialBtnDisplayed(), isFreeTrialOffered(store.getDomain()));

		List<PackageInfo> availablePackages = plansPage.getPackageInfo();

		//Validate the number of packages available
		Assert.assertEquals(availablePackages.size(), availablePackageCount(store.getCountry()));

		//Validate currency
		availablePackages.stream().forEach(e -> Assert.assertTrue(e.getTotalPrice().matches(currencyRegex(store.getCountry()))));

		plansPage.subscribeToPackage(packagePayment.getNewPackage());
	}

	PaymentCompleteInfo selectPayment(SetupStoreDG store, PurchasePlanDG packageAndPayment) {
		List<String> periodPaymentOptions = packagePaymentPage.getDurationOptions();

		//Validate package period options
		Assert.assertEquals(periodPaymentOptions.size(), periodOptionCount(store.getDomain()));

		//Validate available payment options
		Assert.assertEquals(packagePaymentPage.getAvailablePaymentOptions().stream().sorted().collect(Collectors.toList()), availablePaymentOptions(store.getCountry()));

		//Validate currency
		periodPaymentOptions.stream().forEach(e -> Assert.assertTrue(e.matches(pricePerYearRegex(store.getCountry()))));

		//Validate currency
		PlanPaymentReview planPaymentReview = packagePaymentPage.getFinalizePackageInfo();
		Assert.assertTrue(planPaymentReview.getBasePrice().matches(currencyRegex(store.getCountry())));
		Assert.assertTrue(planPaymentReview.getFinalTotal().matches(currencyRegex(store.getCountry())));
		if (store.getDomain().equals(Domain.BIZ)) {
			Assert.assertNull(planPaymentReview.getVatPrice());
		} else {
			Assert.assertTrue(planPaymentReview.getVatPrice().matches(currencyRegex(store.getCountry())));
		}

		String orderId = packagePaymentPage.payThenComplete(packageAndPayment.getPaymentMethod());
		packagePaymentPage.approvePackageInInternalTool(packageAndPayment.getPaymentMethod(), orderId);

		//Validate currency
		PaymentCompleteInfo paymentCompleteInfo = packagePaymentPage.getPaymentCompleteInfo();
		Assert.assertTrue(paymentCompleteInfo.getTotal().matches(currencyRegex(store.getCountry())));
		packagePaymentPage.clickBackToDashboardBtn();

		return paymentCompleteInfo;
	}

	void validatePackageIsEnabled(PurchasePlanDG packageAndPayment) {
		accountPage.navigateByURL();
		Assert.assertEquals(accountPage.getPlanInfo().get(0).get(2), NewPackage.getValue(packageAndPayment.getNewPackage()));
	}

	void validatePackageIsEnabledExp(PurchasePlanDG packageAndPayment, String subscriptionDate, String expiryDate) {
		accountPage.navigateByURL();

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
		storeInfoPage.navigateByURL();
		Assert.assertEquals(storeInfoPage.getShopName(), storeDG.getName());
		Assert.assertEquals(storeInfoPage.getTimezone(), storeDG.getTimezone());
	}

	@Test
	public void SignupWithExistingAccount() {

		String error = signupPage.navigate()
			.selectDisplayLanguage(DisplayLanguage.valueOf(language))
			.fillOutSignupForm(country, username, password)
			.getUsernameExistError();
		
		Assert.assertEquals(error, SignupPage.localizedUsernameAlreadyExistError(DisplayLanguage.valueOf(language)));
	}

	@Test
	public void RegisterThenLoginToContinue() {

		//Randomize data
		storeDG.randomStoreData();
		storeDG.setDomain(Domain.valueOf(domain));
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		System.out.println(storeDG);
		System.out.println(planPaymentDG);

		/* Sign up */
		registerAccount(storeDG);

		new LoginPage(driver, Domain.valueOf(domain)).navigate()
			.changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.performLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());		

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

	@Test
	public void ResendOTPForMailAccount() throws Exception {
		storeDG.setAccountType("EMAIL");
		storeDG.randomStoreData();
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		System.out.println(storeDG);
		System.out.println(planPaymentDG);

		/*Register account*/
		signupPage.navigate()
			.selectDisplayLanguage(DisplayLanguage.valueOf(language))
			.fillOutSignupForm(storeDG);

		String firstKey = getActivationKey(storeDG);
		
		UICommonAction.sleepInMiliSecond(5000, "Wait a little before triggering another API"); 

		signupPage.inputVerificationCode(firstKey).clickResendOTP().clickConfirmOTPBtn();
		signupPage.verifyVerificationCodeError(DisplayLanguage.valueOf(language).name());

		UICommonAction.sleepInMiliSecond(5000);
		
		String resentCode = getActivationKey(storeDG);
		
		Assert.assertNotEquals(resentCode, firstKey, "Verification code");
		
		signupPage.inputVerificationCode(resentCode).clickConfirmOTPBtn();

		/* Setup store */
		setupStorePage.setupShopExp(storeDG);

		/* Select package */
		selectPackage(storeDG, planPaymentDG);

		/* Select payment */
		selectPayment(storeDG, planPaymentDG);

		validatePackageIsEnabled(planPaymentDG);
		validateTimezoneShopName();
	}

//	@Test(description = "Make sure to bypass API limit check before running this TC")
	public void ResendOTPForPhoneAccount() throws Exception {
		storeDG.setAccountType("MOBILE");
		storeDG.randomStoreData();
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		System.out.println(storeDG);
		System.out.println(planPaymentDG);

		/*Register account*/
		signupPage.navigate()
			.selectDisplayLanguage(DisplayLanguage.valueOf(language))
			.fillOutSignupForm(storeDG);

		String firstKey = getActivationKey(storeDG);

		UICommonAction.sleepInMiliSecond(5000, "Wait a little before triggering another API"); 
		
		signupPage.inputVerificationCode(firstKey).clickResendOTP().clickConfirmOTPBtn();
		signupPage.verifyVerificationCodeError(DisplayLanguage.valueOf(language).name());

		UICommonAction.sleepInMiliSecond(5000);
		
		String resentCode = getActivationKey(storeDG);

		Assert.assertNotEquals(resentCode, firstKey, "Verification code");

		signupPage.inputVerificationCode(resentCode).clickConfirmOTPBtn();

		/* Setup store */
		setupStorePage.setupShopExp(storeDG);

		/* Select package */
		selectPackage(storeDG, planPaymentDG);

		/* Select payment */
		selectPayment(storeDG, planPaymentDG);

		validatePackageIsEnabled(planPaymentDG);
		validateTimezoneShopName();
	}

	@Test
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

		validatePackageIsEnabled(planPaymentDG);
		validateTimezoneShopName();
	}

	@Test
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

		validatePackageIsEnabled(planPaymentDG);
		validateTimezoneShopName();
	}



//	@Test(description = "Be cautious! This TC is not updated yet")
	public void ActivateFreeTrialWhenCreatingShop() {

		//Randomize data
		storeDG.setDomain(Domain.valueOf(domain));
		storeDG.randomStoreData();
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		System.out.println(storeDG);
		System.out.println(planPaymentDG);
		
		/* Sign up */
		registerAccount(storeDG);

		/* Setup store */
		setupStorePage.setupShopExp(storeDG);

		/* Select package */
		plansPage.selectDuration(planPaymentDG.getPeriod()).clickFreeTrialBtn();
		homePage.getToastMessage();

		//Validate package plan is activated
		accountPage.navigateByURL();
		Assert.assertEquals(accountPage.getPlanInfo().get(0).get(2), defaultFreeTrialPackage(storeDG.getCountry()));
	}

//	@Test(description = "Be cautious! This TC is not updated yet")
	public void ActivateFreeTrialAfterShopCreated() {

		//Randomize data
		storeDG.setDomain(Domain.valueOf(domain));
		storeDG.randomStoreData();
		System.out.println(storeDG);

		/* Sign up */
		registerAccount(storeDG);

		/* Setup store */
		setupStorePage.setupShopExp(storeDG);

		/* Select package */
		plansPage.getPackagePeriodOptions();

		driver.quit();

		driver = new InitWebdriver().getDriver("chrome", "false");
		homePage = new HomePage(driver);
		plansPage = new PlansPage(driver);
		accountPage = new AccountPage(driver, Domain.valueOf(domain));
		storeInfoPage = new StoreInformation(driver, Domain.valueOf(domain));

		new LoginPage(driver, Domain.valueOf(domain)).navigate()
			.changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.performValidLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());
		homePage.clickUpgradeNow();
		plansPage.clickFreeTrialBtn();
		homePage.getToastMessage();

		//Validate package plan is activated
		accountPage.navigateByURL();
		Assert.assertEquals(accountPage.getPlanInfo().get(0).get(2), defaultFreeTrialPackage(storeDG.getCountry()));

		validateTimezoneShopName();
	}

	@Test
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
		registerAccount(storeDG);

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
		List<String> periodPaymentOptions = packagePaymentPage.getDurationOptions();

		//Validate package period options
		Assert.assertEquals(periodPaymentOptions.size(), periodOptionCount(storeDG.getDomain()));

		//Validate available payment options
		Assert.assertEquals(packagePaymentPage.getAvailablePaymentOptions(), availablePaymentOptions(storeDG.getCountry()));

		//Validate currency
		periodPaymentOptions.stream().forEach(e -> Assert.assertTrue(e.matches(pricePerYearRegex(storeDG.getCountry()))));

		//Validate currency
		PlanPaymentReview finalizedPackagePayment =  packagePaymentPage.getFinalizePackageInfo();
		Assert.assertTrue(finalizedPackagePayment.getBasePrice().matches(currencyRegex(storeDG.getCountry())));
		Assert.assertTrue(finalizedPackagePayment.getFinalTotal().matches(currencyRegex(storeDG.getCountry())));
		if (storeDG.getDomain().equals(Domain.BIZ)) {
			Assert.assertNull(finalizedPackagePayment.getVatPrice());
		} else {
			Assert.assertTrue(finalizedPackagePayment.getVatPrice().matches(currencyRegex(storeDG.getCountry())));
		}

		String orderId = packagePaymentPage.payThenComplete(planPaymentDG.getPaymentMethod());
		packagePaymentPage.approvePackageInInternalTool(planPaymentDG.getPaymentMethod(), orderId);

		//Validate currency
		Assert.assertTrue(packagePaymentPage.getPaymentCompleteInfo().getTotal().matches(currencyRegex(storeDG.getCountry())));
		packagePaymentPage.clickBackToDashboardBtn();

		//Validate package plan is activated
		accountPage.navigateByURL();
		Assert.assertEquals(accountPage.getPlanInfo().get(0).get(2), NewPackage.getValue(planPaymentDG.getNewPackage()));

		//Validate Shop Name and timezone are displayed as expected
		storeInfoPage.navigateByURL();
		Assert.assertEquals(storeInfoPage.getShopName(), storeDG.getName());
		Assert.assertEquals(storeInfoPage.getTimezone(), storeDG.getTimezone());
	}	

	@Test
	public void AbortPaymentProcess() {
		
		//Randomize data
		storeDG.setDomain(Domain.valueOf(domain));
		storeDG.randomStoreData(country);
		storeDG.setUsername(username);
		storeDG.setPassword(password);
		
		System.out.println(storeDG);
		
		PlanStatus currentPlan = new APIAccount(new Login().setLoginInformation(storeDG.getPhoneCode(), storeDG.getUsername(), storeDG.getPassword()).getLoginInformation()).getAvailablePlanInfo().get(0);
		NewPackage currentPlanName = NewPackage.getKeyFromValue(currentPlan.getBundlePackagePlanName());
		Instant registeredDate = Instant.parse(currentPlan.getRegisterPackageDate());
		Instant expiryDate = Instant.parse(currentPlan.getExpiredPackageDate());
		
		new LoginPage(driver, Domain.valueOf(domain)).navigate()
			.changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.performValidLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());
		
		accountPage.navigateByURL().clickRenew();
		
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		planPaymentDG.setNewPackage(currentPlanName);
		
		plansPage.selectDuration(planPaymentDG.getPeriod());
		plansPage.subscribeToPackage(planPaymentDG.getNewPackage());
		
		//Bug cache not cleared
		if (plansPage.isComparisionDialogDisplayed()) plansPage.clickContinueOnFeatureComparisionDialog();
		
		packagePaymentPage.selectPaymentMethod(planPaymentDG.getPaymentMethod());
		packagePaymentPage.abandonPayment(planPaymentDG.getPaymentMethod());
		
		commonAction.navigateBack();
		
		//Validate package plan is activated
		accountPage.navigateByURL();
		
		String dateFormat = "dd-MM-yyyy";
		String subcriptionDate = DateTimeFormatter.ofPattern(dateFormat).format(LocalDateTime.ofInstant(registeredDate, ZoneId.systemDefault()));
		String newExpiryDate = DateTimeFormatter.ofPattern(dateFormat).format(LocalDateTime.ofInstant(expiryDate, ZoneId.systemDefault()));
		validatePackageIsEnabledExp(planPaymentDG, subcriptionDate, newExpiryDate);
	}		
	
//	@Test
	public void AbandonPaymentThenEnableFreeTrial() {
		
		//Randomize data
		storeDG.setDomain(Domain.BIZ);
		storeDG.setAccountType("MOBILE");
		storeDG.randomStoreData();
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		planPaymentDG.setPaymentMethod(PaymentMethod.PAYPAL); //Reset payment method to PAYPAL
		System.out.println(storeDG);
		System.out.println(planPaymentDG);
		
		/* Sign up */
		registerAccount(storeDG);
		
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
		List<String> periodPaymentOptions = packagePaymentPage.getDurationOptions();
		
		//Validate package period options
		Assert.assertEquals(periodPaymentOptions.size(), periodOptionCount(storeDG.getDomain()));
		
		//Validate available payment options
		Assert.assertEquals(packagePaymentPage.getAvailablePaymentOptions(), availablePaymentOptions(storeDG.getCountry()));
		
		//Validate currency
		periodPaymentOptions.stream().forEach(e -> Assert.assertTrue(e.matches(pricePerYearRegex(storeDG.getCountry()))));
		
		//Validate currency
		PlanPaymentReview finalizedPackagePayment =  packagePaymentPage.getFinalizePackageInfo();
		Assert.assertTrue(finalizedPackagePayment.getBasePrice().matches(currencyRegex(storeDG.getCountry())));
		Assert.assertTrue(finalizedPackagePayment.getFinalTotal().matches(currencyRegex(storeDG.getCountry())));
		if (storeDG.getDomain().equals(Domain.BIZ)) {
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
		accountPage.navigateByURL();
		Assert.assertEquals(accountPage.getPlanInfo().get(0).get(2), defaultFreeTrialPackage(storeDG.getCountry()));
		
		//Validate Shop Name and timezone are displayed as expected
		storeInfoPage.clickStoreInformationTab();
		Assert.assertEquals(storeInfoPage.getShopName(), storeDG.getName());
		Assert.assertEquals(storeInfoPage.getTimezone(), storeDG.getTimezone());
	}	

	@Test
	public void ChangePackage() {

		//Randomize data
		storeDG.randomStoreData(country);
		storeDG.setUsername(username);
		storeDG.setPassword(password);
		storeDG.setDomain(Domain.valueOf(domain));
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

		new LoginPage(driver, Domain.valueOf(domain)).navigate()
			.changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.performValidLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());

		accountPage.navigateByURL().clickRenew();

		plansPage.selectDuration(planPaymentDG.getPeriod());

		plansPage.subscribeToPackage(planPaymentDG.getNewPackage()).clickContinueOnFeatureComparisionDialog();

		PlanPaymentReview planPaymentReview =  packagePaymentPage.getFinalizePackageInfo();

		BigDecimal actualTotalAmount = new BigDecimal(planPaymentReview.getFinalTotal().replaceAll("[^\\d+\\.]",""));
		
		//Compare refundAmount
		expectedRefund = PlanMoney.resetRefund(storeDG.getDomain(), planPaymentDG.getNewPackage(), planPaymentDG.getPeriod(), expectedRefund);
		Assert.assertEquals(new BigDecimal(planPaymentReview.getRefundAmount().replaceAll("[^\\d+\\.]","")).compareTo(expectedRefund), 0);
		
		BigDecimal expectedTotal = PlanMoney.calculateFinalTotalPrice(storeDG.getDomain(), planPaymentDG.getNewPackage(), planPaymentDG.getPeriod(), expectedRefund);
		
		boolean isPaymentNeeded = expectedTotal.compareTo(BigDecimal.ZERO) != 0;
		
		if (isPaymentNeeded) {
			Assert.assertTrue(packagePaymentPage.isPaymentMethodSectionDisplayed());
		} else {
			Assert.assertFalse(packagePaymentPage.isPaymentMethodSectionDisplayed());
		}
		
		//Compare total amount
		Assert.assertEquals(actualTotalAmount.compareTo(expectedTotal), 0);
		
		String orderId;
		if (isPaymentNeeded) {
			orderId = packagePaymentPage.payThenComplete(planPaymentDG.getPaymentMethod());
			packagePaymentPage.approvePackageInInternalTool(planPaymentDG.getPaymentMethod(), orderId);
		} else {
			planPaymentDG.setPaymentMethod(PaymentMethod.BANKTRANSFER);
			orderId = packagePaymentPage.clickPlaceOrderBtn().completePayment(planPaymentDG.getPaymentMethod());
		}

		packagePaymentPage.clickBackToDashboardBtn();

		//Validate package plan is activated
		String subcriptionDate = DataGenerator.getCurrentDate("dd-MM-yyyy");
		String newExpiryDate = DataGenerator.forwardTimeWithFormat(planPaymentDG.getPeriod(), TimeUnits.YEARS, "dd-MM-yyyy");
		validatePackageIsEnabledExp(planPaymentDG, subcriptionDate, newExpiryDate);

	}
	
	@Test
	public void RenewPackage() {
		
		//Randomize data
		storeDG.randomStoreData(country);
		storeDG.setUsername(username);
		storeDG.setPassword(password);
		storeDG.setDomain(Domain.valueOf(domain));
		storeDG.setEmail(username);
		System.out.println(storeDG);
		
//		if (!storeDG.getUsername().matches("^\\d+$")) APIMailnesia.deleteAllEmails(storeDG.getEmail());
		
		PlanStatus currentPlan = new APIAccount(new Login().setLoginInformation(storeDG.getPhoneCode(), storeDG.getUsername(), storeDG.getPassword()).getLoginInformation()).getAvailablePlanInfo().get(0);
		NewPackage currentPlanName = NewPackage.getKeyFromValue(currentPlan.getBundlePackagePlanName());
		Instant registeredDate = Instant.parse(currentPlan.getRegisterPackageDate());
		Instant expiryDate = Instant.parse(currentPlan.getExpiredPackageDate());
		int period = PlanMoney.deducePeriod(registeredDate, expiryDate);
		
		new LoginPage(driver, Domain.valueOf(domain)).navigate()
			.changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.performValidLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());
		
		commonAction.refreshPage();
		
		accountPage.navigateByURL().clickRenew();
		
		planPaymentDG.randomPackageAndPaymentMethod(storeDG);
		planPaymentDG.setNewPackage(currentPlanName);
		
		plansPage.selectDuration(planPaymentDG.getPeriod());
		plansPage.subscribeToPackage(planPaymentDG.getNewPackage());
		
		//Bug cache not cleared
		if (plansPage.isComparisionDialogDisplayed()) plansPage.clickContinueOnFeatureComparisionDialog();
		
		PlanPaymentReview planPaymentReview =  packagePaymentPage.getFinalizePackageInfo();
		
		BigDecimal actualTotal = new BigDecimal(planPaymentReview.getFinalTotal().replaceAll("[^\\d+\\.]",""));
		
		BigDecimal expectedTotal = PlanMoney.calculatePriceIncludingTax(storeDG.getDomain(), planPaymentDG.getNewPackage(), planPaymentDG.getPeriod());
		
		//Compare total amount
		Assert.assertEquals(actualTotal.compareTo(expectedTotal), 0, "Actual: %s, expected: %s".formatted(actualTotal, expectedTotal));
		
		String orderId = packagePaymentPage.payThenComplete(planPaymentDG.getPaymentMethod());
		PaymentCompleteInfo paymentCompleteInfo = packagePaymentPage.getPaymentCompleteInfo();
		packagePaymentPage.approvePackageInInternalTool(planPaymentDG.getPaymentMethod(), orderId);
		
		packagePaymentPage.clickBackToDashboardBtn();
		
		//Validate package plan is activated
		String dateFormat = "dd-MM-yyyy";
		String subcriptionDate = DataGenerator.getCurrentDate(dateFormat);
		String newExpiryDate = DataGenerator.forwardTimeWithFormat(LocalDateTime.ofInstant(expiryDate, ZoneId.systemDefault()), planPaymentDG.getPeriod(), TimeUnits.YEARS, dateFormat);
		validatePackageIsEnabledExp(planPaymentDG, subcriptionDate, newExpiryDate);
		
		List<MailType> availableMail = decidedOnMailTypeToCheckOnRenewing(storeDG, planPaymentDG);
//		if (!storeDG.getUsername().matches("^\\d+$")) checkMail(availableMail, storeDG, paymentCompleteInfo, newExpiryDate.replaceAll("-", "/")); //Temporarily skip checking email for phone-based username
	}	
	
	@Test
	public void PackageBenefits() {
		
		//Randomize data
		storeDG.randomStoreData(country);
		storeDG.setUsername(username);
		storeDG.setPassword(password);
		storeDG.setDomain(Domain.valueOf(domain));

		System.out.println(storeDG);

		new LoginPage(driver, Domain.valueOf(domain)).navigate()
			.changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.performValidLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());
		
		accountPage.navigateByURL().clickRenew();
		
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
	public void CompareBenefits() {
		
		//Randomize data
		storeDG.randomStoreData(country);
		storeDG.setUsername(username);
		storeDG.setPassword(password);
		storeDG.setDomain(Domain.valueOf(domain));

		System.out.println(storeDG);
		
		PlanStatus currentPlan = new APIAccount(new Login().setLoginInformation(storeDG.getPhoneCode(), storeDG.getUsername(), storeDG.getPassword()).getLoginInformation()).getAvailablePlanInfo().get(0);
		NewPackage currentPlanName = NewPackage.getKeyFromValue(currentPlan.getBundlePackagePlanName());
		Instant registeredDate = Instant.parse(currentPlan.getRegisterPackageDate());
		Instant expiryDate = Instant.parse(currentPlan.getExpiredPackageDate());
		int period = PlanMoney.deducePeriod(registeredDate, expiryDate);
		
		new LoginPage(driver, Domain.valueOf(domain)).navigate()
			.changeDisplayLanguage(DisplayLanguage.valueOf(language))
			.performValidLogin(storeDG.getCountry(), storeDG.getUsername(), storeDG.getPassword());
		
		accountPage.navigateByURL().clickRenew();
		
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
	public void writeResult(ITestResult result) throws Exception {
		super.writeResult(result);
		driver.quit();
	}

}
