import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Sheet;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import api.dashboard.login.Login;
import pages.dashboard.home.HomePage;
import pages.dashboard.home.Permission;
import pages.dashboard.login.LoginPage;
import pages.dashboard.settings.account.AccountPage;
import pages.dashboard.settings.plans.PlansPage;
import pages.dashboard.signup.SignupPage;
import pages.gomua.headergomua.HeaderGoMua;
import pages.gomua.signup.SignupGomua;
import pages.storefront.header.HeaderSF;
import pages.thirdparty.Mailnesia;
import utilities.PropertiesUtil;
import utilities.UICommonAction;
import utilities.account.AccountTest;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;
import utilities.driver.InitWebdriver;
import utilities.enums.PaymentMethod;
import utilities.excel.Excel;
import utilities.file.FileNameAndPath;
import utilities.model.sellerApp.login.LoginInformation;

public class SignupDashboard extends BaseTest {

	SignupPage signupPage;
	LoginPage loginPage;
	HomePage homePage;
	PlansPage plansPage;

	String mail;
	String password;
	String referralCode;
	String country;
	String phoneCode;
	String currency;
	String storeLanguage;
	String storeName;
	String storeURL;
	String storePhone;
	String pickupAddress;
	String secondPickupAddress;
	String province;
	String district;
	String ward;
	String city;
	String zipCode;
	String plan;
	String paymentMethod;
	LoginInformation loginInformation;

	public void instantiatePageObjects() {
		driver = new InitWebdriver().getDriver(browser, headless);
		signupPage = new SignupPage(driver);
		loginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		commonAction = new UICommonAction(driver);
		generate = new DataGenerator();
	}	

	/**
	 * If the language is "VIE" => return "Vietnam".
	 * @return Vietnam or a random country
	 */
	public String randomCountryBasedOnLanguage(String masterLanguage) {
		return masterLanguage.contentEquals("VIE") ? "Vietnam":generate.randomCountry();
	}	
	
