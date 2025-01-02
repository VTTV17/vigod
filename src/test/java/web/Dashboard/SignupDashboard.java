package web.Dashboard;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Sheet;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import api.Seller.login.Login;

import web.Dashboard.home.HomePage;
import web.Dashboard.home.Permission;
import web.Dashboard.login.LoginPage;
import web.Dashboard.settings.account.AccountPage;
import web.Dashboard.settings.plans.PackagePayment;
import web.Dashboard.settings.plans.PlansPage;
import web.Dashboard.signup.SetUpStorePage;
import web.Dashboard.signup.SignupPage;
import web.GoMua.headergomua.HeaderGoMua;
import web.GoMua.signup.SignupGomua;
import web.StoreFront.GeneralSF;
import web.StoreFront.header.HeaderSF;
import utilities.thirdparty.Mailnesia;
import utilities.utils.PropertiesUtil;
import utilities.commons.UICommonAction;
import utilities.data.DataGenerator;
import utilities.database.InitConnection;
import utilities.driver.InitWebdriver;
import utilities.enums.DisplayLanguage;
import utilities.enums.Domain;
import utilities.enums.PaymentMethod;
import utilities.excel.Excel;
import utilities.file.FileNameAndPath;
import utilities.links.Links;
import utilities.model.dashboard.setupstore.SetupStoreDG;
import utilities.model.sellerApp.login.LoginInformation;

public class SignupDashboard extends BaseTest {

	SignupPage signupPage;
	LoginPage loginPage;
	HomePage homePage;
	PlansPage plansPage;
	PackagePayment packagePayment;

	String mail;
	String password;
	String country;
	String phoneCode;
	String currency;
	String storeLanguage;
	String storeName;
	String storePhone;
	String pickupAddress;
	LoginInformation loginInformation;

	public void instantiatePageObjects() {
		driver = new InitWebdriver().getDriver(browser, headless);
		signupPage = new SignupPage(driver);
		loginPage = new LoginPage(driver);
		homePage = new HomePage(driver);
		packagePayment = new PackagePayment(driver);
		commonAction = new UICommonAction(driver);
		generate = new DataGenerator();
	}	