	public String pickStoreLanguage(String country) {
		String language ="";
		String lang = country.contentEquals("Vietnam") ? "Vietnamese": "English";
		try {
			language = PropertiesUtil.getPropertiesValueByDBLang("signup.wizard.storeLanguage." + lang);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return language;
	}

	/**
	 * @return a random payment method out of these: BANKTRANSFER/ATM/VISA/PAYPAL if the country is Vietnam or otherwise PAYPAL.
	 */
	public String randomPaymentMethod() {
		String [] paymentMethod = PaymentMethod.getAllValues();
		return country.contentEquals("Vietnam") ? paymentMethod[new Random().nextInt(paymentMethod.length)] : PaymentMethod.PAYPAL.name();
	}

	/**
	 * @return a random plan out of these: GoWEB/GoAPP/GoPOS/GoSOCIAL/GoLEAD
	 */
	public String randomPlan() {
		String [] plan = {"GoWEB","GoAPP","GoPOS","GoSOCIAL","GoLEAD"};
		return plan[new Random().nextInt(plan.length)];
	}	
	
	public void generateTestData() {
		country = randomCountryBasedOnLanguage(language);
		phoneCode = generate.getPhoneCode(country);
		storeLanguage = pickStoreLanguage(country);
		storePhone = generate.randomPhoneByCountry(country)+"0";
		mail = "auto0-shop" + storePhone + "@mailnesia.com";
		password = "fortesting!1";
		referralCode = "";
		currency = "";
		storeName = "Automation Shop " + storePhone;
		storeURL = "";
		pickupAddress = "12 Quang Trung";
		secondPickupAddress = "16 Wall Street";
		province = "rd";
		district = "rd";
		ward = "rd";
		city = "Cockney";
		zipCode = generate.generateNumber(6);
		plan = randomPlan();
		paymentMethod = randomPaymentMethod();
	}

	public String getVerificationCode(String phoneCode, String username) throws SQLException {
		if (username.matches("\\d+")) {
			return new InitConnection().getActivationKey(phoneCode + ":" + username);
		}
		return new InitConnection().getActivationKey(username);
	}		

	public void verifyUpgradeNowDialogAppearAfterLogin(String country, String user, String password) throws Exception {
		loginPage.performLogin(country, user, password);
		homePage.verifyUpgradeNowMessage(language).completeVerify();
		homePage.clickUpgradeNow();
	}

	/**
	 * This function check if Upgrade Now dialog appears at 3 random screens
	 */
	public void verifyUpgradeNowDialogAppearEverywhere() {
		List<String> menus = getParentMenuList();
		for (int i=0; i<3; i++) {
			String randomMenu = menus.get(new Random().nextInt(menus.size()));
			homePage.navigateToPage(randomMenu);
			homePage.hideFacebookBubble();
			homePage.clickUpgradeNow();
		}
	}

	/**
	 * This function opens a new tab then looks for new mails in the mailbox.
	 */
	public String[][] getMailHeaders(String username) {
		commonAction.sleepInMiliSecond(3000);
		commonAction.openNewTab();
		commonAction.switchToWindow(1);
		String [][] mailContent = new Mailnesia(driver).navigate(username).getListOfEmailHeaders();
		commonAction.closeTab();
		commonAction.switchToWindow(0);
		return mailContent;
	}

	/**
	 * Verify mails are sent to users upon successful sign-up/payment
	 * @param username
	 * @param typeOfMail accepts one of these values: SIGNUP/PAYMENT/COMPLETE
	 * @throws Exception
	 */
	public void verifyEmail(String username, String typeOfMail) throws Exception {
		String welcomeMsg = PropertiesUtil.getPropertiesValueByDBLang("mail.signup.welcome", language);
		String codeMsg = PropertiesUtil.getPropertiesValueByDBLang("mail.signup.verificationCode", language);
		String signupMsg = PropertiesUtil.getPropertiesValueByDBLang("mail.signup.successfulRegistration", language);
		String paymentMsg = PropertiesUtil.getPropertiesValueByDBLang("mail.signup.paymentConfirmation", language);
		String planMsg = PropertiesUtil.getPropertiesValueByDBLang("mail.signup.planActivation", language);

		String [][] mailContent = getMailHeaders(mail);

		boolean isMatched = false;
		for (String [] header : mailContent) {
			if (header[3].contentEquals(welcomeMsg)) {
				isMatched = true;
				break;
			}
		}
		Assert.assertTrue(isMatched, "Mail: '%s' is missing".formatted(welcomeMsg));

		// If that's a mail account, check further for presence of verification code mail.
		if (!username.matches("\\d+")) {
			isMatched = false;
			for (String [] header : mailContent) {
				if (header[3].contentEquals(signupMsg)) {
					isMatched = true;
					break;
				}
			}
			Assert.assertTrue(isMatched, "Mail: '%s' is missing".formatted(signupMsg));

			isMatched = false;
			for (String [] header : mailContent) {
				if (header[3].contains(codeMsg)) {
					isMatched = true;
					break;
				}
			}
			Assert.assertTrue(isMatched, "Mail: '%s' is missing".formatted(codeMsg));
		}

		if (typeOfMail.contentEquals("COMPLETE")) {
			// If BANKTRANSFER is selected as a payment method, payment message is present or otherwise missing
			if (paymentMethod.contentEquals("BANKTRANSFER")) {
				isMatched = false;
				for (String [] header : mailContent) {
					if (header[3].contains(paymentMsg)) {
						isMatched = true;
						break;
					}
				}
				Assert.assertTrue(isMatched, "Mail: '%s' is missing".formatted(paymentMsg));
			}
			isMatched = false;
			for (String [] header : mailContent) {
				if (header[3].contentEquals(planMsg)) {
					isMatched = true;
					break;
				}
			}
			Assert.assertTrue(isMatched, "Mail: '%s' is missing".formatted(planMsg));
		}
	}

	/**
	 * @return a list of menus we will navigate to
	 */
	public List<String> getParentMenuList() {
		Excel excel = new Excel();
		Sheet planPermissionSheet = null;
		try {
			planPermissionSheet = excel.getSheet(FileNameAndPath.FILE_FEATURE_PERMISSION, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> menuList = new ArrayList<>();

		int lastRowIndex = planPermissionSheet.getLastRowNum();
		for (int row = 1; row <= lastRowIndex; row++) {
			int packageColIndex = excel.getCellIndexByCellValue(planPermissionSheet.getRow(0), "GoFREE");
			int menuColIndex = excel.getCellIndexByCellValue(planPermissionSheet.getRow(0), "MenuItem");
			String permission = planPermissionSheet.getRow(row).getCell(packageColIndex).getStringCellValue();

			if (!permission.contentEquals("D")) continue;
			String menuItem = planPermissionSheet.getRow(row).getCell(menuColIndex).getStringCellValue();

			String parentMenu = menuItem.split("-")[0];
			if (parentMenu.contains("Marketing")) continue;
			if (parentMenu.contains("GoMua") || parentMenu.contains("Lazada")) {
				if (!currency.contains("VND")) continue; // If shop does not use VND, remove this menu item from the list
				if (!country.contentEquals("Vietnam")) continue; // If shop is not in VN, remove this menu item from the list
			}
			menuList.add(parentMenu);
		}
		return new ArrayList<>(new HashSet<>(menuList)); // Duplicated values is filtered out
	}

	@BeforeMethod
	public void setup() {
		instantiatePageObjects();
		generateTestData();
	}

	@DataProvider
	public Object[] paymentMethod(){
		return PaymentMethod.getAllValues();
	}
	@Test(dataProvider = "paymentMethod")
	public void Test_PaymentMethods(String paymentMethod) throws Exception {
		
		String[] plans = {"GoWEB", "GoAPP", "GoPOS", "GoSOCIAL", "GoLEAD"};
		
		for (String plan: plans) {
			loginPage.navigate().performLogin("Vietnam", "auto0-shop0844735279@mailnesia.com", password);
			homePage.navigateToPage("Settings");
			
			List<String> originalInfo = new AccountPage(driver).navigate().getPlanInfo(plan);
			new AccountPage(driver).clickSeePlans();

			/* Buy Plan */
			plansPage = new PlansPage(driver);
			plansPage.selectPlan(plan);
			plansPage.selectPaymentMethod(paymentMethod);
			String orderID = plansPage.completePayment(paymentMethod);
			plansPage.logoutAfterSuccessfulPurchase(paymentMethod, orderID);
			
			loginPage.navigate().performLogin("Vietnam", "auto0-shop0844735279@mailnesia.com", password);
			homePage.navigateToPage("Settings");
			List<String> laterInfo = new AccountPage(driver).navigate().getPlanInfo(plan);
			
	        String regex = "\\d{4}";

	        Pattern pattern = Pattern.compile(regex);
	        Matcher matcher = pattern.matcher(originalInfo.get(1));
	        int orginalExpiryYear = 0;
	        if (matcher.find()) {
	        	orginalExpiryYear = Integer.valueOf(matcher.group());
	        }
	        
	        Pattern pattern1 = Pattern.compile(regex);
	        Matcher matcher1 = pattern1.matcher(laterInfo.get(1));
	        int laterExpiryYear = 0;
	        if (matcher1.find()) {
	        	laterExpiryYear = Integer.valueOf(matcher1.group());
	        }
			
	        Assert.assertEquals(laterExpiryYear-orginalExpiryYear, 1);
	        new HomePage(driver).clickLogout();
		}
	}

	@DataProvider(parallel = true)
	public Object[][] shopData(){
		return new Object[][] {
//				{"Vietnam", "automation0-shop92717@mailnesia.com", "fortesting!1", new String[]{"GoWEB"}},
//				{"Dominican Republic", "6286995300", "fortesting!1", new String[]{"GoAPP"}},
//				{"Vietnam", "automation0-shop8574254116@mailnesia.com", "fortesting!1", new String[]{"GoPOS"}},
//				{"Vietnam", "automation0-shop8573697717@mailnesia.com", "fortesting!1", new String[]{"GoLEAD"}},
				{"Vietnam", "automation0-shop941@mailnesia.com", "fortesting!1", new String[]{"GoSOCIAL"}},
//				{"Vietnam", "3937825176", "fortesting!1", new String[]{"GoWEB", "GoLEAD"}},
//				{"Vietnam", "5440040148", "fortesting!1", new String[]{"GoAPP", "GoLEAD"}},
//				{"Vietnam", "5439938246", "fortesting!1", new String[]{"GoPOS", "GoLEAD"}},
//				{"Vietnam", "5439778919", "fortesting!1", new String[]{"GoPOS", "GoAPP", "GoWEB", "GoSOCIAL", "GoLEAD"}},
//				{"Vietnam", "0268173016", "fortesting!1", new String[]{"GoSOCIAL"}},
//				{"Vietnam", "auto0-shop0282881495@mailnesia.com", "fortesting!1", new String[]{"GoAPP", "GoWEB"}},
//				{"Australia", "cuocsongaus@mailnesia.com", "fortesting!1", new String[]{"GoPOS", "GoWEB", "GoSOCIAL", "GoLEAD"}},
		};
	}
	@Test(dataProvider = "shopData")
	public void Test_Permissions(String country, String username, String password, String[] packages) throws Exception {
		
		/*
		WebDriver driver = new InitWebdriver().getDriver(browser, headless);
		
		new LoginPage(driver).navigate().performLogin(country, username, password);
		
		LoginInformation loginInformation = new Login().setLoginInformation(new DataGenerator().getPhoneCode(country), username, password).getLoginInformation();
		new Permission(driver, loginInformation).testPermission(packages);
		
		driver.quit();
		*/
		
		loginPage.navigate().performLogin(country, username, password);

		loginInformation = new Login().setLoginInformation(generate.getPhoneCode(country), username, password).getLoginInformation();
		new Permission(driver, loginInformation).testPermission(packages);
		homePage.clickLogout();
	}

	//	@Test
	public void SignupDB_01_CheckTranslation() throws Exception {

		/* Set value for some variables */
		String username = storePhone;

		/* Sign up */
		signupPage.navigate().selectDisplayLanguage(language).verifyTextAtSignupScreen(language);
		signupPage.fillOutSignupForm(country, username, password, referralCode).verifyTextAtVerificationCodeScreen(username, language);
		signupPage.inputVerificationCode(getVerificationCode(phoneCode, username)).clickConfirmBtn();

		/* Setup store */
		signupPage.verifyTextAtSetupShopScreen(username, country, language);
	}

	@Test
	public void SignupDB_02_CreateShopUsingEmailAccountWithURLContainingCapitalLetters() throws Exception {

		/* Set value for some variables */
		String username = mail;
		String contact = storePhone;

		// Generate a store url containing capital letters
		Pattern p = Pattern.compile("[A-Za-z0-9]+");
		Matcher m = p.matcher(storeName + generate.generateString(10));
		while (m.find()) {
			storeURL += m.group();
		}

		/* Sign up */
		signupPage.navigate()
				.selectDisplayLanguage(language)
				.fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(phoneCode, username))
				.clickConfirmBtn();

		/* Setup store */
		signupPage.setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);

		/* Check if users are redirected to plan purchase screen */
		plansPage = new PlansPage(driver);
		plansPage.selectPlan(plan);

		/* Check if store URL in database contains only lowercase letters */
		Assert.assertEquals(new InitConnection().getStoreURL(storeName), storeURL.toLowerCase());
	}

	@Test
	public void SignupDB_03_CreateShopUsingPhoneAccountWithURLContainingCapitalLetters() throws Exception {

		/* Set value for some variables */
		String username = storePhone;
		String contact = mail;

		// Generate a store url containing capital letters
		Pattern p = Pattern.compile("[A-Za-z0-9]+");
		Matcher m = p.matcher(storeName + generate.generateString(10));
		while (m.find()) {
			storeURL += m.group();
		}

		/* Sign up */
		signupPage.navigate()
				.selectDisplayLanguage(language)
				.fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(phoneCode, username))
				.clickConfirmBtn();

		/* Setup store */
		signupPage.setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);