	/**
	 * If the language is "VIE" => return "Vietnam".
	 * @return Vietnam or a random country
	 */
	public String randomCountryBasedOnLanguage(String masterLanguage) {
		return masterLanguage.contentEquals("VIE") ? "Vietnam":DataGenerator.randomCountry();
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


	public void generateTestData() {
		country = randomCountryBasedOnLanguage(language);
		phoneCode = DataGenerator.getPhoneCode(country);
		storeLanguage = pickStoreLanguage(country);
		storePhone = DataGenerator.randomPhoneByCountry(country)+"0";
		mail = "auto0-shop" + storePhone + "@mailnesia.com";
		password = "fortesting!1";
		currency = "";
		storeName = "Automation Shop " + storePhone;
	}

	public String getVerificationCode(String phoneCode, String username) throws SQLException {
		if (username.matches("\\d+")) {
			return new InitConnection().getActivationKey(phoneCode + ":" + username);
		}
		return new InitConnection().getActivationKey(username);
	}		


	/**
	 * This function opens a new tab then looks for new mails in the mailbox.
	 */
	public String[][] getMailHeaders(String username) {
		UICommonAction.sleepInMiliSecond(3000);
		commonAction.openNewTab();
		commonAction.switchToWindow(1);
		String [][] mailContent = new Mailnesia(driver).navigate(username).getListOfEmailHeaders();
		commonAction.closeTab();
		commonAction.switchToWindow(0);
		return mailContent;
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
//			plansPage.selectPlan(plan);
			PackagePayment packagePayment = new PackagePayment(driver);
			packagePayment.selectPaymentMethod(paymentMethod);
			String orderID = packagePayment.completePayment(paymentMethod);
			packagePayment.logoutAfterSuccessfulPurchase(paymentMethod, orderID);
			
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

	@DataProvider
	public Object[][] shopData(){
		return new Object[][] {
//				{"Vietnam", "automation0-shop92717@mailnesia.com", "fortesting!1", new String[]{"GoWEB"}},
//				{"Dominican Republic", "6286995300", "fortesting!1", new String[]{"GoAPP"}},
//				{"Vietnam", "automation0-shop8574254116@mailnesia.com", "fortesting!1", new String[]{"GoPOS"}},
//				{"Vietnam", "automation0-shop8573697717@mailnesia.com", "fortesting!1", new String[]{"GoLEAD"}},
//				{"Vietnam", "automation0-shop941@mailnesia.com", "fortesting!1", new String[]{"GoSOCIAL"}},
//				{"Vietnam", "3937825176", "fortesting!1", new String[]{"GoWEB", "GoLEAD"}},
//				{"Vietnam", "5440040148", "fortesting!1", new String[]{"GoAPP", "GoLEAD"}},
//				{"Vietnam", "5439938246", "fortesting!1", new String[]{"GoPOS", "GoLEAD"}},
//				{"Vietnam", "5439778919", "fortesting!1", new String[]{"GoPOS", "GoAPP", "GoWEB", "GoSOCIAL", "GoLEAD"}},
//				{"Vietnam", "auto0-shop04244194400@mailnesia.com", "fortesting!1", new String[]{"GoAPP"}},
//				{"Vietnam", "04264653080", "fortesting!1", new String[]{"GoWEB"}},
				{"Vietnam", "auto0-shop05642156070@mailnesia.com", "fortesting!1", new String[]{"GoWEB", "GoAPP"}},
//				{"Vietnam", "09126456670", "fortesting!1", new String[]{"GGoSOCIAL"}},
//				{"Vietnam", "0268173016", "fortesting!1", new String[]{"GoSOCIAL"}},
//				{"Vietnam", "auto0-shop0282881495@mailnesia.com", "fortesting!1", new String[]{"GoAPP", "GoWEB"}},
//				{"Australia", "cuocsongaus@mailnesia.com", "fortesting!1", new String[]{"GoPOS", "GoWEB", "GoSOCIAL", "GoLEAD"}},
		};
	}
	@Test(dataProvider = "shopData")
	public void Test_Permissions(String country, String username, String password, String[] packages) throws Exception {
		
		loginPage.navigate().performLogin(country, username, password);

		loginInformation = new Login().setLoginInformation(DataGenerator.getPhoneCode(country), username, password).getLoginInformation();
		new Permission(driver).testPermission(packages);
		homePage.clickLogout();
	}

	//	@Test
	@Deprecated
	public void SignupDB_01_CheckTranslation() throws Exception {

		/* Set value for some variables */
		String username = storePhone;

		/* Sign up */
		signupPage.navigate().selectDisplayLanguage(DisplayLanguage.valueOf(language)).verifyTextAtSignupScreen(language);
		signupPage.fillOutSignupForm(country, username, password).verifyTextAtVerificationCodeScreen(username, language);
		signupPage.inputVerificationCode(getVerificationCode(phoneCode, username)).clickConfirmOTPBtn();

		/* Setup store */
		signupPage.verifyTextAtSetupShopScreen(username, country, language);
	}

	@Test
	public void SignupDB_11_CreateShopUsingStorefrontMailAccount() throws Exception {

		/* Set value for some variables */
		String username = mail;
		String contact = storePhone;
		String displayName  = storeName;
		String birthday = "21/02/1990";

		/* Signup in SF */
		new GeneralSF(driver).navigateToURL(Links.SF_URL_TIEN);
		
		web.StoreFront.signup.SignupPage sf = new web.StoreFront.signup.SignupPage(driver);

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
		new SetUpStorePage(driver).setupShopExp(new SetupStoreDG(Domain.valueOf(domain)));

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
		new GeneralSF(driver).navigateToURL(Links.SF_URL_TIEN);
		
		web.StoreFront.signup.SignupPage sf = new web.StoreFront.signup.SignupPage(driver);

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
		new SetUpStorePage(driver).setupShopExp(new SetupStoreDG(Domain.valueOf(domain)));

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
	public void SignupDB_13_CreateShopUsingGomuaMailAccount() throws Exception {

		/* Set value for some variables */
		String domain = "gomua.vn";
		country = "Vietnam";
		phoneCode = DataGenerator.getPhoneCode(country);
		storeLanguage = pickStoreLanguage(country);
		storePhone = DataGenerator.randomPhoneByCountry(country);

		String username = "gomua-seller"+ storePhone +"@mailnesia.com";
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
		UICommonAction.sleepInMiliSecond(1000); //At times it takes <1s for the URL to change

		/* Verify dashboard URL contains gomua.vn */
		String expectedDomain = utilities.links.Links.DOMAIN+utilities.links.Links.LOGIN_PATH+"?domain="+domain;
		Assert.assertEquals(new UICommonAction(driver).getCurrentURL(), expectedDomain);

		/* Log into dashboard */
		loginPage.performLogin(country, username, password);

		/* Setup store */
		new SetUpStorePage(driver).setupShopExp(new SetupStoreDG(Domain.valueOf(domain)));
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
		phoneCode = DataGenerator.getPhoneCode(country);
		storeLanguage = pickStoreLanguage(country);
		storePhone = DataGenerator.randomPhoneByCountry(country);
		
		String username = "gomua-seller"+ storePhone +"@mailnesia.com";
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
		new SetUpStorePage(driver).setupShopExp(new SetupStoreDG(Domain.valueOf(domain)));
		
		/* Buy Plan */
		String plan = "GoLEAD";
		String paymentMethod = PaymentMethod.ATM.name();
		plansPage = new PlansPage(driver);
//		plansPage.selectPlan(plan);
		packagePayment.selectPaymentMethod(paymentMethod);
		packagePayment.completePayment(paymentMethod);
		String orderID = packagePayment.getOrderId();
		packagePayment.logoutAfterSuccessfulPurchase(paymentMethod, orderID);

		/* Re-login to the shop and test permissions */
		loginPage.performLogin(country, username, password);
		loginInformation = new Login().setLoginInformation(DataGenerator.getPhoneCode(country), username, password).getLoginInformation();
		new Permission(driver).testPermission(plan);
		
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
		phoneCode = DataGenerator.getPhoneCode(country);
		storeLanguage = pickStoreLanguage(country);
		storePhone = DataGenerator.randomPhoneByCountry(country);

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
		UICommonAction.sleepInMiliSecond(1000); //At times it takes <1s for the URL to change

		/* Verify dashboard URL contains gomua.vn */
		String expectedDomain = utilities.links.Links.DOMAIN+utilities.links.Links.LOGIN_PATH+"?domain="+domain;
		Assert.assertEquals(new UICommonAction(driver).getCurrentURL(), expectedDomain);

		/* Log into dashboard */
		loginPage.performLogin(country, username, password);

		/* Setup store */
		new SetUpStorePage(driver).setupShopExp(new SetupStoreDG(Domain.valueOf(domain)));
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
		phoneCode = DataGenerator.getPhoneCode(country);
		storeLanguage = pickStoreLanguage(country);
		storePhone = DataGenerator.randomPhoneByCountry(country);
		
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
		new SetUpStorePage(driver).setupShopExp(new SetupStoreDG(Domain.valueOf(domain)));
		
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
	
	@AfterMethod
	public void writeResult(ITestResult result) throws Exception {
		super.writeResult(result);
		driver.quit();
	}

}