		/* Check if users are redirected to plan purchase screen */
		plansPage = new PlansPage(driver);
		plansPage.selectPlan(plan);

		/* Check if store URL in database contains only lowercase letters */
		Assert.assertEquals(new InitConnection().getStoreURL(storeName), storeURL.toLowerCase());
	}

	@Test
	public void SignupDB_04_CreateShopUsingExistingAccount() throws Exception {

		/* Sign up with phone */
		storePhone = AccountTest.ADMIN_SHOP_VI_USERNAME;
		password = AccountTest.ADMIN_SHOP_VI_PASSWORD;
		country = AccountTest.ADMIN_COUNTRY_TIEN;

		signupPage.navigate()
				.selectDisplayLanguage(language)
				.fillOutSignupForm(country, storePhone, password, referralCode)
				.verifyUsernameExistError(language).completeVerify();

		/* Sign up with email */
		mail = AccountTest.ADMIN_ACCOUNT_THANG;
		password = AccountTest.ADMIN_PASSWORD_THANG;
		country = AccountTest.ADMIN_COUNTRY_TIEN;

		signupPage.navigate()
				.selectDisplayLanguage(language)
				.fillOutSignupForm(country, mail, password, referralCode)
				.verifyUsernameExistError(language).completeVerify();
	}

	@Test
	public void SignupDB_05_ResendVerificationCodeToEmail() throws Exception {

		/* Set value for some variables */
		String username = mail;
		String contact = storePhone;

		/* Sign up */
		signupPage.navigate()
				.selectDisplayLanguage(language)
				.fillOutSignupForm(country, username, password, referralCode);

		String firstCode = getVerificationCode(phoneCode, username);
		signupPage.inputVerificationCode(firstCode);
		signupPage.clickResendOTP();
		signupPage.clickConfirmBtn();
		signupPage.verifyVerificationCodeError(language).completeVerify();

		// If that's a mail account then we'll wait until new code is generated
		String resentCode = firstCode;
		if (!username.matches("\\d+")) {
			commonAction.openNewTab();
			commonAction.switchToWindow(1);
			Mailnesia mailnesiaPage = new Mailnesia(driver).navigate(username);
			for (int i=0; i<10; i++) {
				resentCode = mailnesiaPage.getVerificationCode();
				if (!firstCode.contentEquals(resentCode)) break;
				commonAction.sleepInMiliSecond(3000);
				commonAction.refreshPage();
			}
			commonAction.closeTab();
			commonAction.switchToWindow(0);
		}

		signupPage.inputVerificationCode(resentCode);
		Assert.assertNotEquals(firstCode, resentCode, "New verification code");
		signupPage.clickConfirmBtn();

		/* Setup store */
		signupPage.setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();

		/* Log into shop again */
		verifyUpgradeNowDialogAppearAfterLogin(country, username, password);
	}

	@Test
	public void SignupDB_06_ResendVerificationCodeToPhone() throws Exception {

		/* Set value for some variables */
		String username = storePhone;
		String contact = mail;

		/* Sign up */
		signupPage.navigate()
				.selectDisplayLanguage(language)
				.fillOutSignupForm(country, username, password, referralCode);

		String firstCode = getVerificationCode(phoneCode, username);
		signupPage.inputVerificationCode(firstCode);
		signupPage.clickResendOTP();
		signupPage.clickConfirmBtn();
		signupPage.verifyVerificationCodeError(language).completeVerify();
		if (!username.matches("\\d+")) commonAction.sleepInMiliSecond(5000);
		String resentCode = getVerificationCode(phoneCode, username);
		signupPage.inputVerificationCode(resentCode);
		Assert.assertNotEquals(firstCode, resentCode, "New verification code");
		signupPage.clickConfirmBtn();

		/* Setup store */
		signupPage.setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		signupPage.clickLogout();

		/* Log into shop again */
		verifyUpgradeNowDialogAppearAfterLogin(country, username, password);
	}

	@Test
	public void SignupDB_07_ContinueSignupWizardAfterExitingSession() throws SQLException {

		String username = storePhone;

		// Sign up
		signupPage.navigate().fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(phoneCode, username)).clickConfirmBtn();
		signupPage.inputStoreName(storeName);

		// Exit current session
		driver.quit();

		// Re-login
		driver = new InitWebdriver().getDriver("chrome", "false");
		new LoginPage(driver).navigate().performLogin(country, username, password);
		new SignupPage(driver).inputStoreName(storeName);

	}

	@Test
	public void SignupDB_08_CreateShopUsingEmailAccountWithPromotionLinkAndDomain() throws Exception {

		/* Set value for some variables */
		String domain = "abcdefgh";
		String referralCode = "fromthompson";

		String username = mail;
		String contact = storePhone;

		/* Sign up */
		signupPage.navigate("/redirect/signup?domain=%s".formatted(domain))
				.selectDisplayLanguage(language)
				.fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(phoneCode, username))
				.clickConfirmBtn();

		/* Setup store */
		signupPage.setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		homePage.waitTillSpinnerDisappear();

		/* Check if users redirected to Home screen outright and Upgrade Now dialog pops up immediately */
		homePage.verifyUpgradeNowMessage(language).completeVerify();

		/* Check if users are redirected to plan purchase screen upon clicking on Upgrade Now button */
		homePage.clickUpgradeNow();
		new PlansPage(driver).selectPlan(plan);

		/* Verify Upgrade Now dialog appears at every screen to which the user navigates */
		verifyUpgradeNowDialogAppearEverywhere();
		homePage.clickLogout();

		/* Verify Upgrade Now popup appears on the screen when re-logging into dashboard */
		verifyUpgradeNowDialogAppearAfterLogin(country, username, password);
		verifyUpgradeNowDialogAppearEverywhere();

		/* Check mail */
		verifyEmail(username, "SIGNUP");

		/* Check store's data in database */
		String expectedReferralCode = (referralCode.length()>1) ? referralCode.toUpperCase():null;
		Assert.assertEquals(new InitConnection().getStoreDomain(storeName), domain);
		Assert.assertEquals(new InitConnection().getStoreGiftCode(storeName), expectedReferralCode);
	}

	@Test
	public void SignupDB_09_CreateShopUsingPhoneAccountWithPromotionLinkAndDomain() throws Exception {

		/* Set value for some variables */
		String domain = "abcdefgh";
		String referralCode = "fromthompson";

		String username = mail;
		String contact = storePhone;

		/* Sign up */
		signupPage.navigate("/redirect/signup?domain=%s".formatted(domain))
				.selectDisplayLanguage(language)
				.fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(phoneCode, username))
				.clickConfirmBtn();

		/* Setup store */
		signupPage.setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		homePage.waitTillSpinnerDisappear();

		/* Check if users redirected to Home screen outright and Upgrade Now dialog pops up immediately */
		homePage.verifyUpgradeNowMessage(language).completeVerify();

		/* Check if users are redirected to plan purchase screen upon clicking on Upgrade Now button */
		homePage.clickUpgradeNow();
		new PlansPage(driver).selectPlan(plan);

		/* Verify Upgrade Now dialog appears at every screen to which the user navigates */
		verifyUpgradeNowDialogAppearEverywhere();
		homePage.clickLogout();

		/* Verify Upgrade Now popup appears on the screen when re-logging into dashboard */
		verifyUpgradeNowDialogAppearAfterLogin(country, username, password);
		verifyUpgradeNowDialogAppearEverywhere();

		/* Check mail */
		verifyEmail(username, "SIGNUP");

		/* Check store's data in database */
		String expectedReferralCode = (referralCode.length()>1) ? referralCode.toUpperCase():null;
		Assert.assertEquals(new InitConnection().getStoreDomain(storeName), domain);
		Assert.assertEquals(new InitConnection().getStoreGiftCode(storeName), expectedReferralCode);
	}

	@Test
	public void SignupDB_10_CreateShopUsingPhoneAccountWithoutPromotionLinkAndDomain() throws Exception {

		/* Set value for some variables */
		String username = storePhone;
		String contact = mail;

		/* Sign up */
		signupPage.navigate()
				.selectDisplayLanguage(language)
				.fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(phoneCode, username))
				.clickConfirmBtn();

		/* Setup store */
		signupPage.setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);

		/* Check if user is redirected to package registration screen */
		signupPage.clickLogout();

		/* Re-log into shop and verify Upgrade now dialog appears immediately */
		verifyUpgradeNowDialogAppearAfterLogin(country, username, password);

		/* Check if users are redirected to plan purchase screen upon clicking on Upgrade Now button */
		new PlansPage(driver).selectPlan(plan);

		/* Verify Upgrade Now dialog appears at every screen to which the user navigates */
		verifyUpgradeNowDialogAppearEverywhere();

		/* Check mail */
		verifyEmail(username, "SIGNUP");

		/* Check store's data in database */
		String expectedReferralCode = (referralCode.length()>1) ? referralCode.toUpperCase():null;
		Assert.assertEquals(new InitConnection().getStoreDomain(storeName), null);
		Assert.assertEquals(new InitConnection().getStoreGiftCode(storeName), expectedReferralCode);
	}

	@Test
	public void SignupDB_11_CreateShopUsingStorefrontMailAccount() throws Exception {

		/* Set value for some variables */
		String username = mail;
		String contact = storePhone;
		String displayName  = storeName;
		String birthday = "21/02/1990";

		/* Signup in SF */
		pages.storefront.signup.SignupPage sf = new pages.storefront.signup.SignupPage(driver);
		sf.navigate();

		new HeaderSF(driver).clickUserInfoIcon().changeLanguage(language);

		sf.fillOutSignupForm(country, username, password, displayName, birthday);
		sf.inputVerificationCode(getVerificationCode(phoneCode, username))
				.clickConfirmBtn();

		/* Get shop's name in SF */
		String title = commonAction.getPageTitle();

		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		/* Log into dashboard */
		loginPage.navigate()
				.selectDisplayLanguage(language)
				.performLogin(country, username, password);

		/* Setup store */
		signupPage.setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);

		/* Check if user is redirected to package registration screen */
		signupPage.clickLogout();

		/* Re-log into shop and verify Upgrade now dialog appears immediately */
		verifyUpgradeNowDialogAppearAfterLogin(country, username, password);

		/* Verify Upgrade Now dialog appears at every screen to which the user navigates */
		verifyUpgradeNowDialogAppearEverywhere();

		/* Check mail */
		String welcomeMsg = PropertiesUtil.getPropertiesValueByDBLang("mail.signup.welcome", language);
		String signupMsg = PropertiesUtil.getPropertiesValueBySFLang("mail.signup.successfulRegistration", language).formatted(title);
		String codeMsg = PropertiesUtil.getPropertiesValueBySFLang("mail.signup.verificationCode", language).formatted(title);

		String [][] mailContent = getMailHeaders(mail);

		Assert.assertEquals(mailContent[0][3], welcomeMsg);
		// If that's a mail account, check for presence of mail about successful registration on SF.
		if (!username.matches("\\d+")) Assert.assertEquals(mailContent[1][3], signupMsg);
		// If that's a mail account, check further for presence of verification code mail.
		if (!username.matches("\\d+")) Assert.assertTrue(mailContent[2][3].contains(codeMsg));
	}

	@Test
	public void SignupDB_12_CreateShopUsingStorefrontPhoneAccount() throws Exception {

		/* Set value for some variables */
		String username = storePhone;
		String contact = mail;
		String displayName  = storeName;
		String birthday = "21/02/1990";

		/* Signup in SF */
		pages.storefront.signup.SignupPage sf = new pages.storefront.signup.SignupPage(driver);
		sf.navigate();

		new HeaderSF(driver).clickUserInfoIcon().changeLanguage(language);

		sf.fillOutSignupForm(country, username, password, displayName, birthday);
		sf.inputVerificationCode(getVerificationCode(phoneCode, username))
				.clickConfirmBtn();
		sf.inputEmail(contact)
				.clickCompleteBtn();

		/* Get shop's name in SF */
		String title = commonAction.getPageTitle();

		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();

		/* Log into dashboard */
		loginPage.navigate()
				.selectDisplayLanguage(language)
				.performLogin(country, username, password);

		/* Setup store */
		signupPage.setupShop(username, storeName, storeURL, country, currency, storeLanguage, "", pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);

		/* Check if user is redirected to package registration screen */
		signupPage.clickLogout();

		/* Re-log into shop and verify Upgrade now dialog appears immediately */
		verifyUpgradeNowDialogAppearAfterLogin(country, username, password);

		/* Verify Upgrade Now dialog appears at every screen to which the user navigates */
		verifyUpgradeNowDialogAppearEverywhere();

		/* Check mail */
		String welcomeMsg = PropertiesUtil.getPropertiesValueByDBLang("mail.signup.welcome", language);
		String signupMsg = PropertiesUtil.getPropertiesValueBySFLang("mail.signup.successfulRegistration", language).formatted(title);
		String codeMsg = PropertiesUtil.getPropertiesValueBySFLang("mail.signup.verificationCode", language).formatted(title);

		String [][] mailContent = getMailHeaders(contact);

		Assert.assertEquals(mailContent[0][3], welcomeMsg);
		// If that's a mail account, check for presence of mail about successful registration on SF.
		if (!username.matches("\\d+")) Assert.assertEquals(mailContent[1][3], signupMsg);
		// If that's a mail account, check further for presence of verification code mail.
		if (!username.matches("\\d+")) Assert.assertTrue(mailContent[2][3].contains(codeMsg));
	}
	
	@Test
	public void SignupDB_122_CreateShopUsingStorefrontPhoneAccount() throws Exception {
		//BH_1599 :: Version : 2
		/* Set value for some variables */
		String username = storePhone;
		String contact = mail;
		String displayName  = storeName;
		String birthday = "21/02/1990";
		
		/* Signup in SF */
		pages.storefront.signup.SignupPage sf = new pages.storefront.signup.SignupPage(driver);
		sf.navigate();
		
		new HeaderSF(driver).clickUserInfoIcon().changeLanguage(language);
		
		sf.fillOutSignupForm(country, username, password, displayName, birthday);
		sf.inputVerificationCode(getVerificationCode(phoneCode, username))
		.clickConfirmBtn();
		sf.inputEmail(contact)
		.clickCompleteBtn();
		
		/* Get shop's name in SF */
		String title = commonAction.getPageTitle();
		
		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();
		
		/* Log into dashboard */
		loginPage.navigate()
		.selectDisplayLanguage(language)
		.performLogin(country, username, password);
		
		/* Setup store */
		signupPage.setupShop(username, storeName, storeURL, country, currency, storeLanguage, "", pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		
		/* Buy Plan */
		plan = "GoSOCIAL";
		plansPage = new PlansPage(driver);
		plansPage.selectPlan(plan);
		plansPage.selectPaymentMethod(paymentMethod);
		plansPage.completePayment(paymentMethod);
		String orderID = plansPage.getOrderId();
		plansPage.logoutAfterSuccessfulPurchase(paymentMethod, orderID);

		/* Re-login to the shop and test permissions */
		loginPage.performLogin(country, username, password);
		loginInformation = new Login().setLoginInformation(generate.getPhoneCode(country), username, password).getLoginInformation();
		new Permission(driver, loginInformation).testPermission(plan);

		/* Check mail */
//		verifyEmail(username, "COMPLETE");
	}
	
	@Test
	public void SignupDB_111_CreateShopUsingStorefrontMailAccount() throws Exception {
		//BH_1368 :: Version : 2
		/* Set value for some variables */
		String username = mail;
		String contact = storePhone;
		String displayName  = storeName;
		String birthday = "21/02/1990";
		
		/* Signup in SF */
		pages.storefront.signup.SignupPage sf = new pages.storefront.signup.SignupPage(driver);
		sf.navigate();

		new HeaderSF(driver).clickUserInfoIcon().changeLanguage(language);

		sf.fillOutSignupForm(country, username, password, displayName, birthday);
		sf.inputVerificationCode(getVerificationCode(phoneCode, username))
				.clickConfirmBtn();
		
		/* Get shop's name in SF */
		String title = commonAction.getPageTitle();
		
		// Logout
		new HeaderSF(driver).clickUserInfoIcon().clickLogout();
		
		/* Log into dashboard */
		loginPage.navigate()
		.selectDisplayLanguage(language)
		.performLogin(country, username, password);
		
		/* Setup store */
		signupPage.setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		
		/* Buy Plan */
		plan = "GoAPP";
		plansPage = new PlansPage(driver);
		plansPage.selectPlan(plan);
		plansPage.selectPaymentMethod(paymentMethod);
		plansPage.completePayment(paymentMethod);
		String orderID = plansPage.getOrderId();
		plansPage.logoutAfterSuccessfulPurchase(paymentMethod, orderID);
		
		/* Re-login to the shop and test permissions */
		loginPage.performLogin(country, username, password);
		loginInformation = new Login().setLoginInformation(generate.getPhoneCode(country), username, password).getLoginInformation();
		new Permission(driver, loginInformation).testPermission(plan);
		
		/* Check mail */
//		verifyEmail(username, "COMPLETE");
	}

	@Test
	public void SignupDB_13_CreateShopUsingGomuaMailAccount() throws Exception {

		/* Set value for some variables */
		String domain = "gomua.vn";
		country = "Vietnam";
		phoneCode = generate.getPhoneCode(country);
		storeLanguage = pickStoreLanguage(country);
		storePhone = generate.randomPhoneByCountry(country);

		String username = "gomua-seller"+ storePhone +"@mailnesia.com";
		String contact = storePhone;
		String displayName  = "Gomua Seller " + storePhone;
		storeName = displayName;

		/* Signup on Gomua */
		new HeaderGoMua(driver).navigateToGoMua()
				.clickCreateShop()
				.clickCreateGomuaAccountBtn()
				.inputUsername(username)
				.inputPassWord(password)
				.inputDisplayName(displayName)
				.clickContinueBtn()
				.inputVerificationCode(getVerificationCode(phoneCode, username))
				.clickVerifyAndLoginBtn()
				.clickAgreeAndContiueBtn();
		commonAction.sleepInMiliSecond(1000); //At times it takes <1s for the URL to change

		/* Verify dashboard URL contains gomua.vn */
		String expectedDomain = utilities.links.Links.DOMAIN+utilities.links.Links.LOGIN_PATH+"?domain="+domain;
		Assert.assertEquals(new UICommonAction(driver).getCurrentURL(), expectedDomain);

		/* Log into dashboard */
		loginPage.performLogin(country, username, password);

		/* Setup store */
		signupPage.setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		homePage.waitTillSpinnerDisappear();

		/* Verify upgrade now popup can be closed at home screen */
		homePage.verifyUpgradeNowMessage(language).completeVerify();
		homePage.closeUpgradeNowPopUp();
		homePage.skipIntroduction();
		homePage.clickLogout();

		/* Verify upgrade now popup does not appear at several screens */
		loginPage.performLogin(country, username, password);
		homePage.verifyUpgradeNowMessage(language).completeVerify();
		homePage.closeUpgradeNowPopUp();

		homePage.navigateToPage("Products", "All Products");
		Assert.assertFalse(homePage.checkPresenceOfUpgradeNowPopUp());
		Assert.assertFalse(homePage.checkPresenceOfCloseUpgradeNowPopUpIcon());

		homePage.navigateToPage("Orders", "Order List");
		Assert.assertFalse(homePage.checkPresenceOfUpgradeNowPopUp());
		Assert.assertFalse(homePage.checkPresenceOfCloseUpgradeNowPopUpIcon());

		homePage.navigateToPage("Settings");
		Assert.assertFalse(homePage.checkPresenceOfUpgradeNowPopUp());
		Assert.assertFalse(homePage.checkPresenceOfCloseUpgradeNowPopUpIcon());

		homePage.navigateToPage("Services");
		Assert.assertTrue(homePage.checkPresenceOfUpgradeNowPopUp());
		Assert.assertFalse(homePage.checkPresenceOfCloseUpgradeNowPopUpIcon());
		homePage.clickUpgradeNow();

		/* Check mails */
		String welcomeMsg = PropertiesUtil.getPropertiesValueByDBLang("mail.signup.welcome", language);
		String signupMsg = new SignupGomua(driver).SUCCESSFUL_SIGNUP_MESSAGE_VI;
		String codeMsg = new SignupGomua(driver).VERIFICATION_CODE_MESSAGE_VI;

		String [][] mailContent = getMailHeaders(username);

		Assert.assertEquals(mailContent[0][3], welcomeMsg);
		// If that's a mail account, check for presence of mail about successful registration on GoMua.
		if (!username.matches("\\d+")) Assert.assertEquals(mailContent[1][3], signupMsg);
		// If that's a mail account, check further for presence of verification code mail.
		if (!username.matches("\\d+")) Assert.assertTrue(mailContent[2][3].contains(codeMsg));

		/* Verify domain is configured as expected */
		Assert.assertEquals(new InitConnection().getStoreDomain(storeName), domain);
	}
	@Test
	public void SignupDB_133_CreateShopUsingGomuaMailAccount() throws Exception {
		//BH_1365 :: Version : 2
		/* Set value for some variables */
		String domain = "gomua.vn";
		country = "Vietnam";
		phoneCode = generate.getPhoneCode(country);
		storeLanguage = pickStoreLanguage(country);
		storePhone = generate.randomPhoneByCountry(country);
		
		String username = "gomua-seller"+ storePhone +"@mailnesia.com";
		String contact = storePhone;
		String displayName  = "Gomua Seller " + storePhone;
		storeName = displayName;
		
		/* Signup on Gomua */
		new HeaderGoMua(driver).navigateToGoMua()
		.clickCreateShop()
		.clickCreateGomuaAccountBtn()
		.inputUsername(username)
		.inputPassWord(password)
		.inputDisplayName(displayName)
		.clickContinueBtn()
		.inputVerificationCode(getVerificationCode(phoneCode, username))
		.clickVerifyAndLoginBtn();
		
		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		
		/* Setup store */
		signupPage.setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		
		/* Buy Plan */
		plan = "GoLEAD";
		paymentMethod = PaymentMethod.ATM.name();
		plansPage = new PlansPage(driver);
		plansPage.selectPlan(plan);
		plansPage.selectPaymentMethod(paymentMethod);
		plansPage.completePayment(paymentMethod);
		String orderID = plansPage.getOrderId();
		plansPage.logoutAfterSuccessfulPurchase(paymentMethod, orderID);

		/* Re-login to the shop and test permissions */
		loginPage.performLogin(country, username, password);
		loginInformation = new Login().setLoginInformation(generate.getPhoneCode(country), username, password).getLoginInformation();
		new Permission(driver, loginInformation).testPermission(plan);
		
		/* Check mails */
		String welcomeMsg = PropertiesUtil.getPropertiesValueByDBLang("mail.signup.welcome");
		String signupMsg = new SignupGomua(driver).SUCCESSFUL_SIGNUP_MESSAGE_VI;
		String codeMsg = new SignupGomua(driver).VERIFICATION_CODE_MESSAGE_VI;
		
		String [][] mailContent = getMailHeaders(username);
		
		Assert.assertEquals(mailContent[0][3], welcomeMsg);
		// If that's a mail account, check for presence of mail about successful registration on GoMua.
		if (!username.matches("\\d+")) Assert.assertEquals(mailContent[1][3], signupMsg);
		// If that's a mail account, check further for presence of verification code mail.
		if (!username.matches("\\d+")) Assert.assertTrue(mailContent[2][3].contains(codeMsg));
		
		/* Verify domain is configured as expected */
		Assert.assertEquals(new InitConnection().getStoreDomain(storeName), null);
	}

	@Test
	public void SignupDB_14_CreateShopUsingGomuaPhoneAccount() throws Exception {

		/* Set value for some variables */
		String domain = "gomua.vn";
		country = "Vietnam";
		phoneCode = generate.getPhoneCode(country);
		storeLanguage = pickStoreLanguage(country);
		storePhone = generate.randomPhoneByCountry(country);

		String username = storePhone;
		String contact = "gomua-seller"+ storePhone +"@mailnesia.com";
		String displayName  = "Gomua Seller " + storePhone;
		storeName = displayName;

		/* Signup on Gomua */
		new HeaderGoMua(driver).navigateToGoMua()
				.clickCreateShop()
				.clickCreateGomuaAccountBtn()
				.inputUsername(username)
				.inputPassWord(password)
				.inputDisplayName(displayName)
				.clickContinueBtn()
				.inputVerificationCode(getVerificationCode(phoneCode, username))
				.clickVerifyAndLoginBtn()
				.inputEmail(contact)
				.clickComplete()
				.clickAgreeAndContiueBtn();
		commonAction.sleepInMiliSecond(1000); //At times it takes <1s for the URL to change

		/* Verify dashboard URL contains gomua.vn */
		String expectedDomain = utilities.links.Links.DOMAIN+utilities.links.Links.LOGIN_PATH+"?domain="+domain;
		Assert.assertEquals(new UICommonAction(driver).getCurrentURL(), expectedDomain);

		/* Log into dashboard */
		loginPage.performLogin(country, username, password);

		/* Setup store */
		signupPage.setupShop(username, storeName, storeURL, country, currency, storeLanguage, "", pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);
		homePage.waitTillSpinnerDisappear();

		/* Verify upgrade now popup can be closed at home screen */
		homePage.verifyUpgradeNowMessage(language).completeVerify();
		homePage.closeUpgradeNowPopUp();
		homePage.skipIntroduction();
		homePage.clickLogout();

		/* Verify upgrade now popup does not appear at several screens */
		loginPage.performLogin(country, username, password);
		homePage.verifyUpgradeNowMessage(language).completeVerify();
		homePage.closeUpgradeNowPopUp();

		homePage.navigateToPage("Products", "All Products");
		Assert.assertFalse(homePage.checkPresenceOfUpgradeNowPopUp());
		Assert.assertFalse(homePage.checkPresenceOfCloseUpgradeNowPopUpIcon());

		homePage.navigateToPage("Orders", "Order List");
		Assert.assertFalse(homePage.checkPresenceOfUpgradeNowPopUp());
		Assert.assertFalse(homePage.checkPresenceOfCloseUpgradeNowPopUpIcon());

		homePage.navigateToPage("Settings");
		Assert.assertFalse(homePage.checkPresenceOfUpgradeNowPopUp());
		Assert.assertFalse(homePage.checkPresenceOfCloseUpgradeNowPopUpIcon());

		homePage.navigateToPage("Services");
		Assert.assertTrue(homePage.checkPresenceOfUpgradeNowPopUp());
		Assert.assertFalse(homePage.checkPresenceOfCloseUpgradeNowPopUpIcon());
		homePage.clickUpgradeNow();

		/* Check mails */
		String welcomeMsg = PropertiesUtil.getPropertiesValueByDBLang("mail.signup.welcome");
		String signupMsg = new SignupGomua(driver).SUCCESSFUL_SIGNUP_MESSAGE_VI;
		String codeMsg = new SignupGomua(driver).VERIFICATION_CODE_MESSAGE_VI;

		String [][] mailContent = getMailHeaders(contact);

		Assert.assertEquals(mailContent[0][3], welcomeMsg);
		// If that's a mail account, check for presence of mail about successful registration on GoMua.
		if (!username.matches("\\d+")) Assert.assertEquals(mailContent[1][3], signupMsg);
		// If that's a mail account, check further for presence of verification code mail.
		if (!username.matches("\\d+")) Assert.assertTrue(mailContent[2][3].contains(codeMsg));

		/* Verify domain is configured as expected */
		Assert.assertEquals(new InitConnection().getStoreDomain(storeName), domain);
	}
	
	@Test
	public void SignupDB_144_CreateShopUsingGomuaPhoneAccount() throws Exception {
		//BH_1277 :: Version : 2
		/* Set value for some variables */
		String domain = "gomua.vn";
		country = "Vietnam";
		phoneCode = generate.getPhoneCode(country);
		storeLanguage = pickStoreLanguage(country);
		storePhone = generate.randomPhoneByCountry(country);
		
		String username = storePhone;
		String contact = "gomua-seller"+ storePhone +"@mailnesia.com";
		String displayName  = "Gomua Seller " + storePhone;
		storeName = displayName;
		
		/* Signup on Gomua */
		new HeaderGoMua(driver).navigateToGoMua()
		.clickCreateShop()
		.clickCreateGomuaAccountBtn()
		.inputUsername(username)
		.inputPassWord(password)
		.inputDisplayName(displayName)
		.clickContinueBtn()
		.inputVerificationCode(getVerificationCode(phoneCode, username))
		.clickVerifyAndLoginBtn()
		.inputEmail(contact)
		.clickComplete();
		
		/* Log into dashboard */
		loginPage.navigate().performLogin(country, username, password);
		
		/* Setup store */
		signupPage.setupShop(username, storeName, storeURL, country, currency, storeLanguage, "", pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);

		/* Check if user is redirected to package registration screen */
		signupPage.clickLogout();

		/* Re-log into shop and verify Upgrade now dialog appears immediately */
		verifyUpgradeNowDialogAppearAfterLogin(country, username, password);

		/* Verify Upgrade Now dialog appears at every screen to which the user navigates */
		verifyUpgradeNowDialogAppearEverywhere();
		
		/* Check mails */
		String welcomeMsg = PropertiesUtil.getPropertiesValueByDBLang("mail.signup.welcome");
		String signupMsg = new SignupGomua(driver).SUCCESSFUL_SIGNUP_MESSAGE_VI;
		String codeMsg = new SignupGomua(driver).VERIFICATION_CODE_MESSAGE_VI;
		
		String [][] mailContent = getMailHeaders(contact);
		
		Assert.assertEquals(mailContent[0][3], welcomeMsg);
		// If that's a mail account, check for presence of mail about successful registration on GoMua.
		if (!username.matches("\\d+")) Assert.assertEquals(mailContent[1][3], signupMsg);
		// If that's a mail account, check further for presence of verification code mail.
		if (!username.matches("\\d+")) Assert.assertTrue(mailContent[2][3].contains(codeMsg));
		
		/* Verify domain is configured as expected */
		Assert.assertEquals(new InitConnection().getStoreDomain(storeName), null);
	}

	@Test
	public void SignupDB_15_CreateShopUsingPhoneAccountThenBuyPlan() throws Exception {

		/* Set value for some variables */
		String username = storePhone;
		String contact = mail;

		/* Sign up */
		signupPage.navigate()
				.selectDisplayLanguage(language)
				.fillOutSignupForm(country, username, password, "HAPPY")
				.inputVerificationCode(getVerificationCode(phoneCode, username))
				.clickConfirmBtn();

		/* Setup store */
		signupPage.setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);

		/* Buy Plan */
		plansPage = new PlansPage(driver);
		plansPage.selectPlan(plan);
		plansPage.selectPaymentMethod(paymentMethod);
		plansPage.completePayment(paymentMethod);
		String orderID = plansPage.getOrderId();
		plansPage.logoutAfterSuccessfulPurchase(paymentMethod, orderID);

		/* Re-login to the shop and test permissions */
		loginPage.performLogin(country, username, password);
		loginInformation = new Login().setLoginInformation(phoneCode, username, password).getLoginInformation();
		new Permission(driver, loginInformation).testPermission(plan);

		/* Check mail */
		verifyEmail(username, "COMPLETE");
	}

	@Test
	public void SignupDB_16_CreateShopUsingMailAccountThenBuyPlan() throws Exception {

		/* Set value for some variables */
		String username = mail;
		String contact = storePhone;

		/* Sign up */
		signupPage.navigate()
				.selectDisplayLanguage(language)
				.fillOutSignupForm(country, username, password, referralCode)
				.inputVerificationCode(getVerificationCode(phoneCode, username))
				.clickConfirmBtn();

		/* Setup store */
		signupPage.setupShop(username, storeName, storeURL, country, currency, storeLanguage, contact, pickupAddress,
				secondPickupAddress, province, district, ward, city, zipCode);

		/* Buy Plan */
		plansPage = new PlansPage(driver);
		plansPage.selectPlan(plan);
		plansPage.selectPaymentMethod(paymentMethod);
		plansPage.completePayment(paymentMethod);
		String orderID = plansPage.getOrderId();
		plansPage.logoutAfterSuccessfulPurchase(paymentMethod, orderID);

		/* Re-login to the shop and test permissions */
		loginPage.performLogin(country, username, password);
		loginInformation = new Login().setLoginInformation(phoneCode, username, password).getLoginInformation();
		new Permission(driver, loginInformation).testPermission(plan);

		/* Check mail */
		verifyEmail(username, "COMPLETE");
	}

	@AfterMethod
	public void writeResult(ITestResult result) throws IOException {
		super.writeResult(result);
		driver.quit();
	}

}
